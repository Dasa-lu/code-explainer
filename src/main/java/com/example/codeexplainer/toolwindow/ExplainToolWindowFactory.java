package com.example.codeexplainer.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ExplainToolWindowFactory implements ToolWindowFactory {

    // Static reference so ExplainAction can push text into the panel.
    private static ExplainPanel currentPanel;

    public static ExplainPanel getCurrentPanel() {
        return currentPanel;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        currentPanel = new ExplainPanel();
        Content content = ContentFactory.getInstance()
                .createContent(currentPanel.getRoot(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public static class ExplainPanel {

        private final JPanel root;
        private final JComboBox<String> levelCombo;
        private final JTextArea resultArea;

        public ExplainPanel() {
            root = new JPanel(new BorderLayout(4, 4));

            JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            levelCombo = new JComboBox<>(new String[]{"Junior", "Mid", "Senior"});
            topBar.add(new JLabel("Level:"));
            topBar.add(levelCombo);
            root.add(topBar, BorderLayout.NORTH);

            resultArea = new JTextArea();
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);
            resultArea.setText("Select code in the editor and press Ctrl+Shift+E.");
            root.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        }

        public JPanel getRoot() { return root; }

        public String getSelectedLevel() {
            return (String) levelCombo.getSelectedItem();
        }

        public void setResultText(String text) {
            resultArea.setText(text);
        }
    }
}
