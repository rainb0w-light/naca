# 匈牙利命名法重构插件 - 快速测试指南

## 1. 构建插件

```bash
cd /Volumes/AppData/codebase/naca/hungarian-refactorer
./gradlew clean buildPlugin
```

构建产物：`build/distributions/hungarian-refactorer-1.0.0.zip`

## 2. 安装插件到 IDEA

```bash
# 方法 1: 在 IDEA 中
# Settings → Plugins → ⚙️ → Install Plugin from Disk
# 选择 build/distributions/hungarian-refactorer-1.0.0.zip

# 方法 2: 命令行复制到插件目录
cp build/distributions/hungarian-refactorer-1.0.0.zip \
   ~/Library/Application\ Support/Google/IntelliJIDEA2024.1/plugins/
```

## 3. 运行测试

### 3.1 单元测试

```bash
./gradlew test
```

### 3.2 完整测试流程

```bash
# 使用测试脚本
./test-hungarian-refactorer.sh --full

# 或者分步执行
./test-hungarian-refactorer.sh --init
./test-hungarian-refactorer.sh --build
./test-hungarian-refactorer.sh --analyze
./test-hungarian-refactorer.sh --refactor
./test-hungarian-refactorer.sh --compile
./test-hungarian-refactorer.sh --test
./test-hungarian-refactorer.sh --report
```

### 3.3 验证重构结果

```bash
./verify-refactor.sh
```

## 4. 在 naca 项目上测试

### 4.1 准备工作

```bash
cd /Volumes/AppData/codebase/naca

# 创建测试分支
git checkout -b test/hungarian-refactoring

# 备份当前状态
git stash push -m "Before Hungarian refactoring"
```

### 4.2 选择测试文件

推荐以下文件作为测试目标（包含典型匈牙利命名法）：

| 文件 | 匈牙利变量 | 预期修改 |
|------|-----------|---------|
| `naca-rt/src/main/java/idea/action/ActionShowScreen.java` | csPage, csLang, docOutput, eForm, lstForms, nb | page, lang, docOutput, form, forms, count |
| `naca-rt/src/main/java/idea/emulweb/CScenarioPlayer.java` | lstPages, nCurrentPage, nPlayerState, modeRecord | pages, currentPage, playerState, modeRecord |

### 4.3 执行重构

**方法 A: 使用 IDEA GUI**

1. 打开 naca 项目
2. 右键点击 `ActionShowScreen.java`
3. 选择 `Code → Hungarian Notation → Refactor Hungarian Variables`
4. 预览更改
5. 确认执行

**方法 B: 使用无头模式**

```bash
# 分析模式（不实际修改）
/Applications/IntelliJ\ IDEA.app/Contents/MacOS/idea headless \
  --project /Volumes/AppData/codebase/naca \
  --plugin hungarian-refactorer/build/distributions/hungarian-refactorer-1.0.0.zip \
  --batch-action analyze \
  --output /tmp/hungarian-analysis.json

# 重构模式
/Applications/IntelliJ\ IDEA.app/Contents/MacOS/idea headless \
  --project /Volumes/AppData/codebase/naca \
  --plugin hungarian-refactorer/build/distributions/hungarian-refactorer-1.0.0.zip \
  --batch-action refactor \
  --src naca-rt/src/main/java/idea/action
```

## 5. 验证结果

### 5.1 Git 差异检查

```bash
# 查看修改统计
git diff --stat

# 查看详细差异
git diff -- naca-rt/src/main/java/idea/action/ActionShowScreen.java
```

### 5.2 编译验证

```bash
# 编译项目
./gradlew :naca-rt:compileJava

# 预期输出：BUILD SUCCESSFUL
```

### 5.3 测试验证

```bash
# 运行 naca-rt 测试
./gradlew :naca-rt:test

# 预期输出：BUILD SUCCESSFUL
```

### 5.4 功能验证

运行 naca 应用，验证功能正常：

```bash
# 启动应用
./gradlew :naca-rt:run

# 或使用 Docker
docker-compose up
```

## 6. 回滚

如果测试失败，回滚代码：

```bash
# 重置更改
git reset --hard HEAD

# 或者恢复 stash
git stash pop
```

## 7. 测试报告

测试完成后，生成报告：

```bash
./test-hungarian-refactorer.sh --report
```

报告位置：`test-output/reports/final-test-report-*.md`

## 8. 故障排除

### 问题：编译失败

```bash
# 查看编译日志
cat test-output/reports/compile-output.log

# 常见原因：
# 1. 变量作用域冲突
# 2. getter/setter 未更新
# 3. 跨文件引用未更新
```

### 问题：测试失败

```bash
# 查看测试日志
cat test-output/reports/test-output.log

# 运行特定测试
./gradlew :naca-rt:test --tests "*ActionShowScreen*"
```

### 问题：插件未加载

```bash
# 检查插件是否安装
ls ~/Library/Application\ Support/Google/IntelliJIDEA2024.1/plugins/

# 重启 IDEA
```

## 9. 测试检查清单

- [ ] 插件构建成功
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 代码分析识别正确
- [ ] 重构执行成功
- [ ] 编译通过
- [ ] 现有测试通过
- [ ] Git 差异合理
- [ ] 功能验证通过
- [ ] 代码审查通过

## 10. 联系人

如有问题，请联系开发团队。
