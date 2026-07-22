# ST4 全量迁移执行计划

> 说明：本文档仅保留历史执行记录，不再表达当前进度。当前状态以
> `ST4_CLASS_BY_CLASS_AUDIT.md` 为准，最终标准以
> `ST4_FINAL_ARCHITECTURE_CONTRACT.md` 为准，当前数据段专项以
> `ST4_DATA_SECTION_MIGRATION_PLAN.md` 为准。

> 当前状态: 执行中
> 当前进度: Phase 6 - ST4 端到端样例驱动验证
> 最新更新时间: 2026-07-18

## 目标状态

Naca 最终成为“语义分析模型 + ST4 模板引擎”的代码生成系统:

- COBOL 解析和语义分析仍由 Java semantic layer 完成。
- 所有 Java 代码输出都由 ST4 模板完成。
- `CJava*ST` 类只做模板调度和 entity 注入，不拼接最终 Java 字符串。
- ST4 模板只读取属性，不调用 `DoExport()`、`ExportReference()`、`export()`。
- ST4 成为默认生成路径。
- 老的直接字符串生成器可删除。

## 当前进度

| Phase | 名称 | 状态 | 当前结论 |
|---|---|---|---|
| Phase 1 | 建立迁移基线 | 已完成 | ST4 单测、模板语法测试、`:naca-trans:test` 已通过 |
| Phase 2 | 定义严格两阶段契约 | 进行中 | 原则已明确，但代码中仍有 `codeString` / `childrenCode` 遗留 |
| Phase 3 | 引入明确渲染模型 | 进行中 | 已加入 `referenceString`、`increment/decrement` 等局部 accessor |
| Phase 4 | 迁移核心 COBOL 动词 | 进行中 | 已覆盖 assign/add/condition/loop/display/read/bloc 基线 |
| Phase 5 | 迁移程序级和数据声明生成 | 进行中 | file section / filler / data level 已修复一批端到端缺口，class/data 输出仍需模板化 |
| Phase 6 | ST4 端到端转译基线 | 当前执行 | `TEST-A-STANDALONE` 已可经 API 转译、编译、运行；34 行输出中 31 行与 GnuCOBOL 完全一致，剩余 3 行为 COMP/COMP-5 运行时表示差异 |
| Phase 7 | 迁移 CICS / SQL / BMS / FPac | 未开始 | 等普通 COBOL batch 路径稳定后执行 |
| Phase 8 | ST4 切默认 | 未开始 | 当前仍通过 `-Dnaca.transpiler.factory=st4` 显式启用 |
| Phase 9 | 删除旧代码生成器 | 未开始 | 必须在 ST4 默认路径和样例回归稳定后执行 |
| Phase 10 | 质量门禁和文档 | 进行中 | 本文档作为执行跟踪入口 |

## Phase 1: 建立迁移基线

任务:

- 保持 `./gradlew :naca-trans:test` 全绿。
- 固化 ST4 单测集:
  - assign
  - addTo
  - condition
  - loop
  - loopIter
  - display
  - readFile
  - bloc
- 保持 ST4 专用测试命令:
  - `./gradlew :naca-trans:test --tests "generate.java.st.*"`
- 保持模板语法 smoke test:
  - `./gradlew :naca-trans:test --tests generate.templates.TemplateValidationTest`

验收:

- `TemplateValidationTest` 通过。
- `generate.java.st.*` 通过。
- `:naca-trans:test` 通过。

## Phase 2: 定义严格两阶段契约

任务:

- semantic entity 只负责语义结构和模板属性。
- ST controller 只负责选择模板、注入 entity、写出模板结果。
- ST4 模板只负责格式化。
- 逐步消除危险接口:
  - `getCodeString()`
  - `getChildrenCode()`
  - 模板中的 `.codeString`
  - 模板中的 `.childrenCode`

验收:

- 模板中不出现 `.export`。
- 模板中不出现 `.ExportReference`。
- 核心模板逐步不再依赖 `.codeString` / `.childrenCode`。

## Phase 3: 引入明确渲染模型

短期策略:

- 简单数据引用使用 `referenceString`。
- 条件表达式引入明确 condition accessor。
- 表达式引入明确 expression accessor。
- 子语句引入明确 block/statement 渲染模型，替代 `childrenCode`。

长期策略:

- 引入不可变 view DTO，例如:
  - `RenderedStatement`
  - `RenderedBlock`
  - `TemplateModel`

验收:

- condition、loop、readFile、bloc 不再依赖 `childrenCode`。
- assign、addTo、display 不再依赖 `codeString`。

## Phase 4: 迁移核心 COBOL 动词

优先级 1:

- MOVE
- ADD
- SUBTRACT
- MULTIPLY
- DIVIDE
- COMPUTE
- IF / ELSE
- PERFORM
- PERFORM VARYING
- DISPLAY
- ACCEPT
- STOP RUN
- EXIT PROGRAM

优先级 2:

- READ
- WRITE
- REWRITE
- OPEN
- CLOSE
- SORT
- RETURN
- RELEASE

优先级 3:

- INITIALIZE
- INSPECT
- STRING
- UNSTRING / PARSE
- SEARCH
- EVALUATE / CASE
- CALL
- GOTO
- CONTINUE
- NEXT SENTENCE

每迁移一个动词:

- 添加或补足 `CJava*STTest`。
- 模板不调用导出方法。
- ST controller 不拼最终 Java 字符串。
- 和旧 direct generator 在关键场景上等价。

## Phase 5: 迁移程序级和数据声明生成

任务:

- class skeleton:
  - package
  - imports
  - class declaration
  - constructor
  - main/launcher
  - procedure entry
- data division:
  - working-storage
  - linkage section
  - file section
  - constants
  - occurs
  - redefines
  - comp/comp-3
  - filler
- file descriptors。
- comments/original line mapping。
- include/copybook 输出策略。

验收:

- ST4 生成完整 Java 文件可编译。
- 不再依赖旧 `CJavaClass` / 旧 data declaration 字符串生成路径。

## Phase 6: ST4 端到端转译基线

当前执行目标:

- 使用 `-Dnaca.transpiler.factory=st4` 转译 `TEST-A-STANDALONE.cbl`。
- 记录所有失败点。
- 将每个失败点转为单测或模板/accessor 修复。
- 生成 Java 后执行 `javac` 编译。
- 最终与 GnuCOBOL baseline 对比输出。

当前执行记录:

- 2026-06-08: `TEST-A-STANDALONE.cbl` 在第 439 行 `FUNCTION ORD(WS-DUMP-SRC(WS-DUMP-IDX:1))` 处失败。
- 该失败在 direct generator 和 ST4 factory 下均可复现，当前判断为 parser/semantic baseline 阻塞，不是 ST4 专属模板失败。
- Phase 6 继续使用更小样例 `TESTHELLO.cbl` 验证 ST4 生成链路，同时保留 `TEST-A-STANDALONE.cbl` 作为后续 parser/semantic 修复项。
- 2026-06-08: `TESTHELLO.cbl` 使用 `-Dnaca.transpiler.factory=st4` 转译成功，生成 `test-output/st4/TESTHELLO/src/test/Testhello.java`。
- 2026-06-08: `Testhello.java` 使用 `javac` 编译成功，classpath 为 `naca-rt/build/classes/java/main:naca-jlib/build/classes/java/main`。
- 2026-06-08: `BATCH1.cbl` 使用完整 Commons/Includes 临时配置后，在第 92 行 `WHEN '1'` 进入 `CWhenBloc.DoCustomSemanticAnalysis()` 时失败，原因是 `cond == null`。
- 2026-06-08: `BATCH1.cbl` 的 `cond == null` 失败在 direct generator 和 ST4 factory 下均可复现，当前判断为 parser/semantic baseline 阻塞。
- 2026-06-08: 已修复 `CWhenBloc` 构造函数字段遮蔽，`WHEN` 条件现在能进入 semantic analysis。
- 2026-06-08: 已修复 `CProgram` 中 FILE SECTION 只在 LINKAGE SECTION 存在时才挂入 semantic tree 的问题，`BATCH1.cbl` 现在能生成 Java。
- 2026-06-08: 已将 ST4 `EVALUATE/WHEN` 过渡输出改为 if-block 样式，避免生成无效 `switch () {}`。
- 2026-06-08: 已将 ST4 `READ` 输出改为 direct runtime 风格 `read/readInto(...).atEnd()`，不再生成无效 lambda 链式 API。
- 2026-06-08: `BATCH1.cbl` 当前 ST4 生成 Java 仍有 7 个编译错误，剩余缺口为 data declaration/redefines、file assign、`ACCEPT ... FROM TIME`、CALL PROGRAM、include 子字段生成。
- 2026-06-08: 已补充 file verb ST4 访问器和模板，使 `OPEN/CLOSE/WRITE/READ` 更接近 direct runtime 调用。
- 2026-06-08: `BATCH1.cbl` 当前 ST4 编译错误减少到 7 个，`read/write/open` 的空参数问题已推进，剩余集中在:
  - `SYS-TIME` 01 声明缺失导致 `REDEFINES sys_Time` 编译失败。
  - `declare.file(null)` 因 file assign 目标未绑定导致重载歧义。
  - `ACCEPT SYS-TIME FROM TIME` 仍生成 `accept()`。
  - `COPY MSGZONE` 子字段 `MSG-NO` / `MSG-TEXT` 未生成。
  - `CALL 'CALLMSG'` 仍生成 `perform("CALLMSG")`。
- 2026-06-08: 已补充 `REDEFINES` 目标引用登记，`SYS-TIME` 01 声明恢复生成。
- 2026-06-08: 已补充 `ACCEPT` 语义访问器和 ST4 模板，`ACCEPT SYS-TIME FROM TIME` 生成 `move(getTimeBatch(), sys_Time)`。
- 2026-06-08: 已补充 `CALL PROGRAM` 参数视图和 ST4 模板，`CALL 'CALLMSG' USING MSG-ZONE` 生成 `call("CALLMSG").using(msgzone.msg_Zone).executeCall()`。
- 2026-06-08: 已为 file descriptor 增加未绑定 assign 的字符串兜底，避免 `declare.file(null)` 重载歧义。
- 2026-06-08: 已为外部 COPY 数据结构内的结构子字段增加受控完整导出，`Msgzone.java` 现在生成 `msg_No` / `msg_Text`。
- 2026-06-08: `BATCH1.cbl` 使用 `-Dnaca.transpiler.factory=st4` 转译成功，生成 Java 已通过 `javac` 编译。
- 2026-06-08: `FUNCTION ORD(...)` 支持缺口确认: `ORD` 尚未在 COBOL keyword/function parser 中建模，`ReadTerminalExpr()` 也没有通用 intrinsic function 调用解析。
- 2026-06-08: 已修复运行时配置读取、文件打开路径、数据文件 reader/writer 名称保存等 shadowing 问题，`BatchMain` 可加载并运行 ST4 生成的 `Batch1`。
- 2026-06-08: 已将 ST4 `DISPLAY` 迁为模板调度，新增显示函数和显示项视图，恢复多值显示中的 `val(...)` 包装。
- 2026-06-08: 已修复 file section 完整导出策略，FD 记录结构会导出未直接引用的 filler/子字段，避免读取固定长记录时被裁剪。
- 2026-06-08: 已修复匿名属性级 filler 声明，空名 `FILLER` 生成 `filler$N` 并使用 `.filler()`。
- 2026-06-08: `BATCH1.cbl` 使用 ST4 完整闭包转译、`javac` 编译、`BatchMain` 运行成功；控制台统计为 `FILEIN=0000002`、`FILEOUT=0000001`，输出文件为 70 字节单条记录。
- 2026-06-08: 当前仍有运行日志 debug 噪音，尚未执行 GnuCOBOL baseline 对比。
- 2026-07-18: 完成 `FUNCTION ORD(...)` 通用语义模型。parser 将函数名和参数列表构造成 `CEntityIntrinsicFunction`，direct/ST factory 分别选择生成器，ST4 模板只读取模型属性；运行时增加 `CobolIntrinsicFunctions.ord`。
- 2026-07-18: 修复子串、AND、OR 中字段遮蔽导致阶段一语义树丢失的问题，并增加模型保持测试。
- 2026-07-18: 为 `COMPUTE` 目的字段、`DIVIDE` 商/余数、`STRING` 有序拼接项、`EVALUATE` 互斥分支、`PERFORM VARYING` 迭代定义建立明确语义视图并由 ST4 模板格式化。
- 2026-07-18: 修复 `STRING` parser 在 `INTO` 边界丢弃默认 `DELIMITED BY SIZE` 项的问题。
- 2026-07-18: 修复 `PERFORM VARYING TEST BEFORE` 在步长为 1 时错误走 test-after 控制器分支的问题；样例不再多执行一次循环体。
- 2026-07-18: 修复带 PIC 的 01 级 `REDEFINES` 被降级成普通属性的问题，生成声明恢复 `.redefines(...)` 共享存储。
- 2026-07-18: `POST /api/transpile` 对 `TEST-A-STANDALONE.cbl` 返回 `success: true`，生成的 `Test_a.java` 由 API `javac` 自动编译成功；`POST /api/run` 可运行精确大小写类名 `Test_a`。
- 2026-07-18: GnuCOBOL 与 Java 均输出 34 行（标题 + 16×2 场景 + 结束行）。31 行完全一致；剩余差异为 1 行 COMP 接收超出 PIC 位数时的截断规则，以及 2 行 COMP-5 原生端序/高字节显示，属于运行时数值存储语义，不再是 ST4 结构缺失。

当前 Phase 6 精确进度:

- 已完成: ST4 单测基线。
- 已完成: `TESTHELLO.cbl` ST4 转译。
- 已完成: `TESTHELLO.cbl` 生成 Java 编译。
- 已完成: `TEST-A-STANDALONE.cbl` 的 `FUNCTION ORD(...)` 通用语义分析与模板输出。
- 已完成: `TEST-A-STANDALONE.cbl` API 转译及生成 Java 编译。
- 已完成: `TEST-A-STANDALONE.cbl` 运行，共输出 34 行且无额外循环字符。
- 待完成: 统一 COMP 位数溢出截断与 COMP-5 原生端序，使剩余 3 行运行输出与 GnuCOBOL 一致。
- 已推进: `BATCH1.cbl` 已越过 `EVALUATE/WHEN` 语义阻塞并生成 Java。
- 已完成: `BATCH1.cbl` ST4 生成 Java 编译。
- 已完成: `BATCH1.cbl` 生成 Java 可通过 `BatchMain` 运行，输出文件长度和计数符合当前两条输入记录预期。
- 下一步: 清理 debug 输出，补充 BATCH1 端到端脚本化回归，并执行 GnuCOBOL 输出对比。

第一批样例:

- `TEST-A-STANDALONE.cbl`
- `BATCH1.cbl`
- `TESTHELLO.cbl`

验收:

- `TEST-A-STANDALONE.cbl` ST4 转译成功。
- 生成 Java 无手工修复即可编译。
- 运行输出与 GnuCOBOL baseline 一致。

## Phase 7: 迁移 CICS / SQL / BMS / FPac

任务:

- 审计:
  - `CobolIncludeTranscoderEngine`
  - `BMSTranscoderEngine`
  - SQL embedded statements
  - CICS statements
  - FPac generator
- 决定统一接入 ST4 factory，或为 dialect 建独立 ST4 template group。
- 为 CICS/SQL/BMS 增加最小样例测试。

验收:

- 没有生产路径只能走旧 generator。
- dialect-specific 输出也由模板控制。

## Phase 8: ST4 切默认

任务:

- 默认 factory 改为 `CJavaEntityFactoryST`。
- 保留短期回退开关:
  - `-Dnaca.transpiler.factory=direct`
- 本地和 CI 默认跑 ST4。
- direct generator 进入 deprecated 状态。

验收:

- 不带系统属性时生成路径是 ST4。
- 全量测试通过。
- 样例转译、编译、运行通过。

## Phase 9: 删除旧代码生成器

删除前检查:

- 没有生产路径引用旧 `generate/java/verbs/CJava*`。
- 没有测试依赖旧输出。
- ST4 对应功能有单测或端到端覆盖。
- 旧 factory 不再需要。

可删除范围:

- `generate/java/verbs/CJava*`
- 旧 `CJavaEntityFactory`
- 只服务 direct generator 的 string helper
- 已废弃的 exporter 逻辑

谨慎保留:

- 通用 exporter 基类。
- semantic entity。
- runtime library。
- parser/analyzer。

验收:

- 删除后 `./gradlew build` 通过。
- 样例端到端通过。
- 代码库中没有旧 direct generator 引用。

## Phase 10: 质量门禁和文档

任务:

- 写迁移后的开发文档:
  - 如何新增 COBOL 动词模板
  - 如何给 entity 暴露模板属性
  - 什么逻辑不能放模板
  - 什么逻辑不能放 ST controller
- 增加模板 lint:
  - 禁止 `.export`
  - 禁止 `.ExportReference`
  - 禁止模板方法调用
- 增加回归测试说明。
- 记录 ST4 template naming convention。

验收:

- 新增动词时有固定流程。
- 修改最终 Java 输出时优先只改模板。
- 架构规则可测试、可审查。
