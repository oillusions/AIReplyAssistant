package top.o_illusions.mcmods.aira.deepseek;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    protected float top_p;
    protected float presence_penalty;
    protected float frequency_penalty;
    protected int maxTokens;



    public DeepSeekHelper(String apiUrl, String apiKey, Model model, int maxTokens, String cueWord) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;

        this.top_p = 0.2f;
        this.presence_penalty = 0.5f;
        this.frequency_penalty = 0.5f;


        requestTemplateJson.addProperty("model", this.model.getValue());

        this.systemCueWord.addProperty("role", "system");
        this.systemCueWord.addProperty("content", cueWord);
    }

    public void addMsg(Role role, String name, String content) {
        JsonObject tmpMagJson = new JsonObject();
        tmpMagJson.addProperty("content", content);
        tmpMagJson.addProperty("role", role.getValue());
        tmpMagJson.addProperty("name", name);

        messageConText.add(tmpMagJson);
    }

    public JsonObject requestToBody() {
        JsonObject requestJson = requestTemplateJson.deepCopy();
        JsonArray dialogueMessage = new JsonArray();

        dialogueMessage.add(systemCueWord);
        dialogueMessage.addAll(messageConText);

        requestJson.add("messages", dialogueMessage);
        requestJson.addProperty("max_tokens", maxTokens);
        requestJson.addProperty("top_p", top_p);
        requestJson.addProperty("presence_penalty", presence_penalty);
        requestJson.addProperty("frequency_penalty", frequency_penalty);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .method("POST", HttpRequest.BodyPublishers.ofString(gson.toJson(requestJson)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
                case 200 -> {
                    return this.gson.fromJson(response.body(), JsonObject.class);
                }
                case 400 -> {
                    //请求体格式错误
                    System.err.println("请求体格式错误");
                    break;
                }
                case 401 -> {
                    //kay错误错误
                    System.err.println("key错误");
                }
                case 402 -> {
                    //余额不足
                    System.err.println("余额不足");
                }
                case 422 -> {
                    //请求体参数错误
                    System.err.println("请求体参数错误");
                }
                case 429 -> {
                    //请求速率上限
                    System.err.println("速率上限");
                }
                case 500 -> {
                    //服务器内部故障
                }
                case 503 -> {
                    //服务器繁忙
                }
            };
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public String request() {
        return requestToBody()
                .get("choices").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("message").getAsJsonObject()
                .get("content").getAsString();
    }

    public void setTop_p(float top_p) {
        this.top_p = top_p;
    }

    public void setFrequencyPenalty(float frequency_penalty) {
        this.frequency_penalty = frequency_penalty;
    }

    public void setPresencePenalty(float presence_penalty) {
        this.presence_penalty = presence_penalty;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public float getTop_p() {
        return top_p;
    }

    public float getFrequencyPenalty() {
        return frequency_penalty;
    }

    public float getPresencePenalty() {
        return presence_penalty;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setSystemCueWord(JsonObject systemCueWord) {
        this.systemCueWord = systemCueWord;
    }

    public JsonObject getSystemCueWord() {
        return systemCueWord;
    }

    public JsonArray getMessageConText() {
        return messageConText;
    }

    public void setMessageConText(JsonArray messageConText) {
        this.messageConText = messageConText;
    }
}
