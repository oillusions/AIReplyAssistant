package top.o_illusions.mcmods.aira.client;

import kotlin.jvm.internal.Intrinsics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import top.o_illusions.mcmods.aira.hud.ReplyHud;

public class AiraClient implements ClientModInitializer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeek deepSeek = new DeepSeek();
    private final ReplyHud replyHud = new ReplyHud();

    @Override
    public void onInitializeClient() {
        Intrinsics.class.getName();
        ClientReceiveMessageEvents.GAME.register((message, test) -> {
            client.player.sendMessage(message);
            client.player.sendMessage(Text.literal(String.valueOf(test)));
        });
        ClientReceiveMessageEvents.CHAT.register(((text, signedMessage, gameProfile, parameters, instant) -> {
                    deepSeek.onReceiveMessage(text.getString(), gameProfile);

        }));

    }
}
