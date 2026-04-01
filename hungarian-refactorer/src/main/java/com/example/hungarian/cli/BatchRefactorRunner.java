package com.example.hungarian.cli;

import com.example.hungarian.HungarianRefactorerEngine;
import com.example.hungarian.HungarianRefactorerEngine.RefactorResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 命令行接口支持
 * 用于无头模式下的批量重构
 *
 * 使用方式:
 *   java -cp ... com.example.hungarian.cli.BatchRefactorRunner \
 *     --project /path/to/project \
 *     --output /path/to/report.json \
 *     --dry-run
 */
public class BatchRefactorRunner {

    private static final Logger LOG = Logger.getInstance(BatchRefactorRunner.class);

    private final Project project;
    private final HungarianRefactorerEngine engine;
    private final BatchRefactorConfig config;

    public BatchRefactorRunner(
        @NotNull Project project,
        @NotNull BatchRefactorConfig config
    ) {
        this.project = project;
        this.config = config;
        this.engine = new HungarianRefactorerEngine(project);
    }

    /**
     * 执行批量重构
     */
    public RefactorResult run() {
        LOG.info("Starting batch refactoring with config: " + config);

        List<PsiFile> filesToProcess = new ArrayList<>();

        // 收集要处理的文件
        if (config.targetPaths != null && !config.targetPaths.isEmpty()) {
            for (String path : config.targetPaths) {
                collectFiles(path, filesToProcess);
            }
        } else {
            // 处理整个项目
            collectAllJavaFiles(filesToProcess);
        }

        LOG.info("Found " + filesToProcess.size() + " Java files to process");

        // 执行重构
        return engine.refactorFiles(filesToProcess, config.dryRun);
    }

    /**
     * 收集指定路径下的 Java 文件
     */
    private void collectFiles(String path, List<PsiFile> result) {
        File file = new File(path);
        if (!file.exists()) {
            LOG.warn("Path does not exist: " + path);
            return;
        }

        if (file.isFile()) {
            VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(file);
            if (vf != null) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile instanceof PsiJavaFile) {
                    result.add(psiFile);
                }
            }
        } else if (file.isDirectory()) {
            try {
                Files.walk(file.toPath())
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> {
                        VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(p.toFile());
                        if (vf != null) {
                            PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                            if (psiFile instanceof PsiJavaFile) {
                                result.add(psiFile);
                            }
                        }
                    });
            } catch (IOException e) {
                LOG.error("Failed to walk directory: " + path, e);
            }
        }
    }

    /**
     * 收集项目中所有 Java 文件
     */
    private void collectAllJavaFiles(List<PsiFile> result) {
        ApplicationManager.getApplication().runReadAction(() -> {
            // 使用 PSI 遍历项目
            // 这里简化实现，实际应该使用 FilenameIndex
        });
    }

    /**
     * 生成重构报告
     */
    public void generateReport(RefactorResult result, Path outputPath) throws IOException {
        StringBuilder report = new StringBuilder();
        report.append("{\n");
        report.append("  \"summary\": {\n");
        report.append("    \"totalVariables\": ").append(result.totalVariables).append(",\n");
        report.append("    \"successCount\": ").append(result.successCount).append(",\n");
        report.append("    \"failedCount\": ").append(result.failedCount).append(",\n");
        report.append("    \"dryRun\": ").append(config.dryRun).append("\n");
        report.append("  },\n");

        // 重命名列表
        report.append("  \"renamedVariables\": [\n");
        for (int i = 0; i < result.renamedVariables.size(); i++) {
            var info = result.renamedVariables.get(i);
            report.append("    {\n");
            report.append("      \"originalName\": \"").append(info.getVariableName()).append("\",\n");
            report.append("      \"newName\": \"").append(info.getSuggestedName()).append("\",\n");
            report.append("      \"type\": \"").append(info.getVariableType()).append("\",\n");
            report.append("      \"file\": \"").append(info.getFileName()).append("\",\n");
            report.append("      \"line\": ").append(info.getLineNumber()).append("\n");
            report.append("    }");
            if (i < result.renamedVariables.size() - 1) {
                report.append(",");
            }
            report.append("\n");
        }
        report.append("  ]\n");
        report.append("}\n");

        Files.writeString(outputPath, report.toString());
        LOG.info("Report written to: " + outputPath);
    }
}
