package com.example.codeexplainer.settings;

import com.intellij.ide.util.PropertiesComponent;

public final class AppSettings {

    private static final String KEY_API_KEY = "codeExplainer.apiKey";

    private AppSettings() {}

    public static String getApiKey() {
        return PropertiesComponent.getInstance().getValue(KEY_API_KEY, "");
    }

    public static void setApiKey(String apiKey) {
        PropertiesComponent.getInstance().setValue(KEY_API_KEY, apiKey);
    }
}
