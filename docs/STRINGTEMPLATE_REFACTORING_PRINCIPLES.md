# StringTemplate4 (ST4) 代码重构设计原则

> **重要**: 每次涉及代码重写时，都必须返回检查是否符合本设计原则。

---

## 一、核心架构：ST4 PUSH 模型

### 1.1 三层分离

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Model层 (semantic/ 包)                                    │
│    纯数据容器 - 只持有数据，无任何输出逻辑                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│    • CEntity* 类持有语义分析结果                                              │
│    • 只提供 getter 暴露数据                                                   │
│    • 不包含任何字符串拼接或输出代码                                            │
│    • ExportReference() 返回可模板化的数据对象，而非字符串                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ template.add("entity", this)
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Controller层 (generate/java/ 包)                          │
│    纯赋值 - 只推送数据，不做任何逻辑                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│    • DoExport() 只做一件事：推送实体到模板                                     │
│    • 不做任何条件判断、字符串拼接                                              │
│    • 不调用子模板（由模板本身处理嵌套）                                         │
│    • WriteLine() 通过模板基础设施处理                                         │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ ST4 模板引擎渲染
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    View层 (templates/ 目录)                                   │
│    完整渲染逻辑 - 处理所有条件、格式化、嵌套                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│    • 使用 <if(attr)> 处理 null 检查                                          │
│    • 模板嵌套模板处理子实体                                                    │
│    • 按实体类型分发到具体模板                                                  │
│    • 处理所有输出格式化（缩进、分隔符、括号等）                                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 关键原则

| 原则 | Controller (Java) | Template (ST4) |
|------|-------------------|----------------|
| **条件判断** | ❌ 不做 | ✅ `<if(attr)>` |
| **null检查** | ❌ 不做 | ✅ `<if(optionalValue)>` |
| **字符串拼接** | ❌ 不做 | ✅ `<a>, <b>` |
| **循环处理** | ❌ 不做 | ✅ `<list:item()>` |
| **类型分发** | ❌ 不做 | ✅ `<if(type=="X")>` |
| **子实体渲染** | ❌ 不调用子模板 | ✅ 模板嵌套模板 |
| **格式化** | ❌ 不做 | ✅ 分隔符、缩进 |

---

## 二、正确的 vs 错误的实现

### 2.1 ❌ 错误的实现

```java
// 错误: Controller做逻辑，调用子模板
protected void DoExport() {
    String templateName = m_bFillAll ? "moveAll" : "move";  // ❌ 逻辑在Controller
    ST template = TemplateLoader.getTemplate(templateName);
    template.add("values", exportValues());    // ❌ exportValues()里还在做逻辑
    template.add("destinations", exportDests());
    WriteLine(template.render());
}

private String exportValues() {
    StringBuilder sb = new StringBuilder();    // ❌ 字符串拼接在Controller
    for (CDataEntity val : m_arrValues) {
        sb.append(val.ExportReference(getLine()));
    }
    return sb.toString();
}
```

### 2.2 ✅ 正确的实现

```java
// 正确: Controller只推送实体本身，模板处理一切
protected void DoExport() {
    ST template = TemplateLoader.getTemplate("assign");  // 单一模板
    template.add("entity", this);  // ✅ 直接推送整个实体
    m_output.WriteLine(template.render());
}

// Model层: 只提供数据访问
public class CEntityAssign extends CBaseActionEntity {
    private CDataEntity m_Value;
    private List<CDataEntity> m_destinations = new ArrayList<>();
    private boolean m_fillAll;
    
    // 只有getter，没有任何输出逻辑
    public CDataEntity getValue() { return m_Value; }
    public List<CDataEntity> getDestinations() { return m_destinations; }
    public boolean isFillAll() { return m_fillAll; }
}
```

```stg
// 模板: 处理所有逻辑
assign(entity) ::= <<
<if(entity.fillAll)>moveAll(<else><if(entity.moveCorresponding)>moveCorresponding(<else>move(<endif><endif><entity.value.export()>, <entity.destinations:{d | <d.export()>}; separator=", ">);
>>
```

---

## 三、实体设计规范

### 3.1 实体作为数据容器

```java
// ✅ 正确: 实体只持有数据
public abstract class CEntityCondition extends CBaseActionEntity {
    protected CBaseEntityCondition m_Condition;  // 条件表达式实体
    protected CEntityBloc m_ThenBloc;            // THEN块实体
    protected CEntityBloc m_ElseBloc;            // ELSE块实体 (nullable)
    
    // 只有getter，没有输出代码
    public CBaseEntityCondition getCondition() { return m_Condition; }
    public CEntityBloc getThenBloc() { return m_ThenBloc; }
    public CEntityBloc getElseBloc() { return m_ElseBloc; }
    public boolean hasElseBloc() { return m_ElseBloc != null; }
}
```

### 3.2 ExportReference 重构方向

```java
// 当前问题: ExportReference() 返回字符串
public abstract String ExportReference(int nLine);

// 重构方向: ExportReference() 返回实体本身
// 方案1: 实体自带格式化信息
public CDataEntity getSelf() { return this; }
public String getFormattedName() { return FormatIdentifier(m_Name); }
public CDataEntity getOfQualifier() { return m_Of; }

// 方案2: 模板调用实体的属性
// 模板: <if(entity.ofQualifier)><entity.ofQualifier.formattedName>.<endif><entity.formattedName>
```

### 3.3 使用 Lombok 简化 Getter

```java
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CEntityCondition extends CBaseActionEntity {
    private CBaseEntityCondition condition;
    private CEntityBloc thenBloc;
    private CEntityBloc elseBloc;
    
    // Lombok 自动生成 getCondition(), setCondition(), 等
}
```

---

## 四、模板组织结构

### 4.1 目录结构

```
templates/
├── base.stg              # 基础模板 (共享工具)
├── java/
│   ├── java.stg          # Java核心结构 (class, method, block)
│   ├── verbs.stg         # COBOL动词 → Java方法
│   ├── expressions.stg   # 表达式模板
│   ├── data.stg          # 数据结构模板
│   ├── control.stg       # 控制流模板 (if, loop)
│   ├── sql.stg           # SQL语句模板
│   ├── cics.stg          # CICS命令模板
│   └── forms.stg         # 表单字段模板
└── fpac/
    └── fpac.stg          # FPac方言覆盖 (import java/)
```

### 4.2 模板继承

```stg
// base.stg
group base;

// 通用语句分发
statement(entity) ::= <<
<if(entity.type == "condition")><condition(entity)><endif>
<if(entity.type == "assign")><assign(entity)><endif>
<if(entity.type == "loop")><loop(entity)><endif>
>>

// 通用数据引用
dataRef(entity) ::= <<
<if(entity.ofQualifier)><entity.ofQualifier.formattedName>.<endif><entity.formattedName>
>>
```

```stg
// java/java.stg
group java;
import "../base.stg";

// 条件语句
condition(entity) ::= <<
if (<entity.condition.export()>)
{
<entity.thenBloc.children:statement()>
}<if(entity.elseBloc)>
else
{
<entity.elseBloc.children:statement()>
}<endif>
>>

// 赋值语句
assign(entity) ::= <<
<if(entity.moveCorresponding)>moveCorresponding(<else><if(entity.fillAll)>moveAll(<else>move(<endif><endif><entity.value.export()>, <entity.destinations:{d | <d.export()>}; separator=", ">);
>>
```

---

## 五、检查清单

每次代码重写前，检查是否符合以下原则：

### 5.1 Model层检查
- [ ] 实体类只持有数据，不包含输出逻辑？
- [ ] 所有字段都有 getter（可用 Lombok）？
- [ ] 没有字符串拼接代码？
- [ ] ExportReference() 返回可模板化的对象？

### 5.2 Controller层检查
- [ ] DoExport() 只做 `template.add("entity", this)`？
- [ ] 没有条件判断逻辑？
- [ ] 没有调用子模板？
- [ ] WriteLine() 通过模板基础设施处理？

### 5.3 Template层检查
- [ ] 使用 `<if(attr)>` 处理 null 检查？
- [ ] 模板处理所有条件判断？
- [ ] 子实体通过模板嵌套处理？
- [ ] 没有在模板中做值比较（只有 presence 检查）？

---

## 六、重构顺序

### Phase 1: 基础设施
1. 添加 ST4 依赖
2. 创建 TemplateLoader 基础设施
3. 创建 templates 目录结构

### Phase 2: 实体改造
1. 为所有 CEntity* 类添加 getter（或使用 Lombok）
2. 重构 ExportReference() 返回可模板化的数据

### Phase 3: Controller 瘦化
1. 逐类重构 DoExport() 为纯赋值
2. 移除所有字符串拼接

### Phase 4: 模板开发
1. 创建基础模板 (base.stg)
2. 按类别创建具体模板
3. 测试模板渲染结果

### Phase 5: 清理
1. 移除旧的字符串拼接代码
2. 验证输出一致性
3. 性能优化

---

## 七、参考资源

- ST4 官方文档: https://github.com/antlr/stringtemplate4
- ST4 论文: [Enforcing Strict Model-View Separation in Template Engines](https://www.cs.usfca.edu/~parrt/papers/mvc.templates.pdf)
- 项目技能文档: `.opencode/skills/stringtemplate4-codegen/SKILL.md`