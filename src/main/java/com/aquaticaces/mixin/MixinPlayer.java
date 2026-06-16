package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.movement.NoSlowdown;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class MixinPlayer {

    @Inject(method = "isMovingSlowly", at = @At("HEAD"), cancellable = true, require = 0)
    private void aquaticaces$noSlowdown(CallbackInfoReturnable<Boolean> cir) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        Player self = (Player) (Object) this;
        if (self == Minecraft.getInstance().player && NoSlowdown.isActive()) {
            cir.setReturnValue(false);
        }
    }
}
