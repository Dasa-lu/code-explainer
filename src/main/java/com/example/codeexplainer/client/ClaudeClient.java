package com.example.codeexplainer.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class ClaudeClient {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-3-5-haiku-20241022";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    /**
     * Calls the Claude API synchronously. Must be called from a background thread.
     *
     * @param apiKey       Anthropic API key
     * @param level        explanation level: "Junior", "Mid", or "Senior"
     * @param selectedCode the code snippet to explain
     * @return plain-text explanation from Claude
     * @throws IOException on network or API error
     */
    public String explain(String apiKey, String level, String selectedCode) throws IOException {
        String systemPrompt = "You are a code explanation assistant. Explain code clearly and concisely " +
                "for a " + level + " developer. Focus on what the code does, why it's written this way, " +
                "and what patterns it uses.";

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        JsonArray userContent = new JsonArray();
        JsonObject userText = new JsonObject();
        userText.addProperty("type", "text");
        userText.addProperty("text", "Explain this code:\n" + selectedCode);
        userContent.add(userText);
        userMessage.add("content", userContent);

        JsonArray messages = new JsonArray();
        messages.add(userMessage);

        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        body.addProperty("max_tokens", 1024);
        body.addProperty("system", systemPrompt);
        body.add("messages", messages);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(RequestBody.create(gson.toJson(body), JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API error " + response.code() + ": " + response.body().string());
            }
            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
            return json.getAsJsonArray("content")
                       .get(0).getAsJsonObject()
                       .get("text").getAsString();
        }
    }
}
