#!/usr/bin/perl
# 匈牙利命名法变量重命名脚本
# 使用智能替换，避免变量名冲突

use strict;
use warnings;

my $file = $ARGV[0];
open(my $fh, '<', $file) or die "Cannot open $file: $!";
my $content = do { local $/; <$fh> };
close($fh);

# 替换规则：原变量名 -> 新变量名
my %replacements = (
    'csPage' => 'htmlPage',     # 避免与 Document 冲突
    'csLang' => 'lang',
    'eForm' => 'formElement',   # 避免与 ActionForm 冲突
    'docOutput' => 'doc',
    'lstForms' => 'forms',
    'nb' => 'count',
    'lst' => 'list',
    'nLength' => 'len',
    'e' => 'elem',
    'csName' => 'tagName',
);

# 按长度排序，先替换长的变量名
foreach my $old (sort { length($b) <=> length($a) } keys %replacements) {
    my $new = $replacements{$old};
    # 使用单词边界匹配
    $content =~ s/\b$old\b/$new/g;
}

open($fh, '>', $file) or die "Cannot write $file: $!";
print $fh $content;
close($fh);

print "Renamed Hungarian variables in $file\n";
