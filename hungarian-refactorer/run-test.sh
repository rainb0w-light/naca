#!/bin/bash

# 匈牙利命名法重构 - 快速运行脚本
# 用于在 naca 项目上快速执行测试

set -e

PROJECT_ROOT="/Volumes/AppData/codebase/naca"
PLUGIN_DIR="$PROJECT_ROOT/hungarian-refactorer"

echo "=========================================="
echo "匈牙利命名法重构测试"
echo "=========================================="
echo ""

cd "$PROJECT_ROOT"

# 1. 确保代码已提交
echo "步骤 1: 检查 Git 状态..."
if [ -n "$(git status --porcelain)" ]; then
    echo "⚠ 工作区有未提交的更改"
    echo "建议先提交代码，以便后续比对"
    read -p "是否继续？(y/n): " confirm
    if [ "$confirm" != "y" ]; then
        echo "测试已取消"
        exit 0
    fi
fi

# 记录当前 commit
BEFORE_COMMIT=$(git rev-parse HEAD)
echo "当前 Commit: $BEFORE_COMMIT"
echo ""

# 2. 创建测试分支
echo "步骤 2: 创建测试分支..."
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [[ "$CURRENT_BRANCH" != "test/hungarian"* ]]; then
    git checkout -b test/hungarian-refactoring-$(date +%Y%m%d-%H%M%S)
    echo "已创建测试分支"
fi
echo ""

# 3. 构建插件
echo "步骤 3: 构建插件..."
cd "$PLUGIN_DIR"
./gradlew clean buildPlugin --quiet
PLUGIN_PATH=$(find "$PLUGIN_DIR/build/distributions" -name "*.zip" | head -1)
echo "插件已构建：$PLUGIN_PATH"
echo ""

# 4. 分析目标文件
echo "步骤 4: 分析目标文件..."
cd "$PROJECT_ROOT"

# 创建分析脚本
mkdir -p test-output

# 分析 ActionShowScreen.java
echo "分析 ActionShowScreen.java..."
ANALYSIS_FILE="test-output/hungarian-analysis.txt"

echo "匈牙利命名法变量分析报告" > "$ANALYSIS_FILE"
echo "日期：$(date)" >> "$ANALYSIS_FILE"
echo "==========================================" >> "$ANALYSIS_FILE"

# 扫描测试文件
for file in \
    "naca-rt/src/main/java/idea/action/ActionShowScreen.java" \
    "naca-rt/src/main/java/idea/emulweb/CScenarioPlayer.java"
do
    if [ -f "$file" ]; then
        echo "" >> "$ANALYSIS_FILE"
        echo "文件：$file" >> "$ANALYSIS_FILE"
        echo "----------------------------------------" >> "$ANALYSIS_FILE"

        # 提取匈牙利命名法变量
        grep -oE '\b(cs|str|doc|e|n|nb|lst|btn|i|l|b)[A-Z][a-zA-Z]+\b' "$file" | \
            sort | uniq -c | sort -rn >> "$ANALYSIS_FILE"
    fi
done

echo "分析报告已生成：$ANALYSIS_FILE"
cat "$ANALYSIS_FILE"
echo ""

# 5. 编译前测试
echo "步骤 5: 编译前测试..."
./gradlew :naca-rt:compileJava --quiet && echo "✓ 编译成功" || echo "✗ 编译失败"
echo ""

# 6. 运行前测试
echo "步骤 6: 运行单元测试..."
./gradlew :naca-rt:test --quiet && echo "✓ 测试通过" || echo "⚠ 测试有失败"
echo ""

# 7. 提示用户执行重构
echo "步骤 7: 执行重构"
echo ""
echo "由于插件需要通过 IntelliJ GUI 执行，请按以下步骤操作："
echo ""
echo "1. 打开 IntelliJ IDEA"
echo "2. 加载 naca 项目"
echo "3. 安装插件：Settings → Plugins → Install Plugin from Disk"
echo "   选择：$PLUGIN_PATH"
echo "4. 重启 IDEA"
echo "5. 打开文件：naca-rt/src/main/java/idea/action/ActionShowScreen.java"
echo "6. 右键 → Code → Hungarian Notation → Refactor Hungarian Variables"
echo "7. 预览更改并确认"
echo ""
echo "或者使用无头模式（需要 IDEA 命令行工具）："
echo "  /Applications/IntelliJ\ IDEA.app/Contents/MacOS/idea headless \\"
echo "    --project $PROJECT_ROOT \\"
echo "    --plugin $PLUGIN_PATH \\"
echo "    --batch-action refactor"
echo ""

read -p "是否已完成重构？(y/n): " refactor_done
if [ "$refactor_done" != "y" ]; then
    echo "请先完成重构，然后继续..."
    exit 0
fi

# 8. 查看 Git 差异
echo "步骤 8: 查看 Git 差异..."
echo ""
echo "修改统计:"
git diff --stat

echo ""
echo "修改文件列表:"
git diff --name-only

echo ""
echo "详细差异已保存到：test-output/git-diff.patch"
git diff > "test-output/git-diff.patch"
echo ""

# 9. 编译后测试
echo "步骤 9: 编译后测试..."
./gradlew :naca-rt:compileJava --quiet && echo "✓ 编译成功" || echo "✗ 编译失败"
echo ""

# 10. 运行后测试
echo "步骤 10: 运行单元测试..."
./gradlew :naca-rt:test --quiet && echo "✓ 测试通过" || echo "⚠ 测试有失败"
echo ""

# 11. 生成测试报告
echo "步骤 11: 生成测试报告..."

REPORT_FILE="test-output/test-report-$(date +%Y%m%d-%H%M%S).md"

cat > "$REPORT_FILE" << EOF
# 匈牙利命名法重构测试报告

## 基本信息

- **测试日期**: $(date)
- **测试项目**: $PROJECT_ROOT
- **测试分支**: $(git rev-parse --abbrev-ref HEAD)
- **测试人员**: $(whoami)

## 测试前状态

- **Commit**: $BEFORE_COMMIT
- **分支**: $CURRENT_BRANCH

## 插件信息

- **插件路径**: $PLUGIN_PATH
- **版本**: 1.0.0

## 测试文件

- ActionShowScreen.java
- CScenarioPlayer.java

## 测试结果

### 编译测试

$(./gradlew :naca-rt:compileJava --quiet 2>&1 > /dev/null && echo "✅ 通过" || echo "❌ 失败")

### 单元测试

$(./gradlew :naca-rt:test --quiet 2>&1 > /dev/null && echo "✅ 通过" || echo "❌ 失败")

## Git 差异统计

\`\`\`
$(git diff --stat)
\`\`\`

## 修改的文件

$(git diff --name-only)

## 示例变更

\`\`\`diff
$(git diff -- '*.java' | head -50)
\`\`\`

## 结论

重构测试已完成。请检查上述报告，确认：
- [ ] 编译通过
- [ ] 测试通过
- [ ] Git 差异合理
- [ ] 功能正常

## 附录

- 分析报告：test-output/hungarian-analysis.txt
- Git 差异：test-output/git-diff.patch
EOF

echo "测试报告已生成：$REPORT_FILE"
echo ""
cat "$REPORT_FILE"

echo ""
echo "=========================================="
echo "测试完成!"
echo "=========================================="
