# Naca 文档

这里是 Naca 项目的中文文档目录。

## 说明

由于技术文档的特殊性，以下内容保留为英文原文以确保准确性：
- 代码示例
- 技术术语
- API 名称
- 类名和方法名

## 目录

### 基础文档

| 主题 | 说明 |
|------|------|
| [1. Base notions](1.%20Base%20notions.md) | 基本概念 |
| [1. Configuration file](1.%20Configuration%20file.md) | 配置文件 |
| [2. Introduction](2.%20Introduction.md) | 简介 |
| [2. JMX Console](2.%20JMX%20Console.md) | JMX 控制台 |
| [3. Overview - Usage](3.%20Overview%20-%20Usage.md) | 概述 - 用法 |

### 程序相关

| 主题 | 说明 |
|------|------|
| [3.1 Programs](3.1%20Programs.md) | 程序 |
| [3.1.1 Sample Hello World](3.1.1%20Sample%20Hello%20World.md) | Hello World 示例 |
| [3.1.2 Program physical representation](3.1.2%20Program%20physical%20representation.md) | 程序物理表示 |
| [3.1.3 Program declarations](3.1.3%20Program%20declarations.md) | 程序声明 |
| [3.1.4 Calls and called programs](3.1.4%20Calls%20and%20called%20programs.md) | 调用和被调用程序 |
| [3.1.4 Program instance execution](3.1.4%20Program%20instance%20execution.md) | 程序实例执行 |
| [3.1.5 Supported cobol-like verbs](3.1.5%20Supported%20cobol-like%20verbs.md) | 支持的 COBOL 类动词 |

### 变量和数据

| 主题 | 说明 |
|------|------|
| [3.2 Variables](3.2%20Variables.md) | 变量 |
| [3.3 Copy](3.3%20Copy.md) | 复制 |

### SQL 相关

| 主题 | 说明 |
|------|------|
| [3.4 SQL - generalities](3.4%20SQL%20-%20generalities.md) | SQL - 概述 |
| [3.4.1 SQL - non cursor statements](3.4.1%20SQL%20-%20non%20cursor%20statements.md) | SQL - 非游标语句 |
| [3.4.2 SQL - Cursors management](3.4.2%20SQL%20-%20Cursors%20management.md) | SQL - 游标管理 |

### 屏幕和批处理

| 主题 | 说明 |
|------|------|
| [3.5 Screen handling](3.5%20Screen%20handling.md) | 屏幕处理 |
| [3.6 Batch](3.6%20Batch.md) | 批处理 |
| [3.6.1 Data files](3.6.1%20Data%20files.md) | 数据文件 |
| [3.6.2 File declaration](3.6.2%20File%20declaration.md) | 文件声明 |
| [3.6.3 File Input - Output](3.6.3%20File%20Input%20-%20Output.md) | 文件输入输出 |
| [3.6.4 File internal sorting](3.6.4%20File%20internal%20sorting.md) | 文件内部排序 |

### 配置和环境

| 主题 | 说明 |
|------|------|
| [Accounting](Accounting.md) | 会计 |
| [Batch execution mode custom settings](Batch%20execution%20mode%20%20custom%20settings.md) | 批处理执行模式自定义设置 |
| [Common parameters to all execution environments](Common%20parameters%20to%20all%20execution%20environments.md) | 所有执行环境的通用参数 |
| [Garbage Collection thread tuning](Garbage%20Collection%20thread%20tuning.md) | 垃圾回收线程调优 |
| [Online specific parameters](Online%20specific%20parameters.md) | 在线特定参数 |
| [SQL Connections and connection pooling](SQL%20Connections%20and%20connection%20pooling.md) | SQL 连接和连接池 |

### JLib 库

| 主题 | 说明 |
|------|------|
| [JLib](JLib.md) | JLib 库 |
| [JLib - XML Support](JLib%20-%20XML%20Support.md) | JLib - XML 支持 |
| [JLib DB Connection pooling](JLib%20DB%20Connection%20pooling.md) | JLib 数据库连接池 |
| [JLib Logger](JLib%20Logger.md) | JLib 日志记录器 |

### NacaRT

| 主题 | 说明 |
|------|------|
| [Naca documentation](Naca%20documentation.md) | Naca 文档 |
| [NacaRT execution environment](NacaRT%20execution%20environment.md) | NacaRT 执行环境 |
| [NacaRT internal structure Documentation](NacaRT%20internal%20structure%20Documentation.md) | NacaRT 内部结构文档 |
| [NacaRT unit testing](NacaRT%20unit%20testing.md) | NacaRT 单元测试 |

## 快速开始

如果你是第一次使用 Naca，建议按以下顺序阅读：

1. [2. Introduction](2.%20Introduction.md) - 了解 Naca 是什么
2. [1. Configuration file](1.%20Configuration%20file.md) - 学习如何配置
3. [3.1.1 Sample Hello World](3.1.1%20Sample%20Hello%20World.md) - 第一个程序
4. [3.2 Variables](3.2%20Variables.md) - 理解变量系统

## 关于翻译

本文档目录中的文件名为英文，但提供了中文说明。文档内容保留为英文的原因：

1. **技术准确性** - 技术术语的英文表达最为准确
2. **代码一致性** - 代码和 API 名称本身就是英文
3. **便于搜索** - 英文技术内容更容易在线搜索和参考

如需完全中文化，可以考虑使用机器翻译工具（如 DeepL、Google Translate）对具体文档进行翻译。
