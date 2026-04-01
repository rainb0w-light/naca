package com.example.hungarian.actions;

import com.example.hungarian.HungarianRefactorerEngine;
import com.example.hungarian.psi.HungarianVariableInfo;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 分析当前文件中的匈牙利命名法变量（不执行重构）
 */
public class AnalyzeAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(AnalyzeAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project == null) {
            return;
        }

        var psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        var engine = HungarianRefactorerEngine.getInstance(project);
        List<HungarianVariableInfo> variables = engine.analyzeFile(psiFile);

        if (variables.isEmpty()) {
            Messages.showInfoMessage(
                "No Hungarian notation variables found.",
                "Analysis Result"
            );
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append(String.format("Found %d Hungarian notation variables:\n\n", variables.size()));

        // 按类型分组统计
        int fieldCount = 0;
        int localVarCount = 0;
        int paramCount = 0;

        for (HungarianVariableInfo info : variables) {
            switch (info.getVariableType()) {
                case FIELD -> fieldCount++;
                case LOCAL_VARIABLE -> localVarCount++;
                case PARAMETER -> paramCount++;
            }
        }

        report.append(String.format("Fields: %d\n", fieldCount));
        report.append(String.format("Local Variables: %d\n", localVarCount));
        report.append(String.format("Parameters: %d\n\n", paramCount));
        report.append("Detailed List:\n");
        report.append("-".repeat(50)).append("\n");

        for (HungarianVariableInfo info : variables) {
            report.append(String.format(
                "[%s] %s.%s -> %s\n",
                info.getVariableType(),
                info.getFileName(),
                info.getVariableName(),
                info.getSuggestedName()
            ));
        }

        Messages.showInfoMessage(report.toString(), "Analysis Report");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var project = e.getProject();
        var psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(
            project != null && psiFile != null
        );
    }
}
