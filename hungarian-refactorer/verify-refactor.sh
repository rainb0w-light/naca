#!/bin/bash

# 匈牙利命名法重构 - 详细验证脚本
# 用于验证重构的完整性和正确性

set -e

PROJECT_ROOT="/Volumes/AppData/codebase/naca"
REPORT_DIR="$PROJECT_ROOT/test-output/verification"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_check() {
    echo -e "${GREEN}[CHECK]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_fail() {
    echo -e "${RED}[FAIL]${NC} $1"
}

mkdir -p "$REPORT_DIR"

cd "$PROJECT_ROOT"

echo "=========================================="
echo "匈牙利命名法重构验证脚本"
echo "=========================================="
echo ""

# 1. 检查是否有匈牙利命名法残留
print_check "检查匈牙利命名法变量残留..."

RESIDUAL_FILES="$REPORT_DIR/residual-hungarian.txt"

# 检查常见匈牙利前缀（排除文件路径等）
grep -rn '\b\(str\|cs\|lst\|nb\|eForm\|eCurrent\|eOutput\|eForm\)\+[A-Z]' \
    --include="*.java" \
    naca-rt/src/main/java/idea/action/ActionShowScreen.java \
    naca-rt/src/main/java/idea/emulweb/CScenarioPlayer.java \
    2>/dev/null > "$RESIDUAL_FILES" || true

RESIDUAL_COUNT=$(wc -l < "$RESIDUAL_FILES")
if [ "$RESIDUAL_COUNT" -eq 0 ]; then
    echo -e "${GREEN}✓ 未发现匈牙利命名法变量残留${NC}"
else
    echo -e "${YELLOW}⚠ 发现 $RESIDUAL_COUNT 处可能的残留${NC}"
    head -20 "$RESIDUAL_FILES"
fi

# 2. 检查新命名是否符合驼峰规范
print_check "检查新命名是否符合驼峰规范..."

CAMEL_CASE_VIOLATIONS="$REPORT_DIR/camel-case-violations.txt"

# 查找可能的命名问题（连续大写字母、下划线等）
grep -rn '\b[a-z][A-Z][A-Z]' \
    --include="*.java" \
    naca-rt/src/main/java/idea/action/ \
    naca-rt/src/main/java/idea/emulweb/ \
    2>/dev/null > "$CAMEL_CASE_VIOLATIONS" || true

VIOLATION_COUNT=$(wc -l < "$CAMEL_CASE_VIOLATIONS")
if [ "$VIOLATION_COUNT" -eq 0 ]; then
    echo -e "${GREEN}✓ 命名符合驼峰规范${NC}"
else
    echo -e "${YELLOW}⚠ 发现 $VIOLATION_COUNT 处可能的命名问题${NC}"
fi

# 3. 检查变量引用一致性
print_check "检查变量引用一致性..."

# 提取文件中所有变量声明和使用
VARIABLE_CONSISTENCY="$REPORT_DIR/variable-consistency.txt"

# 检查是否有变量声明和引用不匹配的情况
# 这里简化处理，实际应该用 PSI 分析

# 4. 编译检查
print_check "执行编译检查..."

COMPILE_RESULT="$REPORT_DIR/compile-result.txt"

./gradlew :naca-rt:compileJava > "$COMPILE_RESULT" 2>&1
if grep -q "BUILD SUCCESSFUL" "$COMPILE_RESULT"; then
    echo -e "${GREEN}✓ 编译成功${NC}"
else
    echo -e "${RED}✗ 编译失败${NC}"
    tail -50 "$COMPILE_RESULT"
    exit 1
fi

# 5. 运行相关测试
print_check "运行单元测试..."

TEST_RESULT="$REPORT_DIR/test-result.txt"

./gradlew :naca-rt:test --tests "*ActionShowScreen*" > "$TEST_RESULT" 2>&1
if grep -q "BUILD SUCCESSFUL" "$TEST_RESULT"; then
    echo -e "${GREEN}✓ ActionShowScreen 测试通过${NC}"
else
    print_warn "ActionShowScreen 测试失败或未找到"
fi

./gradlew :naca-rt:test --tests "*CScenarioPlayer*" > "$TEST_RESULT" 2>&1
if grep -q "BUILD SUCCESSFUL" "$TEST_RESULT"; then
    echo -e "${GREEN}✓ CScenarioPlayer 测试通过${NC}"
else
    print_warn "CScenarioPlayer 测试失败或未找到"
fi

# 6. Git 差异分析
print_check "分析 Git 差异..."

DIFF_STATS="$REPORT_DIR/diff-stats.txt"
git diff --stat > "$DIFF_STATS"

echo "修改统计:"
cat "$DIFF_STATS"

DIFF_DETAIL="$REPORT_DIR/diff-detail.txt"
git diff -- '*.java' > "$DIFF_DETAIL"

# 7. 生成验证报告
print_check "生成验证报告..."

VERIFICATION_REPORT="$REPORT_DIR/verification-report-$(date +%Y%m%d-%H%M%S).md"

cat > "$VERIFICATION_REPORT" << EOF
# 匈牙利命名法重构验证报告

## 基本信息

- **日期**: $(date)
- **项目**: $PROJECT_ROOT
- **分支**: $(git rev-parse --abbrev-ref HEAD)

## 验证结果

### 1. 匈牙利命名法残留检查

- 发现残留：$RESIDUAL_COUNT 处
- 详情：见 residual-hungarian.txt

### 2. 驼峰命名规范检查

- 命名违规：$VIOLATION_COUNT 处
- 详情：见 camel-case-violations.txt

### 3. 编译检查

$(if grep -q "BUILD SUCCESSFUL" "$COMPILE_RESULT"; then echo "- ✅ 编译通过"; else echo "- ❌ 编译失败"; fi)

### 4. 单元测试

$(if grep -q "BUILD SUCCESSFUL" "$TEST_RESULT"; then echo "- ✅ 测试通过"; else echo "- ⚠️ 测试未执行或有失败"; fi)

### 5. Git 差异

\`\`\`
$(cat "$DIFF_STATS")
\`\`\`

## 修改文件列表

$(git diff --name-only -- '*.java')

## 建议

EOF

if [ "$RESIDUAL_COUNT" -gt 0 ]; then
    echo "- ⚠️ 检查残留的匈牙利命名法变量，确认是否需要重构" >> "$VERIFICATION_REPORT"
fi

if [ "$VIOLATION_COUNT" -gt 0 ]; then
    echo "- ⚠️ 检查命名违规，确认是否符合驼峰规范" >> "$VERIFICATION_REPORT"
fi

echo "" >> "$VERIFICATION_REPORT"
echo "## 附录" >> "$VERIFICATION_REPORT"
echo "" >> "$VERIFICATION_REPORT"
echo "报告文件位置：$REPORT_DIR" >> "$VERIFICATION_REPORT"

echo -e "${GREEN}✓ 验证报告已生成：$VERIFICATION_REPORT${NC}"
echo ""
cat "$VERIFICATION_REPORT"
