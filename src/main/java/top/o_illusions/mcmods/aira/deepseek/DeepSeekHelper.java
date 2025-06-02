package top.o_illusions.mcmods.aira.deepseek;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeepSeekHelper {
    protected  JsonObject requestTemplateJson = new JsonObject();
    protected JsonArray messageConText = new JsonArray();
    protected JsonObject systemCueWord = new JsonObject();
    protected final Gson gson = new Gson();
    protected final HttpClient httpClient = HttpClient.newHttpClient();
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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .method("POST", HttpRequest.BodyPublishers.ofString(gson.toJson(requestJson)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
            JsonObject responseJson = this.gson.fromJson(response.body(), JsonObject.class);
            return responseJson.get("choices").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
