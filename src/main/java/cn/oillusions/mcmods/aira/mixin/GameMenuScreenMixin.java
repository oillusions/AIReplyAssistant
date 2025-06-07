package cn.oillusions.mcmods.aira.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import cn.oillusions.mcmods.aira.client.AiraClient;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        ButtonWidget config = this.addDrawableChild(
                new ButtonWidget.Builder(Text.literal("自动回复:" + String.valueOf(AiraClient.getInstance().isAutoReply())),
                        (button) -> {
                            AiraClient.getInstance().setAutoReply(!AiraClient.getInstance().isAutoReply());
                            System.out.println("自动回复: " + String.valueOf(AiraClient.getInstance().isAutoReply()));
                            client.setScreen(null);
                        }).build());

        config.setWidth(textRenderer.getWidth("自动回复")+ 6);
    }
}
