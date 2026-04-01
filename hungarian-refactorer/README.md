# Hungarian Notation Refactorer

IntelliJ IDEA 插件，用于批量将匈牙利命名法变量重命名为驼峰命名法。

## 功能特性

- **智能识别**：基于类型前缀的匈牙利命名法识别（如 `strName` → `name`, `btnSubmit` → `buttonSubmit`）
- **批量重构**：支持文件、目录、整个项目级别的重构
- **语义安全**：使用 IntelliJ PSI API 和 RefactoringFactory，确保引用全部更新
- **Getter/Setter 更新**：自动更新相关的访问器方法
- **可扩展规则**：支持自定义命名规则
- **无头模式支持**：可通过 CLI 进行批量自动化重构
- **预览功能**：重构前可预览所有更改

## 安装

### 从源码构建

```bash
cd hungarian-refactorer
./gradlew buildPlugin
```

构建产物位于：`build/distributions/hungarian-refactorer-1.0.0.zip`

### 安装插件

1. 打开 IntelliJ IDEA
2. `Settings | Plugins | ⚙️ | Install Plugin from Disk...`
3. 选择 `build/distributions/hungarian-refactorer-1.0.0.zip`
4. 重启 IDEA

## 使用方法

### GUI 方式

1. **分析文件**：
   - 右键 Java 文件 → `Code | Hungarian Notation | Analyze Hungarian Variables`
   - 查看文件中有哪些匈牙利命名法变量

2. **重构文件**：
   - 右键 Java 文件 → `Code | Hungarian Notation | Refactor Hungarian Variables`
   - 或使用快捷键：`Ctrl+Shift+H` (Win/Linux) / `Cmd+Shift+H` (Mac)

3. **重构整个项目**：
   - `Code | Hungarian Notation | Refactor Entire Project`
   - ⚠️ 警告：这会修改整个项目的所有相关文件

### 配置选项

`Settings | Tools | Hungarian Notation Refactorer`

- `Enable Hungarian Notation Refactoring` - 启用/禁用重构功能
- `Process Local Variables` - 处理局部变量（如 `String strName`）
- `Process Fields` - 处理字段（如 `private String strName;`）
- `Process Parameters` - 处理方法参数（谨慎使用）
- `Update Getter/Setter Methods` - 自动更新访问器方法
- `Update Comments` - 更新注释中的引用（可能导致误判）
- `Preview Changes Before Applying` - 应用前预览更改

### CLI 无头模式

```bash
# 分析模式（不实际修改）
idea.sh headless \
  --project /path/to/project \
  --plugin hungarian-refactorer-1.0.0.zip \
  --batch-action analyze \
  --output report.json

# 批量重构
idea.sh headless \
  --project /path/to/project \
  --plugin hungarian-refactorer-1.0.0.zip \
  --batch-action refactor \
  --src /path/to/src/main/java
```

## 支持的命名规则

默认支持以下匈牙利命名法前缀：

| 前缀 | 类型 | 转换示例 |
|------|------|----------|
| str, s | String | strName → name |
| i | int | iCount → count |
| l | long | lTotal → total |
| b, bln | boolean | bVisible → isVisible |
| d | double | dValue → value |
| f | float | fRatio → ratio |
| arr | array | arrItems → items |
| lst | List | lstNames → listNames |
| map | Map | mapData → data |
| set | Set | setId → id |
| btn | JButton | btnSubmit → buttonSubmit |
| lbl | JLabel | lblTitle → labelTitle |
| txt, tf | JTextField | txtName → textName |
| cb | JCheckBox | cbChecked → checkBoxChecked |
| mgr | Manager | mgrUser → managerUser |
| svc | Service | svcOrder → serviceOrder |

## 扩展开发

### 添加自定义命名规则

```java
// 获取注册表实例
HungarianPrefixRegistry registry = HungarianPrefixRegistry.getInstance();

// 添加新规则
registry.registerRule(new PrefixRule(
    "repo",      // 前缀
    "repository", // 替换为
    "Repository", // 目标类型（可选）
    true         // 启用
));
```

### 集成到其他工具

```java
// 获取重构引擎
HungarianRefactorerEngine engine = HungarianRefactorerEngine.getInstance(project);

// 分析文件
List<HungarianVariableInfo> variables = engine.analyzeFile(psiFile);

// 执行重构
RefactorResult result = engine.refactorFile(psiFile, false);

// 获取结果
System.out.println("Success: " + result.successCount);
System.out.println("Failed: " + result.failedCount);
```

## 无头模式集成 Claude Code

对于大规模代码迁移，可以通过 Claude Code 进行自动化：

```bash
# 1. 构建插件
./gradlew buildPlugin

# 2. 在项目目录创建 .claude/settings.json
{
  "hooks": {
    "RefactorCommand": {
      "command": "idea.sh headless --project $PROJECT --plugin hungarian-refactorer.zip"
    }
  }
}

# 3. 使用 Claude Code 执行重构
claude "对项目进行匈牙利命名法重构，先分析再执行"
```

## 技术架构

```
hungarian-refactorer/
├── HungarianPrefixRegistry      # 命名规则注册表（可扩展）
├── HungarianRefactorerEngine    # 重构引擎（核心）
├── HungarianRefactorerSettings  # 配置管理
├── psi/
│   ├── HungarianVariableInfo    # 变量信息
│   ├── HungarianVariableFinder  # 变量查找器
│   ├── HungarianVariableRefactorer # 变量重构器
│   └── NameConverter            # 名称转换器
├── actions/
│   ├── BulkRefactorAction       # 主重构动作
│   ├── AnalyzeAction            # 分析动作
│   └── RefactorAction           # 快速重构动作
└── cli/
    ├── CliMain                  # CLI 入口
    ├── BatchRefactorRunner      # 批量执行器
    └── BatchRefactorConfig      # 批量配置
```

## 注意事项

1. **备份代码**：重构前请确保代码已提交到版本控制
2. **预览更改**：首次使用建议先启用预览功能
3. **测试验证**：重构后请运行测试确保功能正常
4. **团队协作**：大规模重构前请通知团队成员

## 故障排除

### 问题：重构后编译失败
- 检查是否有未更新的 getter/setter 调用
- 查看日志文件：`Help | Show Log in Explorer`

### 问题：某些变量未被识别
- 检查前缀规则是否包含该类型
- 确认变量命名符合匈牙利命名法规范

### 问题：IDEA 卡顿
- 减少单次重构的文件数量
- 增加 IDEA 堆内存：`Help | Change Memory Settings`

## 开发计划

- [ ] 支持 Kotlin 语言
- [ ] 支持更多命名约定（如类型后缀）
- [ ] 改进类型推断准确性
- [ ] 添加重构回滚功能
- [ ] 集成 Qodana 代码质量检查

## License

Apache 2.0

## Contributing

欢迎提交 Issue 和 Pull Request！
