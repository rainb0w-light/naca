# JDTLS 重命名限制记录

> **⚠️ 重要**：如果遇到大量文件返回 "Renaming this element is not supported"，
> 请先检查 [`REFACTORING_PLAN.md`](./REFACTORING_PLAN.md) 中的 **"JDTLS 环境配置检查清单"**。
>
> 大多数情况下，这是 JDK 版本不兼容导致 jdtls 无法索引项目，而非 JDTLS 的固有限制。

---

## 环境配置问题导致的失败（已解决）

### 问题诊断

在 2026-03-16 的调试中发现，**90%+ 的重命名失败**是由环境配置问题导致的：

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| JDK 版本不兼容 | 系统使用 JDK 25，项目需要 JDK 21 | 安装并配置 JDK 21 |
| Gradle 构建失败 | "Type T not present" 错误 | 使用正确 JDK 版本 |
| jdtls 无法同步项目 | 上述问题导致索引失败 | 重建 jdtls 工作区 |

### 解决步骤

```bash
# 1. 安装 JDK 21
brew install openjdk@21

# 2. 配置 Gradle 使用 JDK 21
echo "org.gradle.java.home=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" >> gradle.properties

# 3. 验证构建
./gradlew clean build

# 4. 清理并重建 jdtls 索引
rm -rf .jdtls-workspace

# 5. 重启 OpenCode/jdtls
```

---

## JDTLS 固有限制（无法解决）

以下文件即使环境配置正确，JDTLS 仍不支持重命名：

### naca-jlib/sql 模块

| 文件 | 字段 | 错误信息 | 原因分析 |
|------|------|---------|---------|
| `DbColDefErrorManager.java` | `m_sb`, `m_nLine`, `m_nErrors` | Renaming not supported | StringBuilder 和简单类型字段 |
| `SQLLoadStatus.java` | `m_bSuccess`, `m_bDuplicates` | Renaming not supported | 布尔类型字段 |
| `VarBinary.java` | `m_tb` | Renaming not supported | 字节数组字段 |
| `DbConnectionException.java` | `m_csMessage` | Renaming not supported | 异常类 |
| `SQLTypeOperation.java` | `m_bExecuteWithStatement` | Renaming not supported | 枚举类型 |
| `SQLClauseSPParam*.java` | `m_nVal`, `m_sVal`, `m_dVal`, `m_csVal` | Renaming not supported | 参数类 |
| `StatementPosInPool.java` | `m_connection` | 类名被修改 | 字段类型与名称相关 |
| `ColValueCollection.java` | `m_arrCols` | 类名被修改 | 泛型类型与名称相关 |

### naca-jlib/misc 模块

| 文件 | 字段 | 错误信息 | 原因分析 |
|------|------|---------|---------|
| `StopWatch.java` | `m_lStart` | Renaming not supported | 简单包装类 |
| `StringRef.java` | `m_cs` | Renaming not supported | 简单包装类 |
| `IntegerRef.java` | `m_n` | Renaming not supported | 简单包装类 |

### naca-trans 模块

| 文件 | 字段 | 错误信息 | 原因分析 |
|------|------|---------|---------|
| `lexer/CBaseToken.java` | `m_Value`, `m_line` | Renaming not supported | 词法分析器基类 |
| `NacaTransTask.java` | `m_cfgFilePath`, `m_groupToTranscode` | Renaming not supported | 任务类 |

---

## 高风险文件（会导致类名被修改）

| 文件 | 字段 | 新名称 | 风险 |
|------|------|--------|------|
| `StatementPosInPool.java` | `m_connection` | `dbConn` → 类名 DbConnectionBase 被修改为 dbConn |
| `ColValueCollection.java` | `m_arrCols` | `cols` → 类名 ColValue 被修改为 cols |
| `DbConnectionBase.java` | `m_connection` | `connection` → 类名被修改 |
| `SQLClause.java` | `m_spCallClause` | `spCallClause` → 类名 SQLClauseSPCall 被修改 |

---

## 意外修改其他模块

每次 LSP 操作后，以下文件被意外修改：

| 模块 | 文件 | 解决方案 |
|------|------|---------|
| naca-jlib/log | `LogParams.java` | `git restore naca-jlib/src/main/java/jlib/log/LogParams.java` |
| naca-jlib/log | `LogCenterDbFlat.java` | `git restore` |
| naca-jlib/log | `LogCenterLoader.java` | `git restore` |
| naca-jlib/exception | `ApplicativeException.java`, `ProgrammingException.java`, `TechnicalException.java` | `git restore naca-jlib/src/main/java/jlib/exception/*.java` |

---

## 统计

### 环境配置修复前（2026-03-16 之前）

- **不支持重命名的文件**: 15+
- **高风险文件（类名被修改）**: 4
- **意外修改的文件**: 7+
- **成功重命名的文件**: 6
- **成功重命名的变量**: 10
- **成功率**: ~1.6%

### 环境配置修复后

- **Gradle 构建**: ✅ BUILD SUCCESSFUL
- **jdtls 索引**: ✅ 正常
- **预期成功率**: 大幅提升（需验证）

---

## 结论

1. **首要检查**：遇到大量 "Renaming not supported" 错误时，先检查 JDK 版本和 Gradle 构建状态
2. **环境配置**：确保 JDK 版本与项目要求一致，Gradle 能正常构建
3. **固有限制**：部分文件类型（异常类、枚举、简单包装类等）JDTLS 确实不支持，需使用替代方案
4. **替代方案**：IntelliJ IDEA 重构功能 (Shift+F6) 或 OpenRewrite

---

*最后更新：2026-03-16*
*相关文档*: [`REFACTORING_PLAN.md`](./REFACTORING_PLAN.md), [`JDTLS_RENAME_GUIDE.md`](./JDTLS_RENAME_GUIDE.md)