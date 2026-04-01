package com.example.hungarian.fixers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.refactoring.RefactoringFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法名小写化修复器
 * 将首字母大写的方法名改为小写开头（符合 Java 规范）
 *
 * 例如：
 * 修复前：public void SetName() { ... }
 * 修复后：public void setName() { ... }
 */
public class MethodNamingFixer {

    private static final Logger LOG = Logger.getInstance(MethodNamingFixer.class);

    private final Project project;

    public MethodNamingFixer(Project project) {
        this.project = project;
    }

    /**
     * 修复文件中的方法命名问题
     */
    public FixResult fixFile(@NotNull PsiFile file) {
        List<String> changes = new ArrayList<>();
        List<ConflictInfo> conflicts = new ArrayList<>();

        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                // 跳过构造函数
                if (method.isConstructor()) {
                    return;
                }

                // 检查是否需要修复
                String methodName = method.getName();
                if (methodName != null && needsFix(methodName)) {
                    String newName = decapitalize(methodName);

                    // 检查是否有冲突
                    ConflictInfo conflict = checkConflicts(method, newName);
                    if (conflict != null) {
                        conflicts.add(conflict);
                        LOG.warn("Conflict detected for method: " + methodName + " -> " + newName);
                    } else {
                        // 检查 overridden 方法
                        List<PsiMethod> overridingMethods = findOverridingMethods(method);
                        if (!overridingMethods.isEmpty()) {
                            // 需要同步修改所有子类方法
                            if (renameMethodAndOverrides(method, overridingMethods, newName)) {
                                changes.add("Renamed method: " + methodName + " -> " + newName + " (with " + overridingMethods.size() + " overrides)");
                                LOG.info("Renamed method with overrides: " + methodName + " -> " + newName);
                            }
                        } else {
                            // 普通方法，直接重命名
                            if (renameMethod(method, newName)) {
                                changes.add("Renamed method: " + methodName + " -> " + newName);
                                LOG.info("Renamed method: " + methodName + " -> " + newName);
                            }
                        }
                    }
                }
                super.visitMethod(method);
            }
        });

        return new FixResult(changes, conflicts);
    }

    /**
     * 检查方法名是否需要修复
     */
    private boolean needsFix(@NotNull String methodName) {
        if (methodName.isEmpty()) {
            return false;
        }

        // 首字母大写且第二个字母小写（如 SetName）
        // 或者全大写缩写（如 GetURL -> getURL）
        char firstChar = methodName.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            // 排除常见的允许大写开头的方法
            if (isAllowedUpperCaseStart(methodName)) {
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * 检查是否允许首字母大写（某些特殊情况）
     */
    private boolean isAllowedUpperCaseStart(@NotNull String methodName) {
        // 允许的方法：
        // 1. 全大写（常量方法，虽然不常见）
        // 2. 某些框架特定方法（如 GetInstance, Create 等工厂方法）
        // 3. 长度 1 的方法名（如 A, B 等）

        if (methodName.length() == 1) {
            return true;
        }

        // 检查是否是全大写
        if (methodName.equals(methodName.toUpperCase())) {
            return true;
        }

        return false;
    }

    /**
     * 将首字母小写化
     */
    @NotNull
    private String decapitalize(@NotNull String name) {
        if (name.isEmpty()) {
            return name;
        }
        char firstChar = name.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return Character.toLowerCase(firstChar) + name.substring(1);
        }
        return name;
    }

    /**
     * 查找所有重写该方法的方法
     */
    @NotNull
    private List<PsiMethod> findOverridingMethods(@NotNull PsiMethod method) {
        List<PsiMethod> overrides = new ArrayList<>();

        // 使用 IntelliJ 的搜索 API 查找重写方法
        OverridingMethodsSearch.search(method).forEach((PsiMethod m) -> overrides.add(m));

        return overrides;
    }

    /**
     * 检查重命名是否有冲突
     */
    @Nullable
    private ConflictInfo checkConflicts(@NotNull PsiMethod method, @NotNull String newName) {
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return null;
        }

        // 检查同类中是否已有同名方法
        for (PsiMethod existing : containingClass.getMethods()) {
            if (existing != method && newName.equals(existing.getName())) {
                // 检查参数列表是否相同
                if (hasSameParameterTypes(method, existing)) {
                    return new ConflictInfo(
                        method,
                        newName,
                        "Method '" + newName + "' already exists in class '" + containingClass.getName() + "'"
                    );
                }
            }
        }

        // 检查是否与父类/接口方法冲突
        PsiClass superClass = containingClass.getSuperClass();
        if (superClass != null) {
            for (PsiMethod parentMethod : superClass.getMethods()) {
                if (newName.equals(parentMethod.getName())) {
                    if (!hasSameParameterTypes(method, parentMethod)) {
                        return new ConflictInfo(
                            method,
                            newName,
                            "Renaming would create conflicting override of method in '" + superClass.getName() + "'"
                        );
                    }
                }
            }
        }

        // 检查接口方法
        for (PsiClass interfaceClass : containingClass.getInterfaces()) {
            for (PsiMethod interfaceMethod : interfaceClass.getMethods()) {
                if (newName.equals(interfaceMethod.getName())) {
                    if (!hasSameParameterTypes(method, interfaceMethod)) {
                        return new ConflictInfo(
                            method,
                            newName,
                            "Renaming would create conflicting override of method in interface '" + interfaceClass.getName() + "'"
                        );
                    }
                }
            }
        }

        return null;
    }

    /**
     * 检查两个方法的参数类型是否相同
     */
    private boolean hasSameParameterTypes(@NotNull PsiMethod method1, @NotNull PsiMethod method2) {
        PsiParameter[] params1 = method1.getParameterList().getParameters();
        PsiParameter[] params2 = method2.getParameterList().getParameters();

        if (params1.length != params2.length) {
            return false;
        }

        for (int i = 0; i < params1.length; i++) {
            String type1 = params1[i].getType().getCanonicalText();
            String type2 = params2[i].getType().getCanonicalText();
            if (!type1.equals(type2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 重命名方法及其所有重写方法
     */
    private boolean renameMethodAndOverrides(
        @NotNull PsiMethod method,
        @NotNull List<PsiMethod> overridingMethods,
        @NotNull String newName
    ) {
        try {
            // 首先重命名基类方法
            boolean success = renameMethod(method, newName);
            if (!success) {
                return false;
            }

            // 然后重命名所有子类方法
            for (PsiMethod override : overridingMethods) {
                renameMethod(override, newName);
            }

            return true;
        } catch (Exception e) {
            LOG.error("Failed to rename method and overrides", e);
            return false;
        }
    }

    /**
     * 重命名单个方法
     */
    private boolean renameMethod(@NotNull PsiMethod method, @NotNull String newName) {
        try {
            RefactoringFactory factory = RefactoringFactory.getInstance(project);
            var refactoring = factory.createRename(method, newName);
            refactoring.run();
            return true;
        } catch (Exception e) {
            LOG.warn("Failed to rename method: " + method.getName(), e);
            return false;
        }
    }

    /**
     * 结果类
     */
    public static class FixResult {
        public final List<String> changes;
        public final List<ConflictInfo> conflicts;
        public final int changeCount;
        public final int conflictCount;

        public FixResult(List<String> changes, List<ConflictInfo> conflicts) {
            this.changes = changes;
            this.conflicts = conflicts;
            this.changeCount = changes.size();
            this.conflictCount = conflicts.size();
        }

        public boolean hasChanges() {
            return changeCount > 0;
        }

        public boolean hasConflicts() {
            return conflictCount > 0;
        }

        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("Renamed ").append(changeCount).append(" method(s)");
            if (conflictCount > 0) {
                sb.append(", ").append(conflictCount).append(" conflict(s) detected");
            }
            return sb.toString();
        }
    }

    /**
     * 冲突信息
     */
    public static class ConflictInfo {
        public final PsiMethod method;
        public final String suggestedName;
        public final String message;

        public ConflictInfo(PsiMethod method, String suggestedName, String message) {
            this.method = method;
            this.suggestedName = suggestedName;
            this.message = message;
        }
    }
}
