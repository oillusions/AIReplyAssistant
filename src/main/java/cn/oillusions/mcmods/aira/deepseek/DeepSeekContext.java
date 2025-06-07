package cn.oillusions.mcmods.aira.deepseek;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DeepSeekContext {
    private final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
    private final DeepSeekConfig config;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private JsonArray messageContext = new JsonArray();
    private StringBuilder contentStreamBuffer;
    private StringBuilder reasoningStreamBuffer;

    private final List<Consumer<DeepSeekResponse>> listeners = new ArrayList<>();


    public DeepSeekContext(DeepSeekConfig config) {
        this.config = config;
    }

    public void addMessage(Role role, String content) {
        addMessage(role, null, content);
    }

    public void addMessage(Role role, String name, String content) {
        JsonObject message = new JsonObject();

        message.addProperty("content", content);
        message.addProperty("role", role.getValue());
        if (name != null && !name.isBlank()) {
            message.addProperty("name", name);
        }

        messageContext.add(message);
    }

    protected JsonObject buildRequestBody() {
        JsonObject requestBody = new JsonObject();
        JsonObject systemMessage = new JsonObject();
        JsonObject resultFormat = new JsonObject();
        JsonArray messages = new JsonArray();


        systemMessage.addProperty("content", config.getSystemPrompt());
        systemMessage.addProperty("role", Role.SYSTEM.getValue());

        messages.add(systemMessage);
        messages.addAll(messageContext);

        resultFormat.addProperty("type", config.getFormat().getValue());

        requestBody.addProperty("model", config.getModel().getValue());
        requestBody.addProperty("stream", config.isStream());
        requestBody.add("response_format", resultFormat);

        requestBody.addProperty("max_tokens", config.getMaxTokens());
        requestBody.addProperty("top_p", config.getTopP());
        requestBody.addProperty("presence_penalty", config.getPresencePenalty());
        requestBody.addProperty("frequency_penalty", config.getFrequencyPenalty());

        requestBody.add("messages", messages);

        return requestBody;
    }

    protected HttpRequest buildRequest(JsonObject requestBody) {

        return HttpRequest.newBuilder()
                .uri(URI.create(config.getApiUrl()))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .method("POST", HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .build();
    }

    public void request() {
        HttpRequest request = buildRequest(buildRequestBody());
        contentStreamBuffer = new StringBuilder();
        reasoningStreamBuffer = new StringBuilder();
//        System.out.println(gson.toJson(gson.fromJson(buildRequestBody(), JsonObject.class)));

        if (config.isRequestMode()) {
            if (config.isStream()) {
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                        .thenAccept(response -> {
                            try (InputStream stream = response.body()) {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                                String line;
                                JsonObject prevResponse = new JsonObject();
                                while ((line = reader.readLine()) != null) {
                                    if (line.startsWith("data: ") && !line.startsWith("data: [DONE]")) {
                                        DeepSeekStreamResponse deepSeekResponse = new DeepSeekStreamResponse(gson.fromJson(line.substring(6), JsonObject.class), response.statusCode());
                                        if (deepSeekResponse.isReasoning()) {
                                            reasoningStreamBuffer.append(deepSeekResponse.getReasoningContent());
                                        } else {
                                            contentStreamBuffer.append(deepSeekResponse.getContent());
                                        }
                                        deepSeekResponse.extractDelta().addProperty("reasoning_content", reasoningStreamBuffer.toString());
                                        deepSeekResponse.extractDelta().addProperty("content", contentStreamBuffer.toString());
                                        notifyListener(new DeepSeekStreamResponse(deepSeekResponse.getRewResponse(), response.statusCode()));

                                        prevResponse = deepSeekResponse.getRewResponse().deepCopy();
                                    } else if (line.startsWith("data: [DONE]")){
                                        notifyListener(new DeepSeekNonStreamResponse(prevResponse, response.statusCode()));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            } else {
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            notifyListener(new DeepSeekNonStreamResponse(gson.fromJson(response.body(), JsonObject.class), response.statusCode()));
                        });
            }
        } else {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                notifyListener(new DeepSeekNonStreamResponse(gson.fromJson(response.body(), JsonObject.class), response.statusCode()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void addListener(Consumer<DeepSeekResponse> listener) {
        listeners.add(listener);
    }

    private void notifyListener(DeepSeekResponse response) {
        for (Consumer<DeepSeekResponse> listener : listeners) {
            listener.accept(response);
        }
    }

    public void resetConversation() {
        messageContext = new JsonArray();
    }

    public DeepSeekConfig getConfig() {
        return config;
    }

    public JsonArray getMessageContext() {
        return messageContext;
    }
}
