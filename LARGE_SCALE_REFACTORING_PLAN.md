# 大规模代码规范化重构计划

## 重构目标
- 移除所有 `m_` 前缀的变量名
- 遵循标准 Java 命名规范
- 保持语义安全（使用 JDTLS 语义重命名）

## 命名转换规则

| 原始模式 | 转换后名称 | 示例 |
|---------|-----------|------|
| `m_nVariable` | `variable` | `m_nCurrentSite` → `currentSite` |
| `m_bVariable` | `variable` | `m_bDoAllSites` → `doAllSites` |
| `m_arrVariable` | `variable` | `m_arrValues` → `values` |
| `m_csVariable` | `variable` | `m_csControlerName` → `controlerName` |
| `m_lstVariable` | `variable` | `m_lstChildren` → `children` |
| `m_tabVariable` | `variable` | `m_tabControlers` → `controlers` |
| `m_grpVariable` | `variable` | `m_grpConfig` → `grpConfig` |
| `m_Variable` | `variable` | `m_Config` → `config` |

## 处理策略

### 1. 分批处理（推荐）
按模块优先级分批处理，避免一次性修改过多文件：

**第一批：高优先级模块**
- `naca-jlib/src/main/java/jlib/sql/` (~50 files)
- `naca-jlib/src/main/java/jlib/controler/` (~10 files)  
- `naca-jlib/src/main/java/jlib/misc/` (~30 files)

**第二批：Generate 模块**
- `naca-trans/src/main/java/generate/` (~200 files)

**第三批：Semantic 模块**  
- `naca-trans/src/main/java/semantic/` (~300 files)

**第四批：剩余文件**
- 所有其他文件 (~500 files)

### 2. 单文件处理流程

对于每个文件：
1. **获取符号列表**：`lsp_symbols(filePath, scope="document")`
2. **识别 m_ 字段**：找到所有以 `m_` 开头的字段
3. **逐个处理字段**：
   - 使用 `lsp_prepare_rename()` 检查可重命名性
   - 如果成功 → 使用 `lsp_rename()` 执行重命名
   - 如果冲突 → 选择替代名称重试
4. **验证修改**：
   - `lsp_diagnostics(filePath)` → 检查编译错误
   - `git diff` → 确认修改范围合理
   - `./gradlew compileJava` → 完整编译验证
5. **清理意外修改**：`git restore <unrelated-files>`

### 3. 冲突处理策略

#### 类型 1：局部变量遮蔽（Variable Shadowing）
- **问题**：字段名与参数名冲突
- **解决方案**：使用更具描述性的名称
  - `m_csName` → `columnName` (而不是 `name`)
  - `m_nOrder` → `colOrder` (而不是 `order`)

#### 类型 2：类型名冲突
- **问题**：字段类型与名称相关，可能导致类名被修改
- **解决方案**：使用与类型名完全不同的字段名
  - `ArrayList<ColValue> m_arrCols` → `columnList` (而不是 `cols`)
  - `DbConnectionBase m_connection` → `dbConnection` (而不是 `connection`)

## 验证检查清单

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

## 风险缓解

### 备份策略
- 在开始前提交当前状态：`git commit -m "Pre-refactoring backup"`
- 小批量提交，频繁提交
- 使用特性分支（如果可能）

### 回滚计划
- 如果出现问题，`git checkout` 单个文件
- 对于大问题，`git reset --hard` 到上一个好提交

### 质量保证
- 每批处理后运行 `./gradlew build`
- 确保所有现有测试通过
- 手动测试关键功能

## 工作流程脚本

以下是一个自动化工作流程的示例：

```python
# 伪代码示例
def refactor_m_prefixes_in_file(file_path):
    # 1. 获取符号
    symbols = lsp_symbols(file_path, scope="document")
    
    # 2. 找到所有 m_ 字段
    m_fields = [s for s in symbols if s.name.startswith('m_') and s.type == 'Field']
    
    # 3. 逐个处理
    for field in m_fields:
        new_name = convert_m_name(field.name)
        
        # 3.1 检查是否可重命名
        if not lsp_prepare_rename(file_path, field.line, field.column):
            # 3.2 处理冲突
            new_name = handle_conflict(new_name, field.name)
            if not new_name:
                continue  # 跳过无法重命名的字段
        
        # 3.3 执行重命名
        lsp_rename(file_path, field.line, field.column, new_name)
    
    # 4. 验证
    diagnostics = lsp_diagnostics(file_path)
    if diagnostics.has_errors():
        # 处理错误
        pass
```

## 成功标准

✅ 所有 `m_` 和 `_` 前缀从变量名中移除  
✅ 所有代码编译无错误  
✅ 所有现有测试通过  
✅ 方法名保持驼峰命名（已符合）  
✅ 代码功能未发生改变  

## 预计时间线

- **m_ 变量 (1,096 files)**: 8-16 小时（取决于自动化程度）
- **_ 变量 (24 files)**: 1-2 小时（手动处理）
- **总预计时间**: 10-18 小时

---
最后更新: 2026-03-16
作者: Sisyphus