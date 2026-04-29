package com.example.codeexplainer.actions;

import com.example.codeexplainer.client.ClaudeClient;
import com.example.codeexplainer.settings.AppSettings;
import com.example.codeexplainer.toolwindow.ExplainToolWindowFactory;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class ExplainAction extends AnAction {

    private final ClaudeClient client = new ClaudeClient();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor == null || project == null) return;

        SelectionModel selection = editor.getSelectionModel();
        String selectedText = selection.getSelectedText();

        if (selectedText == null || selectedText.isBlank()) {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("Code Explainer")
                    .createNotification("Please select some code first.", NotificationType.WARNING)
                    .notify(project);
            return;
        }

        // Activate the tool window; run the rest only after it is fully shown
        // (createToolWindowContent sets currentPanel asynchronously).
        ToolWindowManager.getInstance(project).getToolWindow("Code Explainer").activate(() -> {
            ExplainToolWindowFactory.ExplainPanel panel = ExplainToolWindowFactory.getCurrentPanel();
            if (panel == null) return;

            String level = panel.getSelectedLevel();
            String apiKey = AppSettings.getApiKey();

            if (apiKey.isBlank()) {
                panel.setResultText("API key not set. Go to Preferences → Tools → Code Explainer.");
                return;
            }

            panel.setResultText("Explaining... please wait.");

            // Run HTTP call on background thread
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    String result = client.explain(apiKey, level, selectedText);
                    // Update UI on EDT
                    ApplicationManager.getApplication().invokeLater(() -> panel.setResultText(result));
                } catch (Exception ex) {
                    ApplicationManager.getApplication().invokeLater(() ->
                            panel.setResultText("Error: " + ex.getMessage()));
                }
            });
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }
}
