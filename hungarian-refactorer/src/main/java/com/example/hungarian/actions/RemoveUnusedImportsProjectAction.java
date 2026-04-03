package com.example.hungarian.actions;

import com.example.hungarian.fixers.UnusedImportFixer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量删除项目中无用 import
 * 菜单：Code → Code Fixes → Remove Unused Imports (Project)
 */
public class RemoveUnusedImportsProjectAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(RemoveUnusedImportsProjectAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No project found", "Remove Unused Imports (Project)");
            return;
        }

        // 确认对话框
        int result = Messages.showYesNoDialog(
                project,
                "This will remove ALL unused imports in the entire project.\n\n" +
                        "This action cannot be undone easily. Make sure you have committed your changes.\n\n" +
                        "Do you want to continue?",
                "Confirm Project-wide Fix",
                Messages.getWarningIcon()
        );

        if (result != Messages.YES) {
            return;
        }

        // 异步执行
        new Task.Backgroundable(project, "Removing Unused Imports", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                UnusedImportFixer fixer = new UnusedImportFixer(project);
                AtomicInteger fileCount = new AtomicInteger(0);
                AtomicInteger importCount = new AtomicInteger(0);
                AtomicInteger processedCount = new AtomicInteger(0);

                // 遍历项目中的所有 Java 文件
                com.intellij.openapi.application.ApplicationManager.getApplication().runReadAction(
                        (com.intellij.openapi.util.Computable<Void>) () -> {
                            com.intellij.openapi.vfs.VirtualFile[] files =
                                    com.intellij.psi.search.FilenameIndex.getAllFilesByExt(project, "java",
                                                    com.intellij.psi.search.GlobalSearchScope.projectScope(project))
                                            .toArray(new VirtualFile[0]);

                            for (VirtualFile file : files) {
                                if (indicator.isCanceled()) {
                                    break;
                                }

                                int current = processedCount.incrementAndGet();
                                indicator.setFraction((double) current / files.length);
                                indicator.setText("Processing: " + file.getName() + " (" + current + "/" + files.length + ")");

                                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                                if (psiFile instanceof PsiJavaFile) {
                                    UnusedImportFixer.FixResult fixResult = fixer.fixFile(psiFile);
                                    if (fixResult.hasChanges()) {
                                        fileCount.incrementAndGet();
                                        importCount.addAndGet(fixResult.changeCount);
                                    }
                                }
                            }
                            return null;
                        }
                );

                // 显示结果
                int finalFileCount = fileCount.get();
                int finalImportCount = importCount.get();

                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    if (finalImportCount > 0) {
                        LOG.info("Unused imports removed: " + finalImportCount + " imports in " + finalFileCount + " files");
                        Messages.showInfoMessage(
                                "Removed " + finalImportCount + " unused import(s) in " + finalFileCount + " file(s)",
                                "Remove Unused Imports Completed"
                        );
                    } else {
                        Messages.showInfoMessage("No unused imports found in project", "Remove Unused Imports");
                    }
                });
            }
        }.queue();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 菜单始终可见
        e.getPresentation().setEnabledAndVisible(true);
    }
}
