package com.example.hungarian.fixers;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码格式化器
 * 使用 IntelliJ 的代码风格设置格式化代码
 */
public class CodeFormatterFixer {

    private static final Logger LOG = Logger.getInstance(CodeFormatterFixer.class);

    private final Project project;

    public CodeFormatterFixer(Project project) {
        this.project = project;
    }

    /**
     * 格式化文件
     * 使用 ReformatCodeProcessor 直接执行格式化
     */
    public FixResult fixFile(@NotNull PsiFile file) {
        List<String> changes = new ArrayList<>();

        try {
            // 使用 ReformatCodeProcessor，这是 IntelliJ 内置的格式化处理器
            // 它会自动处理文档提交和刷新
            new ReformatCodeProcessor(file, false).run();

            changes.add("Formatted code in: " + file.getName());
            LOG.info("Formatted file: " + file.getName());

        } catch (Exception e) {
            LOG.warn("Failed to format file: " + file.getName(), e);
            changes.add("Format failed: " + e.getMessage());
        }

        return new FixResult(changes);
    }

    /**
     * 结果类
     */
    public static class FixResult {
        public final List<String> changes;
        public final int changeCount;

        public FixResult(List<String> changes) {
            this.changes = changes;
            this.changeCount = changes.size();
        }

        public boolean hasChanges() {
            return changeCount > 0;
        }

        public String getSummary() {
            if (changeCount == 0) {
                return "No changes needed";
            }
            return "Applied " + changeCount + " formatting change(s)";
        }
    }
}
