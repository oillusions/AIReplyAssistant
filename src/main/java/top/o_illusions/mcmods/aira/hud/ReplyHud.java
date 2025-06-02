package top.o_illusions.mcmods.aira.hud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import top.o_illusions.mcmods.aira.client.AiraClient;

public class ReplyHud {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public ReplyHud() {
        HudRenderCallback.EVENT.register(this::render);
    }

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        JsonArray reply = AiraClient.getInstance().getDeepSeek().getReplyCandidate();
        int y = client.textRenderer.fontHeight;
        context.drawText(client.textRenderer, "测试", 0, 0, 0xABD3FFFF, false);
        for (JsonElement text : reply) {
            context.drawText(client.textRenderer, text.getAsString(), 0, y, 0xABD3FFFF, false);
            y += client.textRenderer.fontHeight;
        }

    }
}
