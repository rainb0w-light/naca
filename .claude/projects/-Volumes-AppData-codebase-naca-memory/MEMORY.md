# Naca 项目记忆

**项目**: Naca - COBOL 到 Java 转译器
**最后更新**: 2026-03-11

---

## 核心信息

- **JDK 版本**: 23
- **构建工具**: Gradle 8.9+
- **Spring 版本**: 6.2.3 / Spring Boot 3.4.3
- **数据库连接池**: HikariCP 5.1.0
- **日志框架**: SLF4J 2.0.16 + Log4j 2.24.3

---

## 模块结构

```
naca/
├── naca-jlib        # 基础工具库
├── naca-trans       # COBOL 到 Java 转译器
├── naca-rt          # 运行时环境
└── naca-rt-tests    # 测试套件
```

---

## 架构升级状态

| Phase | 状态 | 内容 |
|-------|------|------|
| Phase 1 | ✅ 完成 | SQL 注入防护、配置外部化、日志统一 |
| Phase 2 | ✅ 完成 | SQL DSL、HikariCP 集成、游标管理 |
| Phase 3 | 🔄 进行中 | BaseProgramManager 模块化重构 |
| Phase 4 | ⏳ 待实施 | 可观测性增强 (Micrometer) |

---

## 关键文档

- [ARCHITECTURE_ANALYSIS.md](ARCHITECTURE_ANALYSIS.md) - 完整架构分析
- [ARCHITECTURE_IMPLEMENTATION_REPORT.md](../ARCHITECTURE_IMPLEMENTATION_REPORT.md) - 实施报告
- [ARCHITECTURE_UPGRADE_GUIDE.md](../ARCHITECTURE_UPGRADE_GUIDE.md) - 使用指南
- [BASEPROGRAMMANAGER_REFACTOR.md](../naca-rt/BASEPROGRAMMANAGER_REFACTOR.md) - 重构计划

---

## 重构原则

1. **向后兼容**: 保留旧 API 作为 Façade
2. **测试先行**: 重构前确保测试通过
3. **小步提交**: 每次重构一个组件
4. **性能基线**: 重构前后对比性能

---

## 常用命令

```bash
# 构建和测试
./gradlew build
./gradlew test

# SQL 注入测试
./gradlew :naca-rt-tests:test --tests "SqlInjectionTests"

# 生成覆盖率报告
./gradlew jacocoAggregateReport
```
