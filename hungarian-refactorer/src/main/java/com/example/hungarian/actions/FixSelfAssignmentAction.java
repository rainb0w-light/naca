package com.example.hungarian.actions;

import com.example.hungarian.fixers.SelfAssignmentFixer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * 自赋值修复 Action
 * 菜单：Code → Fix Self Assignment
 */
public class FixSelfAssignmentAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(FixSelfAssignmentAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            Messages.showErrorDialog("No file selected", "Fix Self Assignment");
            return;
        }

        if (!(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog("Only Java files are supported", "Fix Self Assignment");
            return;
        }

        // 执行修复
        SelfAssignmentFixer fixer = new SelfAssignmentFixer(psiFile.getProject());
        SelfAssignmentFixer.FixResult result = fixer.fixFile(psiFile);

        // 显示结果
        String message = result.getSummary();
        if (result.hasChanges()) {
            LOG.info("Self-assignment fix completed: " + message);
            Messages.showInfoMessage(
                message + "\n\nChanges:\n" + String.join("\n", result.changes),
                "Fix Self Assignment Completed"
            );
        } else {
            Messages.showInfoMessage("No self-assignment issues found", "Fix Self Assignment");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(psiFile instanceof PsiJavaFile);
    }
}
