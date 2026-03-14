# StringTemplate4 重构可行性分析报告

**日期**: 2026-03-11
**目标**: 将当前代码生成器迁移到 StringTemplate4 模板引擎

---

## 一、当前架构分析

### 1.1 当前代码生成方式

当前系统使用 **手写字符串拼接** 方式生成 Java 代码：

```java
// 当前方式：CJavaClass.java
WriteLine("import nacaLib.mapSupport.* ;", 0);
WriteLine("import nacaLib.varEx.* ;", 0);
String line = "public class " + name + " extends " + csProgType;
WriteLine(line);
WriteLine("{");
```

```java
// 当前方式：CJavaAddTo.java
String line = "inc(" + value.ExportReference(getLine()) + ", ";
cs += dest.ExportReference(getLine()) + ") ;";
WriteLine(cs);
```

### 1.2 核心类结构

```
CBaseLanguageExporter (抽象基类)
├── m_Indent: String          # 缩进字符串
├── m_CurrentLine: String     # 当前行缓冲区
├── WriteLine(String, int)    # 写行
├── WriteWord(String, int)    # 写单词
├── WriteEOL(int)             # 写换行
├── StartBloc() / EndBloc()   # 开始/结束代码块
└── DoWriteLine(String)       # 抽象方法，写入输出

CJavaExporter extends CBaseLanguageExporter
├── m_output: PrintStream     # 文件输出流
├── m_FileName: String        # 输出文件路径
└── DoWriteLine(String)       # 写入文件

CEntityClass (语义实体基类)
├── m_ProgramCatalog          # 符号表
├── m_out: CBaseLanguageExporter  # 导出器
└── DoExport()                # 抽象方法，执行导出
```

### 1.3 代码生成类统计

| 类别 | 文件数量 | 代码行数估算 |
|------|----------|--------------|
| `generate/java/` | ~60 个 | ~5,000 行 |
| `generate/java/CICS/` | ~30 个 | ~2,500 行 |
| `generate/java/SQL/` | ~25 个 | ~2,000 行 |
| `generate/java/verbs/` | ~40 个 | ~3,500 行 |
| `generate/java/forms/` | ~35 个 | ~3,000 行 |
| `generate/java/expressions/` | ~30 个 | ~2,500 行 |
| `generate/fpacjava/` | ~50 个 | ~4,000 行 |
| **总计** | **~270 个** | **~23,000 行** |

### 1.4 当前方式的问题

1. **可维护性差**: 字符串拼接逻辑分散在 270 多个类中
2. **格式一致性难保证**: 每个类手动处理缩进和格式化
3. **代码重复**: 相似的模板代码重复出现
4. **修改困难**: 修改输出格式需要修改多个类
5. **无模板验证**: 无法在编译期验证生成代码的正确性

---

## 二、StringTemplate4 介绍

### 2.1 什么是 StringTemplate4

[StringTemplate](https://github.com/antlr/stringtemplate4) 是一个 Java 模板引擎库，由 ANTLR 作者 Terence Parr 开发，专门用于**代码生成**场景。

**核心特性**:
- **模型 - 视图分离**: 模板 (视图) 与数据 (模型) 完全分离
- **无副作用**: 模板不能调用任意 Java 方法，保证安全
- **自动缩进**: 支持嵌套模板的自动缩进对齐
- **条件渲染**: 支持 if/else 条件
- **集合迭代**: 支持列表/集合的遍历渲染
- **模板继承**: 支持模板复用和组合

### 2.2 Maven 依赖

```xml
<dependency>
    <groupId>org.antlr</groupId>
    <artifactId>ST4</artifactId>
    <version>4.3.4</version>
</dependency>
```

### 2.3 基本用法示例

```java
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

// 方式 1: 直接创建模板
ST template = new ST("Hello <name>!");
template.add("name", "World");
System.out.println(template.render());
// 输出：Hello World!

// 方式 2: 从文件加载模板组
STGroupFile group = new STGroupFile("templates/java.stg");
ST template = group.getInstanceOf("classDeclaration");
template.add("className", "MyClass");
template.add("superClass", "BaseProgram");
System.out.println(template.render());
```

### 2.4 模板语法

```stringtemplate
// 基本属性
class(className) ::= <<
public class <className> {
}
>>

// 条件判断
method(name, params, body) ::= <<
public void <name>(<params; separator=", ">) {
<if(body)>
    <body>
<endif>
}
>>

// 集合迭代
classWithFields(name, fields) ::= <<
public class <name> {
<fields:field()>
}
>>

field() ::= <<
    private <it.type> <it.name>;
>>

// 嵌套模板
importStatement(imports) ::= <<
<imports: importLine(); separator="\n">
>>

importLine() ::= <<import <it>;>>
```

### 2.5 自动缩进特性

```stringtemplate
// 模板中定义缩进
method(name, body) ::= <<
public void <name>() {
<indent()>
<body; format="indent">
}
>>
```

StringTemplate 会自动处理嵌套模板的缩进对齐，这是手写字符串拼接无法比拟的优势。

---

## 三、迁移可行性分析

### 3.1 技术可行性：✅ 高

| 评估项 | 评分 | 说明 |
|--------|------|------|
| 功能覆盖 | 5/5 | ST4 支持所有当前代码生成场景 |
| 性能影响 | 4/5 | 模板渲染开销约 5-10%，可接受 |
| 学习曲线 | 4/5 | 模板语法简单，团队易上手 |
| 向后兼容 | 5/5 | 可保持现有语义实体 API 不变 |
| 工具支持 | 4/5 | 有 IntelliJ 插件支持模板编辑 |

### 3.2 架构可行性：✅ 高

**当前架构**:
```
COBOL 解析 → 语义实体 → 导出器 (WriteLine) → Java 代码
```

**目标架构**:
```
COBOL 解析 → 语义实体 → 模板渲染器 → StringTemplate → Java 代码
```

只需修改 `CBaseLanguageExporter` 基类，将字符串拼接改为模板渲染，语义实体层可保持不变。

### 3.3 迁移策略

#### 方案 A: 渐进式迁移（推荐）

1. 第一阶段：引入 StringTemplate，新建模板文件
2. 第二阶段：逐个模块迁移，先迁移简单类（如 CJavaComment）
3. 第三阶段：迁移复杂类（如 CJavaClass, CJavaProcedure）
4. 第四阶段：迁移 CICS/SQL/verbs 专用类
5. 第五阶段：清理旧代码，删除 WriteLine 相关方法

**优点**:
- 风险低，可随时回退
- 不影响现有功能
- 可并行开发和测试

**缺点**:
- 迁移周期较长（预计 4-6 周）
- 需要维护两套代码生成逻辑（过渡期）

#### 方案 B: 一次性重构

1. 完成所有模板文件编写
2. 修改 CBaseLanguageExporter 使用模板
3. 一次性提交并测试

**优点**:
- 迁移周期短
- 无过渡期代码冗余

**缺点**:
- 风险高
- 测试压力大

### 3.4 风险评估

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| 模板语法学习成本 | 低 | 低 | 提供培训和示例 |
| 迁移后格式变化 | 中 | 中 | 保留原有空白行和注释 |
| 性能回归 | 低 | 中 | 基准测试验证 |
| 模板文件管理 | 中 | 低 | 统一目录结构 |
| 部分类难以模板化 | 低 | 中 | 保留手写方式作为例外 |

---

## 四、实施计划

### 4.1 阶段划分

| 阶段 | 目标 | 预计时间 | 交付物 |
|------|------|----------|--------|
| Phase 0 | 环境准备 | 1 天 | Maven 依赖、模板目录 |
| Phase 1 | 基础模板 | 3 天 | class/method/field 模板 |
| Phase 2 | 简单类迁移 | 5 天 | 20 个简单类完成迁移 |
| Phase 3 | 复杂类迁移 | 10 天 | CJavaClass 等核心类迁移 |
| Phase 4 | 专用类迁移 | 10 天 | CICS/SQL/verbs 类迁移 |
| Phase 5 | 测试验证 | 5 天 | 测试流水线通过 |
| Phase 6 | 代码清理 | 3 天 | 删除旧导出代码 |
| **总计** | | **~37 天** | |

### 4.2 模板目录结构

```
naca-trans/src/main/resources/templates/
├── java/
│   ├── class.stg           # 类定义模板
│   ├── method.stg          # 方法定义模板
│   ├── field.stg           # 字段定义模板
│   ├── import.stg          # import 语句模板
│   ├── condition.stg       # 条件语句模板
│   └── expressions/
│       ├── sum.stg         # 加法表达式
│       ├── concat.stg      # 字符串拼接
│       └── ...
├── cic/
│   ├── link.stg            # CICS LINK 模板
│   ├── read.stg            # CICS READ 模板
│   ├── sendMap.stg         # CICS SEND MAP 模板
│   └── ...
├── sql/
│   ├── select.stg          # SQL SELECT 模板
│   ├── insert.stg          # SQL INSERT 模板
│   ├── cursor.stg          # SQL CURSOR 模板
│   └── ...
└── verbs/
    ├── addTo.stg           # ADD TO 动词模板
    ├── moveTo.stg          # MOVE TO 动词模板
    ├── call.stg            # CALL 动词模板
    └── ...
```

### 4.3 模板示例

#### 类定义模板 (java/class.stg)

```stringtemplate
class(name, superClass, imports, fields, methods) ::= <<
<imports:import(); separator="\n">

public class <name> extends <superClass> {
<if(fields)>
<fields:field(); separator="\n">

<endif>
<if(methods)>
<methods:method(); separator="\n">

<endif>
}
>>
```

#### CICS LINK 模板 (cics/link.stg)

```stringtemplate
link(program, commArea, length) ::= <<
CESM.link(<program>)<if(commArea)>.commarea(<commArea>, <if(length)><length><else>-1<endif>)<endif>.go();
>>
```

---

## 五、测试验证方案

### 5.1 单元测试

为每个模板创建单元测试：

```java
@Test
void testClassTemplate() {
    STGroupFile group = new STGroupFile("templates/java/class.stg");
    ST template = group.getInstanceOf("class");
    template.add("name", "BATCH1");
    template.add("superClass", "BatchProgram");

    String result = template.render();
    assertTrue(result.contains("public class BATCH1 extends BatchProgram"));
}
```

### 5.2 集成测试

运行现有测试流水线：

```bash
# 运行转译流水线
./gradlew :naca-trans:transpile -PconfigFile=NacaTrans.cfg

# 运行测试
./gradlew :naca-rt-tests:test

# 对比 COBOL 和 Java 输出
./gradlew compareResults -Pprogram=BATCH1
```

### 5.3 回归测试

确保迁移后生成的代码与迁移前完全一致（空白行和注释除外）：

```bash
# 使用现有测试
./gradlew :naca-rt-tests:test --tests "TranslationPipelineTest"
./gradlew :naca-rt-tests:test --tests "SqlInjectionTests"
```

---

## 六、结论与建议

### 6.1 可行性结论

**✅ 技术上完全可行**，理由如下：

1. StringTemplate4 是专为代码生成设计的成熟框架
2. 当前架构支持渐进式迁移
3. 模板语法简单，学习成本低
4. 有丰富的成功案例（ANTLR、Hibernate 等）

### 6.2 推荐方案

采用 **渐进式迁移方案**，分 6 个阶段在约 37 个工作日内完成：

1. 环境准备和模板设计（4 天）
2. 简单类迁移（5 天）
3. 复杂类迁移（10 天）
4. 专用类迁移（10 天）
5. 测试验证（5 天）
6. 代码清理（3 天）

### 6.3 关键成功因素

1. **保持向后兼容**: 迁移期间新旧代码生成方式并存
2. **充分测试**: 每个类迁移后都要通过测试流水线
3. **文档完善**: 为模板语法和使用编写文档
4. **版本控制**: 小步提交，便于回退

### 6.4 预期收益

| 收益项 | 说明 |
|--------|------|
| 可维护性提升 | 模板集中管理，修改更方便 |
| 代码一致性 | 统一模板保证输出格式一致 |
| 开发效率 | 新增代码生成器更快速 |
| 可读性 | 模板比字符串拼接更清晰 |
| 错误减少 | 编译期检查模板语法 |

---

## 七、参考资料

- [StringTemplate4 官方文档](https://github.com/antlr/stringtemplate4)
- [StringTemplate4 用户指南](http://www.stringtemplate.org/doc/st4.html)
- [Maven Central - ST4](https://mvnrepository.com/artifact/org.antlr/ST4)
- [IntelliJ StringTemplate 插件](https://plugins.jetbrains.com/plugin/10591-stringtemplate)

---

## 八、下一步行动

1. [ ] 创建任务跟踪列表
2. [ ] 添加 StringTemplate4 Maven 依赖
3. [ ] 创建模板目录结构
4. [ ] 编写第一个模板（class.stg）并测试
5. [ ] 开始 Phase 1 实施
