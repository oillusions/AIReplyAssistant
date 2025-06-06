package top.o_illusions.mcmods.aira.deepseek;

import com.google.gson.JsonObject;

public interface DeepSeekResponse {
    public int getStatusCode();

    public JsonObject getRewResponse();

    public String getContent();

    public String getReasoningContent();

    public boolean isSuccess();

    public boolean isReasoning();
}
