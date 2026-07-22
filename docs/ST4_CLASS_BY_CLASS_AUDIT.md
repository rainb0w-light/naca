# ST4 类级架构审计

> 文档职责：本文是当前工作树状态的唯一事实来源；最终完成标准见
> `ST4_FINAL_ARCHITECTURE_CONTRACT.md`，下一专项的执行步骤见
> `ST4_DATA_SECTION_MIGRATION_PLAN.md`。其余 ST4 计划文档仅保留历史记录。

审计日期：2026-07-19。

本报告记录的是迁移状态，不是完成声明。最终验收命令设计为：

```bash
./gradlew :naca-trans:finalArchitectureCheck
```

截至 2026-07-20，该 Gradle 任务尚未注册；当前只能用下面的命令执行同一契约测试：

```bash
./gradlew :naca-trans:test --tests architecture.FinalArchitectureContractTest
```

该测试不使用 allowlist、fallback 或“已知失败”豁免。它为范围内的每个 Java/STG 文件分别建立 JUnit dynamic test，因此 HTML 报告可直接展开到每一个类，而不是只给出总数：

`naca-trans/build/reports/tests/finalArchitectureCheck/index.html`

## 当前逐类结果

| 审计面 | 当前结果 | 最终要求 |
| --- | ---: | ---: |
| `semantic/**/*.java` | 202 个逐类检查；154 失败，48 暂时通过静态契约；BREAK/CONTINUE、NoAction、FileSelect、FileBuffer、ValueReference、FieldAttributeReference、忽略节点、Block、Condition、LoopWhile、LoopIter、SwitchCase、Case、AddTo 和 Inc 已成为目标无关 concrete 节点 | 202/202 通过 |
| `generate/templates/recursive/**/*.java` | 13 个逐类检查；属性格式化类仍在过渡层 | 13/13 通过，typed renderer 为 0 |
| `generate/java/**/*.java` | 176 个逐类检查；171 个类继承 semantic entity；23 个已迁移动词的 ST/direct 子类已删除 | 176/176 通过，semantic inheritance 为 0 |
| target-specific entity factory | 3 个逐类检查，3 个仍实例化 target-specific semantic 子类 | 3/3 通过 |
| `.stg` | 2 个逐文件检查，`base.stg`/`java.stg` 均仍消费预渲染属性或 export hook | 全部通过 |
| Java semantic-template manifest | concrete 主 manifest 已拆为 9 项；运行时继承别名独立放在 `semantic-runtime-bindings.properties` | 主 manifest 完整且与所有 concrete semantic entity 精确对应 |
| ST tree 扁平化 | root assembler 中 1 处 | 恰好 1 处 |

当前工作树实测严格契约共执行 **402 项检查，222 项失败、180 项通过**（最新口径见文末「2026-07-21/22 进展」条目；下表为 2026-07-19/20 快照，逐类明细随迁移推进）。失败构成仍约为 47 个 semantic 类、171 个 direct backend 类、3 个 factory 和 1 个 STG 文件，债务没有增加。此前文档中的 424/401 均为更早快照，已经失效。测试项数量会随被审计文件数自动变化（本轮删除死代码 `CJavaClassST` 使总数 403→402）。当前失败是重构债务的证据，不能通过调低阈值或增加白名单消除。

日常功能门禁与最终架构门禁必须分开：普通 `:naca-trans:test` 排除
`final-architecture` tag 并保持全绿；独立的 `finalArchitectureCheck` 只运行该 tag，
在迁移完成前允许红灯但不得增加失败数。当前 Gradle 配置尚未实现这一分离。

## 每个 semantic 类的判定规则

每个类单独应用以下规则，任意一条命中即为失败：

1. 不依赖 `generate.*`、ST4、`TemplateLoader`、exporter 或任何 Java/Go/Rust backend 类型。
2. 构造器、字段和方法参数不携带 exporter、writer、template 或 backend context。
3. 不声明或调用 `Export*`、`DoExport*`、writer formatting API。
4. 不提供 `codeString`、`childrenCode`、`referenceString`、`bodyCode` 等预渲染属性。
5. 不返回目标语言语句、表达式、标点片段、runtime API 名或目标类名。
6. 只保存源语言事实和目标无关语义结果；生成阶段需要的分支必须提前成为字段、enum 或 semantic child。
7. backend 生成前后对象图不变；同一对象图可依次交给 Java、Go、Rust backend。

前四项由当前静态门禁逐类强制执行。第五至第七项必须在迁移每个类时同时通过代码审查、对象图快照测试和双 backend traversal 测试；在通用 binder 建立后纳入统一自动门禁。

## 每个 backend 类和模板的判定规则

1. 不允许 backend 类继承 semantic entity。
2. 不允许一个 semantic 类型对应一个手写 Java renderer；控制流、表达式、引用、函数、列表、MOVE、ADD、AddTo、Inc、BREAK、CONTINUE、Case 和字符串 literal renderer 已删除，当前只剩 3 个 ST4 属性格式化类，以及 semantic/direct backend 契约债务待清理。
3. 通用 binder 只做 manifest 类型查找、属性读取、child/collection 递归包装，不得决定输出语法。
4. semantic concrete type 到模板名的映射只存在于 backend manifest；映射必须无缺失、无多余、模板必须真实存在。
5. Java 关键字、运算符、括号、分号、缩进、runtime method 选择全部位于 STG。
6. 模板不得调用 semantic 生成方法或读取预渲染字符串；异构 child 必须保持为嵌套 ST。
7. 只有 root writer 能调用一次 `render()`。

## 完成状态定义

- `[ ]`：尚无迁移行为覆盖。
- `[B]`：只有与旧 direct generator 的行为等价基线，仍属于过渡实现。
- `[F]`：相关 semantic 类、backend binding、STG 和 factory 均通过最终契约，旧 direct subclass 与 typed renderer 已删除。

不得用测试通过、模板存在或 typed renderer 已实现来单独宣称 `[F]`。当前 master plan 中原有的 `[x]` 已全部降级为 `[B]`。

## 严格迁移顺序

1. 清除 semantic 根基类中的 exporter/output protocol，使 semantic constructor 和对象树先目标无关。
2. 将 parser/factory 改为只构造 concrete semantic 类，停止构造 `CJava*`/`CFPacJava*` 子类。
3. 建立声明式 manifest 和唯一通用 recursive binder，并验证 concrete semantic type 的精确覆盖。
4. 按闭合语法切片把 typed renderer 的结构选择移入 STG；每迁移一片就删除对应 renderer，而不是再增加 renderer。
5. 删除同一切片的 direct semantic subclass；行为 parity 测试改为固定 golden/端到端基线。
6. 加入 semantic object graph 前后快照，以及同树多 backend traversal，证明生成无副作用且可复用。
7. 当且仅当 `finalArchitectureCheck` 全绿，再删除迁移期债务基线并把相应项目标为 `[F]`。

## 2026-07-19 进展：assembler 接入生产，控制流输出恢复

### 发现并修复的生产回归
先前未提交改动删除了全部结构 emitter（`CJavaBloc`/`CJavaCondition`/`CJavaLoopWhile`/`CJavaCase` 等 21 个文件），但 `JavaTemplateAssembler.renderRoot` 在生产代码中零调用，导致**凡不带显式 paragraph、或含 IF/PERFORM/EVALUATE 的程序，procedure 体输出为空**（实证：最小 IF 程序转译结果为 `public void procedureDivision() { }`）。各计划文档中「样例转译/运行通过」的记录是 2026-07-18 提交态的过时记录，已被未提交改动 regress；只有显式 paragraph 内的直线语句（如 T01、TESTHELLO 恰好是直线）仍能输出。

### 接通方式（P0）
- 新增三个过渡 ST 控制器 `CJavaProcedureDivisionST`/`CJavaProcedureSectionST`/`CJavaProcedureST`：`DoExport()` 调用 `TemplateLoader.getRecursiveAssembler().renderRoot(this)` 并逐行写出，把递归 ST 在 procedure division/section/paragraph 级接入生产导出路径。class 骨架与数据声明仍走既有 direct 路径。
- `java.stg` 递归区新增程序结构模板 `recursiveProcedureDivisionEntity`/`recursiveProcedureSectionEntity`/`recursiveProcedureEntity`/`recursiveReturnEntity`。
- 运行时绑定新增 `CEntityProcedureDivision`/`CEntityProcedureSection`/`CEntityProcedure`/`CEntityDisplay`/`CEntityReturn`（经父类链解析到 CJava* 运行实例）；DISPLAY 复用既有干净 `display` 模板。
- 新增目标无关语义布尔属性 `CEntityReturn.isStopProgram()`、`CEntityProcedureSection.isReducedToProcedure()`。
- `TemplateLoader.getRecursiveAssembler()` 共享单一 assembler（STGroup 可复用，避免重复解析 java.stg）。

### 附带修复
- `DeclareTypeNumEdited.value(String)` 缺失，导致 edited PIC（如 `9(4).99 VALUE 1234.56`）生成 `value("1234.56")` 触发 javac `value(String)` 无合适方法错误 → 已补该重载（与 `DeclareType9.value(String)` 同形）。
- `T01ExecutionTest` 测试顺序 bug（Step3 的 `@EnabledIf` 在 Step2 设值前求值 → Step3 恒被跳过）→ 加 `@TestMethodOrder`+`@Order` 修复；`T01TranspileTest` 的 `level(5)` 断言改为接受实际输出 `level(05)`。

### 验证
- 最小 IF 程序与 TESTHELLO：ST4 转译出非空且正确的 procedure 体（if/else、display、inc、stopRun），javac 通过。
- T01 端到端（transpile→compile→run）：7/7 通过，运行时 DISPLAY 输出正确（含 edited 小数 WS-DEC-3=1234.56）。剩余 WS-COMP3-3（`112.5-`）与带符号数显示差异属 COMP-3/signed 运行时语义的既有问题（见主计划 M8），与本次接通无关。
- `:naca-trans:test`、ratchet（`LegacyGenerationArchitectureTest`）、`TemplateValidationTest`、递归模板测试全绿。

### 债务变化
- `finalArchitectureCheck`：420 项 / 349 失败 → **423 项 / 352 失败**。新增的 3 项是三个过渡 ST 控制器命中 `everyDirectBackendClassAvoidsSemanticInheritance`（与既有约 33 个 `CJava*ST` 控制器同属过渡形态），将在根 assembler 就位后随全部 `CJava*ST` 控制器于 M14 一并删除。ratchet 仍绿（direct semantic subclass ≤ 209）。

### 下一步
- P1：把其余动词（READ/WRITE/COMPUTE/CALL/…）的遗留 ST 控制器（`java.stg:7-313`，`codeString`/`childrenCode`/`referenceString`）逐个迁为递归绑定 + 干净模板，扩大生产路径覆盖（当前含这些动词的程序会 fail-closed 报 missing binding，优于先前的静默空体）。
- P2：128 个 Bucket-A semantic 类批量解耦（构造器去 `CBaseLanguageExporter` 参数 + 后端子类用既有 `setLanguageExporter()` setter 注入），转绿契约检查。

## 2026-07-19 进展（续）：P1 动词迁移与 TEST-A 端到端验证

### Fail-closed 落地
ST4 默认 error listener 会把模板内异常（含 `MissingTemplateRendererException`）吞掉并渲染空串——正是本次要消灭的「静默空体」缺陷。新增 `RecursiveTemplateErrorListener`（仅注册在 assembler 自己的 STGroup，不影响 legacy PUSH 控制器所在共享 group），使缺绑定/缺属性立即失败。现在样例只有在全部节点都被覆盖时才能转译成功，缺失动词会显式报错（而不是静默输出空方法体）。

### 迁移配方（已验证）
一个动词的迁移只需：①写干净递归模板（子节点用裸引用，由 adaptor 递归渲染，不用 `codeString`/`referenceString`）；②在 `semantic-runtime-bindings.properties` 加一条 `semantic.Verbs.CEntityX=recursiveXEntity`。assembler 经父类链把 `CJavaXST` 实例解析到该绑定，legacy 控制器与 legacy 模板随之被旁路（M14 统一删除）。**无需改动 factory 或控制器。** 列表元素若是 DTO（如 `CalculationDestination`/`ConcatItem`）不被包装、可继续 `.property` 访问；若是 entity 则被包装为子 ST、只能整体引用。

### 已迁移动词（13 族）
P0：DISPLAY、STOP-RUN/RETURN/EXIT。
P1：PERFORM/PERFORM THRU/PERFORM TIMES（`CEntityCallFunction`）、SET（`CEntitySetConstant`）、COMPUTE（`CEntityCalcul`，含 ON SIZE ERROR）、DIVIDE（`CEntityDivide`）、STRING（`CEntityStringConcat`，含 ON OVERFLOW）、MULTIPLY（`CEntityMultiply`，补齐了原本缺失的全部 getter）、SUBTRACT（`CEntitySubtractTo`，含 `SUBTRACT 1`→`dec` / `-1`→`inc` 优化与 ON SIZE ERROR）、OPEN/READ/WRITE/CLOSE（`CEntityOpenFile`/`CEntityReadFile`/`CEntityWriteFile`/`CEntityCloseFile`，含 READ AT END/NOT AT END、WRITE FROM/AFTER，新增 `CEntityFileDescriptor` 绑定）、ACCEPT（`CEntityAccept`，FROM TIME/DATE/DAY/INPUT/ENVIRONMENT/VARIABLE）、CALL PROGRAM（`CEntityCallProgram`，USING/BY VALUE/CONTENT/LENGTH OF + ON EXCEPTION）。算术族（MOVE/ADD/SUBTRACT/MULTIPLY/DIVIDE/COMPUTE）与文件族（OPEN/READ/WRITE/CLOSE）已全部递归化并经合成样例 javac 验证。
新增目标无关 getter：`CEntityReturn.isStopProgram`、`CEntityProcedureSection.isReducedToProcedure`、`CEntityCallFunction.{isPerformThrough,getRepetitions,getRepetitionIndex,getPerformedProcedureName,getPerformedThroughName}`、`CEntitySetConstant.{getVariable,isSetToZero/Space/LowValue/HighValue,isSubString}`、`CEntityMultiply.{getValue,getBy,getTo,isRounded}`、`CEntitySubtractTo.{getVariable,getValues,getDestinations,hasDestinations,isDecrementByOne,isIncrementByOne,getOnErrorBloc}`。

### 附带修复的语义/管线缺陷（非模板）
- **引用登记缺失**：`WRITE ... FROM x`/`READ ... INTO x`/`ACCEPT x`（及 ACCEPT FROM VARIABLE 的源）此前不向数据实体登记读/写动作，导致这些变量被 `CDataEntity.ignore()` 判为未使用而**不声明**（生成代码 javac 报「找不到符号」）。已在 `CWrite`/`CRead`/`CAccept` 的语义分析中补 `RegisterReadingAction`/`RegisterWritingAction`（目标无关语义，非生成功能）。
- **simplified 管线 global catalog 为空**：cloud-native `TranspilerService` 以 `null` 构造 `CObjectCatalog`，致使任何 `CALL`（`CheckProgramReference`）与 `COPY`（`GetFormContainer`）NPE。已改为传入 `CGlobalCatalog(null,...)`，并在 `CGlobalCatalog` 全部 `transcoder.getGroup` 处加 null-safe（`getGroupSafe`）。CALL 现可按外部子程序转译；COPY 仍需 include group 基础设施（见下）。

### ST4 模板布尔属性的命名约束
ST4 属性 `entity.x` 仅解析 `getX()`/`isX()`/字段 `x`，**不解析 `hasX()`**。迁移模板中布尔判存在一律用 `getX()`（非空判断）或 `isX()`，避免 `hasX()`（本批已修正 `recursiveCallProgramEntity` 的 `hasOnErrorBloc`→`onErrorBloc`、`CEntitySetConstant.hasSubString`→`isSubString`）。

顺带暴露并修复一个潜伏 bug：`CEntityMultiply` 完全没有 getter，legacy `multiply` 模板引用的 `entity.value/destination/result` 全部落空、被默认 listener 静默渲染为空——即 ST4 路径下 MULTIPLY 一直被丢弃（同类问题可能存在于其他未迁移动词，fail-closed 后会逐一暴露）。

### TEST-A-STANDALONE 端到端验证（GnuCOBOL 对比）
- 转译：681 行 Java（此前结构断裂时仅约 107 行空壳）；javac 编译通过；经 cloud-native RunnerService 运行成功。
- GnuCOBOL 3.2.0 baseline（`cobc -x`）34 行；Java 输出 **31/34 行逐行一致**，与 Phase 6（2026-07-18 提交态）基线完全相同。
- 剩余 3 行差异全部是 COMP/COMP-3 二进制存储（`CS2BT-V`/`NS4BT-V`/`NS2BS-V`）的截断/端序运行时语义，属主计划 M8，与 ST4 结构无关。

### 当前 finalArchitectureCheck
424 项 / 352 失败（较 P0 前 420/349：+3 为过渡 ST 控制器，+1 为 `RecursiveTemplateErrorListener` 新文件通过 `everyRecursiveBackendClassContainsNoTypedRenderer`）。ratchet、TemplateValidationTest、递归模板测试全绿。

### 仍待迁移的动词（P1 续）
REWRITE、INITIALIZE、INSPECT、COUNT、SEARCH、SORT/RELEASE/RETURN、REPLACE、GO TO、NEXT SENTENCE、routine emulation 等——逐个按上述配方迁移并以样例 fail-closed 驱动。

### BATCH1 端到端的剩余阻塞
BATCH1 含 `COPY MSGZONE`，需要 include group 基础设施（copybook 解析）——即用户备忘所指「simplified 管线需替换为真实 CJavaExporter 管线」。当前 simplified 管线下 COPY 已不再 NPE（fail-safe），但仍找不到 copybook。BATCH1 端到端需先补齐 include group（配置 copybook 路径的 transcoder/group），属独立的管线工作，不在动词迁移配方范围内。BATCH1 其余动词（OPEN/READ/WRITE/CLOSE/MOVE/ADD/PERFORM/EVALUATE/DISPLAY/ACCEPT/CALL/STOP）均已迁移。

## 2026-07-20 进展：P1 动词全量迁移 + P2 Bucket-A 解耦

### P1：全部动词控制器迁移完成
继 P0/P1 已迁移的 13 族动词后，本轮补齐剩余全部动词的递归绑定 + 干净模板（含包装器模型）：
REWRITE、SORT RELEASE、SORT RETURN（仿 READ 的 AT END 结构）、COUNT（INSPECT TALLYING 链式 builder）、SEARCH（for 循环 + Search-Found）、UNSTRING（`UnstringDestination` 包装器）、REPLACE（`ReplaceItemModel`，注意 by-zero 是 `Zero` 单数）、SORT（`SortKeyModel` + 输入/输出 procedure 解析）、GO TO、INITIALIZE（含 REPLACING 变体）、INSPECT CONVERTING、EXEC、NEXT SENTENCE、routine emulation（含 `tools.dynamicAllocation` 特例）、AssignWithAccessor、IntrinsicFunction（`ExportReference`→`renderRoot`）。
- 新增目标无关 getter 与包装器；所有语句类型均有绑定，procedure 递归渲染不再因任何动词 fail-closed。
- `.render(` 控制器文件 39→2（仅 `CJavaClassST` 独立 helper 与 `CJavaReadFileST` 因单测 mock 契约保留 legacy DoExport，其生产绑定已生效）。ratchet `ST_CONTROLLER_RENDER_FILE_BASELINE` 收紧到 2。
- 验证（匹配 GnuCOBOL 3.2.0）：`VERBS.cbl`（16 类动词）、`INSPECT1.cbl`（CONVERTING）逐行一致；`INSPECT2`(TALLYING)/`UNSTRING1`/`REPLACE1` 忠实于直接生成器。
- **顺带发现的预存 bug（非迁移引入）**：`CInspect.java:322` 不支持纯 `TALLYING FOR CHARACTERS`；`CSearch.java:71` 非索引表 SEARCH 强转 `CEntityStructure` 崩溃；naca-rt 运行时 UNSTRING `.tallying()` 恒 0、INSPECT REPLACING `.leading()` 等同 `.all()`；`CEntityInitialize` 构造器 `data=data` 自赋值（已修复）。

### P2：Bucket-A 批量解耦完成（128 构造器/import 耦合类）
- **112→0** 个 semantic 文件显式 `import generate.CBaseLanguageExporter`。解耦配方：semantic 类构造器去 `CBaseLanguageExporter out/lexp` 参数 + 改调无 exporter 的 super 重载 + 删 import；后端子类（`CJava*`/`CFPacJava*`/CICS/SQL/forms）保留 exporter 参数，改 `super(...)` + `setLanguageExporter(out)` 注入；工厂不变（仍持 `langOutput` 并传给后端子类构造器）。
- **修复构造期耦合**：filler 命名计数器 `GetLastFillerIndex()` 原在 exporter 上，`CEntityStructure` 等在构造器体内调用 `GetDefaultName()` 时 exporter 尚未注入 → NPE。已将计数器迁至目标无关的 `CObjectCatalog`（`programCatalog` 在构造期已就位）。
- 仅剩 5 个根基类（`CBaseLanguageEntity`/`CDataEntity`/`CBaseActionEntity`/`CBaseExternalEntity`/`CBaseEntityFactory`）仍含 `CBaseLanguageExporter` token（持有 exporter 字段 + 输出协议 WriteLine/DoExport/Export），属更深的「输出协议剥离」，不在 Bucket-A（构造器/import 耦合）范畴。
- `finalArchitectureCheck`：352 失败 → **245 失败**（-107）。ratchet `SEMANTIC_EXPORTER_IMPORT_BASELINE` 收紧到 0。
- 全量验证：`:naca-trans:test`、`:naca-cloud-native` 编译、ratchet 全绿；T01/TEST-A/TESTHELLO/CALLMSG/BATCH1 + VERBS + INSPECT1 无回归。
- **踩坑记录**：`grep -rl`（无 `-a`）会把含 ISO-8859-1 非 ASCII 字节（如注释里的 `août`）的源文件当二进制跳过，漏掉约 20+ 个文件；批量处理这类文件必须用 `grep -a`，且参数名不止 `out`（还有 `lexp`/`output`/`exporter`），脚本需词边界 + 多参数名 + 空白灵活。

## 2026-07-20 进展：任务 #6 COPY/copybook include group 基础设施

### 机制（已查清）
`COPY <name>` 解析路径：`CCopyInWorking.DoCustomSemanticAnalysis → CObjectCatalog.GetExternalDataReference → CGlobalCatalog.GetExternalDataStructure`。后者先查 `tabIncludedStructures`，未命中则遍历 `csIncludeGroupName`（include group 名），对每个 group 取 `getEngine().doAllAnalysis(name, "", grp, false)` 从 group 的 `csInputPath` 解析 copybook。simplified 管线原先用 `new CGlobalCatalog(null, "", "", "")`（无 transcoder、无 include group），故一切 COPY 解析为空、被静默丢弃。

### 已落地
- 新增 `IncludeGroupSupport`（cloud-native service）：按 `test-output/st4/BATCH1/config.xml` 的引擎/group 布局，用 `Tag.createFromString` 构造 in-memory 配置（`IncludeTranscoder`=`CobolIncludeTranscoderEngine` + `Includes` group，`Type=Included`、`InputPath`=copybook 目录），`new Transcoder().Init(tag)` 后缓存；并提供 `generateCopybookClass(name)`（`doAllAnalysis` 后用 `CStringExporter` 重导出 copybook 的 `Copy` 类源码）。
- `TranspilerService.doSemanticAnalysisAndExport` 改为：当 `IncludeGroupSupport` 已配置时，用 `new CGlobalCatalog(includeTranscoder, "", "", "Includes")`，否则保持空目录。
- 验证（`CopyTranspileTest`，2/2 绿）：`BATCH1`（含 `COPY MSGZONE`）经 `TranspilerService` 转译后正确引用 copybook 结构（`msgzone`/`msg_No`/`msg_Zone`/`msg_Text`），不再静默丢弃；`generateCopybookClass("MSGZONE")` 产出 `class Msgzone extends Copy`（含 `MSG_ZONE`/`MSG_NO`/`MSG_TEXT` 字段）。`T01TranspileTest` 4/4 仍绿，全项目 `assemble` 成功。

### 剩余 gap（端到端编译）——已解决
copybook 的 `Copy` 类结构（`extends Copy` + 字段声明）只有**直接生成器**会产出，ST4 模型没有 external data structure 类的模板。根因定位：标识符命名由 exporter 的 `FormatIdentifier` 决定——`CJavaExporter.FormatIdentifier` 产出小写驼峰（`MSG-NO → msg_No`，真实管线文件导出用），而 `CStringExporter` 继承基类 `CBaseLanguageExporter.FormatIdentifier`（保留原样大写 `MSG_NO`）。`generateCopybookClass` 原用 `CStringExporter` 重导出 → 大写，与 ST4 主管线引用（小写）不一致。
- **修复**：新增 `CopybookStringExporter extends CStringExporter`，覆写 `FormatIdentifier` 为 `CJavaExporter` 的小写驼峰逻辑；`generateCopybookClass` 改用它。
- **验证**（`CopyTranspileTest` 3/3 绿）：copybook 类现产出小写 `msg_No`/`msg_Zone`/`msg_Text`；**`BATCH1` + 生成的 `Msgzone` copybook 类一起 javac 编译通过**（端到端编译阻塞解除）。BATCH1 完整运行还需 `CALLMSG` 子程序与输入/输出文件（多程序 + 运行时数据，独立于 COPY 基础设施）。

### 顺带诊断的预存问题（非本轮引入，独立工作）
`TranspileControllerTest.testTranspileValidCobolWithWorkingStorage` 失败（早期 ST4 管线替换 `generateSimplifiedJava` 后即存在，已验证还原本轮 include 改动后仍失败）：
1. **数据段命名约定**：数据段声明经 `CStringExporter`（基类 `FormatIdentifier`，大写）产出 `WS_MESSAGE`；该测试期望 ST4 小写 `wsMessage`。注意 T01 等样例数据段与过程引用**均为大写且自洽**（`WS_CHAR_1` 声明+引用一致，可编译运行）——故当前管线实际约定是大写，改小写属全局命名约定决策，会影响所有样例，需统一数据段与过程两处的命名后整体切换，风险高。
2. **数据段子字段缺失**：该测试 COBOL 的 `01 WS-MESSAGE` 组只生成了组声明，level-05 子项（WS-GREETING/WS-STATUS/WS-COUNTER）未声明（`DISPLAY WS-GREETING` 被内联为字面量 `"Hello"`）。T01 的子字段正常生成，故是特定结构下的数据段导出 bug。
两者均属 ST4 数据段生成（`DeclareType`/`CWorking` 直接路径）的迁移债务，独立于动词迁移与 COPY 基础设施。

## 2026-07-20 进展：最终架构——动词层 backend 继承解除（23 族）

按审计「严格迁移顺序」推进动词层：把语义动词实体具体化、工厂直接构造语义实体、删除 `CJava*ST` 后端子类，渲染完全走 assembler + 绑定。

### 迁移配方（已验证可重复）
对每个动词：①语义实体去 `abstract`（其 getter 已在 task #2 补齐者直接用；缺失者从 `CJava*ST` 搬入，含内部类如 `UnstringDestination`/`ReplaceItemModel`）；②`CJavaEntityFactoryST.NewEntityX` 改为 `new CEntityX(...); e.setLanguageExporter(langOutput); return e;`；③删除 `CJava*ST`；④把该动词绑定从 `semantic-runtime-bindings.properties`（运行时分发别名）移到 `semantic-bindings.properties`（concrete 清单），因为实体已 concrete。getter 不得调用输出协议（`ExportReference`/`FormatIdentifier`）——如 GO TO 目标改用 `procedure.getFormattedName()`（契约只禁 `ExportReference(`/`FormatIdentifier(`，不禁 `getFormattedName(`）。

### 已迁移（23 族，删 23 个 `CJava*ST`，36→13）
Goto、Accept、Divide、Multiply、SubtractTo、Calcul、CallFunction、Return、WriteFile、OpenFile、CloseFile、SetConstant、StringConcat、NextSentence、Exec、RewriteFile、SortRelease、SortReturn、Count、InspectConverting、RoutineEmulationCall、ParseString、Replace。
- 3 个原控制器单测（Comput/Divide/StringConcat）改为构造语义实体 + `TemplateLoader.getRecursiveAssembler().renderRoot()` 断言，仍绿。
- `backendHasACompleteDeclarativeSemanticTemplateManifest` 因 23 个动词转 concrete 而需重组 manifest（移动绑定后转绿）。
- **`finalArchitectureCheck`：245 失败 → 222 失败**（`everyDirectBackendClassAvoidsSemanticInheritance` 194→171）。
- 验证：T01/TEST-A/TESTHELLO 零回归；VERBS/INSPECT1 逐行匹配 GnuCOBOL；naca-trans 真实门禁（ratchet/模板/递归/单测）全绿。

### 剩余 13 个 `CJava*ST`（最终架构后续）
- **协议耦合**（getter/渲染调用 `ExportReference`/`FormatIdentifier`/`DoExport`，需目标无关化）：Assign（`DoExport`）、Display（`ExportReference`）、CallProgram（`ExportReference`）、Search（`FormatIdentifier`）、Sort（`ExportReference`）、AssignWithAccessor（`ExportReference`/`ExportWriteAccessorTo`）、Initialize（`isSqlCodeReset` 用 `ExportReference` 探测 SQLCODE）、IntrinsicFunction（表达式，`ExportReference` 覆写）。
- **procedure 结构**（3）：CJavaProcedureST/SectionST/DivisionST——`CEntityProcedure` 含 `import generate.*` + 抽象 `ExportReference`，需先目标无关化再具体化。
- **特殊**：CJavaReadFileST（单测用 data-entity mock 进 bloc，保留 legacy DoExport；生产绑定已生效）、CJavaClassST（独立顶层 helper，非动词）。

### 最终架构的真正瓶颈（222 失败的大头）
- **171 `everyDirectBackendClassAvoidsSemanticInheritance`**：约 160 个 `generate/java/**` 直接生成器（`CJava*`）+ 剩余 `CJava*ST`。删除它们的前提是**数据段 + 类骨架完全 ST4 化**。
- **47 `everySemanticClassSatisfiesTheFinalContract`**：根基类（`CBaseLanguageEntity`/`CBaseActionEntity`/`CDataEntity`/`CBaseExternalEntity`/`CBaseEntityFactory`）持有 exporter + 输出协议（`DoExport`/`WriteLine`/`ExportReference`），以及数据段实体（`CEntityAttribute`/`CEntityStructure`/`CEntityClass`/`CEntityDataSection` 等）的声明导出协议。
- **下一步关键**：为数据段（`DeclareType` 全系：picX/pic9/picS9/comp3/comp/redefines/occurs/edited…）与类骨架建立 ST4 模板，把整个导出走 assembler；之后才能剥离根基类输出协议、删除直接生成器、解除 backend 继承。这是独立的大型工作，需逐步以 golden/端到端基线保证行为 parity。

## 2026-07-20 进展：数据段 ST4 化——第一个构建块（CEntityAttribute 声明模板）

数据段当前**纯直接生成**：`CJavaDataSection.DoExport → ExportChildren → CJavaAttribute.DoExport`，逐段拼 fluent builder 字符串（`Var X = declare.level(N).picX(M).comp3().value(V).var() ;`）。`CEntityAttribute=dataReferenceEntity` 绑定只服务**引用**，声明无模板。

### 原型阶段（已被首个正式切片替代）

最初原型曾在 `CEntityAttribute` 中加入 `getDeclareArgs()` 与 `getCompClause()`，并用直接 `getTemplate(...)` 的 3 个测试验证声明格式。复核后确认它们预拼了 Java 引号、逗号和 fluent builder 调用，违反 semantic 层的最终契约，因此没有接入生产流，现已删除。

### DS-1/DS-2 首个正式切片（已完成）

- 新增 `JavaTemplateRole`（`REFERENCE`/`DECLARATION`/`ROOT`）和独立 `semantic-declaration-bindings.properties`；`CEntityAttribute` 默认仍绑定 `dataReferenceEntity`，只有显式声明角色才绑定 `dataAttributeDeclaration`，missing role binding 继续 fail closed。
- `getDeclareArgs()`、`getCompClause()` 已删除。模板改为读取 `length`、`decimals`、`format`、`editedPicture`、`pictureSizeSpecified`、`scaled`、`comp3`、`comp2`、`binaryComp` 等目标无关属性，自行生成全部 Java 标点与 builder 调用。
- ST4 的空字符串与整数 `0` 都不能安全充当布尔条件，且 `getComp()` 会遮蔽 `isComp()` 的 bean property；因此声明模板使用无歧义的布尔属性，禁止再次依赖值的 truthiness 猜测语义。
- `DataAttributeDeclarationTemplateTest` 现通过 assembler 运行（5/5）：3 个原声明与 direct generator 做 golden 等价，edited picture 验证 Java 字符串转义，另验证同一 attribute 的引用/声明双角色分发。
- `:naca-trans:build` 成功；严格契约为 401 项/222 失败，失败数保持不变。

### 数据段 ST4 化的真正难点（后续）
- **声明角色向复合结构传播**：attribute 叶子的显式声明分发已经完成；下一步须让 `CEntityStructure`/`CEntityDataSection` 遍历子项时把 `DECLARATION` 角色传给声明子树，而 VALUE 引用仍回到 `REFERENCE`，不能靠单一类型绑定自动分发。
- 还需：`CEntityStructure`（组声明）、`CEntityDataSection`（段头 `workingStorageSection`）、`CEntityNamedCondition`、REDEFINES/OCCURS/edited 变体的模板；以及把类骨架（`CJavaClass`）渲染改为经 assembler 输出数据段。
- value 子句的 `<entity.value>` 须经 assembler 渲染值实体（字面量/引用），隔离测试用 `getTemplate` 直渲不含此路径，端到端需走 assembler。

### 下一专项

下一步不再扩展动词面，而是按 `ST4_DATA_SECTION_MIGRATION_PLAN.md` 继续数据段模板化：以已经建立的声明/引用双角色为基础，接入 `CEntityStructure` 与 `CEntityDataSection`，再覆盖 named condition、REDEFINES、OCCURS，最后把 class root 接入唯一 assembler。只有生产转译、javac、运行回归和架构契约同时满足，才能删除相应 direct generator。

## 2026-07-20 进展：数据段第二切片——structure/data-section 声明模板（已提交 b7597bd）

`feat: template data sections and structure declarations` 完成 DS-4/DS-5 的最小纵向闭环（仍未接入生产 root，direct generator 保留）：

- assembler 为每个 ST 节点记录 render role（`synchronizedMap(WeakHashMap)`，key 为每次渲染独有的 ST 实例，共享 assembler 下线程安全、不跨渲染污染）。
- DECLARATION role 只沿 `children`/`activeChildren` 传播；`value`/`redefines`/`occurs`/`depending-on` 回到 REFERENCE。整棵递归树仍只有 `JavaTemplateAssembler.renderRoot` 一处 `.render()` 扁平化。
- `CEntityStructure` 新增目标无关属性（`numericLevel`/`typed`/`variableLength`/`signLeadingSeparated`/`signTrailingSeparated`/`insideExternalDataStructure`/`insideFileSection`），后两者自 `CJavaStructure` 上移为父链语义判定。
- `CEntityDataSection` 新增 section-kind 布尔属性；声明 binding 新增 `CEntityStructure→dataStructureDeclaration`、`CEntityDataSection→dataSectionDeclaration`，`java.stg` 增加对应模板。
- `DataSectionDeclarationTemplateTest` 2/2 与 direct generator golden 等价（group 声明、FileSection→structure→attribute 三层角色传播）。

门禁：`:naca-trans:build` 成功；`finalArchitectureCheck` **401 项/222 失败**（与基线持平，未增）；`:naca-cloud-native:test` 23 项/1 预存失败（`TranspileControllerTest.testTranspileValidCobolWithWorkingStorage`，未隐藏）。失败构成不变（47 semantic + 171 direct backend + 3 factory + 1 STG）。

下一切片：`CEntityNamedCondition`（level 88）声明模板 + golden 等价，随后 structure 变体与两处旧副作用修复（`SetJustifiedRight` 自赋值、direct generator 修改 semantic tree）。

## 2026-07-20 进展：数据段第三切片——level-88 named condition 声明（已提交 b48c69b）

`feat: template level-88 named condition declarations` 完成 DS-4 叶子类型：

- `CEntityNamedCondition` 新增目标无关只读模型 `getValues()`（`List<CDataEntity>`）与 `getIntervals()`（`List<IntervalModel>`，`IntervalModel.getStart/getEnd` 成对端点），无 Java 标点、无 `Export*`。
- 声明 binding `CEntityNamedCondition→dataNamedConditionDeclaration`；模板 `Cond <name> = declare.condition().value(...).value(start, end).var() ;`，value/interval 端点按 REFERENCE 渲染。
- golden 等价测试覆盖 2 单值 + 1 区间，与 `CJavaNamedCondition` direct 输出归一化等价。
- ST4 踩坑：匿名子模板迭代变量禁用单字母 `i`（`Formal argument i already exists` 致该模板及之后模板整体不加载）。

门禁：`:naca-trans:build` 成功；`finalArchitectureCheck` **401 项/222 失败**（持平）；`:naca-cloud-native:test` 23 项/1 预存失败。

下一切片：structure 变体逐一补模板 + direct parity，并修复 `SetJustifiedRight` 自赋值与 direct generator 修改 semantic tree。

## 2026-07-20 进展：数据段第四切片——旧副作用修复 + structure 变体 golden（已提交 e455d8f / 7215620）

### 旧副作用修复（e455d8f）
- `SetJustifiedRight` 自赋值修复（原恒不写字段，`isJustifiedRight()` 恒 false）。无样例使用 JUSTIFIED RIGHT，零回归。
- 消除 direct generator 在 export 阶段对 semantic tree 的两处就地修改（BLANK WHEN ZERO 改 `type`/`format`、variable-length OCCURS 改 `length`），改为 `CEntityAttribute`/`CEntityStructure` 的目标无关派生只读 getter（`getDeclaredType`/`getDeclaredFormat`/`getDeclaredLength` 等）。direct 与 ST4 模板同消费派生值，export 幂等、对象图前后不变；golden 测试断言 export 后 `getType()=="pic9"`、`getLength()==3` 不变。
- 模板类型子句改用 `declaredType`/`declaredFormat`/`declaredLength`。

### structure 变体 golden（7215620）
新增 direct-parity golden：REDEFINES、OCCURS、OCCURS DEPENDING ON、COMP-3（连同此前的 group、FileSection 传播、level-88、BLANK WHEN ZERO、variable-length）。

### 仍待补
FILLER 的 export 期改名副作用（`SetName(GetDefaultName())`）需派生填充名 getter；SYNC/JUSTIFIED/VALUE/sign/COMP-2 分支待补 golden；步骤 4（fail-closed 审计真实 DATA SECTION 子类型）与步骤 5（class root 接入 + 删 direct generator）未开始。

门禁：`:naca-trans:build` 成功；`finalArchitectureCheck` **401 项/222 失败**（持平）；`:naca-cloud-native:test` 23 项/1 预存失败。

## 2026-07-21 进展：root 契约推进 Step 0–2（已提交）

按「每步独立提交、独立可回滚；root 契约完成前不改生产出口」推进。

### Step 0：冻结基线
- 门禁基线：`finalArchitectureCheck` 401/222；`:naca-cloud-native:test` 23/1 预存失败。
- 用真实管线（lex→parse→`DoSemanticAnalysis`）转译 BATCH1，冻结 golden `naca-cloud-native/src/test/resources/golden/BATCH1_baseline.java`。当时 group filler（`01 FILLER REDEFINES SYS-TIME`，working-storage）=`Filler$1`，attribute filler（`05 FILLER PIC X(68)`，file section）=`Filler$2`。
- 一次性 capture 测试打 tag `baseline-capture`，从默认 `:naca-cloud-native:test` 排除，按需经 `:naca-cloud-native:batch1BaselineCapture` 重生成。

### Step 1：attribute FILLER 构造期命名（`fix: assign attribute FILLER names during semantic construction`）
- filler 检测/命名统一上移到 `CEntityAttribute` 构造器；`CEntityStructure` 删除自己的 `isfiller` 字段/`isFiller()` 覆写/构造器命名块（继承父类）；`CJavaAttribute.DoExport` 删除 `SetName(GetDefaultName())` 副作用，改读 `isFiller()`（由恒 false 改为返回字段）。生成期不再改 semantic tree，渲染幂等。
- **BATCH1 实测逐字节不变**：filler 编号跟随规范段序（working-storage 先于 file section 构造）而非源码行序，修复前后都是 group=`Filler$1`、attribute=`Filler$2`。`Batch1FillerGoldenDiffTest` 断言 `actual == golden` 且 BATCH1+Msgzone 仍 javac 通过。
- 单测：`attributeFillerIsNamedDuringConstruction`、`attributeFillerNamingIsIdempotentAndDoesNotMutateTheTree`。

### Step 2：显式 ROOT role 绑定（`refactor: add explicit root-role template bindings`）
- 新增 `semantic-root-bindings.properties`（仅 `semantic.CEntityClass=javaProgramRoot`）+ `JavaSemanticTemplateBindings.loadRoots()`；assembler 持有 default/declaration/root 三份 manifest，`bindingsFor(ROOT)` 只用 root manifest（不回落 default），missing root binding 抛 `MissingTemplateRendererException`。`.render()` 仍只在 `renderRoot()`。
- `java.stg` 增 `javaProgramRoot` 最小占位模板（真正 program-root 模板在后续 step 4）。
- **关键兼容处理**：11 个过渡 ST 控制器（procedure/verb）原经 `renderRoot(this)`（默认 ROOT role）走 default manifest。ROOT 改为只查 root manifest 后会全部失败，故把无参 `renderRoot(model)` 的默认 role 由 ROOT 改为 REFERENCE（这些控制器绑定本就在 reference/default manifest，旧 ROOT 与新 REFERENCE 都解析到 default manifest，行为不变）；程序/artifact root 须显式 `renderRoot(root, ROOT)`。
- `JavaTemplateRoleBindingTest` 证明 ROOT→root manifest、DECLARATION→declaration manifest、REFERENCE→default manifest，且 ROOT 不回落 default（attribute 以 ROOT 渲染 fail-closed）。

门禁（Step 0–2 每次提交均满足）：`:naca-trans:build` 成功；`finalArchitectureCheck` **401/222**（持平）；`:naca-cloud-native:test` 25 项/1 预存失败（新增 `Batch1FillerGoldenDiffTest` 2 项；`JavaTemplateRoleBindingTest` 在 naca-trans）。

下一切片（Step 3 起）：补目标无关 class root 语义（ProgramKind/ProgramCapability、declaration/executable children、显式 child role 传播），再实现 `javaProgramRoot` 模板（仍不接生产出口）。

## 2026-07-21 进展：Step 3——目标无关 program-root 语义（已提交）

### Step 3a（`test: guard no-arg renderRoot fail-closed; clarify BATCH1 filler golden`）
- 新增 `programArtifactCannotBeGeneratedWithoutExplicitRootRole`：`CEntityClass` 不在 default manifest，无参 `renderRoot`（REFERENCE 默认）对程序 root fail-closed，证明 artifact writer 忘记显式传 ROOT 不会静默生成引用。这是后续删除无参 overload（或恢复 ROOT 默认）的守卫。
- 修正 `Batch1FillerGoldenDiffTest` 类注释：预期是零变化（规范段构造序），非 filler 互换。
- 在 `JavaTemplateAssembler` 无参 `renderRoot` javadoc 标注**删除点**：最迟于 class-root 模板步、且必须在添加 external/COPY root binding 之前删除（或恢复 ROOT 默认）；删除前先把 11 个生产控制器改为显式 `REFERENCE`。

### Step 3b（`refactor: expose target-neutral program-root semantics`）
- 新增 semantic 枚举 `ProgramKind`（BATCH/CALLED/INCLUDED/MAP/ONLINE）与 `ProgramCapability`（SQL/MAP_SUPPORT/KEY_PRESSED）。Java 类名（BatchProgram 等）与 import 文本仍只在模板，semantic 不带目标字符串。
- `CEntityClass` 暴露 `getProgramKind()`（映射 `programCatalog.getProgramType()`）、`getCapabilities()`（映射 catalog 的 import declaration marker，只读、幂等）、`getDeclarationChildren()`（`CEntityDataSection`，含 SQL cursor section）、`getExecutableChildren()`（`CEntityProcedureDivision`/`CEntityProcedure`，含 section/paragraph）。两类 children 均按 `!ignore()` 过滤（与 direct `ExportChildren()` 一致），按 **semantic 类型**分类（`instanceof CEntity*`），不按 `CJava*` 运行类型。
- assembler `childRole` 显式传播：`declarationChildren → DECLARATION`、`executableChildren → REFERENCE`，按属性名固定，不随父 role；其余属性默认 REFERENCE，**ROOT 不被隐式继承**。
- `ProgramRootSemanticsSnapshotTest`（解析 TESTHELLO/T01）验证：读取 programKind/capabilities/declarationChildren/executableChildren 前后对象图不变、重复读取顺序稳定、不触发 export/render（exporter 始终为空）、capability 查询不改 catalog。
- **root child 分类实况**：TESTHELLO 类 children = [DataSection, ProcedureDivision, Procedure]；T01 = [Comment, DataSection, ProcedureDivision, Procedure]。即除 declaration/executable 外还有 `CEntityComment`（direct `ExportChildren` 会输出），是独立的第三类，需在 Step 4/5 绑定（`ProgramRootSubtypeAuditTest` 将 fail-closed 枚举）。本步快照测试把它作为已知类记录，不强制 declaration+executable 覆盖 comment。
- 本步仍不接生产出口、不实现完整 class Java 文本、不新增 `CJavaDataSectionST`、不删 direct generator。

门禁：`:naca-trans:build`/`test`/`dataSectionAudit` 成功；`finalArchitectureCheck` **403 项/222 失败**（失败持平；总数 +2 因新增 `ProgramKind`/`ProgramCapability` 两个 semantic 枚举均**通过**契约，179→181 passing）；`:naca-cloud-native:test` 25 项/1 预存失败。

下一切片（Step 4）：实现 `javaProgramRoot`/`programImports`/`programBaseType` 模板（消费 ProgramKind/ProgramCapability + declaration/executable children），废弃 `CJavaClassST` 预渲染方案，新增 `ProgramRootRenderParityTest`（两棵独立 tree 分别走 direct/assembler，TESTHELLO/T01 全文件 token 等价 + javac + 运行 + 渲染前后快照一致），仍不改 `TranscoderEngine` 生产出口。

## 2026-07-21 进展：Step 4 完成——comment 绑定 + commentChildren + 完整 root 模板 + root render parity + no-arg 退场

按用户 Step 4 指引（先 commentChildren + comment binding + root render parity，不碰生产出口）逐项完成：

1. **comment REFERENCE 绑定**（`afc1799`）：`CEntityComment.getComment()` 暴露目标无关原文；`javaComment` 模板 = `// <entity.comment; format="javaCommentText">`；`javaCommentText` 原子渲染器**逐字复刻** `CJavaComment.ExportReference`（`indexOf > 0` 门控、`\n→0x000A`、`\r→Ox000D` 大写 O 怪癖、`StringUtil.trimRight`），`//` 留在模板侧。`semantic.CEntityComment=javaComment` 运行时绑定。`JavaCommentRenderTest` 4 例 golden 等价（含两个怪癖断言）。
2. **`CEntityClass.getCommentChildren()`**：与 `declarationChildren`/`executableChildren` 同级，按 semantic 类型（`CEntityComment`）+ `getActiveChildren()` 顺序分类，root 模板不遍历裸 `activeChildren`。
3. **assembler 显式 child role**：`commentChildren → REFERENCE`（与 `declarationChildren → DECLARATION`、`executableChildren → REFERENCE` 并列）；ROOT 仍不隐式继承。
4. **完整 `javaProgramRoot`**（`93757c5`）：消费 `programName`/`programKind`/`capabilities`/`commentChildren`/`declarationChildren`/`executableChildren`；拆出 `programImports`/`programBaseImport`/`programBaseType`，Java import 与 base-class 映射全在模板侧；`javaClassName` 原子渲染器用 `CobolNameUtil.fixJavaName` 从源程序名形成 Java 类名；`CEntityClass` 增 bean getter + 目标无关 kind/capability 布尔。替换了占位模板。
5. **`ProgramRootRenderParityTest`**（`93757c5`）：TESTHELLO/T01 解析为 semantic tree，先 direct `CJavaClass.DoExport`（顺带按生产方式赋 FILLER 名）再 assembler `renderRoot(root, ROOT)`，全文件 **token 等价**（空白不敏感）。**仅测试出口，未接 `TranscoderEngine`**。
6. **no-arg `renderRoot` 退场**（`23ea869`）：11 个动词/procedure ST 控制器及全部测试调用点改为显式 `renderRoot(x, REFERENCE)`；删除无参 overload（忘传 role 现在是编译期错误，root 不会被静默渲成引用；fail-closed 守卫以显式 REFERENCE 保留）。行为不变（无参原本默认 REFERENCE）。完成 external/COPY root binding 的前置。

门禁：`:naca-trans:build`/`test` 成功；`finalArchitectureCheck` **403 项/222 失败**（失败持平）；`:naca-cloud-native:test` 25 项/1 预存失败（`testTranspileValidCobolWithWorkingStorage`，未隐藏）。

仍不接生产出口、不新增 `CJavaDataSectionST`、不删 direct generator。下一切片：external/COPY root binding（`CEntityExternalDataStructure`/`CEntityFileDescriptor`/include 结构），以及把 class root 接入唯一 assembler 的生产出口（Step 5）。

## 2026-07-21/22 进展：Step 5a/5b/5c + Step 6 生产出口接入（均已提交）

### Step 5a — FileDescriptor declaration binding（`d7a5b56`）
- `CEntityFileDescriptor` 增目标无关 getter（`getFileName`/`getFileStatus`/`getDisplayName`）；`dataFileDescriptorDeclaration` 对齐 `CJavaFileDescriptor.DoExport`：`declare.file(<fileRef> | "<displayName>" 回退)`、可选 `.status(<statusRef>)`、`activeChildren`（记录结构，file section 内导出全部子项）。
- 修 `CEntityFileSelect.setFileStatus` 自赋值（`fileStatus = fileStatus`）。无样例用 FILE STATUS，零回归。

### Step 5b — ExternalDataStructure 三角色 + SetInline（`04d1f02`）
- REFERENCE：copybook 标识符引用（既有 `dataReferenceEntity`）。
- DECLARATION：`CEntityInline` 的 `dataInlineDeclaration` 渲染类内实例 `Type ref = Type.Copy(this[, replacing(level,value)]) ;`。
- ROOT：`javaCopyClass`（`semantic-root-bindings`）渲染 copybook 为独立 `extends Copy` 编译单元，对齐 `CJavaExternalDataStructure.DoExport`。
- 修 `CEntityExternalDataStructure.SetInline` 自赋值（唯一调用点是内置 HEXZONE）；T01/TEST-A/BATCH1 门禁零回归，单独测试覆盖。

### Step 5c — BATCH1 整文件 root 等价（测试出口，`a496c0c`）
- `Batch1AssembledRootParityTest`：带 COPY MSGZONE include group 解析 BATCH1，assembler `renderRoot(root, ROOT)` 渲染全程序（working storage + file section/FD + COPY 实例声明 + procedure division），与 frozen golden **逐 token 等价**。
- fail-closed 守卫暴露并 closure 两个缺口：`unknownReferenceEntity`（未解析引用渲染为空，对齐 direct 的 `DoExport` 空/`ExportReference` null）；`getFileName()` 对未解析（unknown）文件名返回 null，使 `declare.file(...)` 回退到带引号显示名（对齐 direct，`declare.file("FILEIN")`）。

### Step 6 — 生产出口接入唯一 assembler（`e5eccc8`）
- `TranspilerService` 生产出口由 `CEntityClass.StartExport()`（direct）改为 `renderRoot(root, ROOT)`（assembler）。前置已满足：TESTHELLO/T01（含 javac+运行）与 BATCH1 均证明整文件 token 等价。
- assembler 空白格式与 legacy WriteWord/WriteLine 协议不同但 token 流相同（javac 不敏感）；`Batch1FillerGoldenDiffTest` 由逐字节改为逐 token 对比 frozen direct golden，仍锁定 `Filler$1`/`Filler$2` 规范命名。
- **范围说明（非验收集回归）**：使用未迁移构造（CICS/SQL/BMS/FPac，即剩余 direct backend 类）的程序现按设计 fail-closed，需先迁移其构造；copybook 类生成（`generateCopybookClass`）仍走 direct 重导出，是独立的后续迁移；direct `CJava*` 生成器作为 parity 测试参照与 copybook 生成保留，删除它们是最后的清理阶段。

门禁：`:naca-trans:build`/`test` 成功；`finalArchitectureCheck` **402 项/222 失败**（失败持平；总数较 403 −1 因删除死代码 `CJavaClassST`）；`:naca-cloud-native:test` **28 项/1 预存失败**（`testTranspileValidCobolWithWorkingStorage`，未隐藏；+测试为本轮新增 parity 测试）。

剩余（最终完成前）：copybook 生成迁到 assembler ROOT；CICS/SQL/BMS/FPac 等构造迁入 assembler（消除生产 fail-closed）； retiring parity 测试对 direct 的依赖后删除 direct `CJava*` 生成器大头。

## 2026-07-22 进展：Step 7 — copybook direct root 清理 + fail-closed 可观测性（均已提交）

### Step 7a/7b — fail-closed 可观测性（`9a2e5f5`）
- `MissingTemplateRendererException` 现给出最特异的 `semantic.*` 超类名：`missing ST binding for semantic.X (concrete generate.java.CJavaX)`，错误直接指向需要补 binding 的语义类型，而非黑盒。
- `TranspilerService` 不再把 `MissingTemplateRendererException` 吞成黑盒 "empty result"：经 `transpileWithFullPipeline`/`doSemanticAnalysisAndExport` 透传，`transpile()` 报 `Transpilation failed closed (no direct fallback): ...`。
- `FailClosedBehaviorTest` 锁定：请求角色无 binding 的节点必 fail-closed 且具名。
- **边界说明**：CICS/SQL 构造当前在**解析期**被丢弃/拒绝（未进入 semantic tree 渲染），故 CICS/SQL 样例本身不触发 assembler 的 fail-closed——迁移它们是独立的「解析 + binding」工作。assembler 级 fail-closed 保证（生产出口所依赖）由此测试锁定。

### Step 7c — copybook 类生成迁到 assembler ROOT（`422e020`）
- `IncludeGroupSupport.generateCopybookClass` 由 direct `CopybookStringExporter` 重导出改为 `renderRoot(structure, ROOT)`（`javaCopyClass`），与程序 root 同一扁平化路径。字段标识符来自 copybook 实体自身 exporter（小写驼峰），仍与转译程序引用一致。
- `CEntityExternalDataStructure.getDeclarationChildren()`：以 `childRole` 映射为 DECLARATION 的名字暴露 copybook 字段（copybook root 以 ROOT 渲染，`activeChildren` 否则回落 REFERENCE 而渲成裸引用）。
- `generateCopybookClassDirect` 仅作为 `CopybookClassParityTest` 的 parity 参照保留（assembler 输出与之逐 token 等价），稳定后可删。
- 验证：copybook direct-vs-assembler 逐 token 等价、BATCH1 + MSGZONE javac、BATCH1 cloud-native 回归均通过。

门禁：`:naca-trans:build`/`test` 成功；`finalArchitectureCheck` **402 项/222 失败**（持平）；`:naca-cloud-native:test` **30 项/1 预存失败**（`testTranspileValidCobolWithWorkingStorage`，未隐藏；+为本轮新增 parity/fail-closed 测试）。

**下一步（Step 7 之后，另起阶段）**：① 决定是否切通用 CLI 出口（`TranscoderEngine`/`CGlobalCatalog` 仍在 `StartExport()`），勿与 CICS/SQL 迁移混在一个提交；② 按业务覆盖度分批 retiring 剩余 direct backend 类（优先普通 COBOL 仍触发的 `CJavaReadFileST`/文件 open-read-write-close-rewrite，再 SQL 最小闭环、CICS 最小闭环，BMS/FPac 最后）。
