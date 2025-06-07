package cn.oillusions.mcmods.aira.client;

import com.google.gson.JsonArray;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeyTriggerListener {

    private KeyBinding tabKey;
    private final KeyBinding[] numberKeys = new KeyBinding[6];

    private long lastTabPressTime = 0;
    private static long DOUBLE_CLICK_THRESHOLD = 250;

    private boolean tabPressed = false;


    public KeyTriggerListener() {
        this.tabKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "aira.key.key_trigger.tab",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                "aira.category.key_trigger"
        ));
        for (int i = 0; i < 6; i++) {
            int keyCode = GLFW.GLFW_KEY_1 + i;
            numberKeys[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "aira.key.key_trigger.num" + (i + 1),
                    InputUtil.Type.KEYSYM,
                    keyCode,
                    "aira.category.key_trigger"
            ));
        }

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    public void onClientTick(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        if (tabKey.wasPressed()) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - this.lastTabPressTime < DOUBLE_CLICK_THRESHOLD) {
                AiraClient.getInstance().getDeepSeek().onTriggerGen();
            }
            lastTabPressTime = currentTime;
            tabPressed = true;
        }

        if (tabPressed && !tabKey.isPressed()) {
            tabPressed = false;
        }

        if (tabPressed) {
            for (int i = 0; i < 6; i++) {
                if (numberKeys[i].wasPressed()) {
                    JsonArray reply = AiraClient.getInstance().getDeepSeek().getReplyCandidate();
                    if (reply != null && reply.size() != 1 && reply.size() > i) {
                        client.player.networkHandler.sendChatMessage(reply.get(i).getAsString());
                        JsonArray tmp = new JsonArray();
                        tmp.add("空");
                        AiraClient.getInstance().getDeepSeek().setReplyCandidate(tmp);
                    } else {
                        client.player.sendMessage(Text.literal("还没有生成好呢!"));
                    }
                }
            }
        }
    }
}
