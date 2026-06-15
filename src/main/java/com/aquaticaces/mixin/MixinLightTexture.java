package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.XRay;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class MixinLightTexture {

    @Shadow
    private DynamicTexture lightTexture;

    @Shadow
    private NativeImage lightPixels;

    @Inject(method = "updateLightTexture", at = @At("HEAD"), cancellable = true)
    private void onUpdateLightTexture(float partialTicks, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        if (XRay.isXrayEnabled()) {
            if (lightPixels != null) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        lightPixels.setPixelRGBA(x, y, 0xFFFFFFFF);
                    }
                }
                lightTexture.upload();
                ci.cancel();
            }
        }
    }
}
