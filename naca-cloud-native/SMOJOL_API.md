# Naca Cloud Native SMOJOL API 文档

## 概述

naca-cloud-native 项目现已集成 cobol-rekt 的 SMOJOL (SMol Java-powered CobOL) 能力，提供以下在线服务：

- **COBOL 代码解释执行** - 使用 SMOJOL 解释器实时执行 COBOL 代码
- **COBOL 翻译 Java** - 使用 NacaTrans 将 COBOL 转换为 Java
- **Java 代码执行** - 在线编译和执行生成的 Java 代码
- **程序结构分析** - 分析 COBOL 程序的 DIVISION、段落、变量等
- **控制流图生成** - 生成 CFG 节点、边和 DOT 格式可视化数据

## API 端点

### 1. SMOJOL 服务 (`/api/smojol`)

#### 1.1 解释执行 COBOL

**端点:** `POST /api/smojol/interpret`

**请求:**
```json
{
  "cobolSource": "       IDENTIFICATION DIVISION.\n       PROGRAM-ID. HELLO.\n       PROCEDURE DIVISION.\n       DISPLAY 'Hello World'.\n       STOP RUN.",
  "inputData": ""
}
```

**响应:**
```json
{
  "success": true,
  "output": "SMOJOL interpretation output...",
  "errors": []
}
```

#### 1.2 生成 AST

**端点:** `POST /api/smojol/ast`

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
  "message": "AST building available via LSP4COBOL",
  "sourceLength": 150,
  "structure": {
    "programId": "HELLO",
    "divisions": [],
    "sections": [],
    "paragraphs": ["MAIN-PROCEDURE"]
  }
}
```

#### 1.3 生成控制流图

**端点:** `POST /api/smojol/cfg`

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

#### 1.4 分析程序结构

**端点:** `POST /api/smojol/analyze`

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

#### 1.5 可视化 CFG

**端点:** `GET /api/smojol/visualize?source=...&format=svg`

**响应:** SVG 图像

---

### 2. 转译服务 (`/api`)

#### 2.1 转译 COBOL 到 Java

**端点:** `POST /api/transpile`

**请求:**
```json
{
  "cobolSource": "       IDENTIFICATION DIVISION...\n       PROGRAM-ID. MYPROG.\n       ...",
  "programName": "MYPROG"
}
```

**响应:**
```json
{
  "success": true,
  "javaSource": "package batch;\n\nimport nacaLib.program.*;\n\npublic class MYPROG extends BatchProgram {\n    ...\n}",
  "errors": []
}
```

#### 2.2 转译文件

**端点:** `POST /api/transpile/file`

**请求:** multipart/form-data 上传 .cbl 或 .cob 文件

**响应:** 同 `/api/transpile`

#### 2.3 获取样本程序列表

**端点:** `GET /api/samples`

**响应:**
```json
["BATCH1", "CALLMSG", "ONLINE1"]
```

---

### 3. 执行服务 (`/api`)

#### 3.1 运行已转译的 COBOL 程序

**端点:** `POST /api/run`

**请求:**
```json
{
  "programName": "BATCH1",
  "programType": "batch"
}
```

**响应:**
```json
{
  "success": true,
  "output": "Program output...",
  "errors": []
}
```

---

## 使用示例

### cURL 示例

#### 转译 COBOL

```bash
curl -X POST http://localhost:8080/api/transpile \
  -H "Content-Type: application/json" \
  -d '{
    "cobolSource": "       IDENTIFICATION DIVISION.\n       PROGRAM-ID. TEST.\n       DATA DIVISION.\n       WORKING-STORAGE SECTION.\n       01 WS-VAR PIC X(10).\n       PROCEDURE DIVISION.\n       DISPLAY WS-VAR.\n       STOP RUN.",
    "programName": "TEST"
  }'
```

#### 解释执行

```bash
curl -X POST http://localhost:8080/api/smojol/interpret \
  -H "Content-Type: application/json" \
  -d '{
    "cobolSource": "       IDENTIFICATION DIVISION.\n       PROGRAM-ID. HELLO.\n       PROCEDURE DIVISION.\n       DISPLAY \"Hello from SMOJOL\".\n       STOP RUN."
  }'
```

#### 分析程序

```bash
curl -X POST http://localhost:8080/api/smojol/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "cobolSource": "       IDENTIFICATION DIVISION.\n       PROGRAM-ID. ANALYZE.\n       DATA DIVISION.\n       WORKING-STORAGE SECTION.\n       01 WS-COUNTER PIC 9(3).\n       PROCEDURE DIVISION.\n       MAIN-PROCEDURE.\n       MOVE 0 TO WS-COUNTER.\n       STOP RUN."
  }'
```

#### 生成 CFG

```bash
curl -X POST http://localhost:8080/api/smojol/cfg \
  -H "Content-Type: application/json" \
  -d '{
    "cobolSource": "       IDENTIFICATION DIVISION.\n       PROGRAM-ID. CFG-DEMO.\n       PROCEDURE DIVISION.\n       MAIN-PROCEDURE.\n       PERFORM CALCULATE.\n       STOP RUN.\n       CALCULATE.\n       EXIT."
  }'
```

### JavaScript/TypeScript 示例

```typescript
// 转译 COBOL
async function transpileCobol(cobolSource: string, programName: string) {
  const response = await fetch('http://localhost:8080/api/transpile', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ cobolSource, programName })
  });
  return await response.json();
}

// 分析 COBOL 程序
async function analyzeProgram(cobolSource: string) {
  const response = await fetch('http://localhost:8080/api/smojol/analyze', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ cobolSource })
  });
  return await response.json();
}

// 生成 CFG 并可视化
async function generateCFG(cobolSource: string) {
  const response = await fetch('http://localhost:8080/api/smojol/cfg', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ cobolSource })
  });
  const result = await response.json();
  
  // 使用 Viz.js 渲染 DOT
  const svg = await Viz(result.dotFormat);
  return svg;
}
```

---

## 数据格式

### COBOL 源代码格式

COBOL 源代码应保持原始格式，包括：
- 标识 Division: `IDENTIFICATION DIVISION.`
- 程序 ID: `PROGRAM-ID. program-name.`
- 数据 Division (可选): `DATA DIVISION.`
- 过程 Division: `PROCEDURE DIVISION.`

### 程序类型

- `batch`: 批处理程序 (默认)
- `online`: 在线程序 (包含 CICS 命令)

---

## 错误处理

所有 API 响应统一格式：

**成功响应:**
```json
{
  "success": true,
  "output": "...", // 或 javaSource/analysis 等
  "errors": []
}
```

**错误响应:**
```json
{
  "success": false,
  "output": null,
  "errors": ["错误信息 1", "错误信息 2"]
}
```

---

## 依赖说明

### cobol-rekt 模块

| 模块 | 版本 | 用途 |
|------|------|------|
| smojol-core | 1.0-SNAPSHOT | COBOL 解释器和 AST |
| smojol-toolkit | 1.0-SNAPSHOT | 分析工具包 |
| parser (LSP4COBOL) | 1.0-SNAPSHOT | COBOL 语法解析 |
| engine | 1.0.0-SNAPSHOT | 执行引擎 |
| common | 1.0.8 | 通用工具类 |

### 图可视化

- **JGraphT**: 图数据结构和算法
- **GraphViz Java**: DOT 到 SVG/PNG 渲染

---

## 构建和运行

### 前提条件

1. 构建 cobol-rekt 到本地 Maven 仓库:
```bash
cd /Volumes/AppData/codebase/cobol-rekt
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
mvn clean install -DskipTests
```

2. 构建 Naca 项目:
```bash
cd /Volumes/AppData/codebase/naca
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
./gradlew build -x test
```

### 运行服务

```bash
cd /Volumes/AppData/codebase/naca/naca-cloud-native
../gradlew bootRun
```

服务默认运行在 `http://localhost:8080`

---

## 参考资料

- [cobol-rekt GitHub](https://github.com/avishek-sen-gupta/cobol-rekt)
- [SMOJOL Interpreter](https://github.com/avishek-sen-gupta/cobol-rekt/tree/main/smojol-core)
- [LSP4COBOL](https://github.com/eclipse-che4z/che-che4z-lsp-for-cobol)
- [Naca Documentation](../doc-md/zh/README.md)
