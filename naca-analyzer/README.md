# Naca 与 cobol-rekt 集成指南

## 概述

cobol-rekt 已成功集成到 Naca 项目中，提供了以下功能：
- **LSP4COBOL 解析器**: 完整的 COBOL 85 语法分析
- **SMOJOL 解释器**: COBOL 代码解释执行
- **双 IR 架构**: AST + CFG 用于代码流分析
- **控制流图生成**: 用于程序流程可视化
- **数据结构分析**: COBOL 数据部门分析

## 集成方式

cobol-rekt 通过本地 Maven 仓库集成，不修改其原有项目结构。

### 构建顺序

1. 首先构建 cobol-rekt 模块到本地 Maven 仓库
2. Naca 项目通过 `mavenLocal()` 引用 cobol-rekt 的产物

### 依赖配置

在 `build.gradle.kts` 中添加：

```kotlin
repositories {
    mavenLocal()  // cobol-rekt 本地构建产物
    maven { url = uri("https://maven.aliyun.com/repository/central") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    mavenCentral()
}
```

在模块依赖中添加：

```kotlin
dependencies {
    // SMOJOL Core - COBOL AST 和解释器
    implementation("org.smojol:smojol-core:1.0-SNAPSHOT")

    // SMOJOL Toolkit - 分析工具包
    implementation("org.smojol:smojol-toolkit:1.0-SNAPSHOT")

    // LSP4COBOL 解析器
    implementation("org.eclipse.lsp.cobol:parser:1.0-SNAPSHOT")
    implementation("org.eclipse.lsp.cobol:common:1.0.8")
    implementation("org.eclipse.lsp.cobol:engine:1.0.0-SNAPSHOT")
}
```

## 构建说明

### 环境要求

- Java 21 (使用 `export JAVA_HOME=/opt/homebrew/opt/openjdk@21`)
- Maven 3.9+
- Gradle 8.0+

### 构建 cobol-rekt

```bash
cd /Volumes/AppData/codebase/cobol-rekt
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
mvn clean install -DskipTests -Dmaven.test.skip=true
```

### 构建 Naca

```bash
cd /Volumes/AppData/codebase/naca
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
./gradlew build -x test
```

## 使用示例

### 解析 COBOL 代码

```java
import com.publicitas.naca.analyzer.CobolAnalyzer;

CobolAnalyzer analyzer = new CobolAnalyzer();
AnalysisResult result = analyzer.analyze(cobolSource);

// 获取 AST
Object ast = result.getAst();

// 获取控制流图
Object cfg = result.getCfg();
```

### 执行 COBOL 程序

```java
ExecutionResult execResult = analyzer.execute(cobolSource, inputData);
if (execResult.isSuccess()) {
    System.out.println("输出：" + execResult.getOutput());
} else {
    System.err.println("错误：" + execResult.getErrorMessage());
}
```

### 生成控制流图

```java
String dotGraph = analyzer.generateCFG(cobolSource);
// 可以使用 GraphViz 渲染 DOT 格式
Files.write(Paths.get("cfg.dot"), dotGraph.getBytes());
```

## 模块说明

### naca-analyzer

新增的分析模块，包含：
- `CobolAnalyzer.java`: 主要分析类
- 未来将集成完整的 cobol-rekt 功能

### cobol-rekt 模块

| 模块 | 描述 |
|------|------|
| `smojol-core` | 核心 AST 和解释器 |
| `smojol-toolkit` | 分析工具包 |
| `smojol-cli` | 命令行工具 |
| `parser` | LSP4COBOL 解析器 |
| `engine` | COBOL 执行引擎 |
| `common` | 通用工具类 |
| `dialect-idms` | IDMS 数据库方言 |
| `dialect-daco` | Daco 方言 |

## 注意事项

1. **Java 版本**: 必须使用 Java 21，Java 25 会导致 Lombok 兼容性问题
2. **网络环境**: 使用阿里云 Maven 镜像加速国内访问
3. **本地仓库**: 确保 cobol-rekt 已安装到本地 Maven 仓库 (`~/.m2/repository`)
4. **构建顺序**: 先构建 cobol-rekt，再构建 Naca

## 故障排除

### Lombok 编译错误

如果遇到 Lombok 相关错误，确保：
- 使用 Java 21
- Lombok 版本为 1.18.34 或更高

### Maven 依赖下载失败

检查网络设置，确保可以访问：
- 阿里云 Maven 镜像
- Maven Central

### Gradle 守护进程问题

清理 Gradle 缓存：
```bash
./gradlew --stop
./gradlew clean build
```

## 参考资料

- [cobol-rekt GitHub](https://github.com/avishek-sen-gupta/cobol-rekt)
- [LSP4COBOL](https://github.com/eclipse-che4z/che-che4z-lsp-for-cobol)
- [SMOJOL Interpreter](https://github.com/avishek-sen-gupta/cobol-rekt/tree/main/smojol-core)
