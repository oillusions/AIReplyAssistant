package top.o_illusions.mcmods.aira.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import top.o_illusions.mcmods.aira.deepseek.DeepSeekHelper;
import top.o_illusions.mcmods.aira.deepseek.Model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class DeepSeek {
    private final Path configPath = FabricLoader.getInstance().getConfigDir();
    private final Path airaConfigDir = configPath.resolve("aira");
    private final Path airaConfigFile = airaConfigDir.resolve("aira.json");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeekHelper deepSeek;
    private JsonArray replyCandidate;
    private Thread current = null;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonObject deepSeekConfig;
    private final JsonObject defaultDeepSeekStyle;

    public DeepSeek() {
        this.deepSeekConfig = readConfig();
        this.defaultDeepSeekStyle = readConfig().get("styles").getAsJsonArray()
                .get(0).getAsJsonObject();

        String defaultCueWord =
                "回复请使用JsonArray的格式,如下示例: ['你好!', '你好哦', '你好你好!'] " +
                "玩家信息: [id:%s],游戏信息:[version:%s]".formatted(client.getGameProfile().getName(), client.getGameVersion());

        this.deepSeek = new DeepSeekHelper(
                deepSeekConfig.get("api_url").getAsString(),
                deepSeekConfig.get("api_key").getAsString(),
                Model.CHAT,
                defaultDeepSeekStyle.get("max_tokens").getAsInt(),
                defaultDeepSeekStyle.get("cue").getAsString() + defaultCueWord);

        deepSeek.setTop_p(defaultDeepSeekStyle.get("top_p").getAsFloat());
        deepSeek.setPresencePenalty(defaultDeepSeekStyle.get("presence_penalty").getAsFloat());
        deepSeek.setFrequencyPenalty(defaultDeepSeekStyle.get("frequency_penalty").getAsFloat());

        System.out.println("加载DeepSeek配置: %s".formatted(this.gson.toJson(defaultDeepSeekStyle)));

        replyCandidate = new JsonArray();
        replyCandidate.add("空");
    }

    private JsonObject readConfig() {
        if (!Files.exists(airaConfigDir)) {
            try {
                System.out.println("创建目录中");
                JsonObject defaultConfigFile = new JsonObject();
                defaultConfigFile.addProperty("api_url", "https://api.deepseek.com/chat/completions");
                defaultConfigFile.addProperty("api_key", "sk-xxx");
                JsonArray defaultStyles = new JsonArray();
                JsonObject defaultStyle = new JsonObject();
                defaultStyle.addProperty("cue", "你好DeepSeek! 我希望你现在作为我在Minecraft的对话助手[你可以自称为我], 在你接收到消息[其他玩家或系统发送的]后, 帮助我生成2-6个从幽默到正常的回复,");
                defaultStyle.addProperty("max_tokens", 100);
                defaultStyle.addProperty("top_p", 0.2);
                defaultStyle.addProperty("presence_penalty", 0.2);
                defaultStyle.addProperty("frequency_penalty", 0.2);
                defaultStyles.add(defaultStyle);
                defaultConfigFile.add("styles", defaultStyles);
                Files.createDirectories(airaConfigDir);
                Files.write(airaConfigFile, this.gson.toJson(defaultConfigFile).getBytes());
                return readConfig();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        try {
            return this.gson.fromJson(Files.readString(airaConfigFile), JsonObject.class);
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }


    public void onReceiveMessage(String messageContent, GameProfile gameProfile) {
        if (gameProfile == null) {
            addMsg(GameRole.SYSTEM, null, messageContent);
        } else {
            if (client.player != null && Objects.equals(gameProfile.getName(), client.player.getName().getString())) {
                addMsg(GameRole.THIS, client.getGameProfile().getName(), messageContent);
            } else {
                addMsg(GameRole.PLAYER, gameProfile.getName(), messageContent);
                if (AiraClient.getInstance().isAutoReply()) {
                    this.onTriggerGen();
                }
            }
        }
    }

    public void onTriggerGen() {
        if (this.current == null || !this.current.isAlive()) {
            System.out.println("触发生成");
            this.current = new Thread(() -> {
                JsonArray tmp = new JsonArray();
                tmp.add("生成中");
                AiraClient.getInstance().getDeepSeek().setReplyCandidate(tmp);
                JsonObject response = this.deepSeek.request();
                System.out.println(response.get("content").getAsString());
                tmp = this.gson.fromJson(response.get("content").getAsString(), JsonArray.class);
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
            case PLAYER -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), name, messageContent);
                break;
            }
            case THIS -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), name, messageContent);
                break;
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

    public JsonObject getDeepSeekConfig() {
        return deepSeekConfig.deepCopy();
    }

    public JsonObject getDeepSeekStyle() {
        return defaultDeepSeekStyle.deepCopy();
    }
}
