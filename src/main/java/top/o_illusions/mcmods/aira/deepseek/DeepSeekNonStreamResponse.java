package top.o_illusions.mcmods.aira.deepseek;

import com.google.gson.JsonObject;

public class DeepSeekNonStreamResponse implements DeepSeekResponse {
    private final JsonObject rewResponse;
    private final String content;
    private final String reasoningContent;
    private final int statusCode;
    private final boolean reasoning;

    public DeepSeekNonStreamResponse(JsonObject rewResponse, int statusCode) {
        this.rewResponse = rewResponse;
        this.statusCode = statusCode;

        this.content = extractContent();
        this.reasoningContent = extractReasoningContent();
        this.reasoning = !this.reasoningContent.isBlank();
    }


    private String extractContent()
    {
        StringBuilder contentStreamBuffer = new StringBuilder();
        if (this.rewResponse.has("choices")) {
            try {
                rewResponse.getAsJsonArray("choices").forEach(object -> {
                    JsonObject message = object.getAsJsonObject()
                            .getAsJsonObject("message");
                    if (message.has("content") && !message.get("content").isJsonNull()) {
                        contentStreamBuffer.append(message.get("content").getAsString());
                    }
                });
                return contentStreamBuffer.toString();
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    private String extractReasoningContent() {
        StringBuilder contentStreamBuffer = new StringBuilder();
        if (this.rewResponse.has("choices")) {
            try {
                rewResponse.getAsJsonArray("choices").forEach(object -> {
                    JsonObject message = object.getAsJsonObject()
                            .getAsJsonObject("message");
                    if (message.has("reasoning_content") && !message.get("reasoning_content").isJsonNull()) {
                        contentStreamBuffer.append(message.get("reasoning_content").getAsString());
                    }
                });
                return contentStreamBuffer.toString();
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public JsonObject getRewResponse() {
        return rewResponse;
    }

    public String getContent() {
        return content;
    }

    public String getReasoningContent() {
        return reasoningContent;
    }

    public boolean isSuccess() {
        return statusCode == 200;
    }

    public boolean isReasoning() {
        return reasoning;
    }
}
