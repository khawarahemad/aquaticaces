package com.aquaticaces.mixin;

import com.aquaticaces.ui.BrandedUi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class MixinLoadingOverlay {

    @Shadow @Final private Minecraft minecraft;
    @Shadow private float currentProgress;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void aquaticaces$brandedSplash(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        String version = net.fabricmc.loader.api.FabricLoader.getInstance()
            .getModContainer("aquaticaces")
            .map(c -> c.getMetadata().getVersion().getFriendlyString())
            .orElse("dev");

        BrandedUi.INSTANCE.drawLoadingScreen(guiGraphics, minecraft.font, width, height, currentProgress, version);
        ci.cancel();
    }
}
