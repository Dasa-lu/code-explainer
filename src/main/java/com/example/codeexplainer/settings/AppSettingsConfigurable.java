package com.example.codeexplainer.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AppSettingsConfigurable implements Configurable {

    private JPasswordField apiKeyField;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Code Explainer";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        apiKeyField = new JPasswordField(40);
        apiKeyField.setText(AppSettings.getApiKey());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Claude API Key:"));
        panel.add(apiKeyField);
        return panel;
    }

    @Override
    public boolean isModified() {
        return !new String(apiKeyField.getPassword()).equals(AppSettings.getApiKey());
    }

    @Override
    public void apply() {
        AppSettings.setApiKey(new String(apiKeyField.getPassword()));
    }

    @Override
    public void reset() {
        apiKeyField.setText(AppSettings.getApiKey());
    }
}
