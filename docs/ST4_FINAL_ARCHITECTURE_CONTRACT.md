# ST4 最终架构契约

## 1. 目标

同一棵 COBOL semantic tree 必须能在不修改 semantic 类、不重新做语义分析的前提下，分别交给 Java、Go、Rust 或其他 backend。

最终分层：

1. Parser：产生语法结构。
2. Semantic analysis：解析名称、类型、作用域、引用、表达式、控制流和优化结果，产生完整的目标无关 IR。
3. Backend binding：只负责根据 semantic 运行时类型选择模板，并把 semantic 子节点递归包装为 ST。
4. STG：负责目标语言的所有词法、语法、布局、缩进、括号、语句选择和小模板组合。
5. Root writer：只在整棵 ST tree 完成后调用一次 `render()`。

## 2. Semantic 层零容忍规则

`semantic/**` 中每个 Java 类都必须单独通过以下检查：

- 不得 import 或引用 `generate.*`、`org.stringtemplate.*`、`CJava*`、`TemplateLoader`。
- 不得保存 `CBaseLanguageExporter`、writer、template、backend 或 output 对象。
- 构造器不得接收 exporter/backend。
- 不得声明或调用 `Export*`、`DoExport`、`Write*`、`FormatIdentifier`、`StartOutputBloc`、`EndOutputBloc`。
- 不得提供 `codeString`、`referenceString`、`childrenCode`、`bodyCode` 等预渲染属性。
- 不得包含 Java/Go/Rust 关键字、标点、runtime API 名、类名或调用字符串。
- semantic `String` 只能表示源程序名称、literal、源位置或目标无关的语义值。
- 分支选择、忽略节点、有效 update kind、primary iteration、closing order 等必须在 semantic analysis 结束前形成明确字段或 enum。
- 生成任意 backend 后 semantic tree 不得发生变化；同一棵树连续生成两次必须等价。

## 3. Backend binding 零容忍规则

- 不得存在每个 semantic 类对应一个 `Java*Renderer` 的终态架构。
- semantic type 到 template name 的关系必须位于 backend 自己的声明式 manifest，不放进 semantic 类。
- 同一 semantic 类型存在引用、声明或 root 等不同语法角色时，binding key 可以包含显式 role；role 必须由父上下文传播，不能通过检测具体 Java 子类或预渲染字符串猜测。
- `semantic-bindings.properties` 是 concrete semantic entity 的唯一、精确 inventory；不得包含抽象基类或运行时别名。
- `semantic-runtime-bindings.properties` 仅承载 Java backend 的继承层 dispatch alias；它不能替代 concrete inventory，也不能改变其他 backend 的 semantic 模型。
- binding 只允许：类型分派、属性读取、semantic child/collection 到 ST/`List<ST>` 的递归包装。
- binding 不允许：`if/switch` 决定输出形式、循环组装语句片段、构造括号/分号/关键字、选择 runtime API、预渲染子节点。
- atomic attribute renderer 只允许处理目标语言 identifier 规则和 literal escaping，不允许渲染语句或表达式。
- missing binding 必须立即失败，不得 fallback 到 direct generator 或 `Export*`。

## 4. STG 零容忍规则

- 不得出现 `codeString`、`referenceString`、`childrenCode`、`bodyCode`。
- 不得调用 semantic 的 `Export*`、`DoExport`、`render()` 或其他生成方法。
- 异构子节点由递归 binding 提供 ST；同构列表使用 ST4 template application 组合。
- 语句选择、关键字、操作符、运行时方法名、括号、缩进、换行和目标语言类型名全部归 STG。
- 只能在 root writer 中将 ST tree 扁平化一次。

## 5. Factory 和类型边界

- Parser/semantic analysis 只能创建目标无关 semantic 具体类。
- `CJavaEntityFactory`、`CGoEntityFactory`、`CRustEntityFactory` 不得参与 semantic tree 构建。
- Java backend 类不得 `extends CEntity*` 来重写输出方法。
- backend 的 concrete semantic 类型必须被主 manifest 完整覆盖；多一项、少一项或歧义映射都失败。运行时别名必须通过独立文件显式加载，不能混入主 inventory。

## 6. 校验命令与完成定义

- 日常迁移回归：`./gradlew :naca-trans:test`。
- 最终零容忍契约：`./gradlew :naca-trans:finalArchitectureCheck`。
- 后者在当前阶段预期失败，且必须逐类列出所有违规，不使用 allowlist。
- 日常任务排除 `final-architecture` tag；专项任务只包含该 tag。二者不能因为共享同一个默认 `test` 任务而互相污染结果。
- 任何语法项只有在行为测试通过、对应旧生成路径删除，且零容忍契约不再报告该类时，才能标记为“最终完成”。

## 7. 当前迁移与最终完成的区分

- “行为已覆盖”：有 semantic tree 和 direct parity 测试，但可能仍有 typed Java renderer。
- “最终完成”：semantic 类目标无关，无 direct subclass，无 typed renderer，只由 backend manifest + STG 生成，并通过 `finalArchitectureCheck`。
- 目前已实现的 recursive renderer 全部只能记为过渡性行为验证，不得作为最终迁移完成计数。
