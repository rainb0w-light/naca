package com.example.hungarian.actions;

import com.example.hungarian.fixers.UnusedImportFixer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * 删除无用 import Action
 * 菜单：Code → Code Fixes → Remove Unused Imports
 */
public class RemoveUnusedImportsAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(RemoveUnusedImportsAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            Messages.showErrorDialog("No file selected", "Remove Unused Imports");
            return;
        }

        if (!(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog("Only Java files are supported", "Remove Unused Imports");
            return;
        }

        // 执行删除无用 import
        UnusedImportFixer fixer = new UnusedImportFixer(psiFile.getProject());
        UnusedImportFixer.FixResult result = fixer.fixFile(psiFile);

        // 显示结果
        if (result.hasChanges()) {
            LOG.info("Unused imports removed: " + result.getSummary());
            Messages.showInfoMessage(
                    result.getSummary() + "\n\nChanges:\n" + String.join("\n", result.changes),
                    "Remove Unused Imports Completed"
            );
        } else {
            Messages.showInfoMessage("No unused imports found", "Remove Unused Imports");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 菜单始终可见且启用，在 actionPerformed() 中检查文件类型
        e.getPresentation().setEnabledAndVisible(true);
    }
}
