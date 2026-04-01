package com.example.hungarian;

import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * 匈牙利重构工具配置面板
 */
public class HungarianRefactorerConfigPanel {

    private final JBCheckBox enabledCheckBox = new JBCheckBox("Enable Hungarian Notation Refactoring");
    private final JBCheckBox processLocalVarsCheckBox = new JBCheckBox("Process Local Variables");
    private final JBCheckBox processFieldsCheckBox = new JBCheckBox("Process Fields");
    private final JBCheckBox processParametersCheckBox = new JBCheckBox("Process Parameters");
    private final JBCheckBox updateAccessorsCheckBox = new JBCheckBox("Update Getter/Setter Methods");
    private final JBCheckBox updateCommentsCheckBox = new JBCheckBox("Update Comments (not recommended)");
    private final JBCheckBox previewChangesCheckBox = new JBCheckBox("Preview Changes Before Applying");

    private JPanel rootPanel;

    public HungarianRefactorerConfigPanel() {
        createUI();
    }

    private void createUI() {
        rootPanel = new JPanel(new BorderLayout());

        JPanel settingsPanel = FormBuilder.createFormBuilder()
            .addComponent(enabledCheckBox)
            .addComponent(processLocalVarsCheckBox)
            .addComponent(processFieldsCheckBox)
            .addComponent(processParametersCheckBox)
            .addComponent(updateAccessorsCheckBox)
            .addComponent(updateCommentsCheckBox)
            .addComponent(previewChangesCheckBox)
            .getPanel();

        // 添加说明文字
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JBLabel infoLabel = new JBLabel(
            "<html><body width='400px'>" +
            "Configure how the Hungarian Notation Refactorer processes your code.<br/>" +
            "<b>Note:</b> Changes can be undone using Ctrl+Z (or Cmd+Z on Mac)." +
            "</body></html>"
        );
        infoLabel.setBorder(JBUI.Borders.emptyBottom(10));
        infoPanel.add(infoLabel);

        rootPanel.add(infoPanel, BorderLayout.NORTH);
        rootPanel.add(settingsPanel, BorderLayout.CENTER);

        // 设置工具提示
        enabledCheckBox.setToolTipText("Enable or disable the refactoring feature");
        processLocalVarsCheckBox.setToolTipText("Rename local variables like 'strName'");
        processFieldsCheckBox.setToolTipText("Rename class fields like 'private String strName'");
        processParametersCheckBox.setToolTipText("Rename method parameters (use with caution)");
        updateAccessorsCheckBox.setToolTipText("Automatically rename getter/setter methods");
        updateCommentsCheckBox.setToolTipText("Also update variable names in comments (may cause false positives)");
        previewChangesCheckBox.setToolTipText("Show preview dialog before applying changes");
    }

    public JComponent getRootPanel() {
        return rootPanel;
    }

    /**
     * 检查是否被修改
     */
    public boolean isModified(HungarianRefactorerSettings settings) {
        return enabledCheckBox.isSelected() != settings.isEnabled() ||
            processLocalVarsCheckBox.isSelected() != settings.isProcessLocalVariables() ||
            processFieldsCheckBox.isSelected() != settings.isProcessFields() ||
            processParametersCheckBox.isSelected() != settings.isProcessParameters() ||
            updateAccessorsCheckBox.isSelected() != settings.isUpdateAccessors() ||
            updateCommentsCheckBox.isSelected() != settings.isUpdateComments() ||
            previewChangesCheckBox.isSelected() != settings.isPreviewChanges();
    }

    /**
     * 应用设置
     */
    public void apply(HungarianRefactorerSettings settings) {
        settings.setEnabled(enabledCheckBox.isSelected());
        settings.setProcessLocalVariables(processLocalVarsCheckBox.isSelected());
        settings.setProcessFields(processFieldsCheckBox.isSelected());
        settings.setProcessParameters(processParametersCheckBox.isSelected());
        settings.setUpdateAccessors(updateAccessorsCheckBox.isSelected());
        settings.setUpdateComments(updateCommentsCheckBox.isSelected());
        settings.setPreviewChanges(previewChangesCheckBox.isSelected());
    }

    /**
     * 重置为保存的设置
     */
    public void reset(HungarianRefactorerSettings settings) {
        enabledCheckBox.setSelected(settings.isEnabled());
        processLocalVarsCheckBox.setSelected(settings.isProcessLocalVariables());
        processFieldsCheckBox.setSelected(settings.isProcessFields());
        processParametersCheckBox.setSelected(settings.isProcessParameters());
        updateAccessorsCheckBox.setSelected(settings.isUpdateAccessors());
        updateCommentsCheckBox.setSelected(settings.isUpdateComments());
        previewChangesCheckBox.setSelected(settings.isPreviewChanges());
    }
}
