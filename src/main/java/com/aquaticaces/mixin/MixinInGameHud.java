package com.aquaticaces.mixin;

import com.aquaticaces.AquaticAces;
import com.aquaticaces.event.impl.EventRender2D;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender2D(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        AquaticAces.INSTANCE.getEventBus().post(new EventRender2D(guiGraphics, partialTick));
    }
}
