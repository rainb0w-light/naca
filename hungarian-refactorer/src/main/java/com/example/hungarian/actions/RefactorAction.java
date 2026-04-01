package com.example.hungarian.actions;

import com.example.hungarian.HungarianRefactorerEngine;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * 重构当前文件中的匈牙利命名法变量
 */
public class RefactorAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(RefactorAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project == null) {
            return;
        }

        var psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        var engine = HungarianRefactorerEngine.getInstance(project);
        var result = engine.refactorFile(psiFile, false);

        Messages.showInfoMessage(
            String.format(
                "Refactoring Complete!\n\nTotal: %d\nSuccess: %d\nFailed: %d",
                result.totalVariables,
                result.successCount,
                result.failedCount
            ),
            "Refactoring Result"
        );
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var project = e.getProject();
        var psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(
            project != null && psiFile != null
        );
    }
}
