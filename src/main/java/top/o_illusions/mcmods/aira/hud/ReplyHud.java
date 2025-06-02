package top.o_illusions.mcmods.aira.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class ReplyHud {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public ReplyHud() {
        HudRenderCallback.EVENT.register(this::render);
    }


    public void render(DrawContext context, RenderTickCounter tickCounter) {
        context.drawText(client.textRenderer, "测试", 0, 0, 0xABD3FFFF, false);
    }
}
