# JDTLS Rename 重构指南

## ⚠️ 执行前必读

> **每次使用 JDTLS 进行重命名前，必须先验证环境配置正确！**
>
> 详见 [`REFACTORING_PLAN.md`](./REFACTORING_PLAN.md) 中的 **"JDTLS 环境配置检查清单"** 章节。

### 快速检查清单

| 检查项 | 命令 | 预期结果 |
|--------|------|---------|
| JDK 版本 | `java -version` | 与项目要求一致（本项目：JDK 21） |
| Gradle 构建 | `./gradlew build` | BUILD SUCCESSFUL |
| jdtls 索引 | `ls .jdtls-workspace` | 目录存在且有内容 |

### 常见失败原因

**90%+ 的重命名失败**是由以下原因导致的：

```
JDK 版本不兼容 (如 JDK 25)
    ↓
Gradle 构建失败 ("Type T not present")
    ↓
jdtls 无法同步项目
    ↓
重命名返回 "Renaming this element is not supported"
```

**解决方案**：配置正确的 JDK 版本，重建 jdtls 索引。

---

## 概述

本文档总结了在 Naca 项目中使用 JDTLS (Java Development Tools Language Server) 的 `lsp_rename` 功能进行变量重命名的最佳实践，包括命名冲突检测、处理策略和常见陷阱。

---

## 1. JDTLS Rename 工作原理

### 1.1 核心优势

JDTLS rename 是**语义感知**的重命名工具，与简单的字符串替换不同：

| 特性 | 字符串替换 | JDTLS Rename |
|------|-----------|--------------|
| 作用域 | 全局匹配 | 语义绑定 |
| 跨文件 | 需手动处理 | 自动追踪 |
| 类型安全 | 无保证 | 编译器验证 |
| 注释/字符串 | 可能误改 | 智能区分 |

### 1.2 工作流程

```
1. lsp_symbols    → 获取文件中的所有符号，定位字段位置
2. lsp_prepare_rename → 检查该位置是否可重命名
3. lsp_rename     → 执行重命名，自动更新所有引用
4. lsp_diagnostics → 验证编译无错误
```

### 1.3 基本用法

```typescript
// 1. 获取符号列表
lsp_symbols(filePath: "/path/to/File.java", scope: "document")

// 2. 检查是否可重命名（可选但推荐）
lsp_prepare_rename(
  filePath: "/path/to/File.java",
  line: 33,      // 行号（1-based）
  character: 13  // 列号（0-based，指向变量名起始位置）
)

// 3. 执行重命名
lsp_rename(
  filePath: "/path/to/File.java",
  line: 33,
  character: 13,
  newName: "newVariableName"
)
```

---

## 2. 命名冲突检测

### 2.1 JDTLS 自动检测的冲突类型

#### 类型 1：局部变量遮蔽（Variable Shadowing）

```java
public class Example {
    private int order = 0;  // 字段
    
    public void setOrder(int order) {  // 参数
        this.order = order;  // 如果字段改名为 order，与参数冲突
    }
}
```

**JDTLS 错误信息**：
```
Error: Problem in 'Example.java'. Another name will shadow access to the renamed element
```

**原因**：如果将字段 `m_nOrder` 重命名为 `order`，方法内的 `order` 将指向参数而非字段。

#### 类型 2：类型名冲突

```java
public class DbConnectionBase { ... }

public class SQLClause {
    private DbConnectionBase m_connection;  // 字段类型与类名相关
}
```

**问题**：重命名 `m_connection` 为 `connection` 时，JDTLS 可能错误地将类名 `DbConnectionBase` 也修改。

#### 类型 3：继承/接口冲突

```java
public interface IBase {
    String getName();
}

public class Derived implements IBase {
    private String m_csName;  // 如果改名为 name，与接口方法冲突
}
```

### 2.2 冲突检测最佳实践

1. **始终使用 `lsp_prepare_rename`** 在重命名前检查
2. **观察返回的范围** 是否正确覆盖变量名
3. **注意错误信息** 中的具体冲突描述

---

## 3. 处理策略

### 3.1 策略一：使用更具描述性的名称

**场景**：字段名与参数名冲突

```java
// 原始代码
private String m_csName;
public void setName(String name) { ... }

// ❌ 直接改为 name 会导致冲突
private String name;  // 与参数 name 冲突

// ✅ 使用更具体的名称
private String columnName;  // 或 fieldName, instanceName
```

**命名建议**：
| 原始名称 | 冲突名称 | 推荐替代 |
|---------|---------|---------|
| `m_csName` | `name` | `columnName`, `fieldName`, `instanceName` |
| `m_nOrder` | `order` | `colOrder`, `sortOrder`, `sequence` |
| `m_bValid` | `valid` | `isValid`, `validationState` |

### 3.2 策略二：分步处理泛型类型

**场景**：字段类型与名称相关

```java
// 危险情况
private DbConnectionBase m_connection;

// ❌ 直接重命名可能修改类名
lsp_rename(..., newName: "connection")  // 可能将 DbConnectionBase 改为 connection

// ✅ 使用更安全的名称
lsp_rename(..., newName: "dbConnection")
```

**识别风险**：
- 字段类型包含字段名的一部分（如 `Connection` vs `m_connection`）
- 字段是某个类的实例，且该类名与字段名相似

### 3.3 跨文件引用处理

**JDTLS 正常行为**：自动处理跨文件引用

```java
// ColValue.java
private String m_csName;  // 重命名为 columnName

// SqlRequest.java（另一个文件）
csNames += col.m_csName;  // JDTLS 会自动更新为 col.columnName
```

**JDTLS 返回示例**：
```
Applied 13 edit(s) to 13 file(s):
  - ColValue.java
  - ColValueBigDecimal.java
  - SqlRequest.java
  - ... (共 13 个文件)
```

**正常情况下**：
- JDTLS **自动追踪并更新** 所有跨文件引用
- 不需要手动干预
- 这是 JDTLS 语义重命名的核心优势

**例外情况**（可能需要手动修复）：

| 情况 | 原因 | 解决方案 |
|------|------|---------|
| LSP 缓存未同步 | 文件刚被修改，LSP 未重新索引 | 重启 LSP 或等待索引完成 |
| 引用文件不在工作区 | 项目配置问题 | 确保项目正确导入 LSP |
| 编译错误未被检测 | JDTLS 版本 bug | 手动检查 + 编译验证 |
| 直接字段访问 | 非 getter/setter 方式的访问 | 编译验证时发现并修复 |

**如何减少遗漏**：
1. **确保 LSP 完全索引**：等待 JDTLS 完成项目初始化
2. **批量操作间隔**：多次重命名之间留出索引时间
3. **始终编译验证**：`./gradlew compileJava` 是最终检验

**验证流程**：
1. 重命名后检查 `git status`，确认修改的文件数量合理
2. 编译验证：`./gradlew compileJava`
3. 如有编译错误，检查是否有遗漏的引用（例外情况）

---

## 4. 完整工作流程

### 4.1 标准流程

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: 获取符号列表                                          │
│ lsp_symbols(filePath, scope="document")                      │
│ → 确认所有 m_ 字段的位置                                       │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 2: 逐个处理字段                                          │
│ for each field with m_ prefix:                              │
│   a. lsp_prepare_rename() 检查可重命名性                       │
│   b. 如果成功 → lsp_rename()                                  │
│   c. 如果冲突 → 选择替代名称重试                                │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 3: 验证修改                                              │
│ a. lsp_diagnostics(filePath) → 检查编译错误                    │
│ b. git diff → 确认修改范围合理                                  │
│ c. ./gradlew compileJava → 完整编译验证                        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│ Step 4: 清理意外修改                                          │
│ git restore <unrelated-files>                                │
│ → 恢复非目标文件的修改                                          │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 命名转换规则

| 原始模式 | 目标名称 | 示例 |
|---------|---------|------|
| `m_nVariable` | `variable` | `m_nNbDigits` → `nbDigits` |
| `m_bVariable` | `variable` | `m_bValid` → `valid` |
| `m_arrVariable` | `variable` | `m_arrParams` → `params` |
| `m_csVariable` | `variable` | `m_csName` → `name` 或 `columnName` |
| `m_lstVariable` | `variable` | `m_lstItems` → `items` |
| `m_tabVariable` | `variable` | `m_tabData` → `data` |
| `m_Variable` | `variable` | `m_Config` → `config` |

---

## 5. 常见陷阱与解决方案

### 5.1 陷阱：文件同步问题

**现象**：
```
Error: Resource 'path/to/File.java' is out of sync with file system.
```

**原因**：LSP 缓存与实际文件不同步

**解决方案**：
```typescript
// 重新获取符号，刷新 LSP 缓存
lsp_symbols(filePath, scope="document")
// 然后再次尝试重命名
lsp_rename(...)
```

### 5.2 陷阱：意外修改类名（最危险！）

**现象**：
```java
// 原始
public abstract class DbConnectionBase { }
public class ColValue { }

// 重命名 m_connection 后变成
public abstract class connection { }  // 类名被错误修改！

// 重命名 m_arrCols 后变成
public class cols { }  // 类名被错误修改！
```

**原因**：JDTLS 在某些情况下会错误地将类名与字段名关联

**触发条件**：
| 字段声明 | 风险等级 | 说明 |
|---------|---------|------|
| `DbConnectionBase m_connection` | 🔴 高危 | 类型名包含字段名 |
| `ArrayList<ColValue> m_arrCols` | 🔴 高危 | 泛型参数名包含字段名 |
| `int m_nNbDigits` | 🟢 安全 | 基本类型，无关联 |
| `String m_csName` | 🟡 中等 | 字符串类型，可能有问题 |

**关键发现**：
- 当新字段名与类型名的一部分相同时（如 `cols` vs `ColValue`），JDTLS 可能将类名也修改
- 这是 JDTLS 的语义重命名过度执行的结果

**解决方案**：
1. **使用与类型名完全不同的字段名**
   ```java
   // ❌ 危险：新名称与泛型参数相似
   m_arrCols → cols  // 可能将 ColValue 改成 cols
   
   // ✅ 安全：新名称与类型无关
   m_arrCols → columnList
   ```

2. **重命名后立即检查**
   ```bash
   git diff --name-only  # 检查修改的文件
   git diff <file>       # 确认没有类名被修改
   ```

3. **发现问题立即恢复**
   ```bash
   git restore <file-with-wrong-class-name>
   ```

**最佳实践**：
- 对于集合类型字段，使用描述性名称（如 `columnList`, `itemCollection`）
- 对于对象类型字段，避免使用与类名相似的名称
- 始终在重命名后验证编译

### 5.3 陷阱：泛型类型参数被修改

**现象**：
```java
// 原始
private ArrayList<ColValue> m_arrParams;

// 重命名 m_arrParams 为 params 后
private ArrayList<params> params;  // 泛型类型被错误修改！
```

**原因**：JDTLS 某些版本的重命名逻辑有 bug

**解决方案**：
1. 重命名后检查泛型声明
2. 手动修复被错误修改的泛型类型

### 5.4 陷阱：批量修改导致失控

**现象**：一次 `lsp_rename` 修改了数十个文件

**原因**：字段被广泛引用

**解决方案**：
1. 重命名后检查 `git status`
2. 使用 `git restore` 恢复非目标文件
3. 分批验证编译

---

## 6. 验证检查清单

每次重命名后，执行以下检查：

```bash
# 1. 查看修改的文件
git status --short | grep "^ M"

# 2. 检查具体修改内容
git diff <file-path>

# 3. 编译验证
./gradlew :naca-jlib:compileJava

# 4. 恢复非目标文件
git restore <unrelated-files>
```

---

## 7. 成功案例

### Naca 项目 jlib/sql 模块

成功重构的文件：

| 文件 | 变量数 | 状态 |
|------|-------|------|
| `DbColDefinitionDecimal.java` | 3 | ✅ |
| `BaseDbColDefinition.java` | 3 | ✅ |
| `DbColDefinitionInteger.java` | 1 | ✅ |
| `DbColDefinitionVarchar.java` | 1 | ✅ |
| `DbColDefinitionChar.java` | 1 | ✅ |
| `DbColDefinitionSmallint.java` | 1 | ✅ |
| `ColValue.java` | 3 | ✅ |
| `SqlRequest.java` | 2处引用 | ✅ (JDTLS遗漏，手动修复) |

**编译状态**：BUILD SUCCESSFUL

---

## 8. 总结

### 最佳实践

1. **始终使用 JDTLS rename**，不要用字符串替换
2. **重命名前用 `lsp_prepare_rename`** 检查
3. **冲突时选择替代名称**，而非绕过
4. **每次重命名后验证编译**
5. **检查 git diff 确认修改范围**
6. **及时恢复非目标文件的修改**

### 不应做的事

- ❌ 跳过 `lsp_prepare_rename` 检查
- ❌ 忽略命名冲突错误
- ❌ 批量重命名不验证
- ❌ 使用 `sed` 或字符串替换绕过 JDTLS
- ❌ 在编译失败时继续下一个文件

### 应该做的事

- ✅ 逐个字段处理，小步前进
- ✅ 每次修改后编译验证
- ✅ 记录遇到的冲突和处理方式
- ✅ 使用有意义的替代名称解决冲突
- ✅ 保持代码可编译状态

---

## 附录：工具命令参考

### LSP 工具

| 工具 | 用途 | 参数 |
|------|------|------|
| `lsp_symbols` | 获取文件符号 | `filePath`, `scope="document"` |
| `lsp_prepare_rename` | 检查可重命名 | `filePath`, `line`, `character` |
| `lsp_rename` | 执行重命名 | `filePath`, `line`, `character`, `newName` |
| `lsp_diagnostics` | 检查编译错误 | `filePath` |
| `lsp_find_references` | 查找所有引用 | `filePath`, `line`, `character` |

### Gradle 命令

```bash
# 编译单个模块
./gradlew :naca-jlib:compileJava --no-daemon

# 完整构建
./gradlew build --no-daemon

# 清理后编译
./gradlew clean :naca-jlib:compileJava --no-daemon
```

### Git 命令

```bash
# 查看修改
git status --short

# 查看具体修改
git diff <file-path>

# 恢复文件
git restore <file-path>

# 恢复所有未提交的修改
git restore .
```

---

## 9. JDTLS 限制与已知问题

### 9.1 不支持重命名的情况

在 Naca 项目重构过程中，发现以下情况 JDTLS 返回 "Renaming this element is not supported"：

| 情况 | 示例文件 | 可能原因 |
|------|---------|---------|
| 简单包装类 | `StringRef.java`, `IntegerRef.java` | 字段访问过于简单 |
| 异常类 | `DbConnectionException.java` | 特殊类类型 |
| 枚举/常量类 | `SQLTypeOperation.java` | 静态上下文 |

**建议**：遇到此类情况，可以考虑后续手动重构或使用 IDE 的重构功能。

### 9.2 意外修改其他模块

**现象**：每次 LSP 操作后，`LogParams.java` 被意外修改

**解决方案**：
```bash
# 每次操作后恢复
git restore naca-jlib/src/main/java/jlib/log/LogParams.java
```

### 9.3 类名被错误修改

**触发条件**：字段名与类型名相似时

| 字段声明 | 新名称 | 风险 |
|---------|--------|------|
| `ArrayList<ColValue> m_arrCols` | `cols` | 🔴 将 ColValue 改成 cols |
| `DbConnectionBase m_connection` | `connection` | 🔴 将 DbConnectionBase 改成 connection |

**预防措施**：使用与类型名完全不同的字段名

### 9.4 工作量评估

基于 Naca 项目的实际执行结果：

| 模块 | 总变量数 | 可成功重构 | 成功率 |
|------|---------|-----------|--------|
| naca-jlib/sql | ~100+ | 10 | ~10% |
| naca-jlib/其他 | ~400+ | 0 | 0% |
| naca-trans | ~128 | 0 | 0% |

**结论**：JDTLS rename 在大规模重构中的适用性有限，建议配合 IDE 重构功能使用。

---

## 10. 替代方案

当 JDTLS rename 不可用时，可以考虑：

### 10.1 IDE 内置重构
- IntelliJ IDEA: Refactor → Rename (Shift+F6)
- VS Code with Java Extension: F2

### 10.2 批量重构脚本
使用 OpenRewrite 或自定义脚本进行批量重构（需要额外配置）

### 10.3 手动重构
对于无法自动化的文件，手动编辑并使用 `this.` 前缀避免命名冲突

---

*文档版本：1.1*
*最后更新：2026-03-16*
*适用于：Naca 项目 Java 代码规范化重构*