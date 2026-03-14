# Naca 项目架构分析

**文档生成日期**: 2026-03-11
**目的**: 为后续重构工作提供架构参考

---

## 一、项目概述

**Naca** 是一个 **COBOL 到 Java 的转译器（Transpiler）** 和运行时环境，用于将传统的 COBOL 程序自动转换为 Java 代码，并提供完整的运行时支持。

---

## 二、整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        Naca 项目                                 │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  naca-jlib   │  │  naca-trans  │  │   naca-rt    │          │
│  │  基础工具库   │  │  COBOL 转译器 │  │  运行时环境   │          │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘          │
│         │                 │                  │                   │
│         └─────────────────┼──────────────────┘                   │
│                           ▼                                      │
│                  ┌───────────────┐                               │
│                  │ naca-rt-tests │                               │
│                  │   测试套件     │                               │
│                  └───────────────┘                               │
└─────────────────────────────────────────────────────────────────┘
```

### 模块依赖关系

```
naca-rt-tests
    ├── naca-rt
    │   └── naca-jlib
    └── naca-jlib

naca-trans
    └── naca-jlib
```

---

## 三、模块详细分析

### 1. naca-jlib - 基础工具库

**路径**: `naca-jlib/src/main/java`

**职责**: 提供 Publicitas Java 库，支撑转译器和运行时

**核心功能包**:

| 包名 | 功能 | 关键类 |
|------|------|--------|
| `jlib.log` | 日志系统 | `Log`, `LogCenter`, `LogEvent` |
| `jlib.misc` | 通用工具 | `AsciiEbcdicConverter`, `Time_ms` |
| `jlib.sql` | 数据库操作 | `SQLTypeOperation`, `Db` |
| `jlib.xml` | XML 解析 | `Tag`, `TagCursor`, `XmlHelper` |
| `jlib.classLoader` | 动态类加载 | `CodeManager`, `DumpClassLoader` |
| `jlib/blowfish` | 加密支持 | `Blowfish`, `BlowfishCBC` |
| `jlib.display` | 显示和表单 | `BaseDialog`, `DisplayContext` |
| `jlib.jmxMBean` | JMX 监控 | `JmxRegistration`, `BaseDynamicMBean` |
| `jlib.controler` | 控制器 | `BaseControler`, `ControlerDirector` |

**依赖**:
- SLF4J 2.0.16 + Log4j 2.24.3
- Xerces 2.12.2 (XML)
- Apache Commons (Codec, IO, Net)
- Jakarta Mail 2.0.1

---

### 2. naca-trans - COBOL 转译器

**路径**: `naca-trans/src/main/java`

**职责**: 将 COBOL 源代码转换为 Java 代码

**核心类**:

| 类 | 职责 |
|------|------|
| `NacaTrans.java` | 主入口点 |
| `utils/NacaTransLauncher.java` | 启动器 |
| `utils/Transcoder.java` | 转译核心引擎 |
| `utils/CRulesManager.java` | 规则管理 |

**代码生成器结构**:

```
generate/
├── CBaseLanguageExporter.java     # 基础语言导出器
├── CJavaEntityFactory.java        # Java 实体工厂
├── CJavaFPacEntityFactory.java    # FPac Java 工厂
├── java/                          # Java 代码生成器
│   ├── CJavaClass.java            # 类生成
│   ├── CJavaDataSection.java      # 数据段生成
│   ├── CJavaProcedure.java        # 过程段生成
│   ├── CJavaCondition.java        # 条件生成
│   ├── CICS/                      # CICS 命令转译 (30+ 类)
│   │   ├── CJavaCICSLink.java
│   │   ├── CJavaCICSRead.java
│   │   └── CJavaCICSSendMap.java
│   └── SQL/                       # SQL 语句转译
│       ├── CJavaSQLCall.java
│       └── CJavaCondIsSQLCode.java
└── fpacjava/                      # FPac 特定生成器 (50+ 类)
    ├── CFPacJavaClass.java
    ├── CFPacJavaProcedure.java
    └── CFPacJavaDataSection.java
```

**转译流程**:

```
COBOL 源码 (.cbl)
       │
       ▼
┌─────────────────┐
│  解析器 (Parser) │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  语义分析        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  规则引擎        │ ← NacaTransRules.xml
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  代码生成器      │
└────────┬────────┘
         │
         ▼
Java 源码 (.java)
```

**配置文件**: `NacaTransRules.xml`

```xml
<rules>
  <category id="renameSQLView">
    <rule viewName="VTB8510B" tableName="VTB8510E"/>
  </category>
  <category id="environmentVariable">
    <rule name="EIBFN" methodeRead="getLastCICSCommandExecutedCode()"/>
    <rule name="EIBCALEN" methodeRead="getCommAreaLength()"/>
    <rule name="EIBTIME" methodeRead="getTime()"/>
  </category>
  <category id="keyPressed">
    <rule keyName="ENTER" CICSAlias="DFHENTER"/>
    <rule keyName="PF1" CICSAlias="DFHPF1"/>
    <!-- PF1-PF24 -->
  </category>
</rules>
```

---

### 3. naca-rt - 运行时环境

**路径**: `naca-rt/src/main/java`

**职责**: 提供转译后 Java 程序的执行支持

**核心包结构**:

```
nacaLib/
├── basePrgEnv/              # 基础程序环境
│   ├── BaseProgramManager.java      # 核心程序管理器 (1540 行) ⚠️ 待重构
│   ├── BaseEnvironment.java         # 环境基类
│   ├── BaseSession.java             # 会话管理
│   ├── BaseProgramLoader.java       # 程序加载器
│   └── FileManager.java             # 文件管理
├── batchPrgEnv/             # 批处理环境
│   ├── BatchEnvironment.java
│   ├── BatchProgram.java
│   ├── BatchProgramLoader.java
│   └── BatchProgramManager.java
├── onlinePrgEnv/            # 在线环境 (Web)
│   ├── OnlineEnvironment.java
│   ├── OnlineProgram.java
│   ├── OnlineProgramLoader.java
│   └── OnlineProgramManager.java
├── sql/                     # SQL 支持 (新架构) ✅
│   ├── dsl/
│   │   ├── CobolSqlTemplate.java    # SQL 模板 (参数化查询)
│   │   ├── CobolCursor.java         # 游标管理
│   │   ├── SqlErrorContext.java     # 错误处理
│   │   └── SQLInjectionException.java
│   └── support/
│       ├── CSQLStatus.java
│       ├── SQL.java
│       └── SQLCursor.java
├── components/              # 组件化架构 (重构中) 🔄
│   ├── VariableInitializer.java     # 变量初始化
│   ├── CursorRegistry.java          # 游标注册
│   └── CallParameterMapper.java     # 参数映射
├── config/                  # 配置外部化 ✅
│   ├── DatabaseConfig.java          # 数据库配置 (HikariCP)
│   ├── DatabaseProperties.java
│   └── TranspilerProperties.java
├── logging/                 # 日志统一 ✅
│   └── NacaLogger.java              # SLF4J 封装
├── CESM/                    # CICS 模拟
│   ├── CESMAgency.java
│   ├── CESMLink.java
│   ├── CESMRead.java
│   ├── CESMSendMap.java
│   └── CESMWriteQueue.java
├── calledPrgSupport/        # 被调用程序支持
│   ├── BaseCalledPrgPublicArgPositioned.java
│   └── CommArea.java
├── program/                 # 程序结构
│   ├── Copy.java
│   ├── CopyManager.java
│   ├── Paragraph.java
│   └── Section.java
├── varEx/                   # 变量扩展
│   ├── Var.java
│   ├── VarDefBase.java
│   ├── InitializeCache.java
│   └── MoveCorrespondingEntryManager.java
├── mapSupport/              # 表单/屏幕支持
│   ├── Map.java
│   └── MapContainer.java
└── exceptions/              # 异常处理
    ├── NacaRTException.java
    ├── CESMReturnException.java
    └── CGotoException.java
```

**关键依赖**:

| 依赖 | 版本 | 用途 |
|------|------|------|
| Spring Framework | 6.2.3 | JDBC 和上下文管理 |
| Spring Boot | 3.4.3 | Boot 支持 |
| HikariCP | 5.1.0 | 数据库连接池 |
| Berkeley DB (JE) | 18.3.12 | 数据存储 |
| Jakarta Servlet | 6.1.0 | Web 支持 |
| Apache Struts | 1.3.10 | MVC 框架 |
| Jakarta Mail | 2.0.1 | 邮件发送 |

---

### 4. naca-rt-tests - 测试套件

**路径**: `naca-rt-tests/src/test/java`

**职责**: 验证转译器和运行时的正确性

**测试结构**:

```
com.publicitas.naca.tests/
├── unit/
│   └── ExtraTests.java              # 单元测试
├── integration/
│   └── CobolLikeIntegrationTests.java  # 集成测试
├── transpipeline/
│   └── TranslationPipelineTest.java    # 转译流水线测试
├── sql/
│   └── SqlInjectionTests.java       # SQL 注入防护测试 (8 个用例)
└── base/
    ├── AbstractNacaTest.java        # 测试基类
    └── TestAssertionCollector.java  # 断言收集器
```

**测试覆盖**:

| 测试类 | 测试内容 | 状态 |
|--------|----------|------|
| `SqlInjectionTests` | SQL 注入防护 (8 个用例) | ✅ 完成 |
| `TranslationPipelineTest` | 转译流水线验证 | ✅ 完成 |
| `CobolLikeIntegrationTests` | 类 COBOL 集成测试 | ✅ 完成 |

---

## 四、架构升级计划

### Phase 1: 基础加固 ✅ (已完成)

| 改进项 | 实现类 | 说明 |
|--------|--------|------|
| SQL 注入防护 | `CobolSqlTemplate`, `SQLInjectionException` | 表名白名单 + 参数化查询 |
| 配置外部化 | `DatabaseConfig`, `DatabaseProperties` | application.yml + 环境变量 |
| 日志统一 | `NacaLogger` | SLF4J 封装，替代 System.out |

**配置示例**:

```yaml
# application.yml
naca:
  database:
    url: jdbc:postgresql://localhost:5432/nacadb
    username: ${NACA_DATABASE_USERNAME:naca_user}
    password: ${NACA_DATABASE_PASSWORD:change_me}
    pool:
      max-size: 20
      min-idle: 5
```

---

### Phase 2: SQL 架构升级 ✅ (已完成)

| 功能 | 类 | 说明 |
|------|------|------|
| 连接池 | `DatabaseConfig` | HikariCP 集成 |
| SQL DSL | `CobolSqlTemplate` | 参数化查询模板 |
| 游标 | `CobolCursor` | OPEN/FETCH/CLOSE |

**新 SQL 用法**:

```java
// SELECT INTO
sqlTemplate.queryForVars(
    "SELECT NOM, PRENOM FROM VIT101 WHERE ID = ?",
    params -> params.setInt(1, clientId.get()),
    vars -> {
        wsNom.set(vars.getString(1));
        wsPrenom.set(vars.getString(2));
    }
);

// 游标操作
CobolCursor cursor = new CobolCursor(sqlTemplate, "C1");
cursor.open("SELECT ID, NOM FROM VIT101 WHERE ID > ?", minValue.get());
while (cursor.fetch(wsId, wsNom)) {
    // 处理每一行
}
cursor.close();
```

---

### Phase 3: 模块化重构 🔄 (进行中)

**目标**: 将 `BaseProgramManager.java` (1540 行) 拆分为单一职责的组件

**依赖关系**:

```
ProgramLifecycleManager (原 BaseProgramManager 作为 Façade)
├── VariableInitializer      # 变量命名和索引
├── CursorRegistry           # SQL 游标注册和管理
├── FileManager              # 文件 Section 管理
├── CallParameterMapper      # 调用参数映射
└── WorkingStorageManager    # Working/Linkage Section 管理
```

**已实现组件**:

| 组件 | 路径 | 状态 |
|------|------|------|
| `VariableInitializer` | `nacaLib/components/` | ✅ 完成 |
| `CursorRegistry` | `nacaLib/components/` | ✅ 完成 |
| `CallParameterMapper` | `nacaLib/components/` | ✅ 完成 |
| `FileManager` | `nacaLib/basePrgEnv/` | 📝 计划中 |
| `WorkingStorageManager` | - | 📝 计划中 |

**参考文档**: `naca-rt/BASEPROGRAMMANAGER_REFACTOR.md`

---

### Phase 4: 可观测性增强 ⏳ (待实施)

| 功能 | 技术 | 状态 |
|------|------|------|
| Micrometer 指标 | Micrometer | 📝 计划 |
| 健康检查端点 | Spring Boot Actuator | 📝 计划 |
| 性能基线 | JMH / Micrometer | 📝 计划 |
| SQL 执行统计 | CobolSqlTemplate | 📝 计划 |

---

## 五、测试流水线

### 1. 测试金字塔

```
                    ┌───────────┐
                    │   E2E     │  Docker COBOL 对比测试
                   ┌┴───────────┴┐
                   │ Integration │  集成测试
                  ┌┴─────────────┴┐
                  │     Unit      │  单元测试
                  └───────────────┘
```

### 2. Translation Pipeline Test

**测试类**: `TranslationPipelineTest.java`

**测试组**:

| 测试组 | 描述 |
|--------|------|
| `DockerCobolTests` | 使用 Docker 运行原生 COBOL 程序 |
| `JavaRuntimeTests` | 运行转译后的 Java 程序 |
| `SampleFilesTests` | 验证 COBOL/Java 源文件存在 |
| `OutputComparisonTests` | 对比 COBOL 和 Java 输出 |

**Docker COBOL 执行流程**:

```java
// 1. 创建临时 COBOL 源文件
Path sourcePath = tempDir.resolve(programName + ".cbl");
Files.writeString(sourcePath, cobolSource);

// 2. Docker 编译
docker run --rm -v sourcePath:/src dagui0/gnucobol:latest \
  cobc -x -free -o PROGRAM PROGRAM.cbl

// 3. Docker 运行
docker run --rm -v sourcePath:/src dagui0/gnucobol:latest \
  ./PROGRAM
```

### 3. SQL 注入测试

**测试类**: `SqlInjectionTests.java`

| 测试用例 | 验证内容 |
|----------|----------|
| `shouldAcceptValidTableName` | 接受白名单内的表名 |
| `shouldRejectInvalidTableName` | 拒绝未授权的表名 |
| `shouldRejectNullTableName` | 拒绝 null 输入 |
| `shouldRejectSqlInjectionAttempts` | 阻止 SQL 注入尝试 (6 种模式) |
| `shouldAllowAddingTables` | 动态添加白名单表 |

### 4. Gradle 测试命令

```bash
# 运行所有测试
./gradlew test

# 运行特定测试
./gradlew :naca-rt-tests:test --tests "SqlInjectionTests"
./gradlew :naca-rt-tests:test --tests "TranslationPipelineTest"

# 生成覆盖率报告
./gradlew jacocoAggregateReport

# 查看覆盖率 HTML
open naca-rt-tests/build/reports/jacoco/test/html/index.html
```

---

## 六、构建和部署

### 1. Gradle 配置

```kotlin
// build.gradle.kts (根项目)
plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

// JDK 23
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}
```

### 2. Docker Compose

```yaml
# docker-compose.yml
services:
  cobol-env:
    build: .
    volumes:
      - .:/app
      - gradle-cache:/root/.gradle

  gnucobol:
    image: dagui0/gnucobol:latest
    volumes:
      - ./NacaSamples/cobol:/src
```

### 3. 常用命令

```bash
# 构建
./gradlew build

# 转译 COBOL
./gradlew :naca-trans:transpile -PconfigFile=NacaTrans.cfg

# 运行测试程序
./gradlew :naca-rt-tests:runTest -Pprogram=BATCH1

# Docker 环境
docker-compose build
docker-compose run cobol-env
```

---

## 七、技术栈总览

| 类别 | 技术/版本 |
|------|-----------|
| **JDK** | 23 (target) |
| **构建** | Gradle 8.9+ |
| **Spring** | 6.2.3 / Boot 3.4.3 |
| **数据库** | HikariCP 5.1.0 + Spring JDBC |
| **日志** | SLF4J 2.0.16 + Log4j 2.24.3 |
| **测试** | JUnit 5.11.4 + AssertJ 3.27.3 |
| **覆盖率** | JaCoCo |
| **COBOL** | GnuCOBOL (Docker) |

---

## 八、关键文件清单

### 架构文档
- `ARCHITECTURE_IMPLEMENTATION_REPORT.md` - 架构升级实施报告
- `ARCHITECTURE_UPGRADE_GUIDE.md` - 架构升级使用指南
- `naca-rt/BASEPROGRAMMANAGER_REFACTOR.md` - BaseProgramManager 重构计划

### 配置文件
- `naca-rt/src/main/resources/application.yml.example` - 配置模板
- `naca-rt/src/main/resources/log4j2.xml` - 日志配置
- `NacaSamples/trans/NacaTransRules.xml` - 转译规则

### 核心源码
- `naca-rt/src/main/java/nacaLib/basePrgEnv/BaseProgramManager.java` - 程序管理器 (1540 行)
- `naca-rt/src/main/java/nacaLib/sql/dsl/CobolSqlTemplate.java` - SQL 模板
- `naca-trans/src/main/java/utils/Transcoder.java` - 转译引擎

### 测试
- `naca-rt-tests/src/test/java/com/publicitas/naca/tests/sql/SqlInjectionTests.java`
- `naca-rt-tests/src/test/java/com/publicitas/naca/tests/transpipeline/TranslationPipelineTest.java`

---

## 九、重构注意事项

1. **向后兼容**: 保留 `BaseProgramManager` 作为 Façade，使用 `@Deprecated` 标记旧方法
2. **测试先行**: 重构前先运行现有测试，确保行为不变
3. **小步提交**: 每次重构一个组件，验证后再继续
4. **性能基线**: 重构前后对比性能指标

---

## 十、参考链接

- [README.md](../README.md) - 项目入门
- [ARCHITECTURE_IMPLEMENTATION_REPORT.md](ARCHITECTURE_IMPLEMENTATION_REPORT.md) - 实施报告
- [ARCHITECTURE_UPGRADE_GUIDE.md](ARCHITECTURE_UPGRADE_GUIDE.md) - 使用指南
