package com.example.hungarian;

import com.example.hungarian.actions.RefactorProjectAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * 项目启动时执行的活动
 * 用于支持无头模式下的自动重构
 */
public class HungarianRefactorerStartupActivity implements StartupActivity {

    private static final Logger LOG = Logger.getInstance(HungarianRefactorerStartupActivity.class);

    @Override
    public void runActivity(@NotNull Project project) {
        // 检查是否启用了自动重构
        HungarianRefactorerSettings settings = HungarianRefactorerSettings.getInstance(project);
        if (settings == null || !settings.isEnabled()) {
            return;
        }

        LOG.info("Hungarian Refactorer plugin activated for project: " + project.getName());
    }
}
