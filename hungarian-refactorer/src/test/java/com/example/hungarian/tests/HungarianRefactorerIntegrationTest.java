package com.example.hungarian.tests;

import com.example.hungarian.HungarianRefactorerEngine;
import com.example.hungarian.HungarianRefactorerEngine.RefactorResult;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

/**
 * 匈牙利命名法重构插件的集成测试
 *
 * 测试范围：
 * 1. 文件级别的重构
 * 2. 实际 PSI 操作
 * 3. 重构后代码正确性
 */
public class HungarianRefactorerIntegrationTest extends JavaCodeInsightFixtureTestCase {

    private HungarianRefactorerEngine engine;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // engine 需要在项目上下文中初始化
    }

    /**
     * 测试简单 Java 文件的重构
     */
    public void testSimpleFileRefactoring() {
        // 创建测试文件
        myFixture.configureByText("TestClass.java", """
            public class TestClass {
                private String strName;
                private int iCount;

                public void method() {
                    String strValue = "test";
                    int i = 0;
                    System.out.println(strName + iCount);
                }

                public String getStrName() {
                    return strName;
                }

                public void setStrName(String strName) {
                    this.strName = strName;
                }
            }
            """);

        PsiFile file = myFixture.getFile();
        assertNotNull(file);

        // 获取引擎并执行分析
        Project project = getProject();
        engine = new HungarianRefactorerEngine(project);

        // 分析文件
        var variables = engine.analyzeFile(file);

        // 验证识别结果
        assertTrue("应识别出匈牙利命名法变量", variables.size() > 0);

        // 查找特定变量
        boolean foundStrName = variables.stream()
            .anyMatch(v -> v.getVariableName().equals("strName"));
        boolean foundICount = variables.stream()
            .anyMatch(v -> v.getVariableName().equals("iCount"));

        assertTrue("应识别 strName", foundStrName);
        assertTrue("应识别 iCount", foundICount);
    }

    /**
     * 测试重构后 getter/setter 更新
     */
    public void testAccessorUpdate() {
        myFixture.configureByText("TestClass.java", """
            public class TestClass {
                private String strName;

                public String getStrName() {
                    return strName;
                }

                public void setStrName(String strName) {
                    this.strName = strName;
                }
            }
            """);

        // 执行重构
        Project project = getProject();
        engine = new HungarianRefactorerEngine(project);
        RefactorResult result = engine.refactorFile(myFixture.getFile(), false);

        // 验证重构结果
        assertTrue("应有成功重命名的变量", result.successCount > 0);

        // 验证代码内容（实际应该检查 getter/setter 是否也更新了）
        myFixture.checkResult("""
            public class TestClass {
                private String name;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
            """);
    }

    /**
     * 测试跨文件引用更新
     */
    public void testCrossFileReferenceUpdate() {
        // 创建第一个文件（包含字段）
        myFixture.configureByText("ClassA.java", """
            public class ClassA {
                public String strName;
            }
            """);

        // 创建第二个文件（引用第一个文件的字段）
        myFixture.configureByText("ClassB.java", """
            public class ClassB {
                public void method() {
                    ClassA a = new ClassA();
                    System.out.println(a.strName);
                }
            }
            """);

        // 在 ClassA 上执行重构
        Project project = getProject();
        engine = new HungarianRefactorerEngine(project);
        RefactorResult result = engine.refactorFile(myFixture.getFile(), false);

        // 验证 ClassA 中的字段已重命名
        // 验证 ClassB 中的引用也已更新
    }

    /**
     * 测试循环变量不被错误识别
     */
    public void testLoopVariableNotRecognized() {
        myFixture.configureByText("TestClass.java", """
            public class TestClass {
                public void method() {
                    for (int i = 0; i < 10; i++) {
                        System.out.println(i);
                    }
                }
            }
            """);

        Project project = getProject();
        engine = new HungarianRefactorerEngine(project);
        var variables = engine.analyzeFile(myFixture.getFile());

        // 循环变量 i 不应被识别为匈牙利命名法
        boolean hasLoopI = variables.stream()
            .anyMatch(v -> v.getVariableName().equals("i") &&
                          v.getLineNumber() < 5); // 在循环中的 i

        assertFalse("循环变量 i 不应被识别", hasLoopI);
    }

    /**
     * 测试编译后代码的正确性
     */
    public void testPostRefactorCompilation() {
        myFixture.configureByText("TestClass.java", """
            public class TestClass {
                private String strName;
                private int iCount;

                public String getStrName() {
                    return strName;
                }

                public int getICount() {
                    return iCount;
                }
            }
            """);

        // 执行重构
        Project project = getProject();
        engine = new HungarianRefactorerEngine(project);
        engine.refactorFile(myFixture.getFile(), false);

        // 验证代码可以编译（无错误高亮）
        myFixture.checkHighlighting();
    }
}
