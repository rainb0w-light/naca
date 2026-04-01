package com.example.hungarian.fixers;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 自赋值修复器
 * 修复构造函数中的 "xxx = xxx" 自赋值问题
 *
 * 例如：
 * 修复前：public Foo(String name) { name = name; }
 * 修复后：public Foo(String name) { this.name = name; }
 */
public class SelfAssignmentFixer {

    private static final Logger LOG = Logger.getInstance(SelfAssignmentFixer.class);

    private final Project project;

    public SelfAssignmentFixer(Project project) {
        this.project = project;
    }

    /**
     * 修复文件中的自赋值问题
     */
    public FixResult fixFile(@NotNull PsiFile file) {
        List<String> changes = new ArrayList<>();

        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                if (method.isConstructor()) {
                    List<String> methodChanges = fixConstructor(method);
                    changes.addAll(methodChanges);
                }
                super.visitMethod(method);
            }
        });

        return new FixResult(changes);
    }

    /**
     * 修复单个构造函数中的自赋值问题
     */
    @NotNull
    private List<String> fixConstructor(@NotNull PsiMethod constructor) {
        List<String> changes = new ArrayList<>();
        PsiClass containingClass = constructor.getContainingClass();

        if (containingClass == null) {
            return changes;
        }

        // 获取所有成员变量
        List<String> fieldNames = new ArrayList<>();
        for (PsiField field : containingClass.getFields()) {
            String fieldName = field.getName();
            if (fieldName != null) {
                fieldNames.add(fieldName);
            }
        }

        if (fieldNames.isEmpty()) {
            return changes;
        }

        // 获取参数名
        List<String> paramNames = new ArrayList<>();
        for (PsiParameter param : constructor.getParameterList().getParameters()) {
            String paramName = param.getName();
            if (paramName != null) {
                paramNames.add(paramName);
            }
        }

        // 遍历构造函数体中的所有赋值语句
        PsiCodeBlock body = constructor.getBody();
        if (body == null) {
            return changes;
        }

        for (PsiStatement statement : body.getStatements()) {
            if (statement instanceof PsiExpressionStatement) {
                PsiExpression expression = ((PsiExpressionStatement) statement).getExpression();
                if (expression instanceof PsiAssignmentExpression) {
                    PsiAssignmentExpression assignment = (PsiAssignmentExpression) expression;

                    // 检查是否是自赋值：lhs 和 rhs 都是变量引用且名称相同
                    PsiExpression lhs = assignment.getLExpression();
                    PsiExpression rhs = assignment.getRExpression();

                    if (lhs instanceof PsiReferenceExpression && rhs instanceof PsiReferenceExpression) {
                        String lhsName = ((PsiReferenceExpression) lhs).getReferenceName();
                        String rhsName = ((PsiReferenceExpression) rhs).getReferenceName();

                        // 检查是否是自赋值且参数名与字段名匹配
                        if (lhsName != null && rhsName != null && lhsName.equals(rhsName)) {
                            if (paramNames.contains(lhsName) && fieldNames.contains(lhsName)) {
                                // 需要添加 this. 前缀
                                if (addThisPrefix(assignment, lhs)) {
                                    changes.add("Fixed self-assignment: " + lhsName + " -> this." + lhsName);
                                    LOG.info("Fixed self-assignment in constructor: " + lhsName);
                                }
                            }
                        }
                    }
                }
            }
        }

        return changes;
    }

    /**
     * 为左侧表达式添加 this. 前缀
     */
    private boolean addThisPrefix(@NotNull PsiAssignmentExpression assignment, @NotNull PsiExpression lhs) {
        try {
            // 创建 this.xxx 表达式
            PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
            String fieldName = ((PsiReferenceExpression) lhs).getReferenceName();

            // 创建新的 this.fieldName = rhs 表达式
            PsiExpression rhs = assignment.getRExpression();
            String rhsText = rhs != null ? rhs.getText() : "null";
            String newExpressionText = "this." + fieldName + " = " + rhsText + ";";

            PsiExpressionStatement newStatement = (PsiExpressionStatement) factory.createStatementFromText(newExpressionText, null);
            PsiExpressionStatement oldStatement = (PsiExpressionStatement) assignment.getParent();

            // 替换原语句
            oldStatement.replace(newStatement);

            return true;
        } catch (Exception e) {
            LOG.warn("Failed to add 'this.' prefix for: " + ((PsiReferenceExpression) lhs).getReferenceName(), e);
            return false;
        }
    }

    /**
     * 结果类
     */
    public static class FixResult {
        public final List<String> changes;
        public final int changeCount;

        public FixResult(List<String> changes) {
            this.changes = changes;
            this.changeCount = changes.size();
        }

        public boolean hasChanges() {
            return changeCount > 0;
        }

        public String getSummary() {
            return "Fixed " + changeCount + " self-assignment(s)";
        }
    }
}
