# Hungarian Refactorer 测试案例

## 测试目标

使用 naca 项目测试 Hungarian Refactorer 插件的完整功能，包括：
1. 匈牙利命名法变量识别
2. 批量重命名
3. Getter/Setter 方法更新
4. Git 差异比对
5. 编译验证
6. 单元测试验证

## 测试环境

- **项目**: `/Volumes/AppData/codebase/naca`
- **IDE**: IntelliJ IDEA 2024.1+
- **JDK**: Java 17+
- **插件**: hungarian-refactorer-1.0.0

## 测试文件

选择以下包含匈牙利命名法的文件作为测试目标：

| 文件 | 匈牙利变量示例 | 预期转换 |
|------|---------------|----------|
| `ActionShowScreen.java` | `csPage`, `csLang`, `docOutput`, `eForm`, `lstForms` | `page`, `lang`, `docOutput`, `form`, `forms` |
| `CScenarioPlayer.java` | `filePath`, `docScenario`, `lstPages`, `nCurrentPage`, `modeRecord` | `filePath`, `docScenario`, `pages`, `currentPage`, `modeRecord` |

## 测试步骤

### 步骤 1: 准备工作

```bash
# 1.1 确保代码已提交
cd /Volumes/AppData/codebase/naca
git status
git add -A
git commit -m "Before Hungarian refactoring test"

# 1.2 创建测试分支
git checkout -b test/hungarian-refactoring

# 1.3 构建插件
cd hungarian-refactorer
./gradlew buildPlugin
```

### 步骤 2: 运行分析（Dry Run）

```bash
# 执行分析，生成报告
./test-hungarian-refactorer.sh --analyze-only
```

**预期输出：**
- 识别出约 50-100 个匈牙利命名法变量
- 生成 `test-output/analysis-report.json`

### 步骤 3: 执行重构

```bash
# 执行重构
./test-hungarian-refactorer.sh --refactor

# 查看 Git 差异
git diff --stat
```

**预期输出：**
- 修改的文件列表
- 变量名更改统计

### 步骤 4: 编译验证

```bash
# 编译项目
./test-hungarian-refactorer.sh --compile

# 预期：编译成功，无错误
```

### 步骤 5: 单元测试验证

```bash
# 运行测试
./test-hungarian-refactorer.sh --test

# 预期：所有测试通过
```

### 步骤 6: 生成测试报告

```bash
# 生成完整报告
./test-hungarian-refactorer.sh --report
```

## 验证标准

| 检查项 | 通过标准 |
|--------|----------|
| 变量识别准确率 | > 95% |
| 重命名成功率 | 100% |
| 编译通过率 | 100% |
| 单元测试通过率 | 100% |
| Git 差异可审查 | 所有更改合理 |
| 无破坏性更改 | 功能正常 |

## 回滚方案

如果测试失败：

```bash
# 回滚代码
git reset --hard HEAD
git checkout master
```

## 测试负责人

- 执行人：_____________
- 日期：_____________
- 复核人：_____________
