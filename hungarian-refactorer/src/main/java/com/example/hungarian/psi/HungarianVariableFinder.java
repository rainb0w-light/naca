package com.example.hungarian.psi;

import com.example.hungarian.HungarianPrefixRegistry;
import com.example.hungarian.HungarianRefactorerSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 匈牙利命名法变量查找器
 * 遍历 PSI 树查找符合匈牙利命名法的变量
 */
public class HungarianVariableFinder {

    private static final Logger LOG = Logger.getInstance(HungarianVariableFinder.class);

    private final HungarianPrefixRegistry prefixRegistry;
    private final HungarianRefactorerSettings settings;
    private final NameConverter nameConverter;

    public HungarianVariableFinder(
        @NotNull HungarianPrefixRegistry prefixRegistry,
        @NotNull HungarianRefactorerSettings settings
    ) {
        this.prefixRegistry = prefixRegistry;
        this.settings = settings;
        this.nameConverter = new NameConverter(prefixRegistry);
    }

    /**
     * 查找文件中的所有匈牙利命名法变量
     */
    @NotNull
    public List<HungarianVariableInfo> findHungarianVariables(@NotNull PsiFile psiFile) {
        List<HungarianVariableInfo> result = new ArrayList<>();

        psiFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                if (settings.isProcessLocalVariables()) {
                    analyzeVariable(variable, HungarianVariableInfo.VariableType.LOCAL_VARIABLE, result);
                }
                super.visitLocalVariable(variable);
            }

            @Override
            public void visitField(@NotNull PsiField field) {
                if (settings.isProcessFields()) {
                    analyzeVariable(field, HungarianVariableInfo.VariableType.FIELD, result);
                }
                super.visitField(field);
            }

            @Override
            public void visitParameter(@NotNull PsiParameter parameter) {
                if (settings.isProcessParameters() && !parameter.isVarArgs()) {
                    analyzeVariable(parameter, HungarianVariableInfo.VariableType.PARAMETER, result);
                }
                super.visitParameter(parameter);
            }
        });

        return result;
    }

    /**
     * 查找项目中所有文件的匈牙利命名法变量
     */
    @NotNull
    public Collection<List<HungarianVariableInfo>> findAllHungarianVariablesInProject(@NotNull Project project) {
        Collection<List<HungarianVariableInfo>> allResults = new ArrayList<>();

        // 获取所有 Java 文件
        Collection<VirtualFile> javaFiles = FilenameIndex.getAllFilesByExt(project, "java", null);

        for (VirtualFile file : javaFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile != null) {
                allResults.add(findHungarianVariables(psiFile));
            }
        }

        return allResults;
    }

    /**
     * 分析单个变量
     */
    private void analyzeVariable(
        @NotNull PsiVariable variable,
        @NotNull HungarianVariableInfo.VariableType type,
        @NotNull List<HungarianVariableInfo> result
    ) {
        String varName = variable.getName();
        if (varName == null || varName.isEmpty()) {
            return;
        }

        // 检查是否是匈牙利命名法
        HungarianPrefixRegistry.PrefixRule rule = prefixRegistry.getRuleForVariable(varName);
        if (rule == null) {
            return;
        }

        // 获取类型信息
        PsiType psiType = variable.getType();
        String typeName = psiType != null ? psiType.getPresentableText() : null;

        // 验证类型是否匹配（可选的严格检查）
        if (typeName != null && !rule.targetType().isEmpty()) {
            if (!typeName.contains(rule.targetType())) {
                // 前缀匹配但类型不匹配，可能是误判
                // 例如：str 可能匹配了 Stream 类型而不是 String
                LOG.debug("Type mismatch for variable: " + varName +
                    ", expected: " + rule.targetType() + ", got: " + typeName);
            }
        }

        // 计算新名称
        String suggestedName = nameConverter.convertToCamelCase(varName, rule);

        // 只有当名称确实需要改变时才添加
        if (!varName.equals(suggestedName)) {
            PsiFile containingFile = variable.getContainingFile();
            result.add(new HungarianVariableInfo(
                variable,
                type,
                varName,
                typeName,
                suggestedName,
                containingFile != null ? containingFile.getName() : "unknown",
                variable.getTextOffset(),  // 使用文本偏移量代替行号
                rule
            ));
        }
    }

    /**
     * 查找与变量相关的 getter/setter 方法
     */
    @NotNull
    public List<HungarianVariableInfo> findRelatedAccessors(
        @NotNull PsiField field,
        @NotNull String newFieldName
    ) {
        List<HungarianVariableInfo> result = new ArrayList<>();

        PsiClass containingClass = field.getContainingClass();
        if (containingClass == null) {
            return result;
        }

        String fieldName = field.getName();
        String capitalized = capitalize(fieldName);
        String newCapitalized = capitalize(newFieldName);

        // 查找 getter 方法
        String[] getterPrefixes = {"get", "is"};
        for (String prefix : getterPrefixes) {
            String getterName = prefix + capitalized;
            PsiMethod getter = containingClass.findMethodBySignature(getterName + "()", true);
            if (getter != null) {
                // TODO: 添加 getter 方法到结果
                LOG.debug("Found getter: " + getterName);
            }
        }

        // 查找 setter 方法
        String setterName = "set" + capitalized;
        PsiMethod setter = containingClass.findMethodBySignature(setterName + "(...)", true);
        if (setter != null) {
            // TODO: 添加 setter 方法到结果
            LOG.debug("Found setter: " + setterName);
        }

        return result;
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
