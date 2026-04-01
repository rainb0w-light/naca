package com.example.hungarian.psi;

import com.example.hungarian.HungarianPrefixRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 名称转换器
 * 将匈牙利命名法转换为驼峰命名法
 */
public class NameConverter {

    private final HungarianPrefixRegistry prefixRegistry;

    public NameConverter(@NotNull HungarianPrefixRegistry prefixRegistry) {
        this.prefixRegistry = prefixRegistry;
    }

    /**
     * 将匈牙利命名法转换为驼峰命名法
     *
     * @param hungarianName 匈牙利命名法名称
     * @param rule 匹配的前缀规则
     * @return 转换后的驼峰命名法名称
     */
    @NotNull
    public String convertToCamelCase(
        @NotNull String hungarianName,
        @Nullable HungarianPrefixRegistry.PrefixRule rule
    ) {
        if (rule == null) {
            return hungarianName;
        }

        String prefix = rule.prefix();
        String replacement = rule.replacement();

        // 检查前缀是否匹配
        if (!hungarianName.startsWith(prefix)) {
            return hungarianName;
        }

        // 获取前缀后的剩余部分
        String remainder = hungarianName.substring(prefix.length());

        // 如果剩余部分为空，直接返回替换前缀
        if (remainder.isEmpty()) {
            return replacement.isEmpty() ? hungarianName : replacement;
        }

        // 检查剩余部分的首字符
        char firstChar = remainder.charAt(0);

        if (Character.isUpperCase(firstChar)) {
            // 标准的匈牙利命名法：strName, btnSubmit
            // 移除前缀，将剩余部分首字母小写
            String lowerRemainder = Character.toLowerCase(firstChar) + remainder.substring(1);
            return replacement + lowerRemainder;
        } else {
            // 非标准格式，可能不是匈牙利命名法
            // 例如：stream (str 是前缀但 eam 不是大写字母开头)
            return hungarianName;
        }
    }

    /**
     * 批量转换（用于预览）
     */
    @NotNull
    public ConversionResult convert(@NotNull String hungarianName) {
        HungarianPrefixRegistry.PrefixRule rule = prefixRegistry.getRuleForVariable(hungarianName);
        String converted = convertToCamelCase(hungarianName, rule);
        boolean changed = !hungarianName.equals(converted);

        return new ConversionResult(hungarianName, converted, rule, changed);
    }

    /**
     * 转换结果
     */
    public static class ConversionResult {
        private final String original;
        private final String converted;
        private final HungarianPrefixRegistry.PrefixRule matchedRule;
        private final boolean changed;

        public ConversionResult(
            @NotNull String original,
            @NotNull String converted,
            @Nullable HungarianPrefixRegistry.PrefixRule matchedRule,
            boolean changed
        ) {
            this.original = original;
            this.converted = converted;
            this.matchedRule = matchedRule;
            this.changed = changed;
        }

        @NotNull
        public String getOriginal() {
            return original;
        }

        @NotNull
        public String getConverted() {
            return converted;
        }

        @Nullable
        public HungarianPrefixRegistry.PrefixRule getMatchedRule() {
            return matchedRule;
        }

        public boolean isChanged() {
            return changed;
        }

        @Override
        public String toString() {
            return original + " -> " + converted + (changed ? " [changed]" : " [unchanged]");
        }
    }
}
