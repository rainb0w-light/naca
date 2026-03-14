# Naca COBOL 到 Java 翻译引擎 - 架构升级实施报告

## 执行摘要

本文档记录了根据架构评审报告实施的改进措施。按照优先级分阶段实施，目前完成了 Phase 1、Phase 2 和 Phase 3 的主要工作。

---

## Phase 1: 基础加固 (已完成)

### 1.1 SQL 注入防护

**新增类**:
- `nacaLib.sql.dsl.CobolSqlTemplate` - SQL 模板类，提供参数化查询
- `nacaLib.sql.dsl.SQLInjectionException` - SQL 注入异常
- `nacaLib.sql.dsl.SqlErrorContext` - SQL 错误上下文
- `nacaLib.sql.dsl.CobolCursor` - 游标管理

**核心功能**:
```java
// 表名白名单验证
public static void validateTableName(String tableName) {
    if (!ALLOWED_TABLES.contains(tableName.toUpperCase())) {
        throw new SQLInjectionException("Table '" + tableName + "' is not in the allowed list");
    }
}

// 参数化查询示例
cobolSqlTemplate.queryForVars(
    "SELECT NOM, PRENOM FROM VIT101 WHERE ID = ?",
    params -> params.setInt(1, wsId.get()),
    vars -> {
        wsNom.set(vars.getString(1));
        wsPrenom.set(vars.getString(2));
    }
);
```

**测试用例**:
- `SqlInjectionTests.java` - 包含 8 个测试用例覆盖 SQL 注入场景

### 1.2 配置外部化

**新增类**:
- `nacaLib.config.DatabaseProperties` - 数据库配置属性
- `nacaLib.config.DatabaseConfig` - 数据库配置（HikariCP）
- `nacaLib.config.TranspilerProperties` - 翻译器配置属性

**支持外部化的配置**:
```yaml
naca:
  database:
    url: ${NACA_DATABASE_URL:jdbc:postgresql://localhost:5432/nacadb}
    username: ${NACA_DATABASE_USERNAME:naca_user}
    password: ${NACA_DATABASE_PASSWORD:change_me}
    pool:
      max-size: 20
      min-idle: 5
```

**配置文件示例**:
- `application.yml.example` - 完整的配置模板
- `log4j2.xml` - 日志配置文件

### 1.3 日志系统统一

**新增类**:
- `nacaLib.logging.NacaLogger` - 统一的 SLF4J 日志工具

**迁移路径**:
- `System.out.println` → `NacaLogger.info()`
- `Log.logDebug()` → `NacaLogger.debug()`
- `Log.logImportant()` → `NacaLogger.error()`
- SQL 日志 → `nacaLib.sql` 专用 logger

**依赖更新**:
```gradle
// SLF4J + Log4j 2.x
implementation("org.slf4j:slf4j-api:2.0.16")
implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.3")
```

---

## Phase 2: SQL 架构升级 (已完成)

### 2.1 连接池集成

**新增依赖**:
```gradle
implementation("com.zaxxer:HikariCP:5.1.0")
implementation("org.springframework:spring-jdbc:6.2.3")
```

**配置类**: `DatabaseConfig.java`
```java
@Bean
public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(databaseProperties.getUrl());
    config.setMaximumPoolSize(20);
    return new HikariDataSource(config);
}
```

### 2.2 CobolSqlTemplate 完整功能

支持的操作:
- `queryForVars()` - SELECT INTO
- `executeUpdate()` - INSERT/UPDATE/DELETE
- `executeInsertWithKeys()` - 返回生成键
- `executeBatch()` - 批量执行
- `queryForRow()` - 单行查询

### 2.3 CobolCursor 游标管理

支持的操作:
- `open()` - OPEN cursor
- `fetch()` - FETCH INTO
- `close()` - CLOSE cursor
- `isOpen()` - 游标状态检查

---

## Phase 3: 模块化重构 (进行中)

### 3.1 新增组件类

**VariableInitializer** (`nacaLib.components.VariableInitializer`)
- 变量命名和索引
- InitializeCache 管理
- MoveCorrespondingEntryManager 管理

**CursorRegistry** (`nacaLib.components.CursorRegistry`)
- SQL 游标注册
- 游标生命周期管理
- 游标名称恢复（支持程序池）

**CallParameterMapper** (`nacaLib.components.CallParameterMapper`)
- 调用参数映射
- CommArea 处理
- 参数长度计算

### 3.2 计划的组件

待实现:
- `FileManager` - 文件 Section 管理
- `WorkingStorageManager` - 工作存储区管理
- `ProgramLifecycleManager` - 程序生命周期协调

### 3.3 重构文档

- `BASEPROGRAMMANAGER_REFACTOR.md` - 详细的重构计划

---

## Phase 4: 可观测性增强 (待实施)

### 计划功能

1. **Micrometer 集成**
   - JVM 指标收集
   - SQL 执行时间统计
   - 连接池监控

2. **健康检查端点**
   ```java
   @Bean
   public HealthIndicator databaseHealthIndicator() {
       return () -> {
           // 检查数据库连接
           return Health.up().build();
       };
   }
   ```

3. **性能基线**
   - 翻译时间统计
   - 运行时性能指标
   - 内存使用分析

---

## 文件清单

### 新增文件

| 文件 | 描述 |
|------|------|
| `naca-rt/src/main/java/nacaLib/sql/dsl/CobolSqlTemplate.java` | SQL 模板核心类 |
| `naca-rt/src/main/java/nacaLib/sql/dsl/CobolCursor.java` | 游标管理 |
| `naca-rt/src/main/java/nacaLib/sql/dsl/SqlErrorContext.java` | 错误处理 |
| `naca-rt/src/main/java/nacaLib/sql/dsl/SQLInjectionException.java` | 注入异常 |
| `naca-rt/src/main/java/nacaLib/config/DatabaseProperties.java` | 数据库配置 |
| `naca-rt/src/main/java/nacaLib/config/DatabaseConfig.java` | 数据库配置类 |
| `naca-rt/src/main/java/nacaLib/config/TranspilerProperties.java` | 翻译器配置 |
| `naca-rt/src/main/java/nacaLib/logging/NacaLogger.java` | 日志工具 |
| `naca-rt/src/main/java/nacaLib/components/VariableInitializer.java` | 变量初始化 |
| `naca-rt/src/main/java/nacaLib/components/CursorRegistry.java` | 游标注册 |
| `naca-rt/src/main/java/nacaLib/components/CallParameterMapper.java` | 参数映射 |
| `naca-rt-tests/src/test/java/.../SqlInjectionTests.java` | SQL 注入测试 |
| `naca-rt/src/main/resources/application.yml.example` | 配置示例 |
| `naca-rt/src/main/resources/log4j2.xml` | 日志配置 |

### 修改文件

| 文件 | 修改内容 |
|------|----------|
| `naca-rt/build.gradle.kts` | 添加 Spring JDBC、HikariCP、SLF4J 依赖 |
| `naca-jlib/build.gradle.kts` | 添加 SLF4J 依赖 |

---

## 兼容性说明

### 向后兼容

1. **现有翻译程序**
   - 所有新类都设计为增量采用
   - 现有 SQL.java 仍然可用
   - 新代码通过 `@Deprecated` 标记逐步迁移

2. **配置迁移**
   - 旧 NacaTrans.cfg 仍可工作
   - 推荐使用新的 application.yml
   - 提供环境变量覆盖

3. **日志迁移**
   - jlib.log.Log 仍然可用
   - 新代码使用 NacaLogger
   - Log4j 桥接保持兼容

---

## 下一步行动

### 短期 (1-2 周)
1. 完成 Phase 3 剩余组件
2. 更新现有 SQL.java 使用新的 DSL
3. 添加更多单元测试

### 中期 (1-2 月)
1. 实施 Phase 4 可观测性
2. 性能基准测试
3. 文档完善

### 长期 (3-6 月)
1. 响应式编程支持 (WebFlux)
2. 模块化打包
3. 云原生部署支持

---

## 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 翻译兼容性 | 已翻译程序可能失败 | 保持向后兼容 API，逐步迁移 |
| 性能回归 | 新抽象层增加开销 | 建立性能基线，持续监控 |
| 测试覆盖不足 | 边界条件遗漏 | 增加集成测试，使用生产数据验证 |

---

## 总结

本次架构升级实施了以下关键改进：

1. **安全性**: SQL 注入防护通过白名单和参数化查询
2. **可维护性**: 配置外部化，支持多种部署环境
3. **现代化**: 迁移到 SLF4J 日志、HikariCP 连接池
4. **模块化**: 开始拆分 monolithic BaseProgramManager

这些改进为 Naca 翻译引擎的长期发展奠定了坚实基础。
