package com.example.hungarian.cli;

import com.example.hungarian.HungarianRefactorerEngine.RefactorResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 命令行入口点
 *
 * 使用示例:
 * <pre>
 * # 分析模式（不实际修改）
 * java -cp hungarian-refactorer.jar com.example.hungarian.cli.CliMain \
 *   --project /path/to/project \
 *   --dry-run \
 *   --output report.json
 *
 * # 批量重构模式
 * java -cp hungarian-refactorer.jar com.example.hungarian.cli.CliMain \
 *   --project /path/to/project \
 *   --src /path/to/src/main/java \
 *   --output report.json
 * </pre>
 */
public class CliMain {

    private static final Logger LOG = Logger.getInstance(CliMain.class);

    public static void main(String[] args) {
        LOG.info("Hungarian Refactorer CLI starting...");

        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        try {
            BatchRefactorConfig config = parseArgs(args);

            // 注意：这是一个 IntelliJ 插件，不能直接在纯 CLI 模式下运行
            // 需要通过 IntelliJ 的无头模式启动
            // 这里提供配置解析和报告生成功能

            System.out.println("Configuration parsed:");
            System.out.println("  Project: " + config.targetPaths);
            System.out.println("  Output: " + config.outputPath);
            System.out.println("  Dry Run: " + config.dryRun);

            // 在实际使用中，这需要通过 IntelliJ 的 Application 加载
            System.out.println("\nTo run in headless mode, use:");
            System.out.println("  idea.bat headless --batch-plugin hungarian-refactorer");
            System.out.println("  or");
            System.out.println("  idea.sh headless --batch-plugin hungarian-refactorer");

        } catch (Exception e) {
            LOG.error("Failed to run CLI", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static BatchRefactorConfig parseArgs(String[] args) {
        BatchRefactorConfig config = new BatchRefactorConfig();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--project" -> {
                    if (i + 1 < args.length) {
                        config.targetPaths.add(args[++i]);
                    }
                }
                case "--src" -> {
                    if (i + 1 < args.length) {
                        config.targetPaths.add(args[++i]);
                    }
                }
                case "--output" -> {
                    if (i + 1 < args.length) {
                        config.outputPath = args[++i];
                    }
                }
                case "--dry-run" -> config.dryRun = true;
                case "--no-local-vars" -> config.processLocalVariables = false;
                case "--no-fields" -> config.processFields = false;
                case "--with-parameters" -> config.processParameters = true;
                case "--no-accessors" -> config.updateAccessors = false;
                case "--help" -> {
                    printUsage();
                    System.exit(0);
                }
                default -> {
                    System.err.println("Unknown argument: " + args[i]);
                    printUsage();
                    System.exit(1);
                }
            }
        }

        return config;
    }

    private static void printUsage() {
        System.out.println("""
            Hungarian Notation Refactorer CLI

            Usage: java -cp ... com.example.hungarian.cli.CliMain [options]

            Options:
              --project PATH        Path to the IntelliJ project
              --src PATH            Path to source directory (can be specified multiple times)
              --output PATH         Path to output report (JSON format)
              --dry-run             Analyze only, don't modify files
              --no-local-vars       Don't process local variables
              --no-fields           Don't process fields
              --with-parameters     Also process method parameters
              --no-accessors        Don't update getter/setter methods
              --help                Show this help message

            Examples:
              # Analyze only
              java -cp ... CliMain --project /path/to/project --dry-run --output report.json

              # Refactor specific directory
              java -cp ... CliMain --project /path/to/project --src /path/to/src --output report.json
            """);
    }
}
