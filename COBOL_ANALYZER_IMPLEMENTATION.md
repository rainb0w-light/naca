# CobolAnalyzer 实际实现说明

## 概述

`CobolAnalyzer` 类现已提供**实际的 COBOL 代码分析功能**，不再为空实现。它使用 cobol-rekt 项目的数据结构和算法来解析和分析 COBOL 程序。

## 已实现的功能

### 1. 程序结构分析 (`analyze` 方法)

分析 COBOL 源代码，提取以下信息：

- **Program ID**: 程序标识符
- **Division Count**: DIVISION 数量（IDENTIFICATION、ENVIRONMENT、DATA、PROCEDURE）
- **Paragraph Count**: 段落数量
- **Variable Count**: 变量声明数量
- **Statement Count**: 语句数量
- **Source Lines**: 源代码行数

**返回**: `AnalysisResult` 包含：
- `flowNodes`: 流程节点列表（段落及其 PERFORM 关系）
- `programInfo`: 程序元数据 Map
- `errors`: 错误列表（如果有）

### 2. 控制流图数据 (`getCfgData` 方法)

生成控制流图的节点和边数据：

- **Nodes**: 段落名称集合
- **Edges**: 控制流边（包括顺序流和 PERFORM 调用）

**返回**: `CfgData` 包含：
- `nodes`: 节点 ID 集合
- `edges`: 边列表（source → target）

### 3. DOT 格式生成 (`generateCFG` 方法)

生成 GraphViz DOT 格式的 CFG 表示，可用于可视化工具渲染。

**返回**: DOT 格式字符串

## 使用示例

### 基本分析

```java
import com.publicitas.naca.analyzer.CobolAnalyzer;
import com.publicitas.naca.analyzer.CobolAnalyzer.AnalysisResult;

CobolAnalyzer analyzer = new CobolAnalyzer();

String cobolSource = """
       IDENTIFICATION DIVISION.
       PROGRAM-ID. HELLO-WORLD.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-MESSAGE PIC X(20) VALUE 'Hello World'.
       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           DISPLAY WS-MESSAGE.
           STOP RUN.
       """;

AnalysisResult result = analyzer.analyze(cobolSource);

if (result.isSuccess()) {
    Map<String, Object> info = result.getProgramInfo();
    System.out.println("Program: " + info.get("programId"));
    System.out.println("Paragraphs: " + info.get("paragraphCount"));
    System.out.println("Variables: " + info.get("variableCount"));
}
```

### 生成控制流图

```java
import com.publicitas.naca.analyzer.CobolAnalyzer.CfgData;

CfgData cfg = analyzer.getCfgData(cobolSource);

System.out.println("Nodes: " + cfg.getNodes());
for (Map<String, String> edge : cfg.getEdges()) {
    System.out.println(edge.get("source") + " -> " + edge.get("target"));
}

// 生成 DOT 格式
String dot = analyzer.generateCFG(cobolSource);
System.out.println(dot);
```

### 在 Spring Boot 中使用

```java
@RestController
@RequestMapping("/api/analyze")
public class AnalysisController {

    @Autowired
    private CobolAnalyzer analyzer;

    @PostMapping
    public ResponseEntity<?> analyze(@RequestBody String cobolSource) {
        AnalysisResult result = analyzer.analyze(cobolSource);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest()
                .body(Map.of("errors", result.getErrors()));
        }

        return ResponseEntity.ok(Map.of(
            "programInfo", result.getProgramInfo(),
            "flowNodes", result.getFlowNodes()
        ));
    }
}
```

## API 端点集成

### `/api/smojol/analyze`

**请求:**
```json
{
  "cobolSource": "       IDENTIFICATION DIVISION...."
}
```

**响应:**
```json
{
  "success": true,
  "analysis": {
    "programId": "HELLO",
    "divisionCount": 2,
    "paragraphCount": 1,
    "variableCount": 3,
    "statementCount": 5,
    "sourceLines": 10
  }
}
```

### `/api/smojol/cfg`

**请求:**
```json
{
  "cobolSource": "..."
}
```

**响应:**
```json
{
  "success": true,
  "nodes": ["MAIN-PROCEDURE", "EXIT-PROGRAM"],
  "edges": [
    {"source": "MAIN-PROCEDURE", "target": "EXIT-PROGRAM"}
  ],
  "dotFormat": "digraph CFG { ... }"
}
```

### `/api/smojol/ast`

**请求:**
```json
{
  "cobolSource": "..."
}
```

**响应:**
```json
{
  "success": true,
  "flowNodes": [
    {
      "id": "MAIN-PROCEDURE",
      "type": "PARAGRAPH",
      "line": 1,
      "performs": []
    }
  ],
  "programInfo": {...}
}
```

### `/api/smojol/interpret`

**请求:**
```json
{
  "cobolSource": "...",
  "inputData": ""
}
```

**响应:**
```json
{
  "success": true,
  "output": "COBOL Program Analysis\n=====================\nProgram ID: HELLO\nDivisions: 2\nParagraphs: 1\nVariables: 1\nStatements: 2\nSource Lines: 10\n\nNote: Full SMOJOL interpreter execution requires additional setup.\nThis is static analysis output.",
  "errors": []
}
```

## 技术细节

### 段落提取逻辑

1. 查找 `PROCEDURE DIVISION` 开始
2. 查找 `END PROGRAM` 结束
3. 识别段落名：匹配正则 `^[A-Z][A-Z0-9\-]*\.$`
4. 提取 PERFORM 语句的目标段落

### 变量计数逻辑

1. 查找 `WORKING-STORAGE SECTION` 或 `LINKAGE SECTION`
2. 匹配行首为数字的行（COBOL level 号）
3. 排除注释行（以 `*` 开头）

### 控制流边生成

1. **顺序流**: 相邻段落之间自动添加边
2. **PERFORM 调用**: 从 PERFORM 语句提取目标段落名

## 限制和待改进

### 当前限制

1. **解释执行**: 完整 SMOJOL 解释器需要更多设置（内存布局、数据初始化等）
2. **AST 构建**: 当前使用简化的流程节点，而非完整 LSP4COBOL AST
3. **CFG 精度**: 仅识别 PERFORM 调用，未识别 IF/PERFORM THRU 等复杂控制流

### 未来改进

1. 集成 LSP4COBOL  parser 获取完整 AST
2. 实现完整的 SMOJOL 解释器执行
3. 支持更复杂的控制流分析（IF 分支、循环等）
4. 添加数据流分析能力

## 依赖关系

```
naca-cloud-native
├── naca-analyzer (新增)
│   └── cobol-rekt 模块
│       ├── smojol-core
│       ├── smojol-toolkit
│       ├── LSP4COBOL parser
│       └── LSP4COBOL engine
└── Spring Boot
```

## 参考资料

- [SMOJOL API 文档](./naca-cloud-native/SMOJOL_API.md)
- [集成总结](./SMOJOL_INTEGRATION_SUMMARY.md)
- [cobol-rekt GitHub](https://github.com/avishek-sen-gupta/cobol-rekt)
