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

    /** Packed block/sky light value for full brightness (15, 15). */
    private static int fullLightPixel() {
        return (15 << 4) | (15 << 20);
    }

    @Inject(
        method = "updateLightTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"
        )
    )
    private void aquaticaces$xrayFullBright(float partialTicks, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        if (!XRay.useFullBright()) return;

        if (lightPixels != null) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    lightPixels.setPixelRGBA(x, y, 0xFFFFFFFF);
                }
            }
        }
    }
}
