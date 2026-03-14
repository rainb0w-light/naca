# BaseProgramManager 重构计划

## 当前问题

`BaseProgramManager.java` (1540 行) 承担过多职责：
- 程序生命周期管理
- 变量初始化和索引
- SQL 游标注册和管理
- 文件管理
- 调用参数映射
- 工作存储区管理
- Linkage Section 处理
- Copybook 管理
- 排序操作
- 表单/屏幕处理

## 重构目标

将 `BaseProgramManager` 拆分为以下单一职责的类：

### 1. ProgramLifecycleManager (原 BaseProgramManager)
- 协调程序的生命周期
- 委托给专门的组件
- 保持向后兼容的 API

### 2. VariableInitializer
- 变量命名和索引
- 初始化缓存管理
- 变量元数据管理

### 3. CursorRegistry
- SQL 游标注册
- 游标生命周期管理
- 游标名称恢复

### 4. FileManager
- 文件 Section 管理
- 文件 I/O 协调
- 会话关联

### 5. CallParameterMapper
- 调用参数映射
- CommArea 处理
- 参数长度计算

### 6. WorkingStorageManager
- Working Storage Section 管理
- Linkage Section 处理
- 原始值保存/恢复

## 依赖关系

```
ProgramLifecycleManager
├── VariableInitializer
├── CursorRegistry
├── FileManager
├── CallParameterMapper
└── WorkingStorageManager
```

## 实施步骤

1. 创建新的组件类（保持向后兼容）
2. 逐步将方法迁移到新类
3. 更新测试用例
4. 验证翻译后的程序仍然正常工作
5. 移除旧的代码路径

## 向后兼容性

为了不影响已翻译的 COBOL 程序：
- 保留 `BaseProgramManager` 作为Facade
- 新方法委托给新组件
- 使用 `@Deprecated` 标记旧方法
- 提供迁移指南
