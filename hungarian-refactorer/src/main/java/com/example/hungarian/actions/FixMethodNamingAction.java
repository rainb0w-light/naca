package com.example.hungarian.actions;

import com.example.hungarian.fixers.MethodNamingFixer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * 方法名小写化 Action
 * 菜单：Code → Fix Method Naming
 */
public class FixMethodNamingAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(FixMethodNamingAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            Messages.showErrorDialog("No file selected", "Fix Method Naming");
            return;
        }

        if (!(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog("Only Java files are supported", "Fix Method Naming");
            return;
        }

        // 执行修复
        MethodNamingFixer fixer = new MethodNamingFixer(psiFile.getProject());
        MethodNamingFixer.FixResult result = fixer.fixFile(psiFile);

        // 显示结果
        StringBuilder message = new StringBuilder(result.getSummary());
        if (result.hasConflicts()) {
            message.append("\n\n=== CONFLICTS DETECTED ===\n");
            for (MethodNamingFixer.ConflictInfo conflict : result.conflicts) {
                message.append("\u26A0 ").append(conflict.message).append("\n");
            }
            message.append("\nThese methods were NOT renamed due to conflicts.");
        }

        if (result.hasChanges()) {
            LOG.info("Method naming fix completed: " + result.getSummary());
            Messages.showInfoMessage(message.toString(), "Fix Method Naming Completed");
        } else if (result.hasConflicts()) {
            Messages.showWarningDialog(message.toString(), "Fix Method Naming - Conflicts Detected");
        } else {
            Messages.showInfoMessage("No method naming issues found", "Fix Method Naming");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(psiFile instanceof PsiJavaFile);
    }
}
