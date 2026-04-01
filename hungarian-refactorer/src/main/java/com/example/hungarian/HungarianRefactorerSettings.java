package com.example.hungarian;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;

/**
 * 匈牙利重构工具设置接口
 */
public interface HungarianRefactorerSettings extends PersistentStateComponent<HungarianRefactorerSettingsImpl> {

    /**
     * 是否启用重构
     */
    boolean isEnabled();

    /**
     * 设置是否启用重构
     */
    void setEnabled(boolean enabled);

    /**
     * 是否处理局部变量
     */
    boolean isProcessLocalVariables();

    /**
     * 设置是否处理局部变量
     */
    void setProcessLocalVariables(boolean process);

    /**
     * 是否处理字段
     */
    boolean isProcessFields();

    /**
     * 设置是否处理字段
     */
    void setProcessFields(boolean process);

    /**
     * 是否处理参数
     */
    boolean isProcessParameters();

    /**
     * 设置是否处理参数
     */
    void setProcessParameters(boolean process);

    /**
     * 是否更新 getter/setter 方法
     */
    boolean isUpdateAccessors();

    /**
     * 设置是否更新 getter/setter 方法
     */
    void setUpdateAccessors(boolean update);

    /**
     * 是否处理注释中的引用
     */
    boolean isUpdateComments();

    /**
     * 设置是否处理注释中的引用
     */
    void setUpdateComments(boolean update);

    /**
     * 是否预览更改
     */
    boolean isPreviewChanges();

    /**
     * 设置是否预览更改
     */
    void setPreviewChanges(boolean preview);

    /**
     * 获取实例
     */
    static HungarianRefactorerSettings getInstance(Project project) {
        return project.getService(HungarianRefactorerSettings.class);
    }
}
