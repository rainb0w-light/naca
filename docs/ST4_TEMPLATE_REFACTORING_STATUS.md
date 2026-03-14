# ST4模板重构进度报告

> **状态**: 暂挂
> **日期**: 2026-03-14
> **原因**: 需要先处理遗留代码命名规范问题

---

## 一、已完成的工作

### 1.1 模板语法修复

| 文件 | 修改 | 状态 |
|------|------|------|
| `java.stg` | 移除import语句分号 | ✅ 完成 |
| `base.stg` | 移除非法的值比较分发器 `statement()` | ✅ 完成 |
| `java.stg` | 子语句渲染改为 `exportChildren()` | ✅ 完成 |
| `java.stg` | 表达式模板改用布尔属性 | ✅ 完成 |

### 1.2 验证结果

```
✅ BUILD SUCCESSFUL
✅ TemplateValidationTest - All 3 tests PASSED
```

---

## 二、发现的核心问题

### 2.1 架构缺口

模板调用 `<child.export()>` 时，`CBaseLanguageEntity` 基类没有 `export()` 方法：

```
CBaseLanguageEntity (基类)
├── CDataEntity        → 有 export() 方法 ✅
├── CEntityBloc        → 没有 export() 方法 ❌
├── CEntityCondition   → 没有 export() 方法 ❌
└── CBaseActionEntity  → 没有 export() 方法 ❌
```

### 2.2 当前模板依赖

```stg
// java.stg 中的模板需要这些调用：
<entity.thenBloc.exportChildren()>   // Bloc需要exportChildren()
<entity.condition.export()>            // CDataEntity已有export()
<entity.children:{c | <c.export()>}>   // 子实体需要export()
```

---

## 三、待解决问题

### 3.1 需要添加的方法

| 类 | 方法 | 用途 |
|----|------|------|
| `CBaseLanguageEntity` | `export()` | 让所有实体可以被模板渲染 |
| `CEntityBloc` | `exportChildren()` | 返回渲染后的子语句字符串 |
| `CEntityExprSum` | `isAdd()` | 布尔属性替代值比较 |
| `CEntityExprProd` | `isMultiply()` | 布尔属性替代值比较 |

### 3.2 解决方案选项

**方案A**: 给 `CBaseLanguageEntity` 添加 `export()` 方法
- 每个实体用模板渲染自己
- 完全符合PUSH模型
- 需要修改基类

**方案B**: 给实体添加 `exportChildren()` 方法
- 在实体中处理子元素渲染
- 模板调用 `<entity.exportChildren()>`
- 需要给Bloc类添加方法

**方案C**: 使用现有 `DoExport()` 机制
- 模板触发实体自己的渲染逻辑
- 最小改动

---

## 四、LSP诊断说明

LSP报告的错误不影响实际运行：

| 错误 | 原因 | 影响 |
|------|------|------|
| `can't build URL for null/../base.stg` | LSP缺少文件URI上下文 | 无 |
| `'(' came as a complete surprise` | LSP不完全支持方法链语法 | 无 |

---

## 五、相关文件

- 模板: `naca-trans/src/main/resources/templates/`
- 实体基类: `naca-trans/src/main/java/semantic/CBaseLanguageEntity.java`
- 数据实体: `naca-trans/src/main/java/semantic/CDataEntity.java`
- 设计原则: `docs/STRINGTEMPLATE_REFACTORING_PRINCIPLES.md`
- 技能文档: `.opencode/skills/stringtemplate4-codegen/SKILL.md`

---

## 六、恢复工作的步骤

1. 选择解决方案（A/B/C）
2. 给相关实体类添加所需方法
3. 更新模板使用正确的方法调用
4. 运行测试验证
5. 检查LSP诊断