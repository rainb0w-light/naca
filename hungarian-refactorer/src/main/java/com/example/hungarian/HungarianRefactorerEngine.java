package com.example.hungarian;

import com.example.hungarian.psi.HungarianVariableFinder;
import com.example.hungarian.psi.HungarianVariableRefactorer;
import com.example.hungarian.psi.HungarianVariableInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 匈牙利命名法重构引擎
 * 核心重构逻辑，协调变量查找和重命名
 */
public class HungarianRefactorerEngine {

    private static final Logger LOG = Logger.getInstance(HungarianRefactorerEngine.class);
    private static final String LOG_FILE = "hungarian-refactorer.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final Project project;
    private final HungarianPrefixRegistry prefixRegistry;
    private final HungarianRefactorerSettings settings;
    private final HungarianVariableFinder variableFinder;
    private final HungarianVariableRefactorer refactorer;

    public HungarianRefactorerEngine(Project project) {
        this.project = project;
        this.prefixRegistry = ApplicationManager.getApplication().getService(HungarianPrefixRegistry.class);
        this.settings = HungarianRefactorerSettings.getInstance(project);
        this.variableFinder = new HungarianVariableFinder(prefixRegistry, settings);
        this.refactorer = new HungarianVariableRefactorer(prefixRegistry, settings);

        // 初始化日志文件
        initLogFile(project);
    }

    /**
     * 初始化日志文件
     */
    private void initLogFile(Project project) {
        String logPath = Paths.get(project.getBasePath(), LOG_FILE).toString();
        try (FileWriter writer = new FileWriter(logPath, false)) {
            writer.write("=== Hungarian Refactorer Log Started ===\n");
            writer.write("Time: " + DATE_FORMAT.format(new Date()) + "\n");
            writer.write("Project: " + project.getName() + "\n\n");
        } catch (IOException e) {
            LOG.warn("Failed to initialize log file: " + logPath);
        }
    }

    /**
     * 写入日志到文件
     */
    private void writeLog(String message) {
        String logPath = Paths.get(project.getBasePath(), LOG_FILE).toString();
        String timestamp = DATE_FORMAT.format(new Date());
        try (FileWriter writer = new FileWriter(logPath, true)) {
            writer.write("[" + timestamp + "] " + message + "\n");
        } catch (IOException e) {
            LOG.warn("Failed to write to log file");
        }
        // 同时输出到 IDEA 日志
        LOG.info(message);
    }

    /**
     * 分析文件中的匈牙利命名法变量
     *
     * @param psiFile 要分析的文件
     * @return 匈牙利变量列表
     */
    public List<HungarianVariableInfo> analyzeFile(@NotNull PsiFile psiFile) {
        LOG.info("Analyzing file: " + psiFile.getName());
        return variableFinder.findHungarianVariables(psiFile);
    }

    /**
     * 批量重构文件中的匈牙利命名法变量
     *
     * @param psiFile 要重构的文件
     * @param dryRun  如果为 true，只分析不实际执行重构
     * @return 重构结果
     */
    public RefactorResult refactorFile(@NotNull PsiFile psiFile, boolean dryRun) {
        LOG.info("Refactoring file: " + psiFile.getName() + " (dryRun=" + dryRun + ")");

        List<HungarianVariableInfo> variables = variableFinder.findHungarianVariables(psiFile);
        return performRefactoring(variables, dryRun);
    }

    /**
     * 批量重构多个文件
     *
     * @param files   要重构的文件列表
     * @param dryRun  如果为 true，只分析不实际执行重构
     * @return 重构结果
     */
    public RefactorResult refactorFiles(@NotNull List<PsiFile> files, boolean dryRun) {
        LOG.info("Refactoring " + files.size() + " files (dryRun=" + dryRun + ")");

        List<HungarianVariableInfo> allVariables = new ArrayList<>();
        for (PsiFile file : files) {
            allVariables.addAll(variableFinder.findHungarianVariables(file));
        }

        return performRefactoring(allVariables, dryRun);
    }

    /**
     * 重构整个项目
     *
     * @param dryRun 如果为 true，只分析不实际执行重构
     * @return 重构结果
     */
    public RefactorResult refactorProject(boolean dryRun) {
        String mode = dryRun ? "DRY RUN" : "ACTUAL REFACTOR";
        writeLog("=== Starting Project Refactor (" + mode + ") ===");

        try {
            // 第一步：在 read action 中分析文件（只读操作）
            writeLog("Analyzing project...");
            List<HungarianVariableInfo> allVariables = ApplicationManager.getApplication().runReadAction(
                (com.intellij.openapi.util.Computable<List<HungarianVariableInfo>>) () -> {
                    List<HungarianVariableInfo> variables = new ArrayList<>();
                    variableFinder.findAllHungarianVariablesInProject(project).forEach(variables::addAll);
                    return variables;
                }
            );

            writeLog("Found " + allVariables.size() + " Hungarian variables to refactor");

            // 第二步：执行重构（写操作，不在 read action 内部）
            RefactorResult result = performRefactoring(allVariables, dryRun);

            writeLog("Refactor complete: " + result.getSummary());
            return result;
        } catch (Exception e) {
            LOG.error("Failed to refactor project", e);
            writeLog("ERROR: " + e.getMessage());
            RefactorResult result = new RefactorResult();
            result.errors.add(e.getMessage());
            return result;
        }
    }

    /**
     * 执行实际重构
     * 注意：Refactoring.run() 会自己管理 write action，所以这里不需要包裹在 WriteCommandAction 中
     */
    private RefactorResult performRefactoring(List<HungarianVariableInfo> variables, boolean dryRun) {
        RefactorResult result = new RefactorResult();
        result.totalVariables = variables.size();

        if (dryRun) {
            // 干运行：只收集信息
            result.variablesToRename = new ArrayList<>(variables);
            return result;
        }

        // 实际执行重构 - 直接在 EDT 上执行，不在 write action 内部
        // 因为 Refactoring.run() 会自己管理 write action
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        try {
            for (HungarianVariableInfo variable : variables) {
                try {
                    boolean success = refactorer.renameVariable(variable);
                    if (success) {
                        successCount.incrementAndGet();
                        result.renamedVariables.add(variable);
                    } else {
                        failCount.incrementAndGet();
                        result.failedVariables.add(variable);
                    }
                } catch (Exception e) {
                    LOG.warn("Failed to rename variable: " + variable.getVariableName(), e);
                    failCount.incrementAndGet();
                    result.failedVariables.add(variable);
                    result.errors.add("Failed to rename " + variable.getVariableName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("Error during refactoring", e);
            result.errors.add("Refactoring error: " + e.getMessage());
        }

        result.successCount = successCount.get();
        result.failedCount = failCount.get();

        return result;
    }

    /**
     * 使用进度条异步重构项目
     */
    public void refactorProjectAsync(boolean dryRun, RefactorCallback callback) {
        new Task.Backgroundable(project, "Hungarian Notation Refactoring", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);

                RefactorResult result = refactorProject(dryRun);

                ApplicationManager.getApplication().invokeLater(() -> {
                    if (callback != null) {
                        callback.onComplete(result);
                    }
                });
            }
        }.queue();
    }

    /**
     * 重构结果
     */
    public static class RefactorResult {
        public int totalVariables = 0;
        public int successCount = 0;
        public int failedCount = 0;
        public List<HungarianVariableInfo> variablesToRename = new ArrayList<>();
        public List<HungarianVariableInfo> renamedVariables = new ArrayList<>();
        public List<HungarianVariableInfo> failedVariables = new ArrayList<>();
        public List<String> errors = new ArrayList<>();

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public String getSummary() {
            return String.format(
                "Total: %d, Success: %d, Failed: %d",
                totalVariables, successCount, failedCount
            );
        }
    }

    /**
     * 重构完成回调
     */
    public interface RefactorCallback {
        void onComplete(RefactorResult result);
    }

    /**
     * 获取单例实例
     */
    public static HungarianRefactorerEngine getInstance(Project project) {
        return new HungarianRefactorerEngine(project);
    }
}
