package com.aquaticaces.mixin;

import com.aquaticaces.ui.BrandedUi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class MixinLoadingOverlay {

    private static final ResourceLocation MOJANG_LOGO =
        ResourceLocation.withDefaultNamespace("textures/gui/title/mojangstudios.png");

    @Shadow @Final private Minecraft minecraft;
    @Shadow private float currentProgress;

    /** Replace vanilla progress bar + status text with our branded fake loading UI. */
    @Inject(method = "drawProgressBar", at = @At("HEAD"), cancellable = true)
    private void aquaticaces$brandedProgress(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, float alpha, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        BrandedUi.INSTANCE.drawRealLoadingSplash(guiGraphics, width, height, alpha);
        ci.cancel();
    }

    /** Hide the Mojang logo — our branded splash replaces it. */
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIFFIIII)V"
        )
    )
    private void aquaticaces$skipMojangLogo(
        GuiGraphics graphics,
        ResourceLocation texture,
        int x, int y, int blitWidth, int blitHeight,
        float u, float v, int textureWidth, int textureHeight,
        int sourceWidth, int sourceHeight
    ) {
        if (!com.aquaticaces.module.impl.ghost.SelfDestruct.destructed && texture.equals(MOJANG_LOGO)) {
            return;
        }
        graphics.blit(texture, x, y, blitWidth, blitHeight, u, v, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }
}
