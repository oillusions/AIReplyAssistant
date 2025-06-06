package top.o_illusions.mcmods.aira.deepseek;

import com.google.gson.JsonObject;

public class DeepSeekStreamResponse implements DeepSeekResponse {
    private final JsonObject rewResponse;
    private final String content;
    private final String reasoningContent;
    private final int statusCode;
    private final boolean reasoning;

    public DeepSeekStreamResponse(JsonObject rewResponse, int statusCode) {
        this.rewResponse = rewResponse;
        this.statusCode = statusCode;

        this.content = extractContent();
        this.reasoningContent = extractReasoningContent();
        this.reasoning = !this.reasoningContent.isBlank();
    }


    private String extractContent()
    {
        if (this.rewResponse.has("choices")) {
            try {
                return rewResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("delta")
                        .get("content").getAsString();
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    private String extractReasoningContent() {
        if (this.rewResponse.has("choices")) {
            try {
                JsonObject message = rewResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("delta");
                if (message.has("reasoning_content") && !message.get("reasoning_content").isJsonNull()) {
                    return message.get("reasoning_content").getAsString();
                } else {
                    return "";
                }
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
