package com.example.hungarian.psi;

import com.example.hungarian.HungarianPrefixRegistry;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 匈牙利命名法变量信息
 * 包含变量的所有必要信息用于重构
 */
public class HungarianVariableInfo {

    /**
     * 变量类型
     */
    public enum VariableType {
        FIELD,          // 字段
        LOCAL_VARIABLE, // 局部变量
        PARAMETER,      // 参数
        RESOURCE        // try-with-resources 变量
    }

    private final PsiElement element;
    private final VariableType variableType;
    private final String variableName;
    private final String typeName;
    private final String suggestedName;
    private final String fileName;
    private final int lineNumber;
    private final HungarianPrefixRegistry.PrefixRule matchedRule;

    public HungarianVariableInfo(
        @NotNull PsiElement element,
        @NotNull VariableType variableType,
        @NotNull String variableName,
        @Nullable String typeName,
        @NotNull String suggestedName,
        @NotNull String fileName,
        int lineNumber,
        @Nullable HungarianPrefixRegistry.PrefixRule matchedRule
    ) {
        this.element = element;
        this.variableType = variableType;
        this.variableName = variableName;
        this.typeName = typeName;
        this.suggestedName = suggestedName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.matchedRule = matchedRule;
    }

    /**
     * 获取 PSI 元素
     */
    @NotNull
    public PsiElement getElement() {
        return element;
    }

    /**
     * 获取变量类型
     */
    @NotNull
    public VariableType getVariableType() {
        return variableType;
    }

    /**
     * 获取变量名
     */
    @NotNull
    public String getVariableName() {
        return variableName;
    }

    /**
     * 获取类型名
     */
    @Nullable
    public String getTypeName() {
        return typeName;
    }

    /**
     * 获取建议的新名称
     */
    @NotNull
    public String getSuggestedName() {
        return suggestedName;
    }

    /**
     * 获取文件名
     */
    @NotNull
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取行号
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 获取匹配的前缀规则
     */
    @Nullable
    public HungarianPrefixRegistry.PrefixRule getMatchedRule() {
        return matchedRule;
    }

    /**
     * 是否需要重命名
     */
    public boolean needsRename() {
        return !variableName.equals(suggestedName);
    }

    @Override
    public String toString() {
        return String.format(
            "%s %s -> %s (%s:%d)",
            variableType, variableName, suggestedName, fileName, lineNumber
        );
    }
}
