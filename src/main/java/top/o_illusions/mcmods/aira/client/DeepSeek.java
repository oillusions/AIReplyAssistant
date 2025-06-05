package top.o_illusions.mcmods.aira.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import top.o_illusions.mcmods.aira.Aira;
import top.o_illusions.mcmods.aira.client.config.PresetManager;
import top.o_illusions.mcmods.aira.deepseek.DeepSeekHelper;
import top.o_illusions.mcmods.aira.deepseek.Model;

public class DeepSeek {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeekHelper deepSeek;
    private JsonArray replyCandidate;
    private Thread current = null;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonObject airaConfig;
    private final JsonObject style;
    private String cueWord;

    public DeepSeek() {
        airaConfig = Aira.AIRA_CONFIG.getConfig().deepCopy();
        style = PresetManager.getStyle("default").getConfig().deepCopy();
        cueWord = PresetManager.getCueWord("default").getConfig();

        deepSeek = new DeepSeekHelper(
                airaConfig.get("api_url").getAsString(),
                airaConfig.get("api_key").getAsString(),
                Model.CHAT,
                style.get("max_tokens").getAsInt(),
                cueWord
        );
        deepSeek.setTop_p(style.get("top_p").getAsFloat());
        deepSeek.setPresencePenalty(style.get("presence_penalty").getAsFloat());
        deepSeek.setFrequencyPenalty(style.get("frequency_penalty").getAsFloat());

        System.out.println("身份预设: " + gson.toJson(style) + ", 提示词预设: " + cueWord);

        replyCandidate = new JsonArray();
        replyCandidate.add("空");
    }

    public void onReceiveMessage(String messageContent, String name, GameRole gameRole) {
        this.addMsg(gameRole, name, messageContent);
    }

    public void onTriggerGen() {
        if (this.current == null || !this.current.isAlive()) {
            System.out.println("触发生成");
            this.current = new Thread(() -> {
                JsonArray tmp = new JsonArray();
                tmp.add("生成中");
                AiraClient.getInstance().getDeepSeek().setReplyCandidate(tmp);
                String response = this.deepSeek.request();
                System.out.println(response);
                tmp = this.gson.fromJson(response, JsonArray.class);
                if (MinecraftClient.getInstance().player != null) {
                    AiraClient.getInstance().getDeepSeek().setReplyCandidate(tmp);
                }
                if (AiraClient.getInstance().isAutoReply()) {
                    client.player.networkHandler.sendChatMessage(tmp.get(0).getAsString());
                }
            });
            this.current.start();
        } else {
            System.out.println("当前请求尚未结束");
        }
    }



    public void addMsg(GameRole gameRole, String name, String messageContent) {
        switch (gameRole) {
            case PLAYER, THIS -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), name, messageContent);
            }
            case SYSTEM -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), gameRole.getGameRole(), messageContent);
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
}
