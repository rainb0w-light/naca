package com.example.hungarian.actions;

import com.example.hungarian.fixers.SelfAssignmentFixer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量修复项目中的自赋值问题
 * 菜单：Code → Code Fixes → Fix Self Assignment (Project)
 */
public class FixSelfAssignmentProjectAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(FixSelfAssignmentProjectAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No project found", "Fix Self Assignment (Project)");
            return;
        }

        // 确认对话框
        int result = Messages.showYesNoDialog(
                project,
                "This will fix ALL self-assignment issues in the entire project.\n\n" +
                        "This action cannot be undone easily. Make sure you have committed your changes.\n\n" +
                        "Do you want to continue?",
                "Confirm Project-wide Fix",
                Messages.getWarningIcon()
        );

        if (result != Messages.YES) {
            return;
        }

        // 异步执行
        new Task.Backgroundable(project, "Fixing Self Assignment Issues", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                SelfAssignmentFixer fixer = new SelfAssignmentFixer(project);
                List<String> allChanges = new ArrayList<>();
                AtomicInteger fileCount = new AtomicInteger(0);
                AtomicInteger changeCount = new AtomicInteger(0);
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
                                    SelfAssignmentFixer.FixResult fixResult = fixer.fixFile(psiFile);
                                    if (fixResult.hasChanges()) {
                                        fileCount.incrementAndGet();
                                        changeCount.addAndGet(fixResult.changeCount);
                                        allChanges.addAll(fixResult.changes);
                                    }
                                }
                            }
                            return null;
                        }
                );

                // 显示结果
                int finalFileCount = fileCount.get();
                int finalChangeCount = changeCount.get();

                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    if (finalChangeCount > 0) {
                        LOG.info("Self-assignment fix completed: " + finalChangeCount + " changes in " + finalFileCount + " files");
                        Messages.showInfoMessage(
                                "Fixed " + finalChangeCount + " self-assignment(s) in " + finalFileCount + " file(s)",
                                "Fix Self Assignment Completed"
                        );
                    } else {
                        Messages.showInfoMessage("No self-assignment issues found in project", "Fix Self Assignment");
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
