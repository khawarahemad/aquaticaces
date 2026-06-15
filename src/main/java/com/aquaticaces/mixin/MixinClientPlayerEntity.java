package com.aquaticaces.mixin;

import com.aquaticaces.AquaticAces;
import com.aquaticaces.event.impl.EventChatInput;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPlayerEntity {

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void onSendChat(String message, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        EventChatInput event = new EventChatInput(message);
        AquaticAces.INSTANCE.getEventBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
