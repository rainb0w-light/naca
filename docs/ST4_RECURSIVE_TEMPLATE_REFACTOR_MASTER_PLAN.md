# ST4 递归模板全量重构主计划（历史总计划）

> 本文保留完整迁移背景与历史任务分解，不再作为当前状态入口。当前事实见
> `ST4_CLASS_BY_CLASS_AUDIT.md`，下一执行专项见
> `ST4_DATA_SECTION_MIGRATION_PLAN.md`。

> 状态：**已完成（M0–M14，2026-08-07）**
>
> 最终快照：ST4 为唯一生产生成路径；direct/typed backend 与生产态 legacy
> renderer 已删除；三个 inventory 均为 0；最终架构门禁、全仓 build 和样例矩阵
> 全绿；TEST-A 与 GnuCOBOL 34/34 行一致。
>
> 2026-07-19 最新：递归 assembler 已接入生产导出路径（procedure division/section/paragraph 级 + DISPLAY/STOP-RUN 绑定），控制流输出回归已修复；T01 端到端（transpile→compile→run）7/7 通过。详见 `ST4_CLASS_BY_CLASS_AUDIT.md` 的「2026-07-19 进展」节。注意：此前未提交改动曾使无显式 paragraph / 含 IF 的程序 procedure 体输出为空，文档中早期「样例通过」记录为过时信息。
>
> 计划版本：1.0
>
> 建立日期：2026-07-19
>
> 适用范围：`naca-trans` 全部 COBOL / SQL / CICS / BMS / FPac Java 代码生成路径
>
> 历史记录：`ST4_FULL_MIGRATION_EXECUTION_PLAN.md` 保留既有实施记录；本文档是后续实施、验收和完成判定的权威计划。

## 1. 重构目标

Naca 的 Java 生成链最终必须成为严格的两阶段系统：

1. Parser 和 semantic analysis 构建完整、与目标输出格式无关的语义树。
2. Java ST4 renderer 将语义树组装成由小模板递归组合的 ST 实例树，并且只在根节点调用一次 `render()`。

最终必须满足：

- `semantic/` 只保存 COBOL 语义，不生成 Java 文本。
- semantic entity 不依赖 exporter 才能表达自身语义。
- Java 关键字、运行时方法名、括号、分隔符、缩进和语句终止符只存在于 ST4 模板。
- 子表达式、子条件、子语句和子声明以 ST 实例或 ST 实例列表传给父模板。
- 中间节点不调用 `render()`，不提前把子树压平成字符串。
- ST4 是唯一生产代码生成路径。
- direct generator 仅在迁移期间作为对照基线，最终删除。

## 2. 非目标和禁止方案

以下方案不属于完成后的架构：

- 把旧字符串拼接原样移动到 `CJava*ST` controller。
- 在模板中调用 `export()`、`ExportReference()`、`DoExport()` 或 `exportChildren()`。
- 使用 `codeString`、`childrenCode`、`referenceString` 把旧 generator 的结果传入模板。
- 每个 semantic 节点分别调用 `template.render()`，再由父节点拼接。
- 在 semantic getter 中返回 `move(...)`、`val(...)`、`.class` 等 Java 代码片段。
- 依靠 `CJavaEntityFactoryST extends CJavaEntityFactory` 静默回退到 direct generator。
- 用 ST4 `AttributeRenderer` 递归渲染语法树。Attribute renderer 只用于原子值格式化。
- 在模板中执行语义分析、符号解析、变量替换或作用域计算。

## 3. 目标架构

```text
COBOL source
    -> lexer / parser
    -> semantic analysis
    -> complete semantic tree
    -> JavaTemplateAssembler.renderNode(root)
         -> ST root
             -> child ST instances
                 -> grandchild ST instances
    -> root.render() exactly once
    -> Java source
```

### 3.1 semantic 层

允许：

- semantic entity、enum、不可变列表和语义 DTO。
- 符号引用、变量绑定、作用域、类型、COBOL 选项和控制流关系。
- `getCondition()`、`getOperands()`、`getChildren()`、`getDestinations()` 等结构属性。
- 与 COBOL 语义直接对应的布尔属性，如 `rounded`、`testBefore`、`otherwise`。

禁止：

- `WriteLine`、`WriteWord`、`StartOutputBloc`、`EndOutputBloc`。
- `DoExport`、`ExportReference`、`ExportWriteAccessorTo`、`getCodeString`、`getChildrenCode`。
- Java 标识符格式化和 Java 运行时方法选择。
- 为模板预拼接 Java 字符串。

### 3.2 JavaTemplateAssembler 层

新增统一的递归模板组装入口，其职责是：

- 根据 semantic 节点类型选择模板。
- 注入已完成的 semantic 标量属性。
- 将每个子 semantic 节点递归转换为 ST 实例。
- 将子 ST 或 `List<ST>` 注入父 ST。
- 返回未渲染的 ST 实例。

Assembler 不得：

- 拼接 Java 源码。
- 执行 parser 或 semantic analysis。
- 渲染子模板。
- 通过 direct generator 获得子节点字符串。

建议的核心接口：

```java
interface JavaTemplateRenderer<T> {
    Class<T> modelType();
    ST build(T model, JavaTemplateAssembler assembler);
}

final class JavaTemplateAssembler {
    ST renderNode(CBaseLanguageEntity node);
    List<ST> renderNodes(List<? extends CBaseLanguageEntity> nodes);
    String renderProgram(CEntityClass program); // 唯一最终 render 入口
}
```

接口名称可在实现时调整，但职责边界不得改变。

### 3.3 ST4 模板层

模板只负责输出语法：

```stg
condition(condition, thenBody, elseBody) ::= <<
if (<condition>) {
    <thenBody; separator="\n">
}<if(elseBody)> else {
    <elseBody; separator="\n">
}<endif>
>>

exprSum(left, right, add) ::= <<
<if(add)>add(<left>, <right>)<else>subtract(<left>, <right>)<endif>
>>
```

`condition`、`left`、`right`、`thenBody` 和 `elseBody` 都是 ST 实例，不是预渲染字符串。

### 3.4 模板组结构

目标目录：

```text
templates/
  common/
    atoms.stg
    layout.stg
  java/
    java.stg
    program.stg
    declarations.stg
    references.stg
    expressions.stg
    conditions.stg
    control.stg
    verbs.stg
    files.stg
    sql.stg
    cics.stg
    forms.stg
  fpac/
    fpac.stg
```

要求：

- `java.stg` 作为根 group，通过相对路径导入子 group。
- 加入 classpath/jar 模式的 import smoke test。
- FPac 仅覆盖与普通 Java 不同的模板，复用 ST group inheritance。
- 共享小模板优先于复制大型模板。

## 4. 当前基线

截至 2026-07-19：

| 指标 | 当前值 | 最终目标 |
|---|---:|---:|
| semantic Java 文件 | 201 | 保留纯语义模型 |
| Base factory `NewEntity*` 入口 | 183 | 全部有纯语义构建和 ST 渲染能力 |
| ST factory 覆盖入口 | 41 | 183，随后移除按输出语言选择 semantic subtype 的需要 |
| 未覆盖入口 | 142 | 0 |
| `java.stg` 中 `codeString` | 51 | 0 |
| `java.stg` 中 `childrenCode` | 15 | 0 |
| `java.stg` 中 `referenceString` | 39 | 0 |
| 调用 `.render()` 的 ST controller 文件 | 39 | 仅程序根渲染入口 1 处 |
| direct Java generator 文件 | 201 | 0 |

未覆盖的 142 个 factory 入口分布：

| 领域 | 数量 |
|---|---:|
| 普通 COBOL 剩余入口 | 5 |
| Expression / Condition | 25 |
| 数据引用和声明 | 18 |
| 程序级结构 | 9 |
| SQL | 23 |
| CICS | 28 |
| Forms / BMS | 34 |

已建立但仍需按递归 ST 标准返工的内容：

- MOVE、ADD、SUBTRACT、MULTIPLY、DIVIDE、COMPUTE。
- IF、EVALUATE、PERFORM、PERFORM VARYING。
- STRING、UNSTRING、INSPECT、SEARCH、REPLACE、COUNT。
- DISPLAY、ACCEPT、CALL。
- OPEN、CLOSE、READ、WRITE、REWRITE、SORT、RELEASE、RETURN。
- `FUNCTION ORD` 和通用 intrinsic function 初始模型。

这些项目的 semantic 结构和已有测试应保留，但在移除旧字符串回调前不能标记为“递归 ST 完成”。

2026-07-19 首轮执行记录：

- M0 进行中：已加入 factory 覆盖、direct generator 文件数、semantic exporter 耦合、ST controller `render()` 文件数和模板遗留属性计数门禁；债务允许下降但禁止增长。
- M1 进行中：已加入 strict renderer registry、missing-renderer 错误、最具体类型选择、递归 `ST` / `List<ST>` 组装器和唯一根 `render()` 门禁。
- M1 验证：三层测试模型已经证明父模板通过 ST 实例递归组合子模板，中间 renderer 不进行文本扁平化。
- M2 进行中：实际 `CEntityNumber`、`CEntityString` semantic 节点已注册到递归 renderer；数字格式选择由模板完成，字符串转义使用 ST4 atomic attribute renderer。
- M2 数据引用切片已完成：普通引用、qualified `OF`、structure/external structure、多维 array/subscript 和 substring 均由 semantic renderer 构建嵌套 `ST` 节点。
- M2 验证：identifier 名称规范化和字符串转义保留在 atomic attribute renderer；array index 和 substring bound 通过 terminal expression 递归渲染，不传递预先拼接的代码字符串。
- M2 继续推进：figurative constants、index、environment variable 读引用和 address reference 已进入递归 registry；address 的被引用节点是子 `ST`，不是 `ExportReference()` 结果。
- M3 进行中：terminal、unary opposite、sum/subtract、multiply/divide/power、equals/different、ordered comparison、NOT、AND 和 OR 已迁移。
- M3 验证：算术和条件测试使用实际 semantic 类构建多层树，并与 direct generator 逐字符比较；AND/OR/NOT 保留了旧优先级括号行为。
- M2/M3 继续推进：internal boolean、value wrapper、named condition、`LENGTH OF`、`CURRENT-DATE`、address-of、digits/list/concat 和 generic intrinsic arguments 已进入递归 registry。
- M3 条件补全：named condition、ZERO/SPACE/LOW-VALUE/HIGH-VALUE 和 FPac boolean predicate 已通过 direct parity。
- M4 进行中：generic block 将语义动作列表组装为 `List<ST>`，CONTINUE/BREAK 为叶子语句，IF/ELSE 将 condition/then/else 作为子 `ST` 递归组合。
- M4 继续推进：WHILE/DO-WHILE/UNTIL、PERFORM VARYING TEST BEFORE/AFTER、递增/递减/显式步长、嵌套 AFTER 和 EVALUATE/WHEN/WHEN OTHER 已进入递归 registry。
- M4 组合验证：VARYING 的每个 iteration 使用小 ST 片段开启/闭合；EVALUATE 通过 branch ST 列表组成排他 `if/else if/else` 链，忽略分支不破坏 continuation。
- M5 已启动：MOVE/MOVE ALL/MOVE CORRESPONDING 和 ADD 已迁移；ADD 覆盖单值、INC/DEC、多值嵌套、ROUNDED、substring 和 environment write accessor。
- 当前验证：`:naca-trans:test` 全部通过；新增用例与 direct generator 对比了普通/保留字/qualified/structure/array/substring 输出。

## 5. 实施原则

### 5.1 自底向上迁移

依赖顺序固定为：

1. 原子值和引用。
2. 表达式和条件。
3. block 和控制流。
4. 普通动词。
5. 文件、过程和声明。
6. 完整程序。
7. SQL、CICS、Forms/BMS、FPac。

不能先用字符串占位绕过尚未迁移的子节点，否则会重新形成 `codeString`。

### 5.2 每次只迁移一个闭合语法切片

一个切片必须包含：

- parser/semantic 模型确认。
- semantic getter/DTO 补齐。
- renderer 注册。
- 叶子和父模板。
- semantic model test。
- template composition test。
- 生成 Java 编译测试。
- direct/GnuCOBOL 行为对比。
- 删除该切片对旧生成接口的使用。

### 5.3 迁移期间的 fallback

- Strict 模式：遇到未注册 semantic 类型立即失败，报告类型和 COBOL 行号。
- Compatibility 模式：允许显式 legacy fallback，但必须记录计数、节点类型和调用栈入口。
- CI 的 ST4 架构测试必须使用 Strict 模式。
- fallback 总数只能下降，不能增加。
- 切默认前 fallback 必须为 0。

## 6. 里程碑和工作包

### M0：冻结基线并建立可量化门禁

任务：

- 固化 183 个 factory 入口清单。
- 固化 201 个 semantic 类型和对应 direct generator 清单。
- 建立 legacy API 扫描测试。
- 建立模板 forbidden-expression 测试。
- 建立 renderer registry 完整性测试。
- 保存 TESTHELLO、TEST-A、BATCH1 的 COBOL、生成 Java、编译和运行基线。
- 保存 GnuCOBOL baseline。

门禁：

- 每次合并都输出剩余未迁移类型数。
- 禁止新增 direct generator 类。
- 禁止新增 `codeString`、`childrenCode`、`referenceString` 使用。
- 禁止新增 semantic 到 exporter 的依赖。

完成条件：基线可重复，所有指标由自动测试生成而不是人工统计。

### M1：递归 ST 组装基础设施

任务：

- 实现 `JavaTemplateAssembler` 和 renderer registry。
- 明确 missing-renderer 错误类型。
- 支持 `ST`、`List<ST>`、optional child 和 block。
- 只允许根程序调用最终 `render()`。
- 重构 `TemplateLoader`，支持拆分 group、import 和测试隔离。
- 增加嵌套 ST 实例渲染测试。
- 增加缩进、换行和空列表测试。

完成条件：一个人工构造的三层 semantic tree 能在不调用任何旧 export 方法的情况下递归生成 Java。

### M2：原子值和数据引用

状态标记：`[ ]` 尚未覆盖；`[B]` 只有行为基线（仍可能依赖 typed renderer/direct generator）；`[F]` 已满足最终架构契约。只有 `finalArchitectureCheck` 对相关 semantic、binding 和模板均不再报错时，才允许使用 `[F]`。

逐项迁移：

- [B] 数字 literal
- [B] 字符串 literal
- [B] COBOL figurative constants
- [ ] 其他 constant value 模型统一
- [B] internal boolean
- [B] 普通变量引用
- [B] qualified `OF` 引用
- [B] structure/external structure reference
- [B] value reference wrapper
- [ ] move reference action
- [B] array/subscript 引用
- [B] substring 引用
- [B] address reference
- [B] address-of expression
- [B] index
- [B] named condition reference
- [B] environment variable 读引用
- [ ] environment variable 写 accessor
- [ ] formatted variable reference
- [ ] unknown/ignored reference 的诊断输出
- [B] `LENGTH OF`
- [B] `CURRENT-DATE`
- [B] digits/list/concat
- [B] intrinsic function arguments

完成条件：模板中 `referenceString` 为 0；数据引用不再通过 `ExportReference()` 生成。

### M3：表达式和条件

逐项迁移：

- [B] terminal expression
- [B] unary opposite
- [B] sum/add/subtract expression
- [B] product/multiply/divide/power expression
- [B] concatenation expression
- [ ] function call expression
- [B] generic intrinsic function
- [B] comparison
- [B] equals/different
- [B] NOT
- [B] AND
- [B] OR
- [B] named condition
- [ ] `IS ALL`
- [B] `IS CONSTANT`
- [ ] `IS KIND OF`
- [B] boolean condition
- [ ] file EOF condition
- [ ] SQLCODE condition

重点测试：

- 三层以上混合表达式嵌套。
- AND/OR 优先级和括号。
- NOT 包裹比较与复合条件。
- expression 作为 intrinsic 参数。
- substring 的 start/length 都是表达式。

完成条件：表达式和条件模板不含 `codeString`；semantic expression 不再要求 `Export()`。

### M4：block 和控制流

逐项迁移：

- [B] generic statement block
- [B] IF
- [B] ELSE
- [B] condition ignored / optimized branch
- [B] EVALUATE
- [B] WHEN
- [B] WHEN OTHER
- [ ] PERFORM paragraph
- [ ] PERFORM THRU
- [B] PERFORM UNTIL / WHILE / DO-WHILE
- [B] PERFORM VARYING TEST BEFORE
- [B] PERFORM VARYING TEST AFTER
- [B] nested AFTER iteration
- [ ] GO TO
- [ ] GO TO DEPENDING ON
- [B] CONTINUE
- [B] BREAK
- [ ] explicit get-out
- [ ] NEXT SENTENCE
- [ ] RETURN/EXIT PROGRAM/STOP RUN

完成条件：模板中 `childrenCode` 为 0；所有语句体使用 `List<ST>` 递归组合。

### M5：数据操作动词

每个动词独立完成一个闭合切片：

- [B] MOVE
- [B] MOVE CORRESPONDING
- [B] MOVE ALL
- [ ] MOVE with accessor
- [ ] ASSIGN SPECIAL
- [ ] SET constant/high/low/space/zero
- [ ] SET substring
- [B] INC/DEC through ADD optimization
- [ ] INITIALIZE
- [B] ADD
- [B] ADD multiple values / ROUNDED
- [B] ADD substring/environment write accessor
- [ ] SUBTRACT
- [ ] MULTIPLY
- [ ] DIVIDE / REMAINDER
- [ ] COMPUTE / ROUNDED / ON SIZE ERROR
- [ ] STRING / DELIMITED BY / POINTER / ON OVERFLOW
- [ ] UNSTRING / PARSE
- [ ] INSPECT CONVERTING
- [ ] COUNT
- [ ] REPLACE
- [ ] SEARCH

完成条件：对应 `CJava*ST` controller 只剩 renderer adapter，随后由统一 assembler 取代；controller 中无 Java 字符串拼接。

### M6：交互、CALL 和过程结构

逐项迁移：

- [ ] DISPLAY DEFAULT/CONSOLE/ENVIRONMENT
- [ ] ACCEPT DATE/DAY/DAY-OF-WEEK/TIME/INPUT/ENVIRONMENT
- [ ] CALL literal program
- [ ] CALL variable program
- [ ] BY REFERENCE
- [ ] BY VALUE
- [ ] BY CONTENT
- [ ] LENGTH OF parameter
- [ ] ON EXCEPTION/ON ERROR
- [ ] procedure division
- [ ] section
- [ ] paragraph
- [ ] procedure reference
- [ ] routine emulation call
- [ ] comments and source line mapping

完成条件：semantic 不再返回 `displayEnv`、`console().display`、`.class`、`usingValue` 等 Java 输出词汇。

### M7：文件和 SORT

逐项迁移：

- [ ] FILE SELECT
- [ ] FILE DESCRIPTOR
- [ ] sorted file descriptor
- [ ] file buffer/record
- [ ] file length dependency
- [ ] OPEN INPUT/OUTPUT/I-O/EXTEND
- [ ] CLOSE
- [ ] READ
- [ ] READ INTO
- [ ] AT END
- [ ] NOT AT END
- [ ] WRITE
- [ ] WRITE FROM
- [ ] WRITE AFTER
- [ ] REWRITE
- [ ] SORT
- [ ] RELEASE
- [ ] SORT RETURN

完成条件：BATCH1 的文件声明、文件动词和 block 全部由 ST 实例树生成，运行输出与 GnuCOBOL baseline 一致。

### M8：数据声明和程序根模板

逐项迁移：

- [ ] program class/package/imports
- [ ] constructor和运行入口
- [ ] DATA DIVISION
- [ ] WORKING-STORAGE
- [ ] LINKAGE SECTION
- [ ] FILE SECTION
- [ ] attribute declaration
- [ ] group structure
- [ ] level 01/05/77/88
- [ ] PIC X / 9 / edited PIC
- [ ] VALUE
- [ ] REDEFINES
- [ ] OCCURS / DEPENDING ON
- [ ] FILLER
- [ ] COMP
- [ ] COMP-3
- [ ] COMP-5 独立存储模式
- [ ] external data structure/copybook
- [ ] form/resource declaration入口
- [ ] inline/comment placement

同时处理 runtime 语义缺口：

- 普通 COMP 超出 PIC 位数时的截断。
- COMP-5 与 COMP 分离。
- COMP-5 native endian 和高字节行为。

完成条件：

- `NewEntityClass`、`NewEntityDataSection`、`NewEntityStructure` 等程序级入口进入递归 ST 根树。
- TEST-A 输出达到 34/34 行与 GnuCOBOL 一致。
- `CJavaClassST` 早期实验实现被替换或删除。

### M9：SQL

迁移全部 SQL semantic 类型：

- [ ] SQL CALL
- [ ] CLOSE/OPEN cursor
- [ ] SQLCODE condition
- [ ] COMMIT/ROLLBACK
- [ ] cursor/cursor section
- [ ] cursor SELECT
- [ ] DECLARE TABLE
- [ ] DELETE
- [ ] EXECUTE
- [ ] FETCH
- [ ] INSERT
- [ ] LOCK
- [ ] SELECT
- [ ] UPDATE
- [ ] single statement
- [ ] session declare/drop
- [ ] SQL ON ERROR/ON WARNING GOTO

完成条件：23 个 SQL factory 入口全部注册；SQL 模板无 direct fallback；至少一个 cursor 和一个非 cursor 集成样例通过。

### M10：CICS

迁移全部 CICS semantic 类型：

- [ ] ABEND/RETURN/XCTL/LINK
- [ ] ADDRESS/ASSIGN/INQUIRE/ASKTIME
- [ ] DELAY/START/SYNCPOINT
- [ ] GETMAIN
- [ ] ENQ/DEQ
- [ ] READ/WRITE/REWRITE
- [ ] READQ/WRITEQ/DELETEQ
- [ ] STARTBR/READNEXT 等 browse 结构
- [ ] SEND MAP/RECEIVE MAP
- [ ] HANDLE AID/CONDITION/IGNORE CONDITION
- [ ] RETRIEVE
- [ ] SET TDQUEUE

完成条件：28 个 CICS factory 入口全部注册；每类主要命令族至少一个 parser、template 和 javac 测试。

### M11：Forms / BMS

迁移范围：

- [ ] form/container/redefine
- [ ] field/data/array/array reference
- [ ] field attribute/color/flag/highlight/length/occurs/validated
- [ ] label/skip field
- [ ] cursor
- [ ] key pressed/get/reset key
- [ ] set attribute/color/flag/highlight/cursor
- [ ] field state conditions
- [ ] resource strings

完成条件：34 个 Forms/BMS factory 入口全部注册；BMS map 转译、Java 编译和最小运行测试通过。

### M12：FPac 和多输出方言

任务：

- 盘点 `CJavaFPacEntityFactory` 与普通 Java generator 的差异。
- 共享普通 Java semantic model 和 renderer registry。
- 使用 ST group import/inheritance 覆盖差异模板。
- 禁止复制整套 Java 模板。
- 增加 FPac 最小语法、程序级和运行回归样例。

完成条件：FPac 不再依赖专用字符串 generator；普通 Java 模板修改能自动被 FPac 继承，除非存在显式 override。

### M13：默认切换和兼容期

最终实现：

- 递归 ST4 renderer 是唯一生产出口。
- direct 兼容开关已删除；历史 factory 属性不能恢复旧路径。
- CI/Gradle 门禁使用 fail-closed assembler 和零容忍 architecture scan。
- 生产 fallback 面为 0，legacy renderer 仅保留为 test fixture。

完成条件：

- 无系统属性时使用 ST4。
- 全量 Gradle build 通过。
- 样例矩阵全部转译、javac、运行通过。
- 没有 semantic 类型触发 legacy fallback。

### M14：删除旧 generator 和出口接口

按以下顺序删除：

1. `getCodeString()`、`getChildrenCode()`、`getReferenceString()` 过渡接口。
2. semantic 中的 `DoExport()`、`Export()`、`ExportReference()`、`ExportWriteAccessorTo()`。
3. `CJava*ST` 的逐节点 controller 层。
4. `generate/java/verbs` direct generators。
5. `generate/java/expressions` direct generators。
6. `generate/java/forms`、`CICS`、`SQL` direct generators。
7. `CJavaEntityFactory` 的输出语言 subtype 创建职责。
8. 只为旧 generator 服务的 exporter helper。

完成条件：

- `generate/java` 中没有 direct 拼接 generator。
- semantic 不再持有 `CBaseLanguageExporter output`。
- 代码库扫描不到旧导出接口。
- 删除后完整 build 和端到端样例仍通过。

## 7. 单个迁移项的 Definition of Done

每个 checkbox 只有同时满足以下条件才能勾选：

- [ ] semantic 数据结构完整且无输出语言知识。
- [ ] semantic analysis 在进入 renderer 前全部完成。
- [ ] renderer registry 有唯一、显式映射。
- [ ] 子节点作为 ST 或 `List<ST>` 注入。
- [ ] 中间节点没有调用 `render()`。
- [ ] template 不使用旧字符串属性或导出方法。
- [ ] controller/assembler 没有 Java 字符串拼接。
- [ ] semantic model test 覆盖字段、顺序、optional 和嵌套关系。
- [ ] template unit test 覆盖最小、完整和嵌套场景。
- [ ] 生成 Java 通过 javac。
- [ ] direct generator golden diff 已审查。
- [ ] 有条件时与 GnuCOBOL runtime baseline 一致。
- [ ] 对应 legacy fallback 和 direct 调用已删除。
- [ ] 迁移指标和本文档状态已更新。

## 8. 测试矩阵

### 8.1 每次提交

- semantic model unit tests。
- 受影响的 renderer/template tests。
- `TemplateValidationTest`。
- forbidden API/lint tests。
- renderer registry completeness test。

### 8.2 每个工作包

- `:naca-trans:test`。
- `:naca-rt:test`，如果涉及 runtime。
- 相关 cloud-native API tests。
- 生成 Java 的独立 javac 编译。
- direct 与 ST4 normalized diff。

### 8.3 每个里程碑

- `./gradlew build`。
- TESTHELLO。
- TEST-A-STANDALONE。
- BATCH1。
- 对应 SQL/CICS/BMS/FPac 样例。
- GnuCOBOL 对比。

## 9. CI 架构门禁

必须自动执行：

```text
semantic forbidden APIs:
  DoExport
  ExportReference
  ExportWriteAccessorTo
  getCodeString
  getChildrenCode
  WriteLine / WriteWord

template forbidden properties/calls:
  codeString
  childrenCode
  referenceString
  export
  ExportReference
  DoExport

ST controller forbidden behavior:
  child.render()
  StringBuilder / append for Java output
  ExportReference calls
```

门禁需要支持迁移白名单，但白名单必须：

- 列出准确文件和原因。
- 记录负责人工作包。
- 数量只能下降。
- M14 时必须为空。

## 10. 实施追踪表

每个工作项记录：

| 字段 | 内容 |
|---|---|
| Semantic type | 被迁移的语义类型 |
| COBOL syntax | 对应语法和选项 |
| Direct generator | 当前对照实现 |
| ST template | 新模板名称 |
| Renderer | registry 映射 |
| Dependencies | 子引用、表达式、block 等依赖 |
| Unit tests | 模型和模板测试 |
| Integration sample | 使用该语法的样例 |
| Legacy calls removed | 删除的旧接口 |
| Status | pending / model-ready / template-ready / verified / complete |

禁止只使用“ST 已覆盖”这一种状态；必须区分模型、模板、递归组合、验证和旧路径删除。

## 11. 风险和控制措施

### 11.1 输出格式变化过大

控制：使用 normalized diff 区分纯格式变化和语义变化；每个切片保持最小。

### 11.2 semantic getter 隐藏输出逻辑

控制：审查所有 String getter；禁止返回 Java 方法名和完整表达式片段。

### 11.3 静默 direct fallback

控制：Strict renderer registry；CI 中 missing renderer 立即失败。

### 11.4 ST4 模板承担语义计算

控制：模板只允许 presence/boolean 选择、模板 include 和 list mapping；语义决策在 Stage 1 完成。

### 11.5 模板组 import 在 jar 中失效

控制：增加 classpath jar smoke test，再拆分当前单一 `java.stg`。

### 11.6 一次性删除旧 generator 难以回归

控制：逐语法保留 direct golden oracle；只有对应切片通过后才删除其生产入口。

### 11.7 runtime 差异与模板迁移混杂

控制：分别记录 semantic、rendering、runtime 三类失败；COMP/COMP-5 使用独立工作包和测试。

## 12. 最终完成判定

整个重构只有同时满足以下条件才算完成：

- 183 个 factory 入口不存在未迁移或静默 fallback。
- 201 个 semantic 模型均不承担 Java 文本生成职责。
- 模板旧字符串属性计数为 0。
- 只有完整程序根节点调用一次 ST4 `render()`。
- direct generator 和旧 export API 已删除。
- ST4 是默认且唯一的生产生成路径。
- Java、SQL、CICS、Forms/BMS、FPac 样例矩阵全部通过。
- 全量 Gradle build 通过。
- 生成 Java 全部通过独立 javac。
- TEST-A 与 GnuCOBOL 34/34 行一致。
- BATCH1 文件和控制台结果与 baseline 一致。
- 架构门禁、开发文档和新增语法指南进入 CI。

以上条件已于 2026-08-07 全部满足。遗留 NacaRT 兼容程序的 21 个历史失败
被明确隔离到 `:naca-rt-tests:legacyRuntimeTest` 严格审计任务；它们不经过转码器、
不依赖 ST4，也不构成迁移 fallback。

## 13. 建议的首个实施序列

开始编码时严格按以下顺序推进：

1. M0：自动化盘点和禁止新增债务门禁。
2. M1：实现递归 ST assembler，验证三层人工语义树。
3. M2：迁移 literal、普通引用、qualified reference、array、substring。
4. M3：迁移 terminal、sum/product、compare、AND/OR/NOT。
5. 用新的表达式 ST 树返工 COMPUTE。
6. 用新的 block ST 列表返工 IF。
7. 返工 EVALUATE 和 PERFORM VARYING。
8. 继续 M5 其余普通动词。
9. 进入文件、声明和完整程序根模板。

在第 4 步完成以前，不继续扩展更多顶层动词模板；否则只会扩大 `codeString` 过渡层。
