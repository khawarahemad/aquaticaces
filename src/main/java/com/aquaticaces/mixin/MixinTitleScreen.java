package com.aquaticaces.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    /**
     * Replace the vanilla title screen after it finishes init.
     * Cancelling init early leaves the panorama visible with no buttons.
     */
    @Inject(method = "init", at = @At("RETURN"))
    private void onInitReturn(CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> mc.setScreen(new com.aquaticaces.ui.BrandedSplashScreen()));
    }
}
