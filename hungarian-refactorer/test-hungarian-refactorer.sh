#!/bin/bash

# Hungarian Refactorer 测试脚本
# 用于在 naca 项目上测试匈牙利命名法重构插件

set -e

# 配置
PROJECT_ROOT="/Volumes/AppData/codebase/naca"
PLUGIN_DIR="$PROJECT_ROOT/hungarian-refactorer"
OUTPUT_DIR="$PROJECT_ROOT/test-output"
REPORT_DIR="$OUTPUT_DIR/reports"
BACKUP_DIR="$OUTPUT_DIR/backup"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印函数
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 初始化测试环境
init_test_env() {
    print_header "初始化测试环境"

    # 创建输出目录
    mkdir -p "$OUTPUT_DIR"
    mkdir -p "$REPORT_DIR"
    mkdir -p "$BACKUP_DIR"

    # 检查 git 状态
    cd "$PROJECT_ROOT"
    if [ -n "$(git status --porcelain)" ]; then
        print_warning "工作区有未提交的更改，建议先提交"
        read -p "是否继续？(y/n): " confirm
        if [ "$confirm" != "y" ]; then
            echo "测试已取消"
            exit 0
        fi
    fi

    # 记录当前分支
    CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
    echo "当前分支：$CURRENT_BRANCH"

    # 创建测试分支（如果不是测试分支）
    if [[ "$CURRENT_BRANCH" != "test/hungarian"* ]]; then
        print_header "创建测试分支"
        git checkout -b test/hungarian-refactoring-$(date +%Y%m%d-%H%M%S)
    fi

    print_success "测试环境初始化完成"
}

# 构建插件
build_plugin() {
    print_header "构建插件"

    cd "$PLUGIN_DIR"
    ./gradlew clean buildPlugin

    if [ $? -eq 0 ]; then
        PLUGIN_PATH=$(find "$PLUGIN_DIR/build/distributions" -name "*.zip" | head -1)
        print_success "插件构建成功：$PLUGIN_PATH"
        echo "$PLUGIN_PATH" > "$OUTPUT_DIR/plugin-path.txt"
    else
        print_error "插件构建失败"
        exit 1
    fi
}

# 分析匈牙利命名法变量
analyze_hungarian() {
    print_header "分析匈牙利命名法变量"

    cd "$PROJECT_ROOT"

    # 使用 grep 初步分析（模拟插件分析）
    echo "扫描 Java 源文件..."

    # 查找常见的匈牙利命名法模式
    PATTERNS=(
        "str[A-Z][a-z]+"      # String
        "i[A-Z][a-z]+"        # int
        "l[A-Z][a-z]+"        # long/list
        "b[A-Z][a-z]+"        # boolean
        "btn[A-Z][a-z]+"      # JButton
        "lst[A-Z][a-z]+"      # List
        "cs[A-Z][a-z]+"       # C string
        "doc[A-Z][a-z]+"      # Document
        "e[A-Z][a-z]+"        # Element
        "n[A-Z][a-z]+"        # number
        "nb[A-Z][a-z]+"      # number
        "lst[A-Z][a-z]+"     # List
    )

    # 创建分析报告
    ANALYSIS_REPORT="$REPORT_DIR/hungarian-analysis-$(date +%Y%m%d-%H%M%S).txt"

    echo "匈牙利命名法变量分析报告" > "$ANALYSIS_REPORT"
    echo "生成时间：$(date)" >> "$ANALYSIS_REPORT"
    echo "项目：$PROJECT_ROOT" >> "$ANALYSIS_REPORT"
    echo "==========================================" >> "$ANALYSIS_REPORT"
    echo "" >> "$ANALYSIS_REPORT"

    # 在选定的测试文件中查找
    TEST_FILES=(
        "naca-rt/src/main/java/idea/action/ActionShowScreen.java"
        "naca-rt/src/main/java/idea/emulweb/CScenarioPlayer.java"
    )

    for file in "${TEST_FILES[@]}"; do
        if [ -f "$file" ]; then
            echo "" >> "$ANALYSIS_REPORT"
            echo "文件：$file" >> "$ANALYSIS_REPORT"
            echo "----------------------------------------" >> "$ANALYSIS_REPORT"

            # 查找匈牙利命名法变量
            grep -oE '\b(str|cs|doc|e|n|nb|lst|btn|i|l|b)[A-Z][a-zA-Z]+\b' "$file" | \
                sort | uniq -c | sort -rn >> "$ANALYSIS_REPORT"
        fi
    done

    print_success "分析报告已生成：$ANALYSIS_REPORT"
    cat "$ANALYSIS_REPORT"
}

# 备份当前代码
backup_code() {
    print_header "备份当前代码"

    BACKUP_SUBDIR="$BACKUP_DIR/backup-$(date +%Y%m%d-%H%M%S)"
    mkdir -p "$BACKUP_SUBDIR"

    # 备份测试文件
    cp -r "$PROJECT_ROOT/naca-rt/src/main/java/idea/action" "$BACKUP_SUBDIR/"
    cp -r "$PROJECT_ROOT/naca-rt/src/main/java/idea/emulweb" "$BACKUP_SUBDIR/"

    print_success "代码已备份到：$BACKUP_SUBDIR"
}

# 执行重构（模拟）
perform_refactor() {
    print_header "执行重构"

    cd "$PROJECT_ROOT"

    # 记录重构前的 git 状态
    git status > "$REPORT_DIR/pre-refactor-status.txt"
    git diff > "$REPORT_DIR/pre-refactor-diff.patch"

    echo "重构将通过插件执行，这里是模拟步骤："
    echo "1. 加载插件"
    echo "2. 扫描测试文件"
    echo "3. 识别匈牙利命名法变量"
    echo "4. 执行批量重命名"
    echo "5. 更新相关引用"

    # 实际使用插件时，这里会调用：
    # idea.sh headless --project "$PROJECT_ROOT" --plugin "$PLUGIN_PATH" --batch-action refactor

    print_warning "注意：实际重构需要通过 IntelliJ 插件执行"
    print_success "重构步骤已记录"
}

# 编译验证
compile_verify() {
    print_header "编译验证"

    cd "$PROJECT_ROOT"

    echo "执行 Gradle 编译..."
    ./gradlew clean compileJava 2>&1 | tee "$REPORT_DIR/compile-output.log"

    if [ ${PIPESTATUS[0]} -eq 0 ]; then
        print_success "编译成功！"
        return 0
    else
        print_error "编译失败！"
        print_warning "查看编译日志：$REPORT_DIR/compile-output.log"
        return 1
    fi
}

# 运行单元测试
run_tests() {
    print_header "运行单元测试"

    cd "$PROJECT_ROOT"

    echo "执行 Gradle 测试..."
    ./gradlew test 2>&1 | tee "$REPORT_DIR/test-output.log"

    if [ ${PIPESTATUS[0]} -eq 0 ]; then
        print_success "所有测试通过！"
        return 0
    else
        print_error "测试失败！"
        print_warning "查看测试日志：$REPORT_DIR/test-output.log"
        return 1
    fi
}

# 生成 Git 差异报告
generate_git_diff() {
    print_header "生成 Git 差异报告"

    cd "$PROJECT_ROOT"

    # 生成详细差异报告
    DIFF_REPORT="$REPORT_DIR/git-diff-$(date +%Y%m%d-%H%M%S).patch"

    git diff > "$DIFF_REPORT"

    echo "差异报告已生成：$DIFF_REPORT"
    echo ""
    echo "修改统计："
    git diff --stat

    # 生成详细变更列表
    echo ""
    echo "变更文件列表："
    git diff --name-only

    print_success "Git 差异报告已生成"
}

# 生成最终测试报告
generate_final_report() {
    print_header "生成最终测试报告"

    FINAL_REPORT="$REPORT_DIR/final-test-report-$(date +%Y%m%d-%H%M%S).md"

    cat > "$FINAL_REPORT" << EOF
# Hungarian Refactorer 测试报告

## 基本信息

- **测试日期**: $(date)
- **项目**: $PROJECT_ROOT
- **测试分支**: $(git rev-parse --abbrev-ref HEAD)
- **测试人员**: $(whoami)

## 测试摘要

EOF

    # 添加编译状态
    if [ -f "$REPORT_DIR/compile-output.log" ]; then
        if grep -q "BUILD SUCCESSFUL" "$REPORT_DIR/compile-output.log"; then
            echo "- **编译状态**: ✅ 通过" >> "$FINAL_REPORT"
        else
            echo "- **编译状态**: ❌ 失败" >> "$FINAL_REPORT"
        fi
    else
        echo "- **编译状态**: ⚠️ 未执行" >> "$FINAL_REPORT"
    fi

    # 添加测试状态
    if [ -f "$REPORT_DIR/test-output.log" ]; then
        if grep -q "BUILD SUCCESSFUL" "$REPORT_DIR/test-output.log"; then
            echo "- **测试状态**: ✅ 通过" >> "$FINAL_REPORT"
        else
            echo "- **测试状态**: ❌ 失败" >> "$FINAL_REPORT"
        fi
    else
        echo "- **测试状态**: ⚠️ 未执行" >> "$FINAL_REPORT"
    fi

    # 添加 Git 差异统计
    echo "" >> "$FINAL_REPORT"
    echo "## Git 差异统计" >> "$FINAL_REPORT"
    echo '```' >> "$FINAL_REPORT"
    git diff --stat >> "$FINAL_REPORT"
    echo '```' >> "$FINAL_REPORT"

    # 添加修改文件列表
    echo "" >> "$FINAL_REPORT"
    echo "## 修改的文件" >> "$FINAL_REPORT"
    echo '```' >> "$FINAL_REPORT"
    git diff --name-only >> "$FINAL_REPORT"
    echo '```' >> "$FINAL_REPORT"

    # 添加示例变更
    echo "" >> "$FINAL_REPORT"
    echo "## 示例变更" >> "$FINAL_REPORT"
    echo '```diff' >> "$FINAL_REPORT"
    git diff -- '*.java' | head -100 >> "$FINAL_REPORT"
    echo '```' >> "$FINAL_REPORT"

    # 添加报告文件列表
    echo "" >> "$FINAL_REPORT"
    echo "## 报告文件" >> "$FINAL_REPORT"
    echo "" >> "$FINAL_REPORT"
    ls -la "$REPORT_DIR"/ >> "$FINAL_REPORT"

    print_success "最终测试报告已生成：$FINAL_REPORT"

    # 在终端显示摘要
    cat "$FINAL_REPORT"
}

# 回滚代码
rollback() {
    print_header "回滚代码"

    cd "$PROJECT_ROOT"

    echo "当前分支：$(git rev-parse --abbrev-ref HEAD)"
    read -p "是否确认回滚到测试前状态？(y/n): " confirm

    if [ "$confirm" = "y" ]; then
        # 切换回 master
        git checkout master

        # 删除测试分支
        read -p "是否删除测试分支？(y/n): " delete_branch
        if [ "$delete_branch" = "y" ]; then
            git branch -D test/hungarian-refactoring-*
        fi

        print_success "代码已回滚"
    else
        print_warning "回滚已取消"
    fi
}

# 显示使用帮助
show_help() {
    cat << EOF
Hungarian Refactorer 测试脚本

用法：$0 [选项]

选项:
  --init           初始化测试环境
  --build          构建插件
  --analyze        分析匈牙利命名法变量
  --backup         备份当前代码
  --refactor       执行重构
  --compile        编译验证
  --test           运行单元测试
  --diff           生成 Git 差异报告
  --report         生成最终测试报告
  --rollback       回滚代码
  --full           完整测试流程（所有步骤）
  --help           显示此帮助信息

示例:
  # 执行完整测试
  $0 --full

  # 仅分析
  $0 --analyze

  # 分析 + 重构 + 编译验证
  $0 --analyze --refactor --compile

EOF
}

# 完整测试流程
full_test() {
    init_test_env
    build_plugin
    analyze_hungarian
    backup_code
    perform_refactor
    compile_verify
    run_tests
    generate_git_diff
    generate_final_report
}

# 主程序
main() {
    if [ $# -eq 0 ]; then
        show_help
        exit 0
    fi

    while [ $# -gt 0 ]; do
        case $1 in
            --init)
                init_test_env
                ;;
            --build)
                build_plugin
                ;;
            --analyze)
                analyze_hungarian
                ;;
            --backup)
                backup_code
                ;;
            --refactor)
                perform_refactor
                ;;
            --compile)
                compile_verify
                ;;
            --test)
                run_tests
                ;;
            --diff)
                generate_git_diff
                ;;
            --report)
                generate_final_report
                ;;
            --rollback)
                rollback
                ;;
            --full)
                full_test
                ;;
            --help)
                show_help
                ;;
            *)
                print_error "未知选项：$1"
                show_help
                exit 1
                ;;
        esac
        shift
    done
}

# 执行主程序
main "$@"
