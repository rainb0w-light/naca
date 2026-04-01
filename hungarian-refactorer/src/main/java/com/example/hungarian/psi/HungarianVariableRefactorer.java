package com.example.hungarian.psi;

import com.example.hungarian.HungarianPrefixRegistry;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 匈牙利命名法变量重构器
 * 执行实际的重命名操作，包括更新相关的 getter/setter 方法
 */
public class HungarianVariableRefactorer {

    private static final Logger LOG = Logger.getInstance(HungarianVariableRefactorer.class);

    private final HungarianPrefixRegistry prefixRegistry;
    private final HungarianRefactorerSettings settings;
    private final AccessorUpdater accessorUpdater;

    public HungarianVariableRefactorer(
        @NotNull HungarianPrefixRegistry prefixRegistry,
        @NotNull HungarianRefactorerSettings settings
    ) {
        this.prefixRegistry = prefixRegistry;
        this.settings = settings;
        this.accessorUpdater = new AccessorUpdater();
    }

    /**
     * 重命名单个变量
     *
     * @param variableInfo 变量信息
     * @return 是否成功
     */
    public boolean renameVariable(@NotNull HungarianVariableInfo variableInfo) {
        if (!variableInfo.needsRename()) {
            return true;
        }

        PsiElement element = variableInfo.getElement();
        String newName = variableInfo.getSuggestedName();

        LOG.info("Renaming variable: " + variableInfo.getVariableName() + " -> " + newName);

        try {
            // 使用 IntelliJ 的重构 API 进行安全重命名
            return safeRename(element, newName);
        } catch (Exception e) {
            LOG.warn("Failed to rename variable: " + variableInfo.getVariableName(), e);
            return false;
        }
    }

    /**
     * 安全重命名（使用 RefactoringFactory）
     */
    private boolean safeRename(@NotNull PsiElement element, @NotNull String newName) {
        Project project = element.getProject();
        RefactoringFactory factory = RefactoringFactory.getInstance(project);

        // 创建重命名重构
        RenameRefactoring refactoring = factory.createRename(element, newName);

        // 配置重构选项
        refactoring.searchInComments(settings.isUpdateComments());
        refactoring.searchInNonJavaFiles(false);

        // 执行重命名
        refactoring.run();

        LOG.debug("Successfully renamed to: " + newName);
        return true;
    }

    /**
     * 批量重命名变量
     *
     * @param variables 变量列表
     * @return 成功重命名的数量
     */
    public int renameVariables(@NotNull List<HungarianVariableInfo> variables) {
        int successCount = 0;

        for (HungarianVariableInfo variable : variables) {
            if (renameVariable(variable)) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 按类型分组变量，以便按顺序重命名
     * （先重命名字段，再处理局部变量）
     */
    @NotNull
    public Map<HungarianVariableInfo.VariableType, List<HungarianVariableInfo>> groupByType(
        @NotNull List<HungarianVariableInfo> variables
    ) {
        return variables.stream()
            .collect(Collectors.groupingBy(HungarianVariableInfo::getVariableType));
    }
}

/**
 * Getter/Setter 方法更新器
 */
class AccessorUpdater {

    private static final Logger LOG = Logger.getInstance(AccessorUpdater.class);

    /**
     * 更新与字段相关的 getter/setter 方法
     */
    public void updateAccessors(
        @NotNull PsiField field,
        @NotNull String newFieldName
    ) {
        String oldFieldName = field.getName();
        PsiClass containingClass = field.getContainingClass();

        if (containingClass == null) {
            LOG.debug("No containing class for field: " + oldFieldName);
            return;
        }

        // 生成可能的 getter/setter 名称
        String capitalizedOld = capitalize(oldFieldName);
        String capitalizedNew = capitalize(newFieldName);

        List<String> oldGetterNames = Arrays.asList("get" + capitalizedOld, "is" + capitalizedOld);
        String newGetterPrefix = "get" + capitalizedNew;
        String newIsPrefix = "is" + capitalizedNew;

        String oldSetterName = "set" + capitalizedOld;
        String newSetterName = "set" + capitalizedNew;

        // 查找并重命名 getter 方法
        for (String oldGetterName : oldGetterNames) {
            PsiMethod getter = findMethod(containingClass, oldGetterName);
            if (getter != null) {
                // 确定使用 get 还是 is 前缀
                String newMethodName = getter.getReturnType() == PsiType.BOOLEAN
                    ? newIsPrefix
                    : newGetterPrefix;
                renameMethod(getter, newMethodName);
            }
        }

        // 查找并重命名 setter 方法
        PsiMethod setter = findMethod(containingClass, oldSetterName);
        if (setter != null) {
            renameMethod(setter, newSetterName);
        }
    }

    /**
     * 查找方法（不区分参数）
     */
    @Nullable
    private PsiMethod findMethod(@NotNull PsiClass psiClass, @NotNull String methodName) {
        for (PsiMethod method : psiClass.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    /**
     * 重命名方法
     */
    private void renameMethod(@NotNull PsiMethod method, @NotNull String newName) {
        Project project = method.getProject();
        RefactoringFactory factory = RefactoringFactory.getInstance(project);

        RenameRefactoring refactoring = factory.createRename(method, newName);
        refactoring.run();

        LOG.debug("Renamed method: " + method.getName() + " -> " + newName);
    }

    /**
     * 首字母大写
     */
    private String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
