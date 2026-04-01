package com.example.hungarian.actions;

import com.example.hungarian.HungarianRefactorerEngine;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * 重构整个项目中的匈牙利命名法变量
 */
public class RefactorProjectAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(RefactorProjectAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project == null) {
            return;
        }

        // 确认对话框
        int result = Messages.showYesNoDialog(
            project,
            "This will refactor ALL Hungarian notation variables in the entire project.\n\n" +
            "This action cannot be undone easily. Make sure you have committed your changes.\n\n" +
            "Do you want to continue?",
            "Confirm Project-wide Refactoring",
            Messages.getWarningIcon()
        );

        if (result != Messages.YES) {
            return;
        }

        // 执行重构
        var engine = HungarianRefactorerEngine.getInstance(project);

        engine.refactorProjectAsync(false, refactorResult -> {
            String message = String.format(
                "Project Refactoring Complete!\n\n" +
                "Total Variables: %d\n" +
                "Successfully Renamed: %d\n" +
                "Failed: %d\n\n" +
                "%s",
                refactorResult.totalVariables,
                refactorResult.successCount,
                refactorResult.failedCount,
                refactorResult.hasErrors() ? "Errors:\n" + String.join("\n", refactorResult.errors) : ""
            );

            if (refactorResult.hasErrors()) {
                Messages.showErrorDialog(message, "Refactoring Complete with Errors");
            } else {
                Messages.showInfoMessage(message, "Refactoring Complete");
            }
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只有在有项目时才启用
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
