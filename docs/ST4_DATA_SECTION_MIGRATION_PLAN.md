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

- FILLER（**已完成**，Step 1，提交 `fix: assign attribute FILLER names during semantic construction`）：
  - 根因：`GetDefaultName()` 经 `CObjectCatalog.GetLastFillerIndex()` **自增全局 filler 计数器**，有副作用，命名必须在语义分析期一次性完成。
  - 修复：filler 检测与默认命名统一上移到 `CEntityAttribute` 构造器（`name==""` → `isfiller=true` + `SetName(GetDefaultName())`），`CEntityStructure` 删除自己的 `isfiller` 字段/`isFiller()` 覆写/构造器命名块（继承父类），`CJavaAttribute.DoExport` 删除 `SetName(GetDefaultName())` 改名副作用、改读 `isFiller()`。`isFiller()` 由恒 false 改为返回字段。生成期不再改 semantic tree，渲染幂等。
  - **BATCH1 实测零变化**（比预期更好）：原预期「源码靠前的 attribute filler→`Filler$1`、group filler→`Filler$2`」互换**没有发生**。原因是 filler 编号跟随**构造/输出的规范段序**（Working-Storage 先于 File Section 构造），而非源码行序——BATCH1 中 group filler（`01 FILLER REDEFINES SYS-TIME`，working-storage，源码行52）先构造得 `Filler$1`，attribute filler（`05 FILLER PIC X(68)`，file section，源码行35）后构造得 `Filler$2`。修复前后编号一致，故 BATCH1 **逐字节不变**（`Batch1FillerGoldenDiffTest` 断言 `actual == golden`，且 BATCH1+Msgzone 仍 javac 通过）。这是「BATCH1 不回归」的最强形式。
  - 测试：`attributeFillerIsNamedDuringConstruction`（构造期命名 + `.filler()` + direct parity）、`attributeFillerNamingIsIdempotentAndDoesNotMutateTheTree`（两次渲染相同、export 后名字不变）、`Batch1FillerGoldenDiffTest`（BATCH1 零变化 + javac）。
- SYNC、JUSTIFIED RIGHT、VALUE/VALUE ALL、SPACE/ZERO/LOW/HIGH、sign leading/trailing、COMP/COMP-2 模板分支已存在，golden 已补齐（提交 `252fac7`）。
- 步骤 5（class root 接入唯一 assembler、删除 direct generator）尚未开始。

## 8. 步骤 4 完成：真实 DATA SECTION 子类型 fail-closed 审计

新增按需专项任务 `./gradlew :naca-trans:dataSectionAudit`（tag `data-section-audit`，已从默认 `:naca-trans:test` 排除，沿用 `finalArchitectureCheck` 的门禁分离模式）。`DataSectionSubtypeAuditTest` 用真实管线（lex→parse→`DoSemanticAnalysis`）解析 T01/TESTHELLO/VERBS/INSPECT1/TEST-A-STANDALONE，递归遍历 DATA DIVISION，对每个 concrete 实体以 DECLARATION role 探测 assembler（缺绑定即 `MissingTemplateRendererException` fail-closed，无静默回退）。

实测结果（独立样例）：

| 实际子类型 | 声明绑定 |
| --- | --- |
| `generate.java.CJavaAttribute` | COVERED（`dataAttributeDeclaration`） |
| `generate.java.CJavaStructure` | COVERED（`dataStructureDeclaration`） |
| `generate.java.CJavaDataSection` | COVERED（`dataSectionDeclaration`） |
| `generate.java.SQL.CJavaSQLCursorSection` | COVERED（父链解析到 `dataSectionDeclaration`；SQL 出本专项范围） |

**4 类，0 缺失**。即独立（非文件/非 COPY）样例的数据段所需绑定已齐备，步骤 5 对这些样例可行。BATCH1 含文件与 COPY，会引入 `CEntityFileDescriptor`/`CEntityExternalDataStructure`（当前无声明绑定），接入生产前须先补这两类的 declaration binding + 模板（fail-closed 会明确报缺失）。level-88 `CEntityNamedCondition` 绑定已就绪，但上述独立样例未含 level 88，故未出现在清单中。

### 全段渲染 golden（步骤 5 去风险）

`DataSectionRenderParityTest`（同 `data-section-audit` tag）更进一步：用真实管线解析 TESTHELLO/T01，对每个已知类型数据段把整棵子树指向独立 mock，先跑 direct `CJavaDataSection.DoExport`（按生产顺序赋 filler 名）再跑 assembler `renderRoot(DECLARATION)`，断言**逐 token 等价**。结果：TESTHELLO/T01 的 WorkingStorageSection（含 group + 多个 level-05 子项、VALUE 字面量）经递归 assembler 渲染与 direct 输出 token 完全一致。

唯一差异是空白：递归 ST4 输出紧凑（`<<...>>` 去掉尾换行，兄弟声明间无分隔），legacy direct 经 WriteWord 输出协议带换行/空格。二者 token 流相同，javac 不关心空白；逐声明的精确格式已由 `DataAttribute/DataSectionDeclarationTemplateTest` 锁定，故全段测试用「去空白后比较」验证组合正确性。接入生产 root 时若需可读排版，可给 children 列表加 `separator="\n"`，属可选美化，不影响正确性。

门禁（每次提交均满足）：`:naca-trans:build` 成功；`finalArchitectureCheck` 401/222（持平）；`:naca-cloud-native:test` 23/1 预存失败（未隐藏）。

## 9. 与 root 契约推进的衔接（Step 0–3，详见 `ST4_CLASS_BY_CLASS_AUDIT.md`）

数据段专项与「root 契约」推进合流：Step 0 冻结 BATCH1 golden，Step 1 修 attribute FILLER 构造期命名（BATCH1 零变化），Step 2 建立显式 ROOT role 绑定（`semantic-root-bindings.properties` + `bindingsFor(ROOT)` 不回落 default + `javaProgramRoot` 占位），Step 3 暴露目标无关 program-root 语义（`ProgramKind`/`ProgramCapability`/`declarationChildren`/`executableChildren` + 显式 child role 传播 + 快照测试）。

**无参 `renderRoot(model)` 删除点（硬约束）**：当前无参 `renderRoot` 默认 `REFERENCE` 是过渡兼容（保 11 个 procedure/verb 控制器不变）。**最迟于 Step 4（class-root 模板）完成前、且严格在添加 external/COPY root binding 之前**，必须：先把 11 个生产控制器改为显式 `renderRoot(this, REFERENCE)`，再删除无参 overload（或把无参默认恢复为 `ROOT`）。否则 `CEntityExternalDataStructure` 同时拥有 REFERENCE（copybook 标识符）与 ROOT（完整 Copy 类）两个 binding 后，artifact writer 忘记显式传 ROOT 会静默生成标识符而非 fail-closed。守卫测试 `programArtifactCannotBeGeneratedWithoutExplicitRootRole` 已锁定该 fail-closed 行为。

## 10. Step 5a/5b/5c + Step 6 完成（2026-07-21/22，详见 `ST4_CLASS_BY_CLASS_AUDIT.md`）

上文第 8/9 节列出的接入前置已全部完成：

- **5a**：`CEntityFileDescriptor` declaration binding + `dataFileDescriptorDeclaration` 模板（`declare.file(<fileRef> | "<displayName>" 回退)` + 可选 `.status(...)` + `activeChildren`）；修 `setFileStatus` 自赋值。
- **5b**：`CEntityExternalDataStructure`/`CEntityInline` 三角色（REFERENCE=标识符引用 / DECLARATION=`Type ref = Type.Copy(this)` 实例声明 / ROOT=`javaCopyClass` 独立 Copy 类）；修 `SetInline` 自赋值（仅内置 HEXZONE，门禁零回归）。
- **5c**：`Batch1AssembledRootParityTest` 证明 BATCH1 整文件 root（含 file section/FD + COPY MSGZONE 实例声明 + procedure）经 assembler `renderRoot(ROOT)` 与 frozen golden 逐 token 等价。fail-closed 暴露并 closure 两缺口：`unknownReferenceEntity`（未解析引用渲染为空）、`getFileName()` 对 unknown 文件名回退到带引号显示名。
- **Step 4.6**：删除死代码 `CJavaClassST`，无参 `renderRoot` overload 已删（11 控制器 + 全部测试调用点显式传 role）。
- **Step 6**：`TranspilerService` 生产出口由 direct `StartExport()` 改为 `renderRoot(root, ROOT)`；`Batch1FillerGoldenDiffTest` 由逐字节改为逐 token 对比 frozen golden，仍锁定 `Filler$1`/`Filler$2`。

门禁：`:naca-trans:build`/`test` 成功；`finalArchitectureCheck` **402/222**（失败持平）；`:naca-cloud-native:test` **28/1** 预存失败（未隐藏）。

**最终完成前的剩余债务**（生产出口已接 assembler，但下列未竟）：
1. copybook 类生成（`IncludeGroupSupport.generateCopybookClass`）仍走 direct 重导出，应迁到 assembler ROOT（`javaCopyClass`）。
2. CICS/SQL/BMS/FPac 等构造尚未迁入 assembler：使用这些构造的程序经生产出口会 fail-closed（按设计，优于静默错误），需逐一迁移后才能消除。
3. direct `CJava*` 生成器（约 171 个 direct backend 类）仍作为 parity 测试参照与 copybook 生成保留；retiring 这些 parity 测试对 direct 的依赖后删除生成器大头，是 `finalArchitectureCheck` 全绿前的最后清理。
