# 无头模式运行指南

本文档介绍如何在无头 (Headless) 模式下运行 Hungarian Refactorer 插件进行批量代码重构。

## 什么是无头模式

无头模式是指在没有图形界面的环境下运行 IntelliJ IDEA。这对于：
- CI/CD 流水线
- 批量代码迁移
- 自动化代码审查
- 服务器端代码处理

非常有用。

## 环境要求

- IntelliJ IDEA 2023.3 或更高版本
- Java 17 或更高版本
- 至少 4GB 可用内存

## 方式一：使用 IntelliJ 自带的命令行工具

### 1. 找到 IDEA 命令行启动器

**macOS:**
```bash
/Applications/IntelliJ\ IDEA.app/Contents/MacOS/idea
```

**Linux:**
```bash
/path/to/idea/bin/idea.sh
```

**Windows:**
```batch
C:\Path\To\Idea\bin\idea64.exe
```

### 2. 无头模式命令

```bash
# 基本语法
idea.sh headless \
  --project /path/to/project \
  --plugin /path/to/hungarian-refactorer-1.0.0.zip \
  --batch
```

### 3. 执行批量重构

```bash
idea.sh headless \
  --project /Volumes/AppData/codebase/naca \
  --plugin /path/to/hungarian-refactorer-1.0.0.zip \
  --batch-action "HungarianRefactorer:refactorProject" \
  --output /tmp/refactor-report.json \
  --dry-run  # 先试运行，不实际修改
```

## 方式二：使用脚本调用

创建自动化脚本：

### Bash 脚本 (Linux/macOS)

```bash
#!/bin/bash
# refactor.sh

IDEA_HOME="/Applications/IntelliJ IDEA.app/Contents/MacOS"
PROJECT_PATH="$1"
PLUGIN_PATH="$2"

$IDEA_HOME/idea headless \
  --project "$PROJECT_PATH" \
  --plugin "$PLUGIN_PATH" \
  --batch \
  --log-level INFO \
  2>&1 | tee refactor.log

echo "Refactoring completed. Check refactor.log for details."
```

使用:
```bash
chmod +x refactor.sh
./refactor.sh /path/to/project /path/to/plugin.zip
```

### PowerShell 脚本 (Windows)

```powershell
# refactor.ps1

$IDEA_HOME = "C:\Program Files\JetBrains\IntelliJ IDEA 2024.1\bin"
$PROJECT_PATH = $args[0]
$PLUGIN_PATH = $args[1]

& "$IDEA_HOME\idea64.exe" headless `
  --project $PROJECT_PATH `
  --plugin $PLUGIN_PATH `
  --batch `
  --log-level INFO

Write-Host "Refactoring completed."
```

## 方式三：集成到 Claude Code

在 Claude Code 会话中，可以这样调用：

```bash
# 1. 先构建插件
cd /path/to/hungarian-refactorer
./gradlew buildPlugin

# 2. 执行重构（先 dry-run）
/Applications/IntelliJ\ IDEA.app/Contents/MacOS/idea headless \
  --project /Volumes/AppData/codebase/naca \
  --plugin build/distributions/hungarian-refactorer-1.0.0.zip \
  --batch \
  --dry-run

# 3. 查看分析报告后，执行实际重构
# （移除 --dry-run 参数）
```

## 方式四：Docker 容器化运行

创建 Dockerfile:

```dockerfile
FROM openjdk:17-slim

# 安装依赖
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# 下载 IDEA 命令行工具
RUN wget -q https://download.jetbrains.com/idea/ideaIC-2024.1.tar.gz \
    && tar -xzf ideaIC-2024.1.tar.gz \
    && rm ideaIC-2024.1.tar.gz

# 设置环境变量
ENV IDEA_HOME=/opt/idea-IC-241.14494.240
ENV PATH=$IDEA_HOME/bin:$PATH

# 复制插件
COPY hungarian-refactorer-1.0.0.zip /plugins/

WORKDIR /project

CMD ["idea", "headless", "--batch"]
```

构建并运行:
```bash
docker build -t hungarian-refactorer .
docker run -v /path/to/project:/project hungarian-refactorer
```

## 配置文件

### 项目级配置 (.hungarian/config.json)

在项目根目录创建配置文件：

```json
{
  "enabled": true,
  "processLocalVariables": true,
  "processFields": true,
  "processParameters": false,
  "updateAccessors": true,
  "updateComments": false,
  "dryRun": true,
  "excludedPaths": [
    "**/test/**",
    "**/generated/**",
    "**/target/**"
  ],
  "customRules": [
    {"prefix": "dto", "replacement": "dto", "targetType": "", "enabled": true},
    {"prefix": "vo", "replacement": "vo", "targetType": "", "enabled": true}
  ]
}
```

## CI/CD 集成

### GitHub Actions

```yaml
name: Hungarian Notation Refactor

on:
  workflow_dispatch:
    inputs:
      dry_run:
        description: 'Dry run (no changes)'
        required: true
        default: 'true'

jobs:
  refactor:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Setup IDEA
        run: |
          wget -q https://download.jetbrains.com/idea/ideaIC-2024.1.tar.gz
          tar -xzf ideaIC-2024.1.tar.gz

      - name: Build Plugin
        run: |
          cd hungarian-refactorer
          ./gradlew buildPlugin

      - name: Run Refactor
        run: |
          ./idea-IC-241.14494.240/bin/idea.sh headless \
            --project $GITHUB_WORKSPACE \
            --plugin hungarian-refactorer/build/distributions/hungarian-refactorer-1.0.0.zip \
            --batch \
            ${{ github.event.inputs.dry_run == 'true' && '--dry-run' || '' }}

      - name: Upload Report
        uses: actions/upload-artifact@v4
        with:
          name: refactor-report
          path: refactor-report.json
```

## 输出报告

重构完成后会生成 JSON 格式的报告：

```json
{
  "summary": {
    "totalVariables": 1250,
    "successCount": 1245,
    "failedCount": 5,
    "dryRun": true
  },
  "renamedVariables": [
    {
      "originalName": "strName",
      "newName": "name",
      "type": "FIELD",
      "file": "User.java",
      "line": 42
    },
    {
      "originalName": "btnSubmit",
      "newName": "buttonSubmit",
      "type": "LOCAL_VARIABLE",
      "file": "LoginDialog.java",
      "line": 156
    }
  ],
  "errors": [
    "Failed to rename strData: Conflicting declaration in same scope"
  ]
}
```

## 故障排除

### 问题：`command not found: idea`
**解决**：确保 IDEA 命令行工具在 PATH 中，或使用完整路径。

### 问题：内存不足
**解决**：增加 JVM 参数：
```bash
idea.sh headless -Xmx4g --project ...
```

### 问题：插件未加载
**解决**：检查插件路径是否正确，确保是 ZIP 格式。

### 问题：重构后代码不编译
**解决**：
1. 检查是否有手动调用的 getter/setter
2. 查看错误日志
3. 使用版本控制回滚

## 最佳实践

1. **先 dry-run**：始终先运行 `--dry-run` 查看影响范围
2. **小批量**：分批重构，不要一次处理整个大项目
3. **版本控制**：确保代码已提交，方便回滚
4. **测试验证**：重构后运行完整测试套件
5. **代码审查**：使用 `git diff` 审查所有更改

## 性能优化

对于大型项目：

```bash
# 1. 排除不必要的目录
# 在配置文件中设置 excludedPaths

# 2. 增加内存
export IDEA_VM_OPTIONS="-Xmx8g -XX:+UseG1GC"

# 3. 并行处理
# 将项目分成多个模块，并行执行

# 4. 使用 SSD 存储
# I/O 是瓶颈，SSD 可显著提升速度
```

## 参考资料

- [IntelliJ 命令行选项](https://www.jetbrains.com/help/idea/tuning-the-ide.html)
- [无头模式配置](https://www.jetbrains.com/help/idea/command-line-launcher.html)
