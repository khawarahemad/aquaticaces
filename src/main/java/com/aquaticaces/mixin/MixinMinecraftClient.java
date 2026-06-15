package com.aquaticaces.mixin;

import com.aquaticaces.AquaticAces;
import com.aquaticaces.event.impl.EventClientTick;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        AquaticAces.INSTANCE.getEventBus().post(new EventClientTick(EventClientTick.Phase.PRE));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void postTick(CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        AquaticAces.INSTANCE.getEventBus().post(new EventClientTick(EventClientTick.Phase.POST));
    }
}
