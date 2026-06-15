package com.aquaticaces.mixin;

import com.aquaticaces.AquaticAces;
import com.aquaticaces.event.impl.EventKeyInput;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboard {

    @Inject(method = "keyPress(JIIII)V", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        EventKeyInput event = new EventKeyInput(key, scancode, action, modifiers);
        AquaticAces.INSTANCE.getEventBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
