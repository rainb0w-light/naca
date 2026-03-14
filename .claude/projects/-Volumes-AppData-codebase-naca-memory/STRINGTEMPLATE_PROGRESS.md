# StringTemplate4 重构进度报告

**最后更新时间**: 2026-03-12

## 总体状态

StringTemplate4 代码生成框架的基础设施已完成，正在进行大规模代码迁移。

## 已完成的工作

### 1. 基础框架搭建 ✅
- 添加 StringTemplate4 依赖到 `naca-trans/build.gradle.kts`
- 创建 `TemplateLoader.java` 类用于从 classpath 加载模板
- 配置 Gradle 处理重复资源文件

### 2. 模板文件创建 ✅
已创建 7 个模板文件，包含约 215 个模板：
- `/templates/java/verbs/verbs.stg` - 50+ COBOL verb 模板
- `/templates/java/CICS/cics.stg` - 30+ CICS 命令模板
- `/templates/java/SQL/sql.stg` - 20+ SQL 语句模板
- `/templates/java/expressions/expressions.stg` - 40+ 表达式模板
- `/templates/java/forms/forms.stg` - 25+ 表单模板
- `/templates/java/java.stg` - Java 基础模板
- `/templates/fpacjava/fpac.stg` - 30+ FPac 模板

### 3. 单元测试 ✅
- 创建 `StringTemplateTests.java` 包含 12 个测试用例
- 所有测试通过 (BUILD SUCCESSFUL)

### 4. 已迁移的 Verbs 类 (17 个) ✅
- CJavaAddToST.java (ADD TO verb)
- CJavaAssignST.java (ASSIGN/MOVE verb)
- CJavaCalculST.java (COMPUTE/CALCULATE verb)
- CJavaMultiplyST.java (MULTIPLY verb)
- CJavaDivideST.java (DIVIDE verb)
- CJavaSubtractToST.java (SUBTRACT verb)
- CJavaCallFunctionST.java (CALL FUNCTION/PERFORM)
- CJavaGotoST.java (GO TO verb)
- CJavaBreakST.java (BREAK verb)
- CJavaContinueST.java (CONTINUE verb)
- CJavaAcceptST.java (ACCEPT verb)
- CJavaDisplayST.java (DISPLAY verb)
- CJavaNextSentenceST.java (NEXT SENTENCE verb)
- CJavaReturnST.java (RETURN/STOP RUN verb)
- CJavaOpenFileST.java (OPEN verb)
- CJavaCloseFileST.java (CLOSE verb)
- CJavaReadFileST.java (READ verb)

## 待完成的工作

### 1. 剩余代码迁移 (约 25 个类)
需要迁移的剩余 verbs 类：
- CJavaWriteFile.java (WRITE verb)
- CJavaRewriteFile.java (REWRITE verb)
- CJavaExec.java (EXEC SQL)
- CJavaCase.java (EVALUATE/CASE)
- CJavaCondition.java (IF conditions)
- CJavaInitialize.java (INITIALIZE verb)
- CJavaInspectConverting.java (INSPECT verb)
- CJavaSearch.java (SEARCH verb)
- CJavaSort.java (SORT verb)
- CJavaStringConcat.java (STRING verb)
- CJavaParseString.java (UNSTRING verb)
- CJavaGotoDepending.java (GO TO DEPENDING)
- CJavaLoopWhile.java (PERFORM VARYING)
- CJavaLoopIter.java (loop iterator)
- 等等...

### 2. 测试流水线问题
当前测试失败原因：
- 测试类 `nacaTests.CobolLikeSupport.math` 等需要预先编译
- 这些是手写的 Java 测试类，不是从 COBOL 转译的
- 需要确保 `naca-rt-tests` 模块正确编译

### 3. 已修复的问题
- 删除了不兼容的组件类 (`ProgramLifecycleManager` 等)
- 添加了 Gradle 重复文件处理策略

## 下一步操作

1. **继续迁移剩余 Verbs 模块** - 约 25 个类
2. **迁移 CICS 模块** - 约 25 个类 (已创建模板)
3. **迁移 SQL 模块** - 约 20 个类 (已创建模板)
4. **运行完整测试流水线** - `./gradlew :naca-rt-tests:test`
5. **验证 COBOL 输出一致性** - 比较生成代码与原 COBOL 代码的行为

## 技术要点

### StringTemplate4 语法注意事项
- 不支持 `&&` 和 `!=` 运算符，需要使用嵌套 `<if>`
- quote 嵌套问题需要使用 `<<...>>` 分隔符
- 示例：
```stringtemplate
<if(value)>
<if(value != "1")>, <value><endif>
<endif>
```

### 模板加载
```java
ST template = TemplateLoader.getVerbsTemplate("inc");
template.add("dest", "WS-VAR");
String code = template.render();
WriteLine(code);
```

## 估计工作量

按当前进度，完整迁移约需要 15-20 天的连续工作时间。
建议分批次进行：
1. Verbs 模块 (最高优先级) - 进行中，已完成约 40%
2. Expressions 和 Forms
3. CICS 和 SQL
4. FPac 模块
5. 基础生成器
