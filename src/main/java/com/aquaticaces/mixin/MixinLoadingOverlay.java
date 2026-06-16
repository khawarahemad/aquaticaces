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

/**
 * Replaces the vanilla Mojang loading screen with the Aquatic Aces splash by
 * painting a full branded overlay on top after vanilla renders. We do NOT
 * cancel the vanilla render so the reload completion / fade-out logic still
 * runs and the main menu opens normally.
 */
@Mixin(LoadingOverlay.class)
public class MixinLoadingOverlay {

    @Shadow @Final private Minecraft minecraft;
    @Shadow private float currentProgress;

    @Inject(method = "render", at = @At("TAIL"))
    private void aquaticaces$brandedSplash(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        BrandedUi.INSTANCE.drawLoadingBadge(guiGraphics, minecraft.font, width, height, "Loading Aquatic Aces…");
        BrandedUi.INSTANCE.drawProgressBar(guiGraphics, width, height, currentProgress);
        guiGraphics.drawCenteredString(minecraft.font, "discord.gg/GMDf9vWeuQ", width / 2, height - 20, 0x556677);
    }
}
