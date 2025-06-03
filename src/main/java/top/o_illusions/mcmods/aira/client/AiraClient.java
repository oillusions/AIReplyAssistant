package top.o_illusions.mcmods.aira.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.mixin.client.message.MessageHandlerMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.TranslatableTextContent;
import top.o_illusions.mcmods.aira.hud.ReplyHud;

public class AiraClient implements ClientModInitializer {
    private static AiraClient instance;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeek deepSeek = new DeepSeek();
    private final ReplyHud replyHud = new ReplyHud();
    private boolean autoReply = false;
    private final KeyTriggerListener triggerListener = new KeyTriggerListener();


    public static AiraClient getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        ClientReceiveMessageEvents.CHAT.register(((text, signedMessage, gameProfile, parameters, instant) -> {
            Object[] mutableText = ((TranslatableTextContent) text.getContent()).getArgs();
            String name = ((PlainTextContent.Literal) ((MutableText) mutableText[0]).getContent()).string();
            String msgContent = ((PlainTextContent.Literal) ((MutableText) mutableText[1]).getContent()).string();
            deepSeek.onReceiveMessage(msgContent, name, null);
        }));

        ClientReceiveMessageEvents.GAME.register(((text, b) -> {
            deepSeek.onReceiveMessage(text.getString(), null, GameRole.SYSTEM);
        }));

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
