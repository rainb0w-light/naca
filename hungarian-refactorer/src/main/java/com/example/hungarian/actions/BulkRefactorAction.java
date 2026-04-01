package com.example.hungarian.actions;

import com.example.hungarian.HungarianRefactorerEngine;
import com.example.hungarian.psi.HungarianVariableInfo;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 批量重构匈牙利命名法变量的主动作
 */
public class BulkRefactorAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(BulkRefactorAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project == null) {
            return;
        }

        // 获取选中的文件
        var psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            Messages.showWarningDialog(
                "Please select a Java file to refactor",
                "No File Selected"
            );
            return;
        }

        // 创建重构引擎
        var engine = HungarianRefactorerEngine.getInstance(project);

        // 先分析
        List<HungarianVariableInfo> variables = engine.analyzeFile(psiFile);

        if (variables.isEmpty()) {
            Messages.showInfoMessage(
                "No Hungarian notation variables found in this file.",
                "Analysis Complete"
            );
            return;
        }

        // 显示确认对话框
        int result = Messages.showYesNoDialog(
            project,
            String.format(
                "Found %d Hungarian notation variables:\n\n%s\n\nProceed with refactoring?",
                variables.size(),
                buildPreviewText(variables)
            ),
            "Confirm Refactoring",
            Messages.getQuestionIcon()
        );

        if (result == Messages.YES) {
            // 执行重构
            var refactorResult = engine.refactorFile(psiFile, false);

            // 显示结果
            Messages.showInfoMessage(
                String.format(
                    "Refactoring Complete!\n\n%s",
                    refactorResult.getSummary()
                ),
                "Refactoring Result"
            );
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只有在有项目且选中了文件时才启用
        var project = e.getProject();
        var psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(
            project != null && psiFile != null
        );
    }

    /**
     * 构建预览文本
     */
    private String buildPreviewText(List<HungarianVariableInfo> variables) {
        StringBuilder sb = new StringBuilder();
        int maxDisplay = Math.min(variables.size(), 10);

        for (int i = 0; i < maxDisplay; i++) {
            HungarianVariableInfo info = variables.get(i);
            sb.append(String.format(
                "  %s %s -> %s\n",
                info.getVariableType(),
                info.getVariableName(),
                info.getSuggestedName()
            ));
        }

        if (variables.size() > 10) {
            sb.append(String.format("  ... and %d more", variables.size() - 10));
        }

        return sb.toString();
    }
}
