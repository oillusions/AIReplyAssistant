package top.o_illusions.mcmods.aira.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import kotlinx.serialization.json.Json;
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
    private final JsonObject deepSeekConfig;
    private final JsonObject defaultDeepSeekStyle;

    public DeepSeek() {
        deepSeekConfig = readConfig();
        defaultDeepSeekStyle = readConfig().get("styles").getAsJsonArray()
                .get(0).getAsJsonObject();

        String defaultCueWord =
                "直接返回其内容," +
                "回复已JsonArray的格式[直接返回文本就行， 不需要富文本格式]" +
                "长度不得超过20字" +
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

        System.out.println("加载DeepSeek配置: %s".formatted(new Gson().toJson(defaultDeepSeekStyle)));

        replyCandidate = new JsonArray();
        replyCandidate.add("空");
    };

    private JsonObject readConfig() {
        if (!Files.exists(airaConfigDir)) {
            try {
                System.out.println("创建目录中");
                JsonObject defaultConfigFile = new JsonObject();
                defaultConfigFile.addProperty("api_url", "https://api.deepseek.com/chat/completions");
                defaultConfigFile.addProperty("api_key", "sk-xxx");
                JsonArray defaultStyles = new JsonArray();
                JsonObject defaultStyle = new JsonObject();
                defaultStyle.addProperty("cueword", "你好DeepSeek! 我希望你现在作为我在Minecraft的对话助手[你可以自称为我], 在你接收到消息[其他玩家或系统发送的]后, 帮助我生成2-6个从幽默到正常的回复,");
                defaultStyle.addProperty("max_tokens", 100);
                defaultStyle.addProperty("top_p", 0.2);
                defaultStyle.addProperty("presence_penalty", 0.2);
                defaultStyle.addProperty("frequency_penalty", 0.2);
                defaultStyles.add(defaultStyle);
                defaultConfigFile.add("styles", defaultStyles);
                Files.createDirectories(airaConfigDir);
                Files.write(airaConfigFile, new Gson().toJson(defaultConfigFile).getBytes());
                return readConfig();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        try {
            return new Gson().fromJson(Files.readString(airaConfigFile), JsonObject.class);
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
                addMsg(GameRole.THIS, null, messageContent);
            } else {
                addMsg(GameRole.PLAYER, gameProfile.getName(), messageContent);
            }
        }
    }

    private static class DeepSeekRequest implements Runnable {
        private final DeepSeekHelper deepSeekHelper;

        public DeepSeekRequest(DeepSeekHelper deepSeekHelper) {
            this.deepSeekHelper = deepSeekHelper;
        }

        @Override
        public void run() {
            JsonArray tmp = new JsonArray();
            tmp.add("生成中");
            AiraClient.getInstance().getDeepSeek().setReplyCandidate(tmp);
            JsonObject response = deepSeekHelper.request();
            tmp = new Gson().fromJson(response.get("content").getAsString(), JsonArray.class);
            if (MinecraftClient.getInstance().player != null) {
                AiraClient.getInstance().getDeepSeek().setReplyCandidate(tmp);
            }
        }
    }

    public void onTriggerGen() {
        System.out.println("触发生成");
        DeepSeekRequest request = new DeepSeekRequest(this.deepSeek);
        Thread thread = new Thread(request);
        thread.start();

    }



    public void addMsg(GameRole gameRole, String Name, String messageContent) {
        switch (gameRole) {
            case PLAYER -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), gameRole.getGameRole() + '[' + Name + ']' + "说:" + messageContent);
                break;
            }
            case THIS -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), gameRole.getGameRole() + "说:" + messageContent);
                break;
            }
            case SYSTEM -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), gameRole.getGameRole() + ": " + messageContent);
            }
        }
    }

    public void setReplyCandidate(JsonArray replyCandidate) {
        this.replyCandidate = replyCandidate;
    }

    public JsonArray getReplyCandidate() {
        return replyCandidate;
    }
}
