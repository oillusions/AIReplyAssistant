package top.o_illusions.mcmods.aira.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import top.o_illusions.mcmods.aira.hud.ReplyHud;

public class AiraClient implements ClientModInitializer {
    private static AiraClient instance;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeek deepSeek = new DeepSeek();
    private final ReplyHud replyHud = new ReplyHud();
    private final KeyTriggerListener triggerListener = new KeyTriggerListener();

    public static AiraClient getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        ClientReceiveMessageEvents.GAME.register((message, test) -> {
            deepSeek.onReceiveMessage(message.getString(), null);
        });
        ClientReceiveMessageEvents.CHAT.register(((text, signedMessage, gameProfile, parameters, instant) -> {
                    deepSeek.onReceiveMessage(text.getString(), gameProfile);

        }));

    }

    public DeepSeek getDeepSeek() {
        return deepSeek;
    }

    public ReplyHud getReplyHud() {
        return replyHud;
    }
}
