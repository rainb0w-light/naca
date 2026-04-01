package com.example.hungarian;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 匈牙利重构工具配置页面
 */
public class HungarianRefactorerConfigurable implements SearchableConfigurable {

    private HungarianRefactorerConfigPanel panel;
    private final HungarianRefactorerSettings settings;

    public HungarianRefactorerConfigurable(@NotNull Project project) {
        this.settings = HungarianRefactorerSettings.getInstance(project);
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        panel = new HungarianRefactorerConfigPanel();
        return panel.getRootPanel();
    }

    @Override
    public void disposeUIResources() {
        panel = null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Hungarian Notation Refactorer";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "preferences/hungarian-refactorer";
    }

    @NotNull
    @Override
    public String getId() {
        return "preferences.hungarian.refactorer";
    }

    @Override
    public boolean isModified() {
        if (panel == null || settings == null) {
            return true;
        }
        return panel.isModified(settings);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (panel != null && settings != null) {
            panel.apply(settings);
        }
    }

    @Override
    public void reset() {
        if (panel != null && settings != null) {
            panel.reset(settings);
        }
    }
}
