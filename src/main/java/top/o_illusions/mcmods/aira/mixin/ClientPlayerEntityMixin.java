package top.o_illusions.mcmods.aira.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"))
    private  void onSendChatMessage(String content, CallbackInfo ci) {
        System.out.println(content);
    }
}
