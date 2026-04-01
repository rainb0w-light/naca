package com.example.hungarian;

import java.util.Map;

/**
 * 匈牙利命名法前缀注册表
 * 定义了类型前缀到新名称前缀的映射规则
 *
 * 扩展点：可以通过添加新的 PrefixRule 来支持自定义命名规则
 */
public interface HungarianPrefixRegistry {

    /**
     * 前缀规则
     */
    record PrefixRule(
        String prefix,           // 匈牙利前缀，如 "str", "i", "btn"
        String replacement,      // 替换后的前缀，如 "" (空), "is", "button"
        String targetType,       // 目标类型，如 "String", "int", "JButton"
        boolean enabled          // 是否启用
    ) {}

    /**
     * 获取所有注册的前缀规则
     */
    PrefixRule[] getRules();

    /**
     * 根据变量名前缀获取转换规则
     * @param variableName 变量名
     * @return 匹配的 PrefixRule，如果没有匹配则返回 null
     */
    PrefixRule getRuleForVariable(String variableName);

    /**
     * 根据类型获取转换规则
     * @param typeName 类型名
     * @return 匹配的 PrefixRule 数组
     */
    PrefixRule[] getRulesForType(String typeName);

    /**
     * 注册新的前缀规则
     * @param rule 要注册的规则
     */
    void registerRule(PrefixRule rule);

    /**
     * 注销前缀规则
     * @param prefix 要注销的前缀
     */
    void unregisterRule(String prefix);

    /**
     * 启用/禁用前缀规则
     * @param prefix 前缀
     * @param enabled 是否启用
     */
    void setRuleEnabled(String prefix, boolean enabled);

    /**
     * 导入规则配置
     * @param rules JSON 格式的规则配置
     */
    void importRules(String rules);

    /**
     * 导出规则配置
     * @return JSON 格式的规则配置
     */
    String exportRules();
}
