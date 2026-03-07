# Naca - COBOL to Java Transpiler

## 项目重构说明

本项目已从 Java 1.6 + Ant 构建系统升级到 **JDK 21 + Gradle** 构建系统。

### 主要变更

#### 1. JDK 升级到 21
- 所有模块已配置使用 JDK 21
- 修复了废弃的 `new Integer/Long/Double()` 构造函数（共61处）
- 使用 `Integer.valueOf()`, `Long.valueOf()`, `Double.valueOf()` 替代

#### 2. Gradle 构建系统
- 多项目结构：`jlib` → `naca-rt` / `naca-trans` → `naca-rt-tests`
- 现代依赖管理（见下方依赖替换表）
- 支持 Gradle Wrapper

#### 3. 依赖更新

| 旧依赖 | 新依赖 | 版本 |
|--------|--------|------|
| log4j 1.2.8 | log4j 2.x | 2.24.3 |
| commons-* | 更新版本 | 各模块最新 |
| javax.mail | jakarta.mail | 2.0.1 |
| javax.servlet | jakarta.servlet | 6.1.0 |

## 构建说明

### 前置要求
- JDK 21 或更高版本
- (可选) GnuCOBOL 用于运行原生 COBOL 程序

### 构建命令

```bash
# 查看项目结构
./gradlew projects

# 编译所有模块
./gradlew build

# 编译特定模块
./gradlew :jlib:build
./gradlew :naca-rt:build
./gradlew :naca-trans:build

# 运行测试
./gradlew test
```

## COBOL 工作流

### 检查 COBOL 编译器
```bash
./gradlew checkCobolCompiler
```

### 编译 COBOL 程序
```bash
./gradlew compileCobol
```

### 运行 COBOL 程序
```bash
./gradlew runCobol -Pprogram=BATCH1
```

### 运行转译后的 Java 程序
```bash
./gradlew runTranspiled -Pprogram=BATCH1
```

### 比较执行结果
```bash
./gradlew compareResults -Pprogram=BATCH1
```

## Docker 环境

如果本地没有安装 GnuCOBOL，可以使用 Docker：

```bash
# 构建镜像
docker-compose build

# 进入容器
docker-compose run cobol-env

# 使用预构建 GnuCOBOL 镜像
docker-compose run gnucobol
```

## 模块说明

### jlib
基础工具库，包含日志、XML处理、数据库连接等功能。

### naca-rt
COBOL 运行时环境，提供转译后 Java 程序的执行支持。

### naca-trans
COBOL 到 Java 的转译器。

### naca-rt-tests
测试程序集合，用于验证运行时功能。

## 安装 GnuCOBOL

### macOS
```bash
brew install gnucobol
```

### Ubuntu/Debian
```bash
sudo apt-get install gnucobol4
```

### 验证安装
```bash
cobc --version
```

## 许可证

- JLib, NacaRT: LGPL
- NacaTrans, NacaRTTests: GPL