# Comprehensive Plan for m_ Variable Refactoring (1,096 files)

## ⚠️ 重要：执行前必读 - JDTLS 环境配置检查

> **每次执行代码规范化任务前，必须先验证 JDTLS 环境配置正确！**
> 
> 以下问题是实际项目中遇到的，导致了 **90%+ 的重命名操作失败**。

### 1.0 JDTLS 环境配置检查清单

在开始任何重命名操作前，**必须**执行以下检查：

#### ✅ 检查项 1：JDK 版本兼容性

```bash
# 检查当前 JDK 版本
java -version

# 检查项目要求的 JDK 版本
grep -r "sourceCompatibility\|targetCompatibility" build.gradle
```

**常见问题**：JDK 版本过新（如 JDK 25）与 Gradle 不兼容，导致：
- `Type T not present` 错误
- jdtls 无法同步项目
- 重命名返回 "Renaming this element is not supported"

**解决方案**：
```bash
# 安装项目兼容的 JDK 版本（本项目需要 JDK 21）
brew install openjdk@21

# 配置 Gradle 使用正确的 JDK
echo "org.gradle.java.home=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" >> gradle.properties
```

#### ✅ 检查项 2：Gradle 构建状态

```bash
# 验证 Gradle 能正常构建
./gradlew clean build --no-daemon
```

**如果失败**：检查错误日志，通常是 JDK 版本问题。

#### ✅ 检查项 3：jdtls 索引状态

```bash
# 检查 jdtls 工作区目录是否存在
ls -la .jdtls-workspace/

# 如果索引损坏，清理重建
rm -rf .jdtls-workspace
```

#### ✅ 检查项 4：OpenCode jdtls 配置

确保 `opencode.json` 中配置了正确的 JAVA_HOME：

```json
{
  "lsp": {
    "jdtls": {
      "command": [
        "/path/to/jdtls-direct-wrapper.sh",
        "-data", "/path/to/project/.jdtls-workspace"
      ],
      "extensions": [".java"],
      "settings": {
        "java": {
          "configuration": {
            "runtimes": [
              {
                "name": "JavaSE-21",
                "path": "/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home",
                "default": true
              }
            ]
          }
        }
      }
    }
  }
}
```

**⚠️ 重要：Homebrew jdtls 的问题**

Homebrew 安装的 `jdtls` 脚本会覆盖 `JAVA_HOME` 设置：

```bash
# /opt/homebrew/bin/jdtls 的内容
JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home}" exec "..."
```

这会导致即使设置了 `JAVA_HOME`，jdtls 仍可能使用系统默认 JDK（如 JDK 25）。

**解决方案**：创建直接启动脚本绕过 Homebrew wrapper：

```bash
#!/bin/bash
# jdtls-direct-wrapper.sh - 直接使用 JDK 21 启动 jdtls

JDTLS_HOME="/opt/homebrew/Cellar/jdtls/1.57.0/libexec"
JAVA_21="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home/bin/java"

exec "$JAVA_21" \
  -Declipse.application=org.eclipse.jdt.ls.core.id1 \
  -Declipse.product=org.eclipse.jdt.ls.core.product \
  -Dosgi.sharedConfiguration.area="$JDTLS_HOME/config_mac" \
  -Xms1G \
  --add-modules=ALL-SYSTEM \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  -jar "$JDTLS_HOME/plugins/org.eclipse.equinox.launcher_*.jar" \
  -data "/path/to/project/.jdtls-workspace" \
  "$@"
```

#### ✅ 检查项 5：测试 jdtls 重命名功能

在开始批量重命名前，先用一个简单文件测试：

```bash
# 使用 OpenCode 测试
# 1. 打开一个简单的 Java 文件
# 2. 执行 lsp_prepare_rename 检查某个字段
# 3. 执行 lsp_rename 测试重命名
# 4. 验证编译无误
```

### 1.0.1 失败原因诊断流程

如果 `lsp_rename` 返回 "Renaming this element is not supported"：

```
┌─────────────────────────────────────────────────────────────┐
│ 诊断步骤                                                     │
└─────────────────────────────────────────────────────────────┘
  1. 检查 Gradle 构建 → 失败？ → JDK 版本问题
  2. 检查 jdtls 日志 → "Synchronize project failed"？ → 索引问题
  3. 检查 jdtls 进程 JAVA_HOME → 错误 JDK？ → 使用直接启动脚本
  4. 测试其他文件 → 全部失败？ → 环境配置问题
  5. 测试其他文件 → 部分成功？ → JDTLS 固有限制（见下文）
```

**检查 jdtls 进程使用的 JDK**：
```bash
ps aux | grep jdtls | grep -v grep | awk '{print $2}' | \
  xargs -I{} sh -c 'ps eww -p {} 2>/dev/null | tr " " "\n" | grep JAVA_HOME'
```

如果显示 `/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home`（JDK 25）而非 JDK 21，
说明 Homebrew wrapper 覆盖了配置，需要使用直接启动脚本。

### 1.0.2 JDTLS 固有限制（无法解决）

即使环境配置正确，以下类型的文件 JDTLS 仍不支持重命名：

| 类型 | 示例文件 | 原因 |
|------|---------|------|
| 简单包装类 | `StringRef.java`, `IntegerRef.java` | 字段访问过于简单 |
| 异常类 | `DbConnectionException.java` | 特殊类类型 |
| 枚举/常量类 | `SQLTypeOperation.java` | 静态上下文 |
| 词法分析器基类 | `CBaseToken.java` | 复杂继承结构 |
| Ant Task 类 | `NacaTransTask.java` | 特殊类类型 |

**解决方案**：使用 IntelliJ IDEA 重构功能 (Shift+F6) 或 OpenRewrite。

### 1.0.3 高风险场景（类名被错误修改）

以下情况 JDTLS 可能错误地将类名也修改：

| 字段声明 | 风险等级 | 安全做法 |
|---------|---------|---------|
| `DbConnectionBase m_connection` | 🔴 高危 | 重命名为 `dbConnection` 而非 `connection` |
| `ArrayList<ColValue> m_arrCols` | 🔴 高危 | 重命名为 `columnList` 而非 `cols` |
| `int m_nNbDigits` | 🟢 安全 | 可重命名为 `nbDigits` |

**预防措施**：重命名后立即检查 `git diff --name-only`，确认没有意外修改。

---

## Approach Overview

Given the scale and complexity, I recommend a **manual but systematic approach** using JDTLS rename functionality directly through your IDE or LSP client. This ensures semantic safety while maintaining control over the process.

## Step-by-Step Process

### 1. Set Up Your Environment
- Ensure JDTLS is properly configured with your project
- **Verify JDK version matches project requirements** (见上文检查清单)
- **Confirm Gradle builds successfully before starting**
- Use VS Code with Java extensions, Eclipse, or IntelliJ with JDTLS support
- Verify LSP is working by testing rename on a sample file

### 2. Naming Convention Rules
Apply these conversion rules consistently:

| Original Pattern | Converted Name | Example |
|------------------|----------------|---------|
| `m_nVariable` | `variable` | `m_nCurrentSite` → `currentSite` |
| `m_bVariable` | `variable` | `m_bDoAllSites` → `doAllSites` |
| `m_arrVariable` | `variable` | `m_arrValues` → `values` |
| `m_csVariable` | `variable` | `m_csControlerName` → `controlerName` |
| `m_lstVariable` | `variable` | `m_lstChildren` → `children` |
| `m_tabVariable` | `variable` | `m_tabControlers` → `controlers` |
| `m_grpVariable` | `variable` | `m_grpConfig` → `grpConfig` |
| `m_Variable` | `variable` | `m_Config` → `config` |

### 3. File Processing Strategy

#### Batch 1: High Priority Modules (Start Here)
- `naca-jlib/src/main/java/jlib/sql/` (~50 files)
- `naca-jlib/src/main/java/jlib/controler/` (~10 files)  
- `naca-jlib/src/main/java/jlib/misc/` (~30 files)

#### Batch 2: Generate Module
- `naca-trans/src/main/java/generate/` (~200 files)

#### Batch 3: Semantic Module  
- `naca-trans/src/main/java/semantic/` (~300 files)

#### Batch 4: Remaining Files
- All other files (~500 files)

### 4. For Each File:

1. **Open the file** in your IDE with JDTLS support
2. **Find all m_ variables** using search: `m_[a-zA-Z]`
3. **For each variable**:
   - Right-click → "Rename Symbol" (or F2 in most IDEs)
   - Apply the naming convention from the table above
   - JDTLS will automatically update all references
4. **Save the file**
5. **Verify no compilation errors** appear

### 5. Verification After Each Batch

Run basic compilation to ensure correctness:
```bash
# Compile just the jlib module first
./gradlew :naca-jlib:compileJava

# Once jlib is clean, compile trans
./gradlew :naca-trans:compileJava  

# Finally, full build
./gradlew build
```

## Handling _ Prefixed Variables (24 files)

The `_` prefixed variables require special attention due to setter method conflicts:

### Problem Analysis
Files like `Db.java` have patterns like:
```java
protected String _conString;
public void setConString(String conString) {
    if (!_conString.equals(conString)) cleanConnection();
    _conString = conString;
}
```

After renaming `_conString` to `conString`, this becomes:
```java
protected String conString;
public void setConString(String conString) {
    if (!conString.equals(conString)) cleanConnection(); // Always false!
    conString = conString; // Does nothing!
}
```

### Solution Strategy

**Option 1: Use `this.` prefix (Recommended)**
1. First, manually add `this.` to all field assignments in setter methods:
   ```java
   public void setConString(String conString) {
       if (!this.conString.equals(conString)) cleanConnection();
       this.conString = conString;
   }
   ```
2. Then use JDTLS rename to change `_conString` to `conString`

**Option 2: Rename to more descriptive names**
Instead of `conString`, use `connectionString` to avoid conflicts entirely.

### Files Requiring Manual Handling
The 24 files with `_` prefixed variables are:
```
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/BASE64DecoderInputStream.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/XmlHelper.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/FtpException.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/Db.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/TagRemoverInputStream.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/DefaultNamespaceContextProvider.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/misc/IsTypeUtil.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/blowfish/BlowfishInputStream.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/ZipHelper.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/blowfish/SHA1.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/blowfish/BlowfishOutputStream.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/XmlComparator.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/BASE64InputStream.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/AccessChecker.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/FtpPassiveClient.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/Helpers/ThumbnailHelper.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/misc/StreamUtil.java
/Volumes/AppData/codebase/naca/naca-jlib/src/main/java/jlib/jslibComp/PagingInfo.java
/Volumes/AppData/codebase/naca/naca-rt-tests/src/main/java/nacaTests/ExtraTests/ExtraTest.java
/Volumes/AppData/codebase/naca/naca-rt-tests/src/main/java/nacaTests/CobolLikeSupport/TestVarTypes.java
/Volumes/AppData/codebase/naca/naca-trans/src/main/java/generate/java/verbs/CJavaLoopIter.java
/Volumes/AppData/codebase/naca/naca-trans/src/main/java/parser/Cobol/elements/CInspect.java
/Volumes/AppData/codebase/naca/naca-trans/src/main/java/lexer/FPac/CFPacConstantList.java
/Volumes/AppData/codebase/naca/naca-trans/src/main/java/lexer/FPac/CFPacKeywordList.java
```

### Manual Process for _ Variables:
1. **Open each file** 
2. **Identify setter methods** that reference the `_` prefixed fields
3. **Add `this.` prefix** to field references in setters
4. **Use JDTLS rename** to remove the `_` prefix
5. **Verify compilation**

## Risk Mitigation

### Backup Strategy
- Commit current state before starting: `git commit -m "Pre-refactoring backup"`
- Work in small batches with frequent commits
- Use feature branches if possible

### Rollback Plan
- If issues arise, `git checkout` individual files
- For larger issues, `git reset --hard` to last good commit

### Quality Assurance
- Run `./gradlew build` after each major batch
- Ensure all existing tests pass
- Manual testing of critical functionality

## Expected Timeline

- **m_ variables (1,096 files)**: 8-16 hours depending on automation level
- **_ variables (24 files)**: 1-2 hours with manual handling
- **Total estimated time**: 10-18 hours

## Success Criteria

✅ All `m_` and `_` prefixes removed from variable names  
✅ All code compiles without errors  
✅ All existing tests pass  
✅ Method names remain in camelCase (already compliant)  
✅ No functional changes to the codebase  

This plan provides a systematic, safe approach to achieve full Java naming convention compliance while leveraging JDTLS semantic rename functionality for maximum safety.