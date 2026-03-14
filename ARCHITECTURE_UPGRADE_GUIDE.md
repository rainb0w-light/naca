# NacaRT 架构升级 - 使用指南

## 快速开始

### 1. 配置数据库连接

**方式 A: 使用 application.yml**

在 classpath 中创建 `application.yml`:

```yaml
naca:
  database:
    url: jdbc:postgresql://localhost:5432/mydb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    pool:
      max-size: 20
      min-idle: 5
```

**方式 B: 使用环境变量**

```bash
export NACA_DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
export NACA_DATABASE_USERNAME=myuser
export NACA_DATABASE_PASSWORD=mypassword
```

**方式 C: 使用系统属性**

```bash
java -DNACA_DATABASE_URL=jdbc:postgresql://localhost:5432/mydb \
     -DNACA_DATABASE_USERNAME=myuser \
     -DNACA_DATABASE_PASSWORD=mypassword \
     -jar myapp.jar
```

### 2. 使用新的 SQL DSL (推荐)

**旧的 SQL 用法 (仍然存在):**
```java
// 翻译后生成的代码
sql("insert into VIT101 (ID, NOM, PRENOM) VALUES (#1, #2, #3)")
    .value(1, nId)
    .value(2, csName)
    .value(3, csPrenom);
```

**新的 SQL DSL 用法 (推荐):**
```java
// 在你的 COBOL 程序中直接使用新的 SQL 语法
@Autowired
private CobolSqlTemplate sqlTemplate;

// SELECT INTO
sqlTemplate.queryForVars(
    "SELECT NOM, PRENOM FROM VIT101 WHERE ID = ?",
    params -> params.setInt(1, clientId.get()),
    vars -> {
        wsNom.set(vars.getString(1));
        wsPrenom.set(vars.getString(2));
    }
);

// INSERT
sqlTemplate.executeUpdate(
    "INSERT INTO VIT101 (ID, NOM, PRENOM) VALUES (?, ?, ?)",
    params -> {
        params.setInt(1, id.get());
        params.setString(2, name.get());
        params.setString(3, firstname.get());
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

### 3. 配置日志

使用提供的 `log4j2.xml` 配置:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
        <Logger name="nacaLib.sql" level="debug"/>
    </Loggers>
</Configuration>
```

### 4. SQL 注入防护

新的 SQL DSL 自动启用表名白名单：

**默认允许的表名:**
- VIT101, VIT102, VIT103
- CUSTOMER, ORDER, PRODUCT

**添加自定义表名:**
```java
CobolSqlTemplate.allowTable("MY_TABLE");
```

**尝试使用未授权的表名将抛出异常:**
```java
// 这将抛出 SQLInjectionException
sqlTemplate.execute("SELECT * FROM UNAUTHORIZED_TABLE");
```

### 5. Spring Boot 集成

**在你的 Spring Boot 应用中:**

```java
@SpringBootApplication
public class NacaApplication {

    @Bean
    public CobolSqlTemplate sqlTemplate(DataSource dataSource,
                                         BaseProgramManager programManager) {
        return new CobolSqlTemplate(dataSource, programManager);
    }

    public static void main(String[] args) {
        SpringApplication.run(NacaApplication.class, args);
    }
}
```

---

## 配置选项

### 数据库配置

| 属性 | 默认值 | 说明 | 环境变量 |
|------|--------|------|----------|
| `naca.database.url` | - | JDBC URL | `NACA_DATABASE_URL` |
| `naca.database.username` | - | 数据库用户名 | `NACA_DATABASE_USERNAME` |
| `naca.database.password` | - | 数据库密码 | `NACA_DATABASE_PASSWORD` |
| `naca.database.driver-class-name` | - | JDBC 驱动类名 | - |
| `naca.database.pool.max-size` | 20 | 最大连接数 | - |
| `naca.database.pool.min-idle` | 5 | 最小空闲连接 | - |
| `naca.database.pool.connection-timeout` | 30000 | 连接超时 (ms) | - |

### SQL 配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `naca.database.sql.show-sql` | false | 显示 SQL 语句 |
| `naca.database.sql.format-sql` | true | 格式化 SQL |
| `naca.database.sql.use-explain` | false | 使用 EXPLAIN PLAN |

### 翻译器配置

| 属性 | 默认值 | 说明 | 环境变量 |
|------|--------|------|----------|
| `naca.transpiler.rules-file` | - | 规则文件路径 | `NACA_RULES_FILE` |
| `naca.transpiler.input-path` | - | 输入目录 | `NACA_INPUT_PATH` |
| `naca.transpiler.output-path` | - | 输出目录 | `NACA_OUTPUT_PATH` |

---

## 迁移指南

### 从旧的 SQL API 迁移

**步骤 1:** 识别现有的 SQL 调用

```bash
# 搜索项目中的 SQL 调用
grep -r "sql(" --include="*.java" src/
```

**步骤 2:** 逐个替换为新的 DSL

| 旧 API | 新 API |
|--------|--------|
| `sql("SELECT ...").into(...)` | `sqlTemplate.queryForVars(...)` |
| `sql("INSERT ...").value(...)` | `sqlTemplate.executeUpdate(...)` |
| `new SQLCursor(...).open(...)` | `new CobolCursor(...).open(...)` |

**步骤 3:** 测试验证

```bash
# 运行 SQL 注入测试
./gradlew :naca-rt-tests:test --tests "SqlInjectionTests"
```

---

## 故障排查

### 问题：无法连接到数据库

**解决方案:**
1. 检查环境变量是否正确设置
2. 验证 JDBC 驱动是否在 classpath 中
3. 确认数据库 URL 格式正确

### 问题：SQL 注入异常

**解决方案:**
1. 确认表名在白名单中
2. 使用 `CobolSqlTemplate.allowTable()` 添加表名
3. 检查是否有 SQL 注入尝试

### 问题：日志不输出

**解决方案:**
1. 检查 log4j2.xml 是否在 classpath 中
2. 确认日志级别配置正确
3. 验证 SLF4J 绑定是否正确

---

## 性能优化建议

1. **连接池调优**
   - 根据负载调整 `max-size`
   - 监控连接池指标

2. **SQL 优化**
   - 启用 `show-sql` 进行调试
   - 使用 EXPLAIN PLAN 分析查询

3. **批量操作**
   - 使用 `executeBatch()` 进行批量更新
   - 避免在循环中逐条执行

---

## 参考文档

- [架构实施报告](ARCHITECTURE_IMPLEMENTATION_REPORT.md)
- [BaseProgramManager 重构计划](naca-rt/BASEPROGRAMMANAGER_REFACTOR.md)
- [示例配置文件](naca-rt/src/main/resources/application.yml.example)
