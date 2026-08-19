# CardDemo 云原生运行与 PostgreSQL 适配设计

> 状态：Draft for review  
> 目标分支：`codex/carddemo-cloud-native`  
> 上游样例：AWS CardDemo `59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e`  
> 适用模块：`naca-cloud-native`、`naca-rt`、`naca-jlib`、`naca-rt-tests`  
> 不可修改模块：`naca-trans` 的源代码、模板、语义模型和翻译行为

## 1. 文档目的

本设计定义一个可部署、可演示、可回归验证的 CardDemo 云原生服务。服务必须执行
`naca-trans` 已生成的 Java 程序，以 JSON REST 请求简化模拟 BMS/CICS 在线交互，并使用
Docker PostgreSQL 替代 DB2。为了保持 CardDemo 的核心在线业务语义，PostgreSQL 同时作为
VSAM 兼容记录存储和在线会话持久化基础设施。

本设计不是把 COBOL 业务重新手写为 Spring Service，也不是构造一个只返回模拟数据的 UI。
业务判断、字段移动、校验和程序控制流必须来自翻译后的 CardDemo 程序；云原生代码只负责
入口、会话、基础设施适配、结果封装和运行治理。

## 2. 目标和硬约束

### 2.1 目标

1. 在 `naca-cloud-native` 中提供 CardDemo 演示门户和稳定的 REST API。
2. 以构建期生成并编译的 CardDemo Java 类作为唯一业务执行实现。
3. 复用 NacaRT 的 `Form`、`SEND MAP`、`RECEIVE MAP`、COMMAREA 和程序管理语义。
4. 将 BMS 屏幕抽象成 JSON 字段交换，不追求 3270 像素或终端协议的一比一模拟。
5. 使用 Docker PostgreSQL 运行 CardDemo，不要求 DB2 驱动或 DB2 运行时。
6. 在运行时适配 DB2 SQL、SQLCODE、CICS 文件和事务边界，不改变翻译器。
7. 以真实 CardDemo 交易链路和确定性的业务结果作为完成标准。
8. 支持单机开发，并保留多实例、外部会话存储和容器部署能力。

### 2.2 硬约束

- `naca-trans/**` 必须保持零差异；任何缺失能力必须在运行时、基础设施或数据层解决。
- 不在收到 HTTP 请求后临时翻译或编译任意 COBOL。翻译和编译发生在受控构建阶段。
- 不允许客户端提交 Java 类名或任意程序名执行；交易到程序的关系来自只读白名单目录。
- 不把 NacaRT 内部 XML 暴露为公共 API。
- 不直接用 Jackson 序列化 `Form`、`Var` 或 NacaRT 环境对象。
- 不通过静默忽略模拟不支持的 CICS、SQL 或 BMS 选项；必须实现或返回结构化错误。
- 不以改写 COBOL 业务、手写等价 Java 业务服务作为兼容方案。
- Java 源码继续遵守项目现有 ISO-8859-1 编译约束，除非另行批准编码迁移。

## 3. 范围定义

### 3.1 核心在线配置（首要完成标准）

核心配置覆盖 CardDemo 主应用的在线交互：

| 交易 | MapSet | 程序 | 业务能力 |
|---|---|---|---|
| CC00 | COSGN00 | COSGN00C | 登录 |
| CM00 | COMEN01 | COMEN01C | 主菜单 |
| CAVW | COACTVW | COACTVWC | 账户查询 |
| CAUP | COACTUP | COACTUPC | 账户更新 |
| CCLI | COCRDLI | COCRDLIC | 卡片列表 |
| CCDL | COCRDSL | COCRDSLC | 卡片详情 |
| CCUP | COCRDUP | COCRDUPC | 卡片更新 |
| CT00 | COTRN00 | COTRN00C | 交易列表 |
| CT01 | COTRN01 | COTRN01C | 交易详情 |
| CT02 | COTRN02 | COTRN02C | 新增交易 |
| CR00 | CORPT00 | CORPT00C | 交易报表入口 |
| CB00 | COBIL00 | COBIL00C | 账单支付 |
| CA00 | COADM01 | COADM01C | 管理菜单 |
| CU00 | COUSR00 | COUSR00C | 用户列表 |
| CU01 | COUSR01 | COUSR01C | 新增用户 |
| CU02 | COUSR02 | COUSR02C | 更新用户 |
| CU03 | COUSR03 | COUSR03C | 删除用户 |

这些程序原本主要访问 CICS VSAM 文件。为了完整运行核心在线链路，不能简单把文件能力继续
延期；本设计使用 PostgreSQL 实现 CICS 索引记录存储，但保持 NacaRT 已有 CICS 调用形状。

### 3.2 DB2 替换配置（核心配置的组成部分）

下列 CardDemo 扩展原本使用 DB2，目标实现必须在 PostgreSQL 上运行：

| 交易/程序 | MapSet | 能力 |
|---|---|---|
| CTTU / COTRTUPC | COTRTUP | 交易类型新增、编辑 |
| CTLI / COTRTLIC | COTRTLI | 交易类型列表、更新、删除和游标读取 |
| COBTUPDT | 无在线 Map | 交易类型批量维护逻辑的可调用验收 |

DB2 替换指 SQL 数据库语义替换，不是字符串品牌替换。宿主变量绑定、游标、事务、唯一约束、
无数据、重复键、回滚等 COBOL 可观察行为必须保持。

### 3.3 完整扩展配置（后续完成标准）

AWS CardDemo 还提供 IMS/DB2/MQ 授权和 VSAM/MQ 查询模块。为了避免“完整”定义含混，项目
维护两套验收配置：

- `core-online-postgres`：核心在线配置 + DB2/PostgreSQL 配置，必须优先达到绿色。
- `full-extended`：在核心配置之上增加 CPVS、CPVD、CP00、CDRD、CDRA 以及对应批处理程序。

`full-extended` 需要额外的 IMS Repository 和 MQ Port 适配。默认实现可以由 PostgreSQL
Repository 和进程内请求/响应队列提供业务语义，不要求安装 IBM IMS 或 IBM MQ；但其消息
相关性、一次消费、超时、错误码和提交/回滚行为必须由契约测试固定。

### 3.4 非目标

- 3270 数据流、TN3270 协议和 24×80 终端的一比一模拟。
- BMS 自动生成高保真 HTML 页面。
- JCL 解析器、通用批量调度平台、GDG/PDS 管理平台。
- 在门户中上传并执行任意 COBOL 或 Java。
- 将所有主机安全、RACF、IMS 或 MQ 管理能力产品化。
- 一开始就实现分布式 XA；首版使用单 PostgreSQL 本地事务覆盖核心业务。

## 4. 当前资产与缺口

### 4.1 已有资产

- `naca-trans` 已有 BMS 解析、Map/Save Map 生成和 `SEND/RECEIVE MAP` 代码生成。
- NacaRT `Form` 能导入和导出 Map 字段，包含字段值、长度及部分输入/显示属性。
- NacaRT 在线环境已有 `OnlineSession`、`OnlineEnvironment`、COMMAREA、程序装载和
  `SEND/RECEIVE MAP` 调用骨架。
- NacaRT/JLib 已有 JDBC、PreparedStatement、SQL 状态、游标和提交/回滚能力。
- NacaRT 已有 Spring `DatabaseProperties`、HikariCP 和 `JdbcTemplate` 初步配置。
- 当前 CardDemo 验收账本固定了 44 个 COBOL 程序；其中 3 个批处理程序已完成严格执行验收。
- 上游固定版本包含 17 个基础 BMS MapSet，以及 DB2、IMS、MQ 可选扩展资产。

### 4.2 关键缺口

1. 当前仓库只固定了少量 CardDemo 源码和数据，完整在线 COBOL/BMS/copybook 尚未进入离线
   可重复构建资产。
2. `naca-cloud-native` 当前排除了 `DataSourceAutoConfiguration`，没有 PostgreSQL 驱动、
   Flyway、CardDemo Schema 或就绪检查。
3. 旧连接管理显式偏向 DB2/Oracle；PostgreSQL 驱动识别和错误映射未形成契约。
4. `SEND MAP` 的 ALARM、ERASE、FREEKB、ACCUM、PAGING、WAIT 等选项有空实现。
5. `RECEIVE MAP` 依赖内部 XML，且 `MAPSET` 校验不完整。
6. 核心在线程序依赖 CICS READ/WRITE/REWRITE/DELETE/STARTBR/READNEXT/READPREV/ENDBR；
   当前索引文件运行时未接入可用存储后端。
7. 在线 REST 会话、COMMAREA 持久化、请求去重、并发控制和交易白名单不存在。
8. DB2 SQL 方言差异与 PostgreSQL SQLSTATE 到 COBOL SQLCODE 的映射没有治理清单。
9. 当前在线语料基线仍会因部分 SQL/CICS 语句静默丢失而保持红色。在不修改翻译器的硬约束
   下，只有已被翻译器正确保留下来的程序才能进入运行实施；其余情况必须登记为外部阻塞，
   不能在云原生层伪造业务结果。

## 5. 总体架构

```text
Browser / API Client
        |
        | JSON POST
        v
CardDemo REST Controller
        |
        v
Transaction Application Service
  | session | idempotency | unit of work | allowlist
        |
        v
Generated Program Registry -------- Build-time generated CardDemo classes
        |
        v
NacaRT Online Runtime
  | RECEIVE MAP / SEND MAP
  | COMMAREA / XCTL / RETURN / LINK
  | SQL / CICS file commands / compatibility calls
        |
        +--------------------+-----------------------+
        |                    |                       |
        v                    v                       v
JSON Map Gateway      PostgreSQL SQL Adapter   CICS Record Store Port
        |                    |                       |
        +--------------------+-----------------------+
                             |
                             v
                    Docker PostgreSQL
         relational tables | VSAM records | sessions | outbox
```

### 5.1 模块责任

#### `naca-trans`

作为封闭的构建期依赖使用。输入 COBOL/BMS/copybook，输出 Java 和 Map 产物。本项目不修改其
解析、语义实体、ST4 模板、绑定或输出规则。

#### `naca-rt`

保留 COBOL/CICS 可观察语义，并定义基础设施端口：

- Map 输入/输出快照；
- CICS 索引记录操作；
- 程序跳转和 COMMAREA 快照；
- SQL 方言与错误码适配钩子；
- 外部兼容函数和程序调用注册表。

NacaRT 不依赖 Spring Web 或 PostgreSQL 实现类。

#### `naca-jlib`

负责 JDBC 连接包装、PreparedStatement、驱动识别和底层 SQL 执行。只允许增加通用
PostgreSQL 能力，不得加入 CardDemo 业务规则。

#### `naca-cloud-native`

承载所有云原生入口和具体基础设施实现，不增加新的 Gradle 子模块。建议包结构：

```text
com.publicitas.naca.cloudnative.carddemo
├── api                 # Controller、DTO、异常映射
├── application         # 交易编排、UOW、幂等、白名单
├── catalog             # transaction/program/map 注册目录
├── map                 # JSON 与 NacaRT Map 快照转换
├── runtime             # NacaRT 在线环境启动和结果提取
├── session             # 会话、COMMAREA、并发版本
├── infrastructure
│   ├── postgres        # SQL、VSAM、会话、迁移实现
│   └── messaging       # full-extended 配置使用
└── observability       # 指标、审计、健康检查
```

#### `naca-rt-tests`

保存固定来源、哈希、翻译/编译/运行验收及业务黄金场景。在线资产必须采用与现有 CardDemo
账本相同的来源固定和哈希校验方式。

## 6. 构建期生成与制品布局

### 6.1 资产固定

完整在线资产从固定提交导入仓库测试资源或独立、可校验的样例资源目录。必须记录：

- 上游 URL 和 commit；
- Apache-2.0 许可证；
- 每个 COBOL、BMS、copybook、DCL、DDL 和数据文件的 SHA-256；
- 字符编码和记录长度；
- 被纳入或排除的原因。

构建和测试不能依赖运行时网络下载上游仓库。

### 6.2 生成目录

生成物不手工维护，建议布局：

```text
naca-cloud-native/build/generated/carddemo/
├── java/               # naca-trans 生成 Java
├── maps/               # BMS 生成结构和兼容产物
├── classes/            # 编译后的 CardDemo 类
├── reports/            # 翻译、编译、能力清单
└── manifest.json       # 来源、类、交易、Map、哈希
```

应用镜像只包含编译后的受信类、Map 元数据和 manifest，不包含运行时 Java 编译器入口。

### 6.3 构建门禁

- 所有目标 COBOL/BMS 成功分析并输出；
- 不允许 `naca-trans` 目录出现 diff；
- 生成 Java 使用 JDK 21 编译；
- manifest 中每个交易都能解析到唯一程序和允许的 MapSet；
- 不允许未解析 COPY、DCL、BMS 或外部程序依赖；
- 不允许静默丢弃 EXEC CICS/EXEC SQL；
- `finalArchitectureCheck` 和现有 ST4 门禁保持绿色。

## 7. BMS JSON Map Gateway

### 7.1 设计原则

- BMS 提供字段结构和主机交互语义，不决定 Web 布局。
- 客户端只提交允许修改的字段和 AID；服务端不信任客户端回传的保护、长度或类型属性。
- 缺失字段表示未修改；显式 `modified=true` 且空值表示用户清空字段。
- 内部第一阶段继续复用 NacaRT XML Document，JSON/XML 只在边界转换。
- 公共契约保持传输中立，未来移除 XML 不改变 API。

### 7.2 API

启动或继续一个交易：

```http
POST /api/v1/carddemo/transactions/{transactionId}
Content-Type: application/json
Idempotency-Key: <uuid>
```

请求：

```json
{
  "conversationId": "optional-opaque-id",
  "mapSet": "COSGN00",
  "map": "COSGN0A",
  "aid": "ENTER",
  "fields": {
    "USERIDI": {
      "value": "USER0001",
      "modified": true
    },
    "PASSWDI": {
      "value": "PASSWORD",
      "modified": true
    }
  }
}
```

响应：

```json
{
  "conversationId": "opaque-id",
  "requestId": "uuid",
  "transactionId": "CC00",
  "program": "COSGN00C",
  "nextTransactionId": "CM00",
  "mapSet": "COMEN01",
  "map": "COMEN1A",
  "cursorPosition": 0,
  "terminal": {
    "alarm": false,
    "erase": true,
    "freeKeyboard": true
  },
  "fields": [
    {
      "name": "TITLEO",
      "value": "CardDemo Main Menu",
      "length": 40,
      "protected": true,
      "numeric": false,
      "required": false
    }
  ],
  "messages": [],
  "completed": false,
  "version": 2
}
```

### 7.3 AID 与字段规则

支持 `ENTER`、`CLEAR`、`PF1` 至 `PF24`、必要的 PA 键和终端结束动作。未知键返回 400，
不能降级成 ENTER。

服务端按生成的 Map 结构校验：

- 字段是否存在；
- 字段是否可输入；
- 最大长度；
- 数字属性；
- Map/MapSet 是否与当前会话屏幕一致；
- 当前版本是否与会话版本一致。

### 7.4 SEND MAP 结果

运行时输出快照至少保存：

- Map 和 MapSet；
- `Form` 字段值和服务端属性；
- cursor 或 cursorPosition；
- ALARM、ERASE、FREEKB；
- DATAONLY；
- 结构化不支持选项。

首版不实现 PAGING/ACCUM/WAIT 的终端行为时，必须在目标程序能力清单中证明这些选项不可达，
否则交易启动失败并返回 `CICS_OPTION_UNSUPPORTED`。

### 7.5 前端

门户可以对核心页面手工编排简单表单，也可以使用通用字段渲染器。无论采用哪种方式，前端只
消费 REST DTO，不读取 NacaRT XML，不复刻 COBOL 业务校验。

## 8. 在线会话和 CICS 程序控制

### 8.1 会话状态

会话记录包含：

- 不透明 `conversationId`；
- 当前 transaction/program/map/mapset；
- COMMAREA 原始字节和长度；
- 当前用户和授权上下文；
- 最后响应版本；
- 创建、访问、过期时间；
- 执行锁/乐观版本；
- 最近幂等键和响应摘要。

会话默认持久化在 PostgreSQL，而不是 HTTP Session 或单 JVM Map，以支持重启恢复和多实例。
演示模式可配置内存实现，但它不能作为完整验收的唯一实现。

### 8.2 一次请求的语义

一个 REST POST 对应一个 CICS 交易工作单元：

1. 校验交易白名单、会话版本和幂等键。
2. 锁定会话，借用一条 PostgreSQL Connection。
3. 恢复 COMMAREA、当前程序和输入 Map。
4. 执行翻译后的程序，跟随受控 `LINK`/`XCTL`。
5. 捕获 `SEND MAP`、`RETURN`、下一 transaction 和 COMMAREA。
6. 正常 RETURN 或 SYNCPOINT 时提交；ABEND/异常时回滚。
7. 保存会话和响应，释放锁并返回 JSON。

SQL、VSAM 记录和会话更新尽可能使用同一 PostgreSQL 本地事务，避免首版 XA。

### 8.3 程序目录

交易目录是构建生成、应用启动时只读加载的白名单：

```text
CC00 -> COSGN00C -> COSGN00
CM00 -> COMEN01C -> COMEN01
...
```

`XCTL` 和 `LINK` 只能解析目录中声明的目标。类加载使用固定 ClassLoader，不接受请求提供的
包名、路径或字节码。

### 8.4 并发和幂等

- 同一 conversation 同时只允许一个执行请求。
- 更新交易要求 `Idempotency-Key`；相同键返回首次响应，不重复执行业务。
- 会话版本冲突返回 HTTP 409 和 `CONVERSATION_VERSION_CONFLICT`。
- 执行超时后回滚数据库事务，标记会话可重试或终止，不能返回不确定成功。

## 9. PostgreSQL 基础设施

### 9.1 Docker 开发环境

开发环境使用仓库根或云原生模块内的 Compose 配置启动固定主版本 PostgreSQL。设计配置为：

```yaml
service: carddemo-postgres
image: postgres:16-alpine
database: carddemo
user: carddemo
port: 5432
healthcheck: pg_isready
volume: named volume
```

密码通过 `.env` 或环境变量提供，示例文件不得包含生产密码。应用通过以下环境变量连接：

```text
NACA_DATABASE_URL=jdbc:postgresql://localhost:5432/carddemo
NACA_DATABASE_USERNAME=carddemo
NACA_DATABASE_PASSWORD=carddemo
NACA_DATABASE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

### 9.2 连接管理

`naca-cloud-native` 使用 Spring 管理的 Hikari DataSource。每个 CardDemo 请求借用一个 JDBC
Connection，并通过 NacaRT 已有外部连接入口绑定到当前执行环境。这样：

- 翻译后的 SQL 继续调用现有 NacaRT SQL API；
- CICS Record Store 和会话可共享同一连接；
- 不需要应用同时维护 Hikari 和旧 JLib 两套独立连接池；
- commit/rollback 仍由 CICS/NacaRT 工作单元驱动。

JLib 必须识别 PostgreSQL 驱动，但业务服务不调用 `initDB2()`。旧 DB2/Oracle 启动入口保留给
兼容场景，CardDemo 云原生配置不使用它们。

### 9.3 Schema 迁移

使用 Flyway 维护版本化迁移：

```text
db/migration/carddemo/
├── V001__runtime_schema.sql
├── V002__core_record_store.sql
├── V003__db2_compat_tables.sql
├── V004__carddemo_seed.sql
└── V005__session_and_idempotency.sql
```

Docker init 脚本只负责数据库/扩展的最低初始化；业务表和种子数据必须由 Flyway 管理。迁移
必须支持空库启动，并在 Schema 版本不匹配时使 readiness 失败。

### 9.4 Schema 分区

- `carddemo_runtime`：会话、幂等、程序/能力版本、可选 outbox。
- `carddemo_vsam`：CICS 记录存储。
- `carddemo`：DB2 替代表和后续关系型业务表。
- `carddemo_compat`：必要的 DB2 兼容函数/视图。

应用角色遵循最小权限；迁移角色与运行角色分离是生产部署目标。

## 10. DB2 到 PostgreSQL 的兼容策略

### 10.1 适配位置

遵循以下优先级，避免任意 SQL 改写：

1. **Schema 兼容**：优先使用相同的非引号表名、列名、数据类型和约束。
2. **PostgreSQL 兼容对象**：通过 view/function/domain 模拟简单 DB2 对象。
3. **NacaRT SQL Dialect Adapter**：只做清单化、AST 特征明确或词法边界明确的重写。
4. **失败关闭**：未登记方言返回 `DB2_DIALECT_UNSUPPORTED`，不得直接把未知 SQL 交给数据库。

适配器作用于翻译器产生的 SQL 字符串和 JDBC 执行之间，因此不改变翻译结果。

### 10.2 预期兼容项

在导入完整 DB2 程序后生成机器可读 SQL 清单，并至少分类：

- `CHAR/VARCHAR/SMALLINT/INTEGER/BIGINT/DECIMAL`；
- `DATE/TIME/TIMESTAMP`；
- `CURRENT DATE`、`CURRENT TIME`、`CURRENT TIMESTAMP`；
- `FETCH FIRST n ROWS ONLY`；
- `WITH UR`；
- identity、sequence 和 generated key；
- cursor、OPEN/FETCH/CLOSE；
- `SELECT ... INTO` 宿主变量；
- NULL indicator；
- DB2 schema qualifier；
- `SYSIBM.SYSDUMMY1` 或 `VALUES`；
- `MERGE`、特殊寄存器和 DB2 函数（如有）。

每条规则包含输入、输出、适用程序、测试和是否可逆。禁止使用不考虑字符串字面量、注释和
标识符边界的全局正则替换。

### 10.3 数据类型与字符语义

- COBOL PIC 9/COMP/COMP-3 映射到 PostgreSQL `numeric` 或整数类型时不能丢失 scale。
- 固定 CHAR 的填充、空格比较和截断必须通过 JDBC 集成测试固定。
- 日期时间在数据库使用原生类型，COBOL 格式转换位于 NacaRT 兼容函数，不散落在 Controller。
- 未加引号的 DB2 大写标识符映射为 PostgreSQL 小写对象；迁移脚本统一使用未加引号标识符。
- 所有外部输入通过宿主变量/PreparedStatement 绑定，不拼接到 SQL。

### 10.4 SQLCODE 映射

业务程序观察的是 SQLCODE/SQLSTATE，而不是 Java 异常。PostgreSQL 异常适配至少固定：

| 语义 | COBOL/DB2 可观察结果 |
|---|---|
| 成功 | SQLCODE 0 |
| SELECT/FETCH 无数据 | SQLCODE +100 |
| 唯一键冲突 | SQLCODE -803 |
| 外键冲突 | 对应 CardDemo 预期的负 SQLCODE |
| 非空约束 | 对应 CardDemo 预期的负 SQLCODE |
| serialization/deadlock | 可重试的负 SQLCODE，事务回滚 |
| 未支持方言 | 结构化运行时错误，不伪装成 SQLCODE 0 |

最终数值以 CardDemo 程序实际判断分支和 DB2 基线为准；映射表必须是测试数据，而不是散落的
`if` 语句。

### 10.5 事务

- `EXEC CICS SYNCPOINT`/SQL COMMIT 提交当前 PostgreSQL UOW。
- ROLLBACK、ABEND、运行时异常回滚 SQL、记录存储和会话状态。
- cursor holdability 按目标程序需要逐项验证；不假设 PostgreSQL 与 DB2 默认值相同。
- 更新交易使用明确隔离级别，核心验收至少覆盖并发更新冲突。

## 11. PostgreSQL CICS Record Store

### 11.1 为什么需要

CardDemo 核心在线程序并非只依赖 DB2。用户、账户、卡、交易和账单主要通过 CICS VSAM
命令访问。如果不实现这些语义，登录后的主要业务全部不可达。

### 11.2 端口

NacaRT 定义与现有 fluent CICS API 对接的存储端口，支持：

- READ；
- WRITE；
- REWRITE；
- DELETE；
- STARTBR；
- READNEXT；
- READPREV；
- RESETBR（目标程序需要时）；
- ENDBR。

端口输入输出保持文件逻辑名、键字节、记录字节、长度、RESP/RESP2 和浏览句柄。翻译后的
程序不感知 PostgreSQL。

### 11.3 物理模型

首版使用忠实的通用固定记录表：

```text
file_name       varchar
primary_key     bytea
record_data     bytea
record_length   integer
version         bigint
updated_at      timestamptz
```

主键为 `(file_name, primary_key)`。`bytea` 保留 COBOL 固定记录，避免为每个 copybook 手工
重写业务。加载器依据 copybook/文件清单从上游 ASCII/EBCDIC 样例构造键和记录。

### 11.4 语义要求

- READ 未找到、重复 WRITE、REWRITE 不存在、DELETE 不存在必须返回正确 CICS RESP。
- 浏览顺序必须匹配目标键编码的字节排序；ASCII/EBCDIC 差异必须由 fixture 验证。
- UPDATE 模式使用行锁或版本检查，防止丢失更新。
- STARTBR 产生请求/会话内句柄；READNEXT/READPREV 不把 JDBC ResultSet 跨 HTTP 请求保存。
- 所有记录操作加入当前 PostgreSQL UOW。
- 文件名到键描述、记录长度、编码的映射来自受控 manifest。

长期可以为查询热点建立关系型投影，但投影不是翻译后程序的权威写路径。

## 12. 基础设施兼容层

### 12.1 IBM LE 和工具调用

`CEE3ABD`、日期格式转换等外部调用通过 NacaRT 程序/函数注册表适配，保持 CALL 名称和参数
约定。适配器按以下分类实现：

- 纯函数：日期、时间、编码、格式转换；
- 控制函数：ABEND、返回码；
- 环境函数：用户、终端、当前时间；
- 外部系统：MQ、IMS。

适配器不得吞掉错误；例如 `CEE3ABD` 必须产生可识别 ABEND、触发回滚并记录 COBOL abcode。

### 12.2 IMS/MQ 扩展

`full-extended` 配置引入端口而不是 IBM 客户端硬编码：

- `AuthorizationRepository` 模拟目标 IMS 业务实体和层级读取；
- `MessageRequestReplyPort` 模拟 MQ OPEN/GET/PUT/CLOSE 的相关性和错误码；
- `Outbox` 保证数据库更新与待发送消息在一个 PostgreSQL 事务内落库。

首个实现可以是 PostgreSQL + 进程内 worker；将来可换成真正 MQ/Kafka，而不改变生成程序调用
或业务 REST 契约。

### 12.3 时间

运行时注入 `Clock`。测试使用固定时间，生产使用 UTC 存储和配置时区展示，避免 ASKTIME、
FORMATTIME、报表日期随机器环境漂移。

## 13. 错误模型

公共错误响应：

```json
{
  "error": {
    "code": "CICS_RECORD_NOT_FOUND",
    "message": "The requested record was not found",
    "transactionId": "CAVW",
    "program": "COACTVWC",
    "conversationId": "opaque-id",
    "requestId": "uuid",
    "retryable": false,
    "details": {}
  }
}
```

错误分层：

- API 校验：400；
- 会话/版本冲突：409；
- 业务 Map 错误：通常返回正常屏幕响应和字段消息；
- 未支持运行时能力：422 或 501，附能力编号；
- 数据库暂时故障：503，只有确认回滚时才允许重试；
- ABEND：500，记录 abcode，不泄露栈和密码。

COBOL 程序已经处理并显示的业务错误不得被 Controller 改写成 HTTP 异常。

## 14. 安全设计

- 交易、程序、MapSet 和字段均使用白名单。
- 登录密码不写日志；演示账户只用于本地配置。
- conversationId 使用高熵不透明值，不包含 COMMAREA 或用户数据。
- 对请求体、字段数量、字段长度、会话期限和程序执行时间设置上限。
- JSON 到内部 XML 使用 DOM 构造，不解析客户端 XML，关闭外部实体能力。
- SQL 全部使用 PreparedStatement；动态表名必须来自 manifest。
- 更新 API 使用幂等键和 CSRF/CORS 明确配置。
- 生产镜像以非 root 用户运行，数据库凭据来自 Secret。
- 管理/诊断 API 不返回 SQL 参数、卡号全值、密码或原始固定记录。

## 15. 可观测性和运维

### 15.1 日志上下文

每次执行记录：

- requestId、conversationId（脱敏）、transactionId、program；
- 起止 Map、下一交易；
- 执行耗时和程序跳转次数；
- SQL/CICS 操作类型、耗时、结果码，不记录敏感参数；
- commit/rollback/abend 结论。

### 15.2 指标

- 交易请求数、成功率、业务错误、ABEND；
- 按 program/transaction 的延迟；
- PostgreSQL 连接池、SQL 延迟和回滚；
- CICS READ/WRITE/REWRITE/DELETE/browse 计数；
- 活跃/过期会话、版本冲突、幂等命中；
- 不支持能力命中数，目标为零。

### 15.3 健康检查

- liveness：JVM 和应用上下文；
- readiness：PostgreSQL、Flyway 版本、程序 manifest、Map catalog、种子数据版本；
- capability：每个目标交易的 `translated/compiled/runtimeDependenciesReady/accepted` 状态。

## 16. 测试和验收策略

### 16.1 测试层次

1. **资产测试**：来源、哈希、编码、记录长度、44 程序账本。
2. **翻译契约**：调用现有 naca-trans，确认目标语句保留；不修改翻译器。
3. **运行时单元测试**：Map、SQL 方言、SQLCODE、CICS RESP、COMMAREA。
4. **PostgreSQL 集成测试**：使用真实 PostgreSQL 容器，不以 H2 代替。
5. **API 契约测试**：JSON schema、字段 modified/cleared、AID、错误模型。
6. **纵向业务测试**：真实生成程序 + Map + PostgreSQL + REST。
7. **回归测试**：现有 sampleAcceptance、CardDemo 批处理、架构和质量门禁。

### 16.2 最小 Map 证明

在接入 CardDemo 前，使用受控小程序证明：

```text
JSON -> RECEIVE MAP -> COBOL IF/MOVE -> SEND MAP -> JSON
```

覆盖 ENTER、PF3、modified、清空字段、protected 字段拒绝、cursor、alarm、会话恢复和回滚。

### 16.3 核心业务黄金场景

至少固定以下端到端场景：

1. USER0001/PASSWORD 登录并进入主菜单。
2. 错误密码返回原程序业务消息，不建立已认证会话。
3. 查询账户、卡列表和卡详情。
4. 更新账户并在重新查询时可见。
5. 查询交易列表/详情并新增交易。
6. 支付账单，余额和交易记录在同一事务更新。
7. 管理员新增、更新、删除用户。
8. PostgreSQL 交易类型列表、游标分页、新增、更新、删除。
9. 重复 Idempotency-Key 不产生第二次更新。
10. 数据库故障或 ABEND 后业务数据与会话均回滚。

每个场景保存请求序列、响应字段断言、最终数据库状态和程序路径。不能只断言 HTTP 200。

### 16.4 完成定义

单个交易达到 `accepted` 必须同时满足：

- 真实固定源完成递归 ST4 翻译；
- Java 编译；
- 所有外部依赖有明确实现；
- REST 首屏和至少一个提交/返回路径通过；
- 业务输出/状态与 CardDemo 基线一致；
- PostgreSQL 提交与回滚通过；
- 无静默降级；
- 加入机器可读验收账本。

`core-online-postgres` 完成要求范围内全部交易 accepted，且 Docker 一键启动后可从登录连续操作
至各核心页面。`full-extended` 只有在额外 IMS/MQ 交易也 accepted 后才可宣称完整扩展完成。

## 17. 实施阶段

### Phase 0：设计和资产基线

交付：

- 本设计评审通过；
- 完整在线资产和许可证固定；
- 交易/程序/Map/文件/SQL/外部调用清单；
- `naca-trans` 零修改门禁；
- 核心和扩展验收账本。

退出条件：每个目标程序都有确定依赖和首个技术阻塞，不存在“未探测即支持”的声明。

### Phase 1：PostgreSQL 平台骨架

交付：

- Docker Compose、Hikari、PostgreSQL Driver、Flyway；
- Schema、种子数据、readiness；
- Spring Connection 到 NacaRT 执行环境的桥接；
- SQLCODE 和事务基础测试。

退出条件：空库一键启动，应用可以在同一 UOW 执行 SQL、回滚并报告健康。

### Phase 2：BMS JSON 最小纵切

交付：

- REST DTO、Map gateway、会话存储；
- SEND/RECEIVE Map 快照和终端 flags；
- 最小 COBOL/BMS 端到端程序；
- capability endpoint。

退出条件：最小 Map 证明全部通过，内部 XML 未暴露。

### Phase 3：登录和菜单

交付：CC00、CM00、CA00；用户安全记录加载；XCTL/RETURN/COMMAREA。

退出条件：真实 COSGN00C 登录成功/失败并导航到真实菜单程序。

### Phase 4：PostgreSQL CICS Record Store

交付：READ/WRITE/REWRITE/DELETE/STARTBR/READNEXT/READPREV/ENDBR 和数据加载器。

退出条件：核心文件操作 RESP、顺序、锁和回滚测试通过。

### Phase 5：账户、卡和用户纵切

交付：CAVW、CAUP、CCLI、CCDL、CCUP、CU00-CU03。

退出条件：从菜单连续完成查询和修改，数据库状态与 Map 响应一致。

### Phase 6：交易、账单和报表纵切

交付：CT00-CT02、CB00、CR00；时间兼容函数；必要报表状态。

退出条件：新增交易和支付账单具备原子性，回滚场景通过。

### Phase 7：DB2/PostgreSQL 扩展

交付：DB2 SQL 清单、方言规则、DCL/DDL 迁移、CTTU、CTLI、COBTUPDT。

退出条件：游标、CRUD、约束错误、SQLCODE 和同步点全部通过 PostgreSQL 验收。

### Phase 8：完整扩展和加固

交付：IMS/MQ 端口实现、CPVS/CPVD/CP00/CDRD/CDRA；安全、性能、故障恢复、镜像。

退出条件：`full-extended` 账本绿色；若用户只批准核心范围，则该阶段保持显式未完成，不影响
`core-online-postgres` 的完成声明。

## 18. 提交与变更纪律

- 文档、基础设施、运行时行为、资产导入和业务验收分开提交。
- 每个阶段先增加失败的契约/验收，再实现运行时能力。
- 不修改生成 Java；生成物通过重新翻译产生。
- 每个 PR 附 `git diff -- naca-trans` 零差异证明。
- 数据库迁移只能前向追加，不重写已发布版本。
- 能力账本只能由未支持向已验证推进，不能把缺失能力改名隐藏。
- 所有未支持分支 fail closed，并包含 program、source location 和 capability code。

## 19. 主要风险与缓解

| 风险 | 影响 | 缓解 |
|---|---|---|
| 在线 EXEC CICS/SQL 未被现有翻译器完整保留 | 硬阻塞真实业务 | Phase 0 fail-closed 盘点；不在云层伪造；将阻塞反馈为独立翻译器议题 |
| VSAM 键排序与编码不同 | 列表、browse、查找错误 | bytea 键、真实数据 fixture、双向 browse 黄金测试 |
| DB2/PostgreSQL 方言或错误码差异 | 进入错误 COBOL 分支 | 清单化规则、真实 PostgreSQL 测试、SQLSTATE 映射表 |
| 旧 NacaRT 在线环境线程安全不足 | 会话串扰 | 每请求独立环境、显式会话快照、禁止共享可变程序实例 |
| XCTL/LINK 循环或任意程序执行 | 资源耗尽/安全问题 | 白名单、最大跳转深度、执行超时 |
| REST 重试导致重复写 | 重复交易或支付 | Idempotency-Key 与同一 UOW 响应存档 |
| 部分 CICS 选项空实现 | 表面成功、语义错误 | 能力预检和 fail-closed，目标可达选项必须实现 |
| 完整 CardDemo 定义不断扩张 | 无法收口 | `core-online-postgres` 与 `full-extended` 两套固定账本 |
| 直接重写业务以绕过阻塞 | 演示失真 | 业务执行路径必须能追踪到生成程序和源段落 |

## 20. 架构决策摘要

1. **不修改 naca-trans**：所有云迁移差异在生成程序以下适配。
2. **构建期翻译**：运行时只加载受信的已编译程序。
3. **JSON Map 而非 3270**：保留字段/AID/Map 语义，放弃终端像素模拟。
4. **内部兼容 XML 暂时保留**：仅作为 NacaRT 内部桥，不成为公共协议。
5. **PostgreSQL 为核心单一基础设施**：承接 DB2 表、VSAM 记录、会话和幂等。
6. **一个 POST 一个 CICS UOW**：SQL、记录和会话尽可能使用同一数据库事务。
7. **通用记录表优先**：保持固定记录语义，避免手工重写 CardDemo 数据访问业务。
8. **Schema 兼容优先于 SQL 重写**：重写只允许白名单规则。
9. **完整性用两套 Profile 定义**：核心在线与 IMS/MQ 可选扩展分别验收。
10. **能力失败关闭**：没有实现的运行时语义不能伪装成功。

## 21. 设计评审通过条件

开始写实现代码前，应确认：

- 是否接受 `core-online-postgres` 作为第一完成里程碑；
- 是否接受 PostgreSQL 同时承接 VSAM 兼容记录存储；
- 是否接受内部 XML 作为首版临时兼容桥；
- 是否接受构建期生成、运行时禁止任意编译；
- 是否接受 `full-extended` 的 IMS/MQ 作为后续独立 Profile；
- 是否接受 Flyway、Hikari 和 PostgreSQL 16 作为默认技术选型；
- 是否接受一个 REST POST 对应一个 CICS UOW 和 PostgreSQL 本地事务。

以上决策通过后，实施从 Phase 0 的在线资产固定和能力清单开始，而不是先搭 Controller 或
编写模拟业务。
