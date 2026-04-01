package com.example.hungarian;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 匈牙利命名法前缀注册表实现
 * 使用 PersistentStateComponent 实现配置持久化
 */
@State(
    name = "HungarianPrefixRegistry",
    storages = @Storage("hungarian-prefix-rules.xml")
)
public class HungarianPrefixRegistryImpl implements HungarianPrefixRegistry, PersistentStateComponent<HungarianPrefixRegistryImpl> {

    /**
     * 默认前缀规则
     */
    private static final PrefixRule[] DEFAULT_RULES = {
        // String 类型
        new PrefixRule("str", "", "String", true),
        new PrefixRule("s", "", "String", true),

        // 基本类型
        new PrefixRule("i", "", "int", true),
        new PrefixRule("l", "", "long", true),
        new PrefixRule("b", "is", "boolean", true),
        new PrefixRule("bln", "is", "boolean", true),
        new PrefixRule("d", "", "double", true),
        new PrefixRule("f", "", "float", true),
        new PrefixRule("c", "", "char", true),
        new PrefixRule("by", "", "byte", true),

        // 集合类型
        new PrefixRule("arr", "", "[]", true),
        new PrefixRule("lst", "list", "List", true),
        new PrefixRule("set", "", "Set", true),
        new PrefixRule("map", "", "Map", true),
        new PrefixRule("col", "collection", "Collection", true),

        // Swing/AWT 组件
        new PrefixRule("btn", "button", "JButton", true),
        new PrefixRule("lbl", "label", "JLabel", true),
        new PrefixRule("txt", "text", "JTextField", true),
        new PrefixRule("tf", "text", "JTextField", true),
        new PrefixRule("ta", "textArea", "JTextArea", true),
        new PrefixRule("cb", "checkBox", "JCheckBox", true),
        new PrefixRule("rb", "radioButton", "JRadioButton", true),
        new PrefixRule("cbx", "comboBox", "JComboBox", true),
        new PrefixRule("sp", "spinner", "JSpinner", true),
        new PrefixRule("pb", "progressBar", "JProgressBar", true),
        new PrefixRule("sl", "slider", "JSlider", true),
        new PrefixRule("tbl", "table", "JTable", true),
        new PrefixRule("tree", "tree", "JTree", true),
        new PrefixRule("list", "list", "JList", true),
        new PrefixRule("scrl", "scrollPane", "JScrollPane", true),
        new PrefixRule("tp", "tabbedPane", "JTabbedPane", true),
        new PrefixRule("dp", "desktopPane", "JDesktopPane", true),
        new PrefixRule("ip", "internalFrame", "JInternalFrame", true),
        new PrefixRule("dlg", "dialog", "JDialog", true),
        new PrefixRule("fr", "frame", "JFrame", true),
        new PrefixRule("pn", "panel", "JPanel", true),

        // AWT 组件
        new PrefixRule("frm", "frame", "Frame", true),
        new PrefixRule("win", "window", "Window", true),
        new PrefixRule("dlog", "dialog", "Dialog", true),

        // 其他常见类型
        new PrefixRule("ds", "", "DataSet", true),
        new PrefixRule("dt", "date", "Date", true),
        new PrefixRule("cal", "calendar", "Calendar", true),
        new PrefixRule("rs", "resultSet", "ResultSet", true),
        new PrefixRule("stmt", "statement", "Statement", true),
        new PrefixRule("ps", "preparedStatement", "PreparedStatement", true),
        new PrefixRule("conn", "connection", "Connection", true),
        new PrefixRule("rw", "row", "Row", true),
        new PrefixRule("dr", "row", "DataRow", true),

        // 自定义/业务类型前缀
        new PrefixRule("mgr", "manager", "", true),
        new PrefixRule("svc", "service", "", true),
        new PrefixRule("repo", "repository", "", true),
        new PrefixRule("dao", "dao", "", true),
        new PrefixRule("dto", "dto", "", true),
        new PrefixRule("vo", "vo", "", true),
        new PrefixRule("bo", "bo", "", true),
        new PrefixRule("ctx", "context", "", true),
        new PrefixRule("cfg", "config", "", true),
    };

    /**
     * 持久化的规则列表
     */
    public List<PrefixRuleData> rules = new ArrayList<>();

    /**
     * 规则的内存缓存
     */
    private Map<String, PrefixRule> ruleCache = new HashMap<>();

    /**
     * 用于 XML 序列化的数据结构
     */
    public static class PrefixRuleData {
        public String prefix;
        public String replacement;
        public String targetType;
        public boolean enabled;

        public PrefixRuleData() {}

        public PrefixRuleData(PrefixRule rule) {
            this.prefix = rule.prefix();
            this.replacement = rule.replacement();
            this.targetType = rule.targetType();
            this.enabled = rule.enabled();
        }

        public PrefixRule toRule() {
            return new PrefixRule(prefix, replacement, targetType, enabled);
        }
    }

    public HungarianPrefixRegistryImpl() {
        // 初始化默认规则
        for (PrefixRule defaultRule : DEFAULT_RULES) {
            rules.add(new PrefixRuleData(defaultRule));
        }
        rebuildCache();
    }

    private void rebuildCache() {
        ruleCache = rules.stream()
            .filter(PrefixRuleData::isEnabled)
            .map(PrefixRuleData::toRule)
            .collect(Collectors.toMap(PrefixRule::prefix, r -> r, (a, b) -> a));
    }

    @Override
    public PrefixRule[] getRules() {
        return rules.stream().map(PrefixRuleData::toRule).toArray(PrefixRule[]::new);
    }

    @Override
    public PrefixRule getRuleForVariable(String variableName) {
        if (variableName == null || variableName.isEmpty()) {
            return null;
        }

        // 精确匹配最长前缀
        PrefixRule bestMatch = null;
        for (PrefixRule rule : ruleCache.values()) {
            if (variableName.startsWith(rule.prefix())) {
                if (bestMatch == null || rule.prefix().length() > bestMatch.prefix().length()) {
                    // 检查前缀后是否是大写字母（确保是完整前缀）
                    int prefixLen = rule.prefix().length();
                    if (prefixLen < variableName.length()) {
                        char nextChar = variableName.charAt(prefixLen);
                        if (Character.isUpperCase(nextChar) || rule.replacement().isEmpty()) {
                            bestMatch = rule;
                        }
                    } else {
                        bestMatch = rule;
                    }
                }
            }
        }
        return bestMatch;
    }

    @Override
    public PrefixRule[] getRulesForType(String typeName) {
        if (typeName == null) {
            return new PrefixRule[0];
        }

        return rules.stream()
            .filter(r -> r.enabled && (r.targetType.isEmpty() || typeName.contains(r.targetType)))
            .map(PrefixRuleData::toRule)
            .toArray(PrefixRule[]::new);
    }

    @Override
    public void registerRule(PrefixRule rule) {
        // 移除已存在的相同前缀规则
        rules.removeIf(r -> r.prefix.equals(rule.prefix()));
        rules.add(new PrefixRuleData(rule));
        rebuildCache();
    }

    @Override
    public void unregisterRule(String prefix) {
        rules.removeIf(r -> r.prefix.equals(prefix));
        rebuildCache();
    }

    @Override
    public void setRuleEnabled(String prefix, boolean enabled) {
        rules.stream()
            .filter(r -> r.prefix.equals(prefix))
            .findFirst()
            .ifPresent(r -> {
                r.enabled = enabled;
                rebuildCache();
            });
    }

    @Override
    public void importRules(String json) {
        // TODO: 实现 JSON 导入
        // 这里可以使用 Jackson 或 Gson 解析 JSON 并更新 rules
    }

    @Override
    public String exportRules() {
        // TODO: 实现 JSON 导出
        return "{}";
    }

    @Nullable
    @Override
    public HungarianPrefixRegistryImpl getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull HungarianPrefixRegistryImpl state) {
        XmlSerializerUtil.copyBean(state, this);
        rebuildCache();
    }

    /**
     * 获取单例实例
     */
    public static HungarianPrefixRegistry getInstance() {
        return ApplicationManager.getApplication().getService(HungarianPrefixRegistry.class);
    }
}
