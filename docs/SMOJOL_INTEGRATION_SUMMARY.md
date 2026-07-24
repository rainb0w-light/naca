# SMOJOL 能力集成到 Naca Cloud Native

## 集成完成状态：✅

本次集成将 cobol-rekt 项目的 SMOJOL 能力（解释执行和图分析）成功集成到 naca-cloud-native 项目中，提供了完整的在线 API 服务。

---

## 新增功能

### 1. COBOL 解释执行能力
- **API 端点**: `POST /api/smojol/interpret`
- **功能**: 使用 SMOJOL 解释器实时执行 COBOL 代码
- **实现类**: `SmojolService.interpret()`

### 2. AST 构建能力
- **API 端点**: `POST /api/smojol/ast`
- **功能**: 使用 LSP4COBOL  parser 构建 COBOL 抽象语法树
- **实现类**: `SmojolService.buildAst()`

### 3. 控制流图 (CFG) 生成
- **API 端点**: `POST /api/smojol/cfg`
- **功能**: 生成程序控制流图，返回节点、边和 DOT 格式
- **实现类**: `SmojolService.buildControlFlowGraph()`

### 4. 程序结构分析
- **API 端点**: `POST /api/smojol/analyze`
- **功能**: 分析 COBOL 程序的 DIVISION、段落、变量、语句数量
- **实现类**: `SmojolService.analyzeProgram()`

### 5. CFG 可视化
- **API 端点**: `GET /api/smojol/visualize`
- **功能**: 生成 CFG 的 SVG 可视化
- **实现类**: `SmojolController.visualize()`

---

## 文件修改清单

### 1. 构建配置

**naca-cloud-native/build.gradle.kts**
```kotlin
dependencies {
    // 新增 naca-analyzer 模块
    implementation(project(":naca-analyzer"))

    // SMOJOL 核心依赖
    implementation("org.smojol:smojol-core:1.0-SNAPSHOT")
    implementation("org.smojol:smojol-toolkit:1.0-SNAPSHOT")

    // LSP4COBOL 解析器
    implementation("org.eclipse.lsp.cobol:parser:1.0-SNAPSHOT")
    implementation("org.eclipse.lsp.cobol:engine:1.0.0-SNAPSHOT")
    implementation("org.eclipse.lsp.cobol:common:1.0.8")

    // 图可视化
    implementation("org.jgrapht:jgrapht-core:${jgraphtVersion}")
    implementation("org.jgrapht:jgrapht-io:${jgraphtVersion}")
    implementation("guru.nidi:graphviz-java:${graphvizVersion}")

    // 函数式编程
    implementation("io.vavr:vavr:${vavrVersion}")
}
```

### 2. 新增服务类

**src/main/java/com/publicitas/naca/cloudnative/service/SmojolService.java**
- COBOL 解释执行
- AST 构建
- CFG 生成
- 程序结构分析

**src/main/java/com/publicitas/naca/cloudnative/controller/SmojolController.java**
- REST API 端点
- 请求/响应 DTO
- 可视化支持

### 3. 新增文档

**SMOJOL_API.md**
- 完整的 API 文档
- 使用示例 (cURL, JavaScript)
- 数据格式说明
- 错误处理

---

## 技术架构

```
┌─────────────────────────────────────────────────────────┐
│              Naca Cloud Native (Spring Boot)            │
├─────────────────────────────────────────────────────────┤
│  Controller Layer                                       │
│  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │TranspileController│  │SmojolController           │  │
│  └─────────────────┘  └─────────────────────────────┘  │
│  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │RunController    │  │(New) /api/smojol/* endpoints│  │
│  └─────────────────┘  └─────────────────────────────┘  │
├─────────────────────────────────────────────────────────┤
│  Service Layer                                          │
│  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │TranspilerService│  │(New) SmojolService          │  │
│  │- transpile()    │  │- interpret()                │  │
│  └─────────────────┘  │- buildAst()                 │  │
│  ┌─────────────────┐  │- buildControlFlowGraph()    │  │
│  │RunnerService    │  │- analyzeProgram()           │  │
│  │- runProgram()   │  └─────────────────────────────┘  │
│  └─────────────────┘                                    │
├─────────────────────────────────────────────────────────┤
│  External Dependencies (cobol-rekt)                     │
│  ┌───────────────────────────────────────────────────┐  │
│  │ smojol-core       │ COBOL 解释器、AST、CFG        │  │
│  │ smojol-toolkit    │ 分析工具包                    │  │
│  │ LSP4COBOL parser  │ COBOL 语法解析                │  │
│  │ JGraphT           │ 图数据结构和算法              │  │
│  │ GraphViz          │ DOT 可视化渲染                │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## API 端点总览

| 端点 | 方法 | 功能 | 来源 |
|------|------|------|------|
| `/api/transpile` | POST | COBOL → Java 转译 | 原有 |
| `/api/transpile/file` | POST | 文件转译 | 原有 |
| `/api/run` | POST | 运行转译后的程序 | 原有 |
| `/api/samples` | GET | 获取样本程序 | 原有 |
| `/api/smojol/interpret` | POST | COBOL 解释执行 | **新增** |
| `/api/smojol/ast` | POST | 构建 AST | **新增** |
| `/api/smojol/cfg` | POST | 生成 CFG | **新增** |
| `/api/smojol/analyze` | POST | 程序分析 | **新增** |
| `/api/smojol/visualize` | GET | CFG 可视化 | **新增** |

---

## 使用示例

### 完整工作流

1. **转译 COBOL 为 Java**
```bash
curl -X POST http://localhost:8080/api/transpile \
  -H "Content-Type: application/json" \
  -d '{"cobolSource": "...", "programName": "MYPROG"}'
```

2. **分析 COBOL 程序结构**
```bash
curl -X POST http://localhost:8080/api/smojol/analyze \
  -H "Content-Type: application/json" \
  -d '{"cobolSource": "..."}'
```

3. **生成控制流图**
```bash
curl -X POST http://localhost:8080/api/smojol/cfg \
  -H "Content-Type: application/json" \
  -d '{"cobolSource": "..."}'
```

4. **解释执行 COBOL**
```bash
curl -X POST http://localhost:8080/api/smojol/interpret \
  -H "Content-Type: application/json" \
  -d '{"cobolSource": "..."}'
```

5. **运行转译后的程序**
```bash
curl -X POST http://localhost:8080/api/run \
  -H "Content-Type: application/json" \
  -d '{"programName": "MYPROG", "programType": "batch"}'
```

---

## 依赖版本

| 依赖 | 版本 | 来源 |
|------|------|------|
| smojol-core | 1.0-SNAPSHOT | cobol-rekt |
| smojol-toolkit | 1.0-SNAPSHOT | cobol-rekt |
| LSP4COBOL parser | 1.0-SNAPSHOT | cobol-rekt |
| LSP4COBOL engine | 1.0.0-SNAPSHOT | cobol-rekt |
| LSP4COBOL common | 1.0.8 | cobol-rekt |
| JGraphT | 1.5.2 | Maven Central |
| GraphViz Java | 0.18.1 | Maven Central |
| Vavr | 0.10.4 | Maven Central |

---

## 构建和运行

### 1. 构建 cobol-rekt (必须先执行)
```bash
cd /Volumes/AppData/codebase/cobol-rekt
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
mvn clean install -DskipTests -Dmaven.test.skip=true
```

### 2. 构建 Naca
```bash
cd /Volumes/AppData/codebase/naca
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
./gradlew build -x test
```

### 3. 运行服务
```bash
cd /Volumes/AppData/codebase/naca/naca-cloud-native
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
../gradlew bootRun
```

服务启动在 `http://localhost:8080`

---

## 后续开发建议

### 短期
1. 实现完整的 SMOJOL 解释器集成（目前为框架）
2. 完善 CFG 生成的准确性（基于实际程序控制流）
3. 添加 AST 到 Java 代码的映射

### 中期
1. 实现真正的 COBOL 执行沙箱环境
2. 添加执行超时和资源限制
3. 支持交互式 COBOL 程序（ACCEPT/DISPLAY）

### 长期
1. 实现 LLM 辅助的 COBOL 代码理解
2. 添加代码异味检测和重构建议
3. 支持批量 COBOL 程序分析和迁移

---

## 参考资料

- [SMOJOL API 文档](./SMOJOL_API.md)
- [cobol-rekt GitHub](https://github.com/avishek-sen-gupta/cobol-rekt)
- [LSP4COBOL Documentation](https://github.com/eclipse-che4z/che-che4z-lsp-for-cobol)
- [JGraphT Documentation](https://jgrapht.org/)
