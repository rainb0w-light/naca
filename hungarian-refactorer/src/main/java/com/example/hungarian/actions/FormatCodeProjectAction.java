package com.example.hungarian.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量格式化项目中的代码
 * 菜单：Code → Code Fixes → Format Code (Project)
 * <p>
 * VERSION: 1.0.2
 */
public class FormatCodeProjectAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(FormatCodeProjectAction.class);
    private static final String VERSION = "1.0.2";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LOG.info("[FormatCodeProjectAction] ===== ACTION STARTED (Version: " + VERSION + ") =====");

        Project project = e.getProject();
        if (project == null) {
            LOG.error("[FormatCodeProjectAction] ERROR: No project found");
            Messages.showErrorDialog("No project found", "Format Code (Project)");
            return;
        }

        LOG.info("[FormatCodeProjectAction] Project: " + project.getName());

        // 确认对话框
        int result = Messages.showYesNoDialog(
                project,
                "This will format ALL Java files in the entire project.\n\n" +
                        "This action cannot be undone easily. Make sure you have committed your changes.\n\n" +
                        "Do you want to continue?",
                "Confirm Project-wide Format",
                Messages.getWarningIcon()
        );

        if (result != Messages.YES) {
            LOG.info("[FormatCodeProjectAction] User cancelled the operation");
            return;
        }

        LOG.info("[FormatCodeProjectAction] User confirmed, starting background task...");

        // 异步执行
        new Task.Backgroundable(project, "Formatting Project Code", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                LOG.info("[FormatCodeProjectAction] Background task started");

                CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
                AtomicInteger fileCount = new AtomicInteger(0);
                AtomicInteger processedCount = new AtomicInteger(0);

                // 遍历项目中的所有 Java 文件
                VirtualFile[] files = WriteAction.computeAndWait(() -> {
                    VirtualFile baseDir = PsiManager.getInstance(project).getProject().getBaseDir();
                    return baseDir.getChildren();
                });

                // 递归处理所有 Java 文件
                processFiles(project, codeStyleManager, files, fileCount, processedCount, indicator);

                // 显示结果
                int finalFileCount = fileCount.get();
                LOG.info("[FormatCodeProjectAction] Processing completed. Formatted " + finalFileCount + " files");

                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    if (finalFileCount > 0) {
                        LOG.info("[FormatCodeProjectAction] Code formatted: " + finalFileCount + " files");
                        Messages.showInfoMessage(
                                "Formatted " + finalFileCount + " file(s)",
                                "Format Code Completed"
                        );
                    } else {
                        Messages.showInfoMessage("No files needed formatting", "Format Code");
                    }
                });
            }

            private void processFiles(Project project, CodeStyleManager codeStyleManager,
                                      VirtualFile[] files, AtomicInteger fileCount,
                                      AtomicInteger processedCount, ProgressIndicator indicator) {
                for (VirtualFile file : files) {
                    if (indicator.isCanceled()) {
                        LOG.warn("[FormatCodeProjectAction] User cancelled, breaking loop");
                        break;
                    }

                    if (file.isDirectory()) {
                        processFiles(project, codeStyleManager, file.getChildren(), fileCount, processedCount, indicator);
                        continue;
                    }

                    if (!"java".equals(file.getExtension())) {
                        continue;
                    }

                    processedCount.incrementAndGet();
                    indicator.setText("Formatting: " + file.getName());

                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        try {
                            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                            if (psiFile instanceof PsiJavaFile) {
                                codeStyleManager.reformat(psiFile);
                                fileCount.incrementAndGet();
                            }
                        } catch (Exception ex) {
                            LOG.error("[FormatCodeProjectAction] Failed to format file: " + file.getName(), ex);
                        }
                    });
                }
            }
        }.queue();

        LOG.info("[FormatCodeProjectAction] Action completed (async task queued)");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }
}
