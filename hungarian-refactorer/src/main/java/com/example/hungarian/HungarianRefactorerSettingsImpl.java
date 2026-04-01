package com.example.hungarian;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 匈牙利重构工具设置实现
 */
@State(
    name = "HungarianRefactorerSettings",
    storages = @Storage("hungarian-refactorer-settings.xml")
)
public class HungarianRefactorerSettingsImpl implements HungarianRefactorerSettings {

    public boolean enabled = true;
    public boolean processLocalVariables = true;
    public boolean processFields = true;
    public boolean processParameters = false;
    public boolean updateAccessors = true;
    public boolean updateComments = false;
    public boolean previewChanges = true;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isProcessLocalVariables() {
        return processLocalVariables;
    }

    @Override
    public void setProcessLocalVariables(boolean processLocalVariables) {
        this.processLocalVariables = processLocalVariables;
    }

    @Override
    public boolean isProcessFields() {
        return processFields;
    }

    @Override
    public void setProcessFields(boolean processFields) {
        this.processFields = processFields;
    }

    @Override
    public boolean isProcessParameters() {
        return processParameters;
    }

    @Override
    public void setProcessParameters(boolean processParameters) {
        this.processParameters = processParameters;
    }

    @Override
    public boolean isUpdateAccessors() {
        return updateAccessors;
    }

    @Override
    public void setUpdateAccessors(boolean updateAccessors) {
        this.updateAccessors = updateAccessors;
    }

    @Override
    public boolean isUpdateComments() {
        return updateComments;
    }

    @Override
    public void setUpdateComments(boolean updateComments) {
        this.updateComments = updateComments;
    }

    @Override
    public boolean isPreviewChanges() {
        return previewChanges;
    }

    @Override
    public void setPreviewChanges(boolean previewChanges) {
        this.previewChanges = previewChanges;
    }

    @Nullable
    @Override
    public HungarianRefactorerSettingsImpl getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull HungarianRefactorerSettingsImpl state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static HungarianRefactorerSettings getInstance(Project project) {
        return project.getService(HungarianRefactorerSettings.class);
    }
}
