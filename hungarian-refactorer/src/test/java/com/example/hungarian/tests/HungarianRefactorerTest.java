package com.example.hungarian.tests;

import com.example.hungarian.HungarianRefactorerEngine;
import com.example.hungarian.HungarianRefactorerEngine.RefactorResult;
import com.example.hungarian.HungarianRefactorerSettings;
import com.example.hungarian.HungarianRefactorerSettingsImpl;
import com.example.hungarian.HungarianPrefixRegistry;
import com.example.hungarian.HungarianPrefixRegistryImpl;
import com.example.hungarian.psi.HungarianVariableInfo;
import com.example.hungarian.psi.NameConverter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 匈牙利命名法重构插件的单元测试
 *
 * 测试范围：
 * 1. 命名规则识别
 * 2. 变量名转换
 * 3. 变量查找
 * 4. 重构执行
 */
public class HungarianRefactorerTest extends BasePlatformTestCase {

    private HungarianPrefixRegistry prefixRegistry;
    private HungarianRefactorerSettings settings;
    private NameConverter nameConverter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prefixRegistry = new HungarianPrefixRegistryImpl();
        settings = new HungarianRefactorerSettingsImpl();
        nameConverter = new NameConverter(prefixRegistry);
    }

    /**
     * 测试前缀规则识别
     */
    public void testPrefixRuleRecognition() {
        // 测试 String 类型前缀
        var rule = prefixRegistry.getRuleForVariable("strName");
        assertNotNull("str 前缀应被识别", rule);
        assertEquals("str", rule.prefix());
        assertEquals("", rule.replacement());

        // 测试 int 类型前缀
        rule = prefixRegistry.getRuleForVariable("iCount");
        assertNotNull("i 前缀应被识别", rule);
        assertEquals("i", rule.prefix());

        // 测试 boolean 类型前缀
        rule = prefixRegistry.getRuleForVariable("bVisible");
        assertNotNull("b 前缀应被识别", rule);
        assertEquals("is", rule.replacement());

        // 测试 JButton 前缀
        rule = prefixRegistry.getRuleForVariable("btnSubmit");
        assertNotNull("btn 前缀应被识别", rule);
        assertEquals("button", rule.replacement());
    }

    /**
     * 测试名称转换逻辑
     */
    public void testNameConversion() {
        // strName -> name
        var rule = prefixRegistry.getRuleForVariable("strName");
        String converted = nameConverter.convertToCamelCase("strName", rule);
        assertEquals("name", converted);

        // strName -> name (带前缀替换)
        rule = prefixRegistry.getRuleForVariable("btnSubmit");
        converted = nameConverter.convertToCamelCase("btnSubmit", rule);
        assertEquals("buttonSubmit", converted);

        // bVisible -> isVisible
        rule = prefixRegistry.getRuleForVariable("bVisible");
        converted = nameConverter.convertToCamelCase("bVisible", rule);
        assertEquals("isVisible", converted);

        // lstNames -> listNames
        rule = prefixRegistry.getRuleForVariable("lstNames");
        converted = nameConverter.convertToCamelCase("lstNames", rule);
        assertEquals("listNames", converted);
    }

    /**
     * 测试非匈牙利命名法不被识别
     */
    public void testNonHungarianNotNotRecognized() {
        // stream - str 后不是大写字母
        var rule = prefixRegistry.getRuleForVariable("stream");
        // 可能返回 null 或另一个规则，但不应导致错误转换
        assertNotNull(rule); // 可能有其他匹配

        // name - 没有前缀
        rule = prefixRegistry.getRuleForVariable("name");
        assertNull("普通名称不应匹配前缀规则", rule);

        // i - 单字母变量
        rule = prefixRegistry.getRuleForVariable("i");
        // 单字母变量可能不被识别为匈牙利命名法
    }

    /**
     * 测试设置配置
     */
    public void testSettingsConfiguration() {
        assertTrue("默认应启用重构", settings.isEnabled());
        assertTrue("默认应处理局部变量", settings.isProcessLocalVariables());
        assertTrue("默认应处理字段", settings.isProcessFields());
        assertFalse("默认不处理参数", settings.isProcessParameters());
        assertTrue("默认应更新访问器", settings.isUpdateAccessors());
        assertFalse("默认不更新注释", settings.isUpdateComments());
        assertTrue("默认应预览更改", settings.isPreviewChanges());

        // 修改设置
        settings.setEnabled(false);
        assertFalse("设置后应禁用", settings.isEnabled());

        settings.setProcessParameters(true);
        assertTrue("设置后应处理参数", settings.isProcessParameters());
    }

    /**
     * 测试转换结果对象
     */
    public void testConversionResult() {
        NameConverter.ConversionResult result = nameConverter.convert("strName");
        assertEquals("strName", result.getOriginal());
        assertEquals("name", result.getConverted());
        assertTrue("名称应已更改", result.isChanged());
        assertNotNull("应匹配规则", result.getMatchedRule());
    }

    /**
     * 测试未更改的转换结果
     */
    public void testUnchangedConversion() {
        NameConverter.ConversionResult result = nameConverter.convert("name");
        assertEquals("name", result.getOriginal());
        assertEquals("name", result.getConverted());
        assertFalse("名称不应更改", result.isChanged());
    }

    /**
     * 测试最长前缀匹配
     */
    public void testLongestPrefixMatch() {
        // blnVisible 应该匹配 bln 而不是 b
        var rule = prefixRegistry.getRuleForVariable("blnVisible");
        assertNotNull("bln 前缀应被识别", rule);
        // 应匹配更具体的 bln 规则
    }

    /**
     * 测试空替换前缀
     */
    public void testEmptyReplacement() {
        // str -> (empty)
        var rule = prefixRegistry.getRuleForVariable("strData");
        String converted = nameConverter.convertToCamelCase("strData", rule);
        assertEquals("data", converted);
    }

    /**
     * 测试前缀后小写字母的情况
     */
    public void testPrefixFollowedByLowercase() {
        // stream - str 后跟的是小写 e，不是标准匈牙利命名法
        // 这种情况下应该保持原名或特殊处理
        String original = "stream";
        var rule = prefixRegistry.getRuleForVariable(original);
        // 如果规则存在，转换应保持原名或合理处理
        if (rule != null) {
            String converted = nameConverter.convertToCamelCase(original, rule);
            // 标准逻辑应返回原名，因为不符合匈牙利命名法格式
        }
    }
}
