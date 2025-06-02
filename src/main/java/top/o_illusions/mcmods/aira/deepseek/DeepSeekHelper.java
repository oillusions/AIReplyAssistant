package top.o_illusions.mcmods.aira.deepseek;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class DeepSeekHelper {
    protected  JsonObject requestTemplateJson = new JsonObject();
    protected JsonArray messageConText = new JsonArray();
    protected JsonObject systemCueWord = new JsonObject();
    protected final Gson gson = new Gson();
    protected final OkHttpClient httpClient = new OkHttpClient();
    protected String apiUrl;
    protected String apiKey;
    protected Model model;
    protected int maxTokens;

    public DeepSeekHelper(String apiUrl, String apiKey, Model model, int maxTokens, String cueWord) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;

        requestTemplateJson.addProperty("model", this.model.getValue());

        this.systemCueWord.addProperty("role", "system");
        this.systemCueWord.addProperty("content", cueWord);
    }

    public void addMsg(Role role, String content) {
        JsonObject tmpMagJson = new JsonObject();
        tmpMagJson.addProperty("role", role.getValue());
        tmpMagJson.addProperty("content", content);
        messageConText.add(tmpMagJson);
    }

    public JsonObject request() {
        JsonObject requestJson = requestTemplateJson.deepCopy();
        JsonArray dialogueMessage = new JsonArray();

        dialogueMessage.add(systemCueWord);
        dialogueMessage.addAll(messageConText);

        requestJson.add("messages", dialogueMessage);
        requestJson.addProperty("max_tokens", maxTokens);
        requestJson.addProperty("top_p", 0.2);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestJson.toString());
        Request request = new Request.Builder()
                .url(apiUrl)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            if (response.body() == null) {
                return null;
            }
            JsonObject responseJson = this.gson.fromJson(response.body().charStream(), JsonObject.class);
            return responseJson.get("choices").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject();
        } catch (IOException ignored) {
        }


    return null;
    }

}
