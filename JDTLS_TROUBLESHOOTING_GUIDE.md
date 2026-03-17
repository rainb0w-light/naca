# JDTLS 重命名服务故障排除与解决方案指南

## 问题现象
- JDTLS `lsp_prepare_rename` 和 `lsp_rename` 返回 "Renaming this element is not supported"
- 即使简单字段也无法重命名
- LSP 可以提供符号（基本解析工作），但语义分析失败

## 根本原因分析

### 1. JDK 版本不匹配（主要问题）
- **症状**：根目录 `.classpath` 配置为 `JavaSE-25`，但子项目使用 `JavaSE-21`
- **影响**：JDTLS 无法正确索引项目，导致语义分析失败
- **验证方法**：
  ```bash
  grep -r "JavaSE-25" .classpath
  java -version  # 应该显示 JDK 21
  ```

### 2. JDTLS 工作空间配置错误
- **症状**：OpenCode 配置指向不存在的 `.jdtls-workspace` 目录
- **影响**：JDTLS 使用缓存的工作空间，无法正确加载项目结构
- **验证方法**：
  ```bash
  ls -la .jdtls-workspace/  # 应该存在且有内容
  ps aux | grep jdtls       # 检查工作空间路径
  ```

### 3. 构建文件重复冲突
- **症状**：同时存在 `build.gradle` 和 `build.gradle.kts` 文件
- **影响**：造成构建系统混淆，JDTLS 无法确定正确的项目配置
- **验证方法**：
  ```bash
  find . -name "*.gradle*" | head -10
  ```

## 解决方案步骤

### 步骤 1: 修复 JDK 版本配置
```bash
# 1.1 检查当前 JDK 版本
java -version

# 1.2 修复根目录 .classpath 文件
# 将 JavaSE-25 改为 JavaSE-21
sed -i '' 's/JavaSE-25/JavaSE-21/g' .classpath

# 1.3 验证子项目 .classpath 文件
# 确保所有子项目的 .classpath 都使用 JavaSE-21
grep -r "JavaSE-21" */.classpath
```

### 步骤 2: 创建正确的 JDTLS 工作空间
```bash
# 2.1 创建工作空间目录结构
mkdir -p .jdtls-workspace/.metadata/.plugins

# 2.2 更新 OpenCode 配置
# 在 opencode.json 中确保：
# - command 使用正确的 jdtls-direct-wrapper.sh
# - -data 参数指向 .jdtls-workspace 目录
# - settings 中指定正确的 JDK 21 路径
```

### 步骤 3: 重启 JDTLS 服务
```bash
# 3.1 杀死所有 JDTLS 进程
pkill -f "jdtls"

# 3.2 等待几秒钟让 OpenCode 重新启动
sleep 5

# 3.3 验证 JDTLS 进程使用正确的配置
ps aux | grep jdtls
```

### 步骤 4: 验证修复效果
```bash
# 4.1 创建简单测试文件
cat > test/SimpleTest.java << 'EOF'
package test;
public class SimpleTest {
    private String m_testField = "test";
    public String getTestField() { return m_testField; }
}
EOF

# 4.2 测试 prepare rename
lsp_prepare_rename test/SimpleTest.java 3 19

# 4.3 如果成功，测试实际重命名
lsp_rename test/SimpleTest.java 3 19 "testField"

# 4.4 清理测试文件
rm test/SimpleTest.java
```

## 预防措施

### 1. 统一 JDK 版本
- 确保 `build.gradle`、`.classpath`、`opencode.json` 都使用相同的 JDK 版本
- 在 `gradle.properties` 中设置 `org.gradle.java.home`

### 2. 避免构建文件重复
- 删除不需要的 `.gradle.kts` 或 `.gradle` 文件
- 保持构建脚本的一致性

### 3. 定期清理工作空间
- 如果遇到奇怪的 LSP 问题，执行：
  ```bash
  rm -rf .jdtls-workspace
  pkill -f "jdtls"
  ```

## 故障诊断流程图

```
"Renaming not supported" 错误
          ↓
检查 JDK 版本是否一致？
          ↓
是 → 检查 JDTLS 工作空间配置
          ↓
否 → 修复 JDK 版本配置
          ↓
创建/验证 .jdtls-workspace 目录
          ↓
重启 JDTLS 服务
          ↓
测试简单重命名操作
          ↓
成功！可以进行大规模重构
```

## 常用命令参考

```bash
# 检查 JDK 版本
java -version

# 检查 Gradle 构建
./gradlew build --no-daemon

# 查看 JDTLS 进程
ps aux | grep jdtls

# 清理 JDTLS 缓存
rm -rf .jdtls-workspace
pkill -f "jdtls"

# 测试 LSP 功能
lsp_symbols <file> document
lsp_prepare_rename <file> <line> <column>
lsp_rename <file> <line> <column> <newName>
lsp_diagnostics <file>
```

## 相关配置文件位置

- **OpenCode 配置**: `opencode.json`
- **JDTLS 启动脚本**: `jdtls-direct-wrapper.sh`
- **项目 JDK 配置**: `build.gradle`, `.classpath`
- **Gradle JDK 配置**: `gradle.properties`
- **JDTLS 工作空间**: `.jdtls-workspace/`

---
最后更新: 2026-03-16
作者: Sisyphus