package cn.oillusions.mcmods.aira.hud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import cn.oillusions.mcmods.aira.client.AiraClient;

public class ReplyHud {
    private final MinecraftClient client = MinecraftClient.getInstance();
    public ReplyHud() {
        HudRenderCallback.EVENT.register(this::render);
    }

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        TextRenderer textRenderer = client.textRenderer;
        JsonArray reply = AiraClient.getInstance().getDeepSeek().getReplyCandidate();
        int y = textRenderer.fontHeight + 2;
        context.drawText(textRenderer, "测试", 0, 0, 0xFFFFFFFF, false);
        for (JsonElement text : reply) {
            context.drawText(textRenderer, text.getAsString(), 0, y, 0xFFFFFFFF, false);
            y += textRenderer.fontHeight;
        }

    }
}
