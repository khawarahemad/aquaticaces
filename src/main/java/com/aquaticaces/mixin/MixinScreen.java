package com.aquaticaces.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables Minecraft's built-in Gaussian blur that is applied to the world
 * behind every open screen. Keeps menus crisp and the world clearly visible.
 */
@Mixin(Screen.class)
public class MixinScreen {

    @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true, require = 0)
    private void aquaticaces$noBlur(float partialTick, CallbackInfo ci) {
        ci.cancel();
    }
}
