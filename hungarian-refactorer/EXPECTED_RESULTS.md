# 匈牙利命名法重构 - 预期结果

## 测试文件分析

### 1. ActionShowScreen.java

**位置**: `naca-rt/src/main/java/idea/action/ActionShowScreen.java`

**识别的匈牙利命名法变量**:

| 原名称 | 类型 | 预期新名称 | 前缀规则 |
|--------|------|-----------|---------|
| `csPage` | String | `page` | cs → (empty) |
| `csLang` | String | `lang` | cs → (empty) |
| `docOutput` | Document | `docOutput` | doc → doc (保持) |
| `eForm` | Element | `form` | e → (empty) |
| `lstForms` | NodeList | `forms` | lst → (empty) |
| `nb` | int | `count` | nb → count |

**预期修改的代码行**:

```diff
- String csPage = reqLoader.getFieldValue("showPage").toUpperCase();
+ String page = reqLoader.getFieldValue("showPage").toUpperCase();

- String csLang = reqLoader.getFieldValue("showLanguage");
+ String lang = reqLoader.getFieldValue("showLanguage");

- Document docOutput = merger.BuildXLMStructure(resMan.getXmlFrame(), eForm) ;
+ Document docOutput = merger.BuildXLMStructure(resMan.getXmlFrame(), form) ;

- NodeList lstForms = eOutput.getElementsByTagName("form");
+ NodeList forms = eOutput.getElementsByTagName("form");

- int nb = lstForms.getLength();
+ int count = forms.getLength();

- for (int i=0; i<nb; i++)
+ for (int i=0; i<count; i++)

- Element eForm = (Element)lstForms.item(i);
+ Element form = (Element)forms.item(i);
```

### 2. CScenarioPlayer.java

**位置**: `naca-rt/src/main/java/idea/emulweb/CScenarioPlayer.java`

**识别的匈牙利命名法变量**:

| 原名称 | 类型 | 预期新名称 | 前缀规则 |
|--------|------|-----------|---------|
| `filePath` | String | `filePath` | file → file (保持) |
| `docScenario` | Document | `docScenario` | doc → doc (保持) |
| `lstPages` | NodeList | `pages` | lst → (empty) |
| `nCurrentPage` | int | `currentPage` | n → current |
| `modeRecord` | enum | `modeRecord` | mode → mode (保持) |
| `nPlayerState` | int | `playerState` | n → (empty) |
| `nNbCycles` | int | `nbCycles` | n → (empty) |
| `csValue` | String | `value` | cs → (empty) |

**预期修改的代码行**:

```diff
- protected NodeList lstPages = null ;
+ protected NodeList pages = null ;

- protected int nCurrentPage = 0 ;
+ protected int currentPage = 0 ;

- nCurrentPage = 0 ;
+ currentPage = 0 ;

- if (nCurrentPage >= lstPages.getLength())
+ if (currentPage >= pages.getLength())

- Element eCurrentCycle = (Element)lstPages.item(nCurrentPage) ;
+ Element currentCycle = (Element)pages.item(currentPage) ;
```

## 编译验证预期

### 预期编译输出

```
> Task :naca-rt:compileJava
Compiling 150 source files...
Note: Some files use deprecated APIs
Note: Recompile with -Xlint:deprecation for details

BUILD SUCCESSFUL in 25s
```

### 可能的问题

1. **局部变量作用域冲突**
   - 如果同一作用域内有多个类似名称的变量可能导致冲突
   - 解决方案：插件应检测并跳过这类变量

2. **循环变量冲突**
   - `i` 是常用循环变量，不应被识别为匈牙利命名法
   - 插件应排除单字母变量

3. **方法参数重命名**
   - 如果启用了参数重命名，需要确保所有调用点都更新
   - 插件使用 RefactoringFactory 应自动处理

## 单元测试预期

### 预期测试输出

```
> Task :naca-rt:test

ActionShowScreenTest > testExecute PASSED
ActionShowScreenTest > testSetFormProperties PASSED
CScenarioPlayerTest > testPlayerState PASSED
CScenarioPlayerTest > testScenarioRecord PASSED

BUILD SUCCESSFUL in 15s
```

### 测试覆盖的功能

1. **变量访问正确性**
   - 重命名后的变量能正确访问
   - 作用域没有冲突

2. **引用更新正确性**
   - 所有变量引用都已更新
   - 没有遗漏的旧名称引用

3. **功能等价性**
   - 重构后功能与重构前等价
   - 输出结果一致

## Git 差异统计预期

```
25 files changed, 150 insertions(+), 150 deletions(-)
```

### 修改类型分布

| 修改类型 | 数量 | 百分比 |
|---------|------|--------|
| 变量声明 | 50 | 33% |
| 变量使用 | 80 | 53% |
| 注释更新 | 10 | 7% |
| 其他 | 10 | 7% |

## 成功标准

| 检查项 | 预期结果 | 实际结果 |
|--------|---------|---------|
| 编译成功 | ✅ | ☐ |
| 测试通过 | ✅ | ☐ |
| 无破坏性更改 | ✅ | ☐ |
| Git 差异可审查 | ✅ | ☐ |
| 代码审查通过 | ✅ | ☐ |

## 回滚步骤

如果测试失败，执行以下命令回滚：

```bash
cd /Volumes/AppData/codebase/naca

# 重置当前分支
git reset --hard HEAD~1

# 或者切换到 master
git checkout master

# 删除测试分支
git branch -D test/hungarian-refactoring-*
```

## 后续步骤

1. **代码审查**
   - 查看 Git 差异
   - 确认所有更改合理

2. **功能测试**
   - 手动测试关键功能
   - 确认用户体验无影响

3. **性能测试**
   - 对比重构前后性能
   - 确认无性能下降

4. **合并到主分支**
   - 创建 Pull Request
   - 获得批准后合并
