package com.example.hungarian.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量重构配置
 */
public class BatchRefactorConfig {

    /**
     * 目标文件/目录路径列表
     */
    public List<String> targetPaths = new ArrayList<>();

    /**
     * 输出报告路径
     */
    public String outputPath;

    /**
     * 是否只分析不执行（dry run）
     */
    public boolean dryRun = false;

    /**
     * 是否处理局部变量
     */
    public boolean processLocalVariables = true;

    /**
     * 是否处理字段
     */
    public boolean processFields = true;

    /**
     * 是否处理参数
     */
    public boolean processParameters = false;

    /**
     * 是否更新 getter/setter 方法
     */
    public boolean updateAccessors = true;

    @Override
    public String toString() {
        return "BatchRefactorConfig{" +
            "targetPaths=" + targetPaths +
            ", outputPath='" + outputPath + '\'' +
            ", dryRun=" + dryRun +
            ", processLocalVariables=" + processLocalVariables +
            ", processFields=" + processFields +
            ", processParameters=" + processParameters +
            ", updateAccessors=" + updateAccessors +
            '}';
    }
}
