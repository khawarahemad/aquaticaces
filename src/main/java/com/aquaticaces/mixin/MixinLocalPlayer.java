package com.aquaticaces.mixin;

import com.aquaticaces.AquaticAces;
import com.aquaticaces.event.impl.EventMove;
import com.aquaticaces.module.impl.movement.NoSlowdown;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        LocalPlayer player = (LocalPlayer) (Object) this;
        Vec3 vel = player.getDeltaMovement();
        EventMove event = new EventMove(vel.x, vel.y, vel.z);
        AquaticAces.INSTANCE.getEventBus().post(event);
        if (event.isCancelled()) {
            player.setDeltaMovement(Vec3.ZERO);
        } else if (event.getX() != vel.x || event.getY() != vel.y || event.getZ() != vel.z) {
            player.setDeltaMovement(new Vec3(event.getX(), event.getY(), event.getZ()));
        }
    }

    @Inject(method = "isMovingSlowly", at = @At("HEAD"), cancellable = true)
    private void aquaticaces$noSlowdown(CallbackInfoReturnable<Boolean> cir) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        if (NoSlowdown.isActive()) {
            cir.setReturnValue(false);
        }
    }
}
