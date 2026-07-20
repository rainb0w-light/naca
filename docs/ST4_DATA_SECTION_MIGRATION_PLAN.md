# ST4 数据段模板化专项计划

## 1. 专项目标

把 Java class skeleton 与 DATA DIVISION 的生产输出从 direct generator 迁到唯一的递归 ST4 assembler，同时保持 semantic tree 与目标语言无关。

完成后必须满足：

- `CEntityDataSection`、`CEntityStructure`、`CEntityAttribute`、`CEntityNamedCondition` 等只保存 COBOL 语义。
- Java 标识符、`Var`、`declare.level(...)`、fluent builder 方法、括号、引号、逗号和分号全部位于 STG 或原子 formatter。
- 同一个 `CEntityAttribute` 在过程语句中按“引用”渲染，在数据段中按“声明”渲染，不能依赖单一类型绑定碰运气分发。
- class root、data section、procedure section 形成一棵 ST instance tree，只在 root writer 调用一次 `render()`。
- 对应 direct data/class generator 只有在 golden、javac、运行和架构门禁都通过后才删除。

本专项不处理 COMP/COMP-3/COMP-5 的运行时存储语义差异，也不顺带迁移 CICS、SQL、BMS 或 FPac 的其他输出面。

## 2. 当前基线

- 动词与 procedure 子树已在生产路径中使用 recursive assembler。
- `CEntityAttribute` 的声明模板只有 3 个隔离测试，尚未由 data section 或 class root 调用。
- `CEntityDataSection` 仍是 abstract semantic 节点，生产输出仍经过 `CJavaDataSection.DoExport()`。
- `CEntityAttribute` 的运行时绑定仍是 `dataReferenceEntity`，只适合引用语境。
- 原型中的 `getDeclareArgs()` 和 `getCompClause()` 已拆除；声明模板现只消费目标无关属性。
- 当前严格架构契约为 401 项、222 项失败；普通功能测试除最终架构契约外全绿。

## 3. 设计决定：声明与引用采用显式渲染角色

一个 semantic 类型可以出现在不同语法角色中。backend binding 必须显式携带 role，而不是把声明逻辑塞回 semantic getter，也不是为声明创建 Java semantic 子类。

第一阶段至少定义：

- `REFERENCE`：过程语句中的数据引用，沿用 `dataReferenceEntity`。
- `DECLARATION`：DATA DIVISION 中的声明，使用独立的声明 binding manifest。
- `ROOT`：class/data/procedure 顶层结构。

role 只决定查询哪一份声明式 binding；具体 Java 语法仍由 STG 决定。assembler 可以遍历 collection 并构造子 ST，但不能拼接源代码或根据 COBOL 类型硬编码 Java 语法。

## 4. 实施顺序

### DS-0：冻结基线和门禁

- 普通 `:naca-trans:test` 排除 `final-architecture` tag并保持全绿。
- 注册 `:naca-trans:finalArchitectureCheck`，只运行最终契约测试。
- 保存 T01、TEST-A、TESTHELLO、VERBS、INSPECT1、BATCH1 的生成 Java/golden 摘要；VERBS 和 INSPECT1 增加可重复的 GnuCOBOL 输出对比。

### DS-1：净化数据 semantic model

- 用 `compKind`/enum 或等价目标无关字段替代 `getCompClause()`。
- 暴露 `length`、`decimals`、`picture`、`level`、`valueKind`、`sync`、`justifiedRight`、`blankWhenZero` 等原始语义。
- 删除 `getDeclareArgs()` 及 semantic 中的 Java 引号、逗号和 fluent builder 片段。
- 为 semantic tree 增加无副作用快照测试。

### DS-2：建立 role-aware 声明式 binding

- 保留 reference manifest；新增 declaration/root manifest 或等价的分角色声明结构。
- assembler 递归包装子实体时传播明确 role。
- missing role binding 必须 fail closed。
- 保持整棵 ST tree 只有一个 flattening 点。

### DS-3：迁移叶子声明

- `CEntityAttribute`：picX、pic9、picS9、edited picture、COMP/COMP-2/COMP-3。
- VALUE：literal、SPACE/ZERO/LOW/HIGH、VALUE ALL。
- SYNC、JUSTIFIED RIGHT、BLANK WHEN ZERO、FILLER。
- 每个变体同时做 direct golden 与生成 Java 编译验证。

### DS-4：迁移复合数据结构

- `CEntityStructure` 与层级嵌套。
- REDEFINES、OCCURS、index、depending-on。
- `CEntityNamedCondition`（level 88）。
- external/copybook structure 与 file descriptor 相关声明。

### DS-5：接入生产 root

- `CEntityDataSection` 模板化 working-storage/linkage/file section。
- class skeleton 通过 root assembler 组合 data 与 procedure 子树。
- 禁止 class/data controller 逐节点 render 或回退 direct `DoExport()`。

### DS-6：删除过渡路径

- 删除已覆盖的 `CJavaAttribute`、`CJavaStructure`、`CJavaDataSection` 和 class/data direct 输出协议。
- concrete semantic factory 不再创建对应 Java 子类。
- 收紧 ratchet，并确认最终架构失败数只降不升。

## 5. 每个切片的验收标准

每次提交必须同时满足：

1. 模板单测覆盖当前声明变体。
2. direct/golden 输出在允许的空白归一化后等价。
3. 生成 Java 经 `javac` 编译。
4. T01、TEST-A、TESTHELLO、VERBS、INSPECT1、COPY/BATCH1 无回归。
5. `:naca-trans:build` 成功。
6. `:naca-cloud-native:test` 不增加现有失败。
7. `:naca-trans:finalArchitectureCheck` 的失败数下降或保持，绝不通过 allowlist 隐藏债务。

## 6. 首个开发切片

首个切片只做 DS-1 与 DS-2 的最小闭环：清除 `CEntityAttribute` 的预渲染 Java 字符串，引入 declaration role binding，并让 assembler 构造 attribute declaration ST。现已完成：5 个声明测试通过 assembler 运行，3 个基础变体与 direct generator 等价，edited picture 转义和同实体双角色分发也已覆盖。

下一个切片进入 DS-4/DS-5 的最小纵向闭环：先为 `CEntityStructure` 建立 declaration binding 和子声明 role 传播，再由 `CEntityDataSection` 遍历顶层数据实体。暂不删除 direct generator；先以 T01/VERBS 的 working-storage 输出做 golden，对齐后再接 class root。
