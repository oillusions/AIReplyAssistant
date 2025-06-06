package top.o_illusions.mcmods.aira.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import top.o_illusions.mcmods.aira.client.config.PresetManager;
import top.o_illusions.mcmods.aira.hud.ReplyHud;
import top.o_illusions.mcmods.aira.util.TextFiller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiraClient implements ClientModInitializer {
    private static AiraClient instance;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeek deepSeek;
    private final ReplyHud replyHud = new ReplyHud();
    private boolean autoReply = false;
    private final KeyTriggerListener triggerListener = new KeyTriggerListener();

    public AiraClient() {
        initFillerKey();
        deepSeek = new DeepSeek();
    }

    public static Map<String, String> extractNameAndMessage(String input) {
        Map<String, String> result = new HashMap<>();

        if (input == null || input.trim().isEmpty()) {
            result.put("error", "输入不能为空");
            return result;
        }

        // 匹配两种格式：<Name> Message 或 [Name] Message
        Pattern pattern = Pattern.compile("^(?:\\s*<([^>]+)>\\s*|\\s*\\[([^]]+)]\\s*)(.*)$");
        Matcher matcher = pattern.matcher(input.trim());

        if (!matcher.matches()) {
            result.put("error", "无效的消息格式");
            return result;
        }

        // 提取名称（来自<>或[]）
        String name = matcher.group(1) != null ? matcher.group(1).trim() :
                matcher.group(2) != null ? matcher.group(2).trim() : "";

        if (name.isEmpty()) {
            result.put("error", "名称不能为空");
            return result;
        }

        // 提取消息内容（保留所有空格和符号）
        String message = matcher.group(3) != null ? matcher.group(3) : "";

        result.put("name", name);
        result.put("message", message);
        return result;
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        ClientReceiveMessageEvents.CHAT.register(((text, signedMessage, gameProfile, parameters, instant) -> {
            Map<String, String> message = extractNameAndMessage(text.getString());
            if (message.size() == 1) {
                return;
            }
            String name = message.get("name");
            String messageStr = message.get("message");

            if (name.equals(client.getGameProfile().getName())) {
                deepSeek.addMsg(GameRole.THIS, name, messageStr);
            } else if ("Server".equals(name)) {
                deepSeek.addMsg(GameRole.SYSTEM, "Server", messageStr);
            } else {
                deepSeek.addMsg(GameRole.PLAYER, name, messageStr);
                if (this.isAutoReply()) {
                    deepSeek.onTriggerGen();
                }
            }
        }));

        ClientReceiveMessageEvents.GAME.register(((text, b) -> {
            deepSeek.onReceiveMessage(text.getString(), null, GameRole.SYSTEM);
        }));

    }

    private void initFillerKey() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        TextFiller filler = PresetManager.getFiller();

        filler.register("playerName", client.getGameProfile()::getName);
        filler.register("currentTime", () ->timeFormatter.format((LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()))));
        filler.register("gameVersion", FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion()::getFriendlyString);

    }

    public static AiraClient getInstance() {
        return instance;
    }

    public DeepSeek getDeepSeek() {
        return deepSeek;
    }

    public ReplyHud getReplyHud() {
        return replyHud;
    }

    public void setAutoReply(boolean autoReply) {
        this.autoReply = autoReply;
    }

    public boolean isAutoReply() {
        return autoReply;
    }
}
