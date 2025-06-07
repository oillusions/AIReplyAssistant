package cn.oillusions.mcmods.aira.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import cn.oillusions.mcmods.aira.Aira;
import cn.oillusions.mcmods.aira.client.config.PresetManager;
import cn.oillusions.mcmods.aira.deepseek.DeepSeekConfig;
import cn.oillusions.mcmods.aira.deepseek.DeepSeekContext;
import cn.oillusions.mcmods.aira.deepseek.Model;
import cn.oillusions.mcmods.aira.deepseek.ResultFormat;

public class DeepSeek {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeekContext deepSeek;
    private JsonArray replyCandidate;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonObject airaConfig;
    private final JsonObject style;
    private final String cueWord;
    private boolean complete = true;

    public DeepSeek() {
        airaConfig = Aira.AIRA_CONFIG.getConfig().deepCopy();
        style = PresetManager.getStyle(airaConfig.get("current").getAsString()).getConfig().deepCopy();
        cueWord = PresetManager.getFiller().filler(PresetManager.getCueWord(airaConfig.get("current").getAsString()).getConfig());

        DeepSeekConfig deepSeekConfig = new DeepSeekConfig.Builder()
                .requestMode(true)
                .apiUrl(airaConfig.get("api_url").getAsString())
                .apiKey(airaConfig.get("api_key").getAsString())
                .model(Model.CHAT)
                .format(ResultFormat.JSON_OBJECT)
                .stream(false)

                .maxTokens(style.get("max_tokens").getAsInt())
                .topP(style.get("top_p").getAsFloat())
                .presencePenalty(style.get("presence_penalty").getAsFloat())
                .frequencyPenalty(style.get("frequency_penalty").getAsFloat())
                .systemPrompt(cueWord)
                .build();
        deepSeek = new DeepSeekContext(deepSeekConfig);
        deepSeek.addListener((response -> {
            try {
                complete = true;
                JsonArray reply = gson.fromJson(response.getContent(), JsonObject.class).getAsJsonArray("responses");
                System.out.println(gson.toJson(reply));
                replyCandidate = reply;
                if (AiraClient.getInstance().isAutoReply()) {
                    if (client.player != null) {
                        client.player.networkHandler.sendChatMessage(reply.get(0).getAsString());
                    }
                }
            } catch (Exception e) {

            }
        }));

        replyCandidate = new JsonArray();
        replyCandidate.add("空");
    }

    public void onReceiveMessage(String messageContent, String name, GameRole gameRole) {
        addMsg(gameRole, name, messageContent);
    }

    public void onTriggerGen() {
        if (complete) {
            System.out.println("触发生成");
            complete = false;
            deepSeek.request();

            replyCandidate = new JsonArray();
            replyCandidate.add("生成中");
        } else {
            System.out.println("当前请求尚未结束");
        }
    }



    public void addMsg(GameRole gameRole, String name, String messageContent) {
        switch (gameRole) {
            case PLAYER, THIS -> {
                deepSeek.addMessage(gameRole.getDsRole(), name, messageContent);
            }
            case SYSTEM -> {
                deepSeek.addMessage(gameRole.getDsRole(), gameRole.getGameRole(), messageContent);
            }
        }
    }

    public void setReplyCandidate(JsonArray replyCandidate) {
        this.replyCandidate = replyCandidate;
    }

    public JsonArray getReplyCandidate() {
        return replyCandidate;
    }

    public JsonObject getStyle() {
        return style;
    }

    public String getCueWord() {
        return cueWord;
    }

    public boolean isComplete() {
        return complete;
    }
}
