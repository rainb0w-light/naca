package com.example.hungarian.actions;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * 格式化代码 Action
 * 菜单：Code → Code Fixes → Format Code
 * <p>
 * 使用与 Cmd+Option+L 相同的方式格式化代码
 * <p>
 * VERSION: 1.0.2
 */
public class FormatCodeAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(FormatCodeAction.class);
    private static final String VERSION = "1.0.2";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LOG.info("[FormatCodeAction] ===== ACTION STARTED (Version: " + VERSION + ") =====");

        Project project = e.getProject();
        if (project == null) {
            LOG.error("[FormatCodeAction] ERROR: No project found");
            Messages.showErrorDialog("No project found", "Format Code");
            return;
        }

        LOG.info("[FormatCodeAction] Project: " + project.getName());

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            LOG.error("[FormatCodeAction] ERROR: No PSI file found");
            Messages.showErrorDialog("No file selected", "Format Code");
            return;
        }

        LOG.info("[FormatCodeAction] PSI File: " + psiFile.getName());

        // 关键：获取当前激活的编辑器，而不是直接使用 PsiFile
        // 这样可以确保格式化的是编辑器中实际显示的内容
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        LOG.info("[FormatCodeAction] Editor: " + (editor != null ? "found" : "not found"));

        if (editor != null) {
            // 有打开的编辑器时，通过编辑器格式化
            LOG.info("[FormatCodeAction] Formatting via editor");
            formatViaEditor(project, editor, psiFile);
        } else {
            // 没有编辑器时，直接格式化 PsiFile
            LOG.info("[FormatCodeAction] Formatting via PsiFile");
            formatViaPsiFile(project, psiFile);
        }

        LOG.info("[FormatCodeAction] Action completed");
    }

    /**
     * 通过编辑器格式化（优先使用，与 Cmd+Option+L 效果一致）
     */
    private void formatViaEditor(Project project, Editor editor, PsiFile psiFile) {
        LOG.info("[FormatCodeAction] formatViaEditor called");

        WriteCommandAction.runWriteCommandAction(project, "Format Code", null, () -> {
            try {
                // 确保文档与 PSI 同步
                PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

                // 使用 ReformatCodeProcessor，这与 Cmd+Option+L 执行的是同一个类
                // 第二个参数 false 表示格式化整个文件
                new ReformatCodeProcessor(psiFile, false).run();

                LOG.info("[FormatCodeAction] Formatted file via editor: " + psiFile.getName());
            } catch (Exception ex) {
                LOG.error("[FormatCodeAction] ERROR in formatViaEditor: " + ex.getMessage(), ex);
                throw ex;
            }
        });

        LOG.info("[FormatCodeAction] formatViaEditor completed");
    }

    /**
     * 通过 PsiFile 格式化（当文件没有在编辑器中打开时使用）
     */
    private void formatViaPsiFile(Project project, PsiFile psiFile) {
        LOG.info("[FormatCodeAction] formatViaPsiFile called");

        WriteCommandAction.runWriteCommandAction(project, "Format Code", null, () -> {
            try {
                new ReformatCodeProcessor(psiFile, false).run();
                LOG.info("[FormatCodeAction] Formatted file via PsiFile: " + psiFile.getName());
            } catch (Exception ex) {
                LOG.error("[FormatCodeAction] ERROR in formatViaPsiFile: " + ex.getMessage(), ex);
                throw ex;
            }
        });

        LOG.info("[FormatCodeAction] formatViaPsiFile completed");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }
}
