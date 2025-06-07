package cn.oillusions.mcmods.aira.deepseek;

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
        this.reasoning = this.content.isBlank();
    }

    protected JsonObject extractDelta() {
        if (this.rewResponse.has("choices")) {
            try {
                return rewResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("delta");
            } catch (Exception e) {
                return new JsonObject();
            }
        }
        return new JsonObject();
    }

    protected String extractContent()
    {
            try {
                JsonObject messageStream = extractDelta();
                if (messageStream.has("content") && !messageStream.get("content").isJsonNull()) {
                    return messageStream.get("content").getAsString();
                }
            } catch (Exception e) {
                return "";
            }
        return "";
    }

    protected String extractReasoningContent() {
            try {
                JsonObject messageStream = extractDelta();
                if (messageStream.has("reasoning_content") && !messageStream.get("reasoning_content").isJsonNull()) {
                    return messageStream.get("reasoning_content").getAsString();
                }
            } catch (Exception e) {
                return "";
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
