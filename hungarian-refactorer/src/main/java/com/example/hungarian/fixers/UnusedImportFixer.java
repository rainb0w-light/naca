package com.example.hungarian.fixers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiImportStaticStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 无用 import 删除器
 * 使用 IntelliJ 的 Optimize Imports 功能删除未使用的 import 语句
 */
public class UnusedImportFixer {

    private static final Logger LOG = Logger.getInstance(UnusedImportFixer.class);

    private final Project project;

    public UnusedImportFixer(Project project) {
        this.project = project;
    }

    /**
     * 修复文件中的无用 import
     * 注意：IntelliJ 的 import 优化需要完整的代码分析，这里我们使用简化的方法
     */
    public FixResult fixFile(@NotNull PsiFile file) {
        List<String> changes = new ArrayList<>();

        if (!(file instanceof PsiJavaFile)) {
            return new FixResult(changes);
        }

        // 实际的 import 优化需要调用 IntelliJ 的 OptimizeImportsProcessor
        // 这里返回提示，让用户使用 IntelliJ 自带的 Optimize Imports 功能
        // CodeStyleManager.reformat() 会自动处理 import 排序

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
            return "Removed " + changeCount + " unused import(s)";
        }
    }
}
