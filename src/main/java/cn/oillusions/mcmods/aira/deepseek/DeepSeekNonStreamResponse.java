package cn.oillusions.mcmods.aira.deepseek;

import com.google.gson.JsonObject;

public class DeepSeekNonStreamResponse implements DeepSeekResponse {
    private final JsonObject rawResponse;
    private final String content;
    private final String reasoningContent;
    private final int statusCode;
    private final boolean reasoning;

    public DeepSeekNonStreamResponse(JsonObject rawResponse, int statusCode) {
        this.rawResponse = rawResponse;
        this.statusCode = statusCode;

        this.content = extractContent();
        this.reasoningContent = extractReasoningContent();
        this.reasoning = !this.reasoningContent.isBlank();
    }

    protected JsonObject extractChoices(int index) {
        if (this.rawResponse.has("choices")) {
            return rawResponse.getAsJsonArray("choices").get(index).getAsJsonObject();
        }
        return new JsonObject();
    }

    protected JsonObject extractMessage() {
        return extractChoices(0).getAsJsonObject("message");
    }


    protected String extractContent()
    {
        try {
            return extractMessage().get("content").getAsString();
        } catch (Exception e) {
            System.err.println(new DeepSeekException(e.getMessage(), -1).getMessage());
            e.printStackTrace();
        }
        return "";
    }

    protected String extractReasoningContent() {
        try {
            JsonObject message = extractMessage();
            if (message.has("reasoning_content") && !message.get("reasoning_content").isJsonNull()) {
                return message.get("reasoning_content").getAsString();
            }
        } catch (Exception e) {
            System.err.println(new DeepSeekException(e.getMessage(), -1).getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public JsonObject getrawResponse() {
        return rawResponse;
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
