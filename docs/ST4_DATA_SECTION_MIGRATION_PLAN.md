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

首个切片只做 DS-1 与 DS-2 的最小闭环：清除 `CEntityAttribute` 的预渲染 Java 字符串，引入 declaration role binding，并让 assembler 构造 attribute declaration ST。现已完成（提交 `c624b86`）：5 个声明测试通过 assembler 运行，3 个基础变体与 direct generator 等价，edited picture 转义和同实体双角色分发也已覆盖。

第二个切片（提交 `b7597bd`，`feat: template data sections and structure declarations`）完成 DS-4/DS-5 的最小纵向闭环，但尚未接入生产 root：

- assembler 为每个 ST 节点记录 render role（`Collections.synchronizedMap(new WeakHashMap<>())`，key 为每次渲染独有的 ST 实例，共享 assembler 下线程安全且不跨渲染污染）。
- DECLARATION role 只沿 `children`/`activeChildren` 传播；`value`、`redefines`、`occurs`、`depending-on` 等普通实体属性一律回到 REFERENCE（`childRole` 对其它属性名返回 REFERENCE）。整棵递归树仍只有 `JavaTemplateAssembler.renderRoot` 一个 `.render()` 扁平化点。
- `CEntityStructure` 新增目标无关属性：`numericLevel`、`typed`、`variableLength`、`signLeadingSeparated`/`signTrailingSeparated`、`insideExternalDataStructure`/`insideFileSection`（后两者由 `CJavaStructure` 上移，均为父链语义判定，无 Java 标点）。
- `CEntityDataSection` 新增 section-kind 布尔属性：`workingStorageSection`/`linkageSection`/`fileSection`/`variableSection`。
- 声明 binding 新增 `CEntityStructure → dataStructureDeclaration`、`CEntityDataSection → dataSectionDeclaration`；`java.stg` 增加对应模板（FileSection/external 走 `children`，其余走 `activeChildren`）。
- `DataSectionDeclarationTemplateTest` 2/2 与 direct generator golden 等价：group 声明、以及 FileSection → structure → attribute 三层声明角色传播。

门禁：`:naca-trans:build` 成功；`finalArchitectureCheck` 401 项/222 失败（与基线持平，未增）；`:naca-cloud-native:test` 23 项/1 个预存失败（`TranspileControllerTest.testTranspileValidCobolWithWorkingStorage`，数据段命名/子字段债务，未隐藏）。

下一个切片按 `ST4_CLASS_BY_CLASS_AUDIT.md` 的明确顺序推进：先补声明树叶子的 `CEntityNamedCondition`（level 88），暴露 values/intervals 的目标无关只读模型 + declaration binding + STG 模板，value/interval 端点按 REFERENCE 渲染，并与 `CJavaNamedCondition` direct 输出做 golden 等价。随后覆盖 structure 变体（REDEFINES/OCCURS/COMP/sign/VALUE/FILLER/SYNC/JUSTIFIED/BLANK WHEN ZERO），再修复 `SetJustifiedRight` 自赋值与 direct generator 修改 semantic tree 的旧副作用。生产 root 接入与 direct generator 删除仍须在 golden、javac、运行、架构门禁全过后进行。

第三个切片（提交 `b48c69b`，`feat: template level-88 named condition declarations`）完成 DS-4 叶子类型 `CEntityNamedCondition`：

- `CEntityNamedCondition` 新增目标无关只读模型：`getValues()` 返回 `List<CDataEntity>`，内部 `IntervalModel`（`getStart()`/`getEnd()`）成对暴露 start/end 端点，`getIntervals()` 返回 `List<IntervalModel>`。全部为 COBOL 语义，无 Java 标点、无 `Export*` 调用。
- 声明 binding 新增 `CEntityNamedCondition → dataNamedConditionDeclaration`；`java.stg` 增加模板 `Cond <name> = declare.condition().value(...).value(start, end).var() ;`，value 与 interval 端点均按 REFERENCE 角色渲染（属性名非 `children`/`activeChildren`，`childRole` 回落 REFERENCE）。
- `DataSectionDeclarationTemplateTest` 新增 level-88 golden 等价测试（2 个单值 + 1 个区间），与 `CJavaNamedCondition` direct 输出归一化等价。
- ST4 踩坑：匿名子模板迭代变量不能用单字母 `i`（`Formal argument i already exists` 解析失败，导致该模板及其后模板整体不加载、`getInstanceOf` 返回 null）；改用 `val`/`interval` 命名。

门禁：`:naca-trans:build` 成功；`finalArchitectureCheck` 401 项/222 失败（持平，未增）；`:naca-cloud-native:test` 23 项/1 预存失败（未隐藏）。

下一切片：structure 变体（REDEFINES/OCCURS/OCCURS DEPENDING ON/variable length/typed group/COMP/sign/VALUE/FILLER/SYNC/JUSTIFIED RIGHT/BLANK WHEN ZERO）逐一补模板测试与 direct parity，并修复 `SetJustifiedRight` 自赋值、direct generator 修改 semantic tree 两处旧副作用。

## 7. 后续切片进展（2026-07-20，已提交）

### 旧副作用修复（提交 `e455d8f`）

- `SetJustifiedRight` 自赋值 bug 修复：原 `bJustifiedRight = bJustifiedRight` 从不写入字段，`isJustifiedRight()` 恒 false；改为写入 `isjustifiedRight`。无样例/测试使用 JUSTIFIED RIGHT，零回归。
- direct generator 在 export 阶段修改 semantic tree 的两处副作用已消除：BLANK WHEN ZERO 原就地把 `type`/`format`（`pic9`→`pic`+`999.99`）改写；variable-length OCCURS 原就地把 `length` 上乘 tableSize。现改为 `CEntityAttribute` 目标无关派生只读 getter（`getDeclaredType`/`getDeclaredFormat`/`isBlankWhenZeroEditedNumeric`/`isDeclaredEditedPicture`/`isDeclaredPictureSizeSpecified`/`getDeclaredLength`），`CEntityStructure` 覆写 `getDeclaredLength`。direct generator 与 ST4 模板同消费派生值，export 幂等、生成前后对象图不变；golden 测试断言 `getType()=="pic9"`、`getLength()==3` 在 export 后不变。
- 模板类型子句改用 `declaredType`/`declaredFormat`/`declaredLength`。

### structure 变体 golden 覆盖（提交 `7215620`）

已有 direct-parity golden 测试：group、FileSection 三层角色传播、level-88 named condition（values + interval）、BLANK WHEN ZERO（pic9→edited）、variable-length OCCURS、REDEFINES、OCCURS、OCCURS DEPENDING ON、COMP-3。

### 仍待补

- FILLER（需设计决策，尚未改）：
  - 根因查清：`GetDefaultName()` 经 `CObjectCatalog.GetLastFillerIndex()` **自增全局 filler 计数器**，是有副作用的，因此命名必须在语义分析期一次性完成，不能在生成期反复调用。
  - **group filler（`CEntityStructure`）已合规**：构造器在 `name==""` 时即 `SetName(GetDefaultName())`，export 期的改名是死代码；模板 `formattedName` 与 direct 已一致。已补 golden 测试 `rendersAGroupFillerLikeTheDirectGenerator`（`declare.level(1).filler() ;`，且断言名字在构造期已赋值、export 后不变）。
  - **pic filler（`CEntityAttribute`）仍不合规**：构造器不命名 filler，命名发生在 `CJavaAttribute.DoExport` 的 `SetName(GetDefaultName())`（export 期 semantic 改名）。正确修法是把命名移到 `CEntityAttribute` 构造期（与 structure 一致）。
  - **副作用/风险**：当前 BATCH1 因「structure-filler 构造期命名 + attribute-filler export 期命名」的混合时序，源码在前的 `05 FILLER PIC X(68)`（行35）被命名为 `filler$2`、源码在后的 `01 FILLER REDEFINES`（行52）反而是 `filler$1`（见 `NacaSamples/src/batch/BATCH1.java`）。把 attribute-filler 命名移到构造期会使二者按源码顺序改为 `filler$1`/`filler$2`（互换）。FILLER 不被按名引用，互换语义无害且更正确，但**会改变 BATCH1 生成输出**；现有测试（`CopyTranspileTest` 只要求编译通过、`RunnerServiceTest` 宽松）不会捕获，也没有 BATCH1 全量 golden 基线。按「BATCH1 不回归」原则，此改动需先建立 BATCH1 golden 基线或经明确确认后再做，故本轮不擅改。
- SYNC、JUSTIFIED RIGHT、VALUE/VALUE ALL、SPACE/ZERO/LOW/HIGH、sign leading/trailing、COMP/COMP-2 模板分支已存在，需逐一补 golden。
- 步骤 4（fail-closed 审计真实 DATA SECTION 子类型：`CEntityFileDescriptor`/`CEntityExternalDataStructure`/COPY-include）与步骤 5（class root 接入唯一 assembler、删除 direct generator）尚未开始。

门禁（每次提交均满足）：`:naca-trans:build` 成功；`finalArchitectureCheck` 401/222（持平）；`:naca-cloud-native:test` 23/1 预存失败（未隐藏）。
