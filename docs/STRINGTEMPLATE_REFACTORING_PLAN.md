# StringTemplate4 详细重构计划

> **前置条件**: ST4 已在 `naca-trans/build.gradle.kts` 中配置为依赖 (`org.antlr:ST4:4.3.4`)

> **状态更新**: Phase 1-3 已完成 (2026-03-13)

---

## 完成状态

| Phase | 状态 | Git Commit |
|-------|------|------------|
| Phase 1: 基础设施 | ✅ 完成 | `4e08323` |
| Phase 2: 实体改造 | ✅ 完成 | `9d388e1` |
| Phase 3: Controller瘦化 | ✅ 完成 | `697707e` |
| Phase 4: 模板开发 | 🔄 进行中 | - |
| Phase 5: 验证清理 | ⏳ 待开始 | - |

---

## 一、现状分析

### 1.1 依赖状态 (已更新)
- ✅ ST4 4.3.4 已配置
- ✅ TemplateLoader 已实现
- ✅ 模板文件目录已创建

### 1.2 实体类 Getter 状态 (已更新)

| 类 | 关键字段 | Getter状态 |
|---|---|---|
| `CEntityCondition` | `m_Condition`, `m_ThenBloc`, `m_ElseBloc` | ✅ 已添加 |
| `CEntityAssign` | `m_Value`, `m_arrRefTo`, `m_bFillAll`, `m_bMoveCorresponding` | ✅ 已添加 |
| `CEntityAddTo` | `m_arrValues`, `m_arrDest`, `m_bRounded` | ✅ 已添加 |
| `CEntityReadFile` | `m_eFileDescriptor`, `m_eDataInto`, `m_eAtEndBloc`, `m_eNotAtEndBloc` | ✅ 已添加 |
| `CEntityLoopWhile` | `m_WhileCondition`, `m_bDoBefore` | ✅ 已添加 |
| `CBaseLanguageEntity` | `m_lstChildren`, `m_Name`, `m_line`, `m_parent` | ✅ 已添加 |
| `CDataEntity` | `m_Name`, `m_Of`, `export()` | ✅ 已添加 |

### 1.3 Controller 类现状

| 目录 | 文件数 | 问题 |
|---|---|---|
| `generate/java/` | 24 | 字符串拼接在 DoExport() 中 |
| `generate/java/verbs/` | 39 | 大量条件判断和拼接逻辑 |
| `generate/java/expressions/` | 24 | Export() 方法返回字符串 |
| `generate/java/SQL/` | 22 | 嵌套语句拼接 |
| `generate/java/CICS/` | 30 | 命令参数拼接 |
| `generate/java/forms/` | 43 | 属性组合拼接 |

---

## 二、重构阶段规划

### Phase 1: 基础设施搭建 (预计 2-3 天)

#### 1.1 创建模板目录结构
```
naca-trans/src/main/resources/templates/
├── base.stg              # 基础模板
└── java/
    ├── java.stg          # Java核心结构
    ├── verbs.stg         # COBOL动词
    ├── expressions.stg   # 表达式
    ├── control.stg       # 控制流
    ├── data.stg          # 数据结构
    ├── sql.stg           # SQL语句
    ├── cics.stg          # CICS命令
    └── forms.stg         # 表单字段
```

#### 1.2 创建 TemplateLoader 类
```java
// naca-trans/src/main/java/generate/templates/TemplateLoader.java
package generate.templates;

import org.stringtemplate.v4.*;
import java.io.*;

public class TemplateLoader {
    private static STGroup javaGroup;
    
    static {
        String templatePath = TemplateLoader.class
            .getResource("/templates/java/java.stg")
            .getPath();
        javaGroup = new STGroupFile(templatePath);
    }
    
    public static ST getTemplate(String name) {
        return javaGroup.getInstanceOf(name);
    }
    
    public static ST getVerbsTemplate(String name) {
        return javaGroup.getInstanceOf(name);  // 通过继承访问
    }
}
```

#### 1.3 创建基础模板文件
```stg
// templates/base.stg
group base;

// 通用语句分发
statement(entity) ::= <<
<if(entity.entityType == "condition")><condition(entity)><endif>
<if(entity.entityType == "assign")><assign(entity)><endif>
<if(entity.entityType == "loop")><loop(entity)><endif>
<if(entity.entityType == "readFile")><readFile(entity)><endif>
>>

// 通用数据引用
dataRef(entity) ::= <<
<if(entity.ofQualifier)><entity.ofQualifier.formattedName>.<endif><entity.formattedName>
>>

// 空值检查
nullCheck(value, default) ::= <<
<if(value)><value><else><default><endif>
>>
```

#### 1.4 任务清单
- [ ] 创建 `templates/` 目录结构
- [ ] 实现 `TemplateLoader.java`
- [ ] 创建 `base.stg` 模板
- [ ] 创建 `java.stg` 模板骨架
- [ ] 编写单元测试验证模板加载

---

### Phase 2: 实体类改造 (预计 3-5 天)

#### 2.1 选择方案: Lombok vs 手写 Getter

**推荐**: 手写 Getter (避免引入新依赖，保持代码一致性)

#### 2.2 基类改造

**CBaseLanguageEntity.java** 需添加:
```java
// 现有字段
protected LinkedList<CBaseLanguageEntity> m_lstChildren = new LinkedList<>();
protected String m_Name = "";
private int m_line = 0;
protected CBaseLanguageEntity m_parent = null;
protected String m_csDisplayName = "";

// 需要添加的 getter
public LinkedList<CBaseLanguageEntity> getChildren() { return m_lstChildren; }
public String getName() { return m_Name; }
public int getLine() { return m_line; }
public CBaseLanguageEntity getParent() { return m_parent; }
public String getDisplayName() { return m_csDisplayName; }
public String getEntityType() { return this.getClass().getSimpleName(); }
```

**CDataEntity.java** 需添加:
```java
public CBaseExternalEntity getOfQualifier() { return m_Of; }
public String getFormattedName() { 
    return m_output != null ? m_output.FormatIdentifier(GetDisplayName()) : GetDisplayName();
}
```

#### 2.3 按类别改造实体类

**优先级 1 - 控制流实体**:
```
semantic/CEntityCondition.java
semantic/Verbs/CEntityLoopWhile.java
semantic/Verbs/CEntityLoopIter.java
semantic/Verbs/CEntityCase.java
semantic/Verbs/CEntitySwitchCase.java
```

**优先级 2 - 数据操作实体**:
```
semantic/Verbs/CEntityAssign.java
semantic/Verbs/CEntityAddTo.java
semantic/Verbs/CEntitySubtractTo.java
semantic/Verbs/CEntityMultiply.java
semantic/Verbs/CEntityDivide.java
```

**优先级 3 - 文件操作实体**:
```
semantic/Verbs/CEntityReadFile.java
semantic/Verbs/CEntityWriteFile.java
semantic/Verbs/CEntityOpenFile.java
semantic/Verbs/CEntityCloseFile.java
```

**优先级 4 - 表达式实体**:
```
semantic/expression/CEntityExprSum.java
semantic/expression/CEntityCondOr.java
semantic/expression/CEntityCondAnd.java
semantic/expression/CEntityCondCompare.java
```

#### 2.4 任务清单
- [ ] 为 `CBaseLanguageEntity` 添加基础 getter
- [ ] 为 `CDataEntity` 添加 getter
- [ ] 为控制流实体添加 getter (5个类)
- [ ] 为数据操作实体添加 getter (5个类)
- [ ] 为文件操作实体添加 getter (4个类)
- [ ] 为表达式实体添加 getter (4个类)
- [ ] 编译验证无错误

---

### Phase 3: Controller 瘦化 (预计 5-7 天)

#### 3.1 DoExport() 重构模式

**重构前** (CJavaCondition.java):
```java
protected void DoExport() {
    if (m_Condition == null) return;
    if (m_Condition.ignore()) {
        if (m_ElseBloc != null && !m_ElseBloc.ignore()) {
            WriteLine("{", m_ElseBloc.getLine());
            DoExport(m_ElseBloc);
            WriteLine("}");
        }
        return;
    }
    WriteWord("if (");
    String cs = m_Condition.Export() + ") {";
    WriteWord(cs);
    WriteEOL();
    DoExport(m_ThenBloc);
    WriteLine("}", m_ThenBloc.GetEndLine());
    if (m_ElseBloc != null) {
        WriteLine("else {", m_ElseBloc.getLine());
        DoExport(m_ElseBloc);
        WriteLine("}", m_ElseBloc.GetEndLine());
    }
}
```

**重构后** (CJavaConditionST.java):
```java
protected void DoExport() {
    ST template = TemplateLoader.getTemplate("condition");
    template.add("entity", this);
    m_output.WriteLine(template.render(), getLine());
}
```

#### 3.2 ExportReference() 重构方向

**当前问题**: ExportReference() 返回字符串，在模板中无法嵌套处理

**解决方案 1**: 保留 ExportReference() 但在模板中调用
```stg
// 模板中使用实体的方法
dataRef(entity) ::= "<entity.exportReference()>"
```

**解决方案 2**: 创建数据传输对象
```java
public class ExportData {
    private final String name;
    private final ExportData ofQualifier;
    
    public String getName() { return name; }
    public ExportData getOfQualifier() { return ofQualifier; }
}

public ExportData getExportData() {
    return new ExportData(
        FormatIdentifier(GetDisplayName()),
        m_Of != null ? m_Of.getExportData() : null
    );
}
```

#### 3.3 按类别重构 Controller

**优先级 1 - 简单控制流**:
```
CJavaBloc.java      → CJavaBlocST.java (测试模板嵌套)
CJavaCondition.java → CJavaConditionST.java
CJavaLoopWhile.java → CJavaLoopWhileST.java
```

**优先级 2 - 简单动词**:
```
CJavaAssign.java    → CJavaAssignST.java
CJavaAddTo.java     → CJavaAddToST.java
CJavaContinue.java  → CJavaContinueST.java
```

**优先级 3 - 复杂动词**:
```
CJavaReadFile.java
CJavaWriteFile.java
CJavaCalcul.java
```

#### 3.4 任务清单
- [ ] 创建第一个 ST 类 `CJavaBlocST.java` 作为概念验证
- [ ] 创建对应模板 `block` 在 `java.stg`
- [ ] 验证模板渲染输出与原实现一致
- [ ] 重构 `CJavaCondition` + 创建 `condition` 模板
- [ ] 重构 `CJavaAssign` + 创建 `assign` 模板
- [ ] 逐步重构其他动词类

---

### Phase 4: 模板开发 (预计 5-7 天)

#### 4.1 java.stg 核心模板

```stg
group java;
import "../base.stg";

// ==================== 控制流 ====================

// 条件语句
condition(entity) ::= <<
<if(entity.condition)>if (<entity.condition.export()>)
{
<entity.thenBloc.children:statement()>
}<if(entity.elseBloc)>
else
{
<entity.elseBloc.children:statement()>
}<endif><endif>
>>

// 循环语句
loop(entity) ::= <<
<if(entity.doBefore)>do
{
<entity.children:statement()>
} while (<entity.whileCondition.export()>);<else>
while (<entity.whileCondition.export()>)
{
<entity.children:statement()>
}<endif>
>>

// ==================== 数据操作 ====================

// 赋值语句
assign(entity) ::= <<
<if(entity.moveCorresponding)>moveCorresponding(<else><if(entity.fillAll)>moveAll(<else>move(<endif><endif><entity.value.export()>, <entity.destinations:{d | <d.export()>}; separator=", ">);
>>

// 加法语句
addTo(entity) ::= <<
<if(entity.hasSingleValue)><if(entity.isIncrement)>inc(<entity.singleDestination>);<else><if(entity.isDecrement)>dec(<entity.singleDestination>);<else>inc(<entity.singleValue>, <entity.singleDestination>);<endif><endif><else>add(<entity.values:{v | <v.export()>}; separator=", ">).<if(entity.rounded)>toRounded<else>to<endif>(<entity.destinations:{d | <d.export()>}; separator=", ">);<endif>
>>

// ==================== 文件操作 ====================

// 读文件
readFile(entity) ::= <<
read(<entity.fileDescriptor.export()>, <entity.dataInto.export()>)<if(entity.atEndBloc)>.atEnd(()-\<{
<entity.atEndBloc.children:statement()>
})<endif><if(entity.notAtEndBloc)>.notAtEnd(()-\<{
<entity.notAtEndBloc.children:statement()>
})<endif>;
>>

// ==================== 数据结构 ====================

// 变量声明
structure(entity) ::= <<
Var <entity.formattedName> = declare.level(<entity.level>)<if(entity.redefines)>.redefines(<entity.redefines.export()>)<endif><if(entity.occurs)>.occurs(<entity.occurs.export()>)<endif>.<entity.type>(<entity.length><if(entity.decimals)>, <entity.decimals><endif>)<if(entity.comp3)>.comp3()<endif><if(entity.value)>.value(<entity.value.export()>)<endif>.var();
>>
```

#### 4.2 expressions.stg 表达式模板

```stg
group expressions;
import "../base.stg";

// 条件表达式
condOr(left, right) ::= "(<left.export>() || <right.export()>)"
condAnd(left, right) ::= "(<left.export>() && <right.export()>)"
condNot(expr) ::= "!<expr.export()>"
condCompare(left, op, right) ::= "<left.export()> <op> <right.export()>"
condEquals(left, right) ::= "<left.export()>.equals(<right.export()>)"

// 算术表达式
exprSum(left, right, type) ::= <<
<if(type == "ADD")>add(<left.export()>, <right.export()>)<else>subtract(<left.export()>, <right.export()>)<endif>
>>
exprProd(left, right, type) ::= <<
<if(type == "MULTIPLY")>multiply(<left.export()>, <right.export()>)<else>divide(<left.export()>, <right.export()>)<endif>
>>
```

#### 4.3 任务清单
- [ ] 创建 `java.stg` 基础结构
- [ ] 实现控制流模板 (condition, loop)
- [ ] 实现数据操作模板 (assign, addTo)
- [ ] 实现文件操作模板 (readFile, writeFile)
- [ ] 创建 `expressions.stg`
- [ ] 创建 `sql.stg`
- [ ] 创建 `cics.stg`

---

### Phase 5: 验证与清理 (预计 2-3 天)

#### 5.1 输出一致性验证

```bash
# 1. 生成测试用例的原始输出
./gradlew :naca-trans:run --args="-InputDir=test/cobol -OutputDir=test/output-original"

# 2. 使用ST4模板生成输出
./gradlew :naca-trans:run --args="-InputDir=test/cobol -OutputDir=test/output-st4"

# 3. 对比输出
diff -r test/output-original test/output-st4
```

#### 5.2 性能验证

```java
// 基准测试
@Test
void benchmarkTemplateVsStringConcat() {
    long start = System.nanoTime();
    // 原始字符串拼接
    for (int i = 0; i < 10000; i++) {
        originalAssign.DoExport();
    }
    long concatTime = System.nanoTime() - start;
    
    start = System.nanoTime();
    // ST4模板
    for (int i = 0; i < 10000; i++) {
        st4Assign.DoExport();
    }
    long templateTime = System.nanoTime() - start;
    
    assertTrue(templateTime < concatTime * 1.5, "模板不应慢于50%以上");
}
```

#### 5.3 清理工作

- [ ] 移除旧的字符串拼接代码
- [ ] 删除或标记废弃的原 Controller 类
- [ ] 更新 Factory 类使用新的 ST 类
- [ ] 文档更新

---

## 三、风险与缓解

| 风险 | 影响 | 缓解措施 |
|---|---|---|
| 输出不一致 | 高 | 逐类迁移，每类验证后提交 |
| 性能下降 | 中 | ST4 编译模板缓存，基准测试 |
| 嵌套模板复杂 | 中 | 保持模板简单，逻辑在实体 getter 中 |
| 依赖 ST4 特性 | 低 | ST4 成熟稳定，无已知重大问题 |

---

## 四、迁移顺序总览

```
Phase 1: 基础设施
    └─ 创建模板目录、TemplateLoader、基础模板
    
Phase 2: 实体改造
    ├─ CBaseLanguageEntity (基类)
    ├─ CDataEntity (数据基类)
    ├─ 控制流实体 (Condition, Loop)
    ├─ 数据操作实体 (Assign, AddTo)
    └─ 文件/表达式实体
    
Phase 3: Controller 重构
    ├─ CJavaBlocST (概念验证)
    ├─ CJavaConditionST
    ├─ CJavaAssignST
    └─ 逐类迁移...
    
Phase 4: 模板开发
    ├─ java.stg (核心)
    ├─ expressions.stg
    ├─ sql.stg
    └─ cics.stg
    
Phase 5: 验证清理
    ├─ 输出对比验证
    ├─ 性能基准测试
    └─ 代码清理
```

---

## 五、检查点

每个阶段完成后检查:

### Phase 1 完成标准
- [ ] 模板目录结构创建
- [ ] TemplateLoader 可正常加载模板
- [ ] 基础模板 base.stg 可渲染

### Phase 2 完成标准
- [ ] 所有目标实体类有 getter
- [ ] 编译无错误
- [ ] getter 返回正确的数据类型

### Phase 3 完成标准
- [ ] 至少 3 个 Controller 类重构完成
- [ ] 模板渲染输出与原实现一致
- [ ] 单元测试通过

### Phase 4 完成标准
- [ ] 核心模板完成 (java.stg, expressions.stg)
- [ ] 所有已重构 Controller 有对应模板
- [ ] 模板可正确处理嵌套

### Phase 5 完成标准
- [ ] 输出一致性验证通过
- [ ] 性能无明显下降
- [ ] 代码清理完成