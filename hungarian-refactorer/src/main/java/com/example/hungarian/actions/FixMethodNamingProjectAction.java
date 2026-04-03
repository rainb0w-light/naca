package com.example.hungarian.actions;

import com.example.hungarian.fixers.MethodNamingFixer;
import com.example.hungarian.fixers.MethodNamingFixer.FixResult;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ReadAction;
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
 * 批量修复项目中的方法命名问题
 * 菜单：Code → Code Fixes → Fix Method Naming (Project)
 */
public class FixMethodNamingProjectAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(FixMethodNamingProjectAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No project found", "Fix Method Naming (Project)");
            return;
        }

        // 确认对话框
        int result = Messages.showYesNoDialog(
                project,
                "This will fix ALL method naming issues in the entire project.\n\n" +
                        "This action cannot be undone easily. Make sure you have committed your changes.\n\n" +
                        "Do you want to continue?",
                "Confirm Project-wide Fix",
                Messages.getWarningIcon()
        );

        if (result != Messages.YES) {
            return;
        }

        // 异步执行
        new Task.Backgroundable(project, "Fixing Method Naming Issues", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                MethodNamingFixer fixer = new MethodNamingFixer(project);
                List<String> allChanges = new ArrayList<>();
                List<MethodNamingFixer.ConflictInfo> allConflicts = new ArrayList<>();
                AtomicInteger fileCount = new AtomicInteger(0);
                AtomicInteger changeCount = new AtomicInteger(0);
                AtomicInteger conflictCount = new AtomicInteger(0);
                AtomicInteger processedCount = new AtomicInteger(0);

                // 获取所有 Java 文件
                VirtualFile[] files = com.intellij.openapi.application.ReadAction.compute(
                        () -> com.intellij.psi.search.FilenameIndex.getAllFilesByExt(project, "java",
                                        com.intellij.psi.search.GlobalSearchScope.projectScope(project))
                                .toArray(new VirtualFile[0])
                );

                LOG.info("Found " + files.length + " Java files to process");

                for (int i = 0; i < files.length; i++) {
                    if (indicator.isCanceled()) {
                        break;
                    }

                    VirtualFile file = files[i];
                    int current = processedCount.incrementAndGet();
                    indicator.setFraction((double) current / files.length);
                    indicator.setText("Processing: " + file.getName() + " (" + current + "/" + files.length + ")");

                    PsiFile psiFile = ReadAction.compute(() -> PsiManager.getInstance(project).findFile(file));
                    if (psiFile instanceof PsiJavaFile) {
                        // 在 WriteAction 中执行修复
                        FixResult fixResult = com.intellij.openapi.application.WriteAction.compute(
                                () -> fixer.fixFile(psiFile)
                        );
                        if (fixResult.hasChanges()) {
                            fileCount.incrementAndGet();
                            changeCount.addAndGet(fixResult.changeCount);
                            allChanges.addAll(fixResult.changes);
                        }
                        if (fixResult.hasConflicts()) {
                            conflictCount.addAndGet(fixResult.conflictCount);
                            allConflicts.addAll(fixResult.conflicts);
                        }
                    }
                }

                // 显示结果
                int finalFileCount = fileCount.get();
                int finalChangeCount = changeCount.get();
                int finalConflictCount = conflictCount.get();
                List<String> finalChanges = new ArrayList<>(allChanges);
                List<MethodNamingFixer.ConflictInfo> finalConflicts = new ArrayList<>(allConflicts);

                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    StringBuilder message = new StringBuilder();
                    message.append("Renamed ").append(finalChangeCount)
                            .append(" method(s) in ").append(finalFileCount).append(" file(s)");

                    if (finalConflictCount > 0) {
                        message.append("\n\n=== CONFLICTS DETECTED ===\n");
                        message.append(finalConflictCount).append(" conflict(s) detected:\n");
                        for (MethodNamingFixer.ConflictInfo conflict : finalConflicts) {
                            message.append("⚠ ").append(conflict.message).append("\n");
                        }
                    }

                    if (finalChangeCount > 0) {
                        LOG.info("Method naming fix completed: " + finalChangeCount + " changes in " + finalFileCount + " files");
                        Messages.showInfoMessage(message.toString(), "Fix Method Naming Completed");
                    } else {
                        Messages.showInfoMessage("No method naming issues found in project", "Fix Method Naming");
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
