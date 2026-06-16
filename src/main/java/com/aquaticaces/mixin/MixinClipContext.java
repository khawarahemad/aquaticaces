package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.world.LiquidInteract;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClipContext.class)
public class MixinClipContext {

    @ModifyVariable(
        method = "<init>(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/entity/Entity;)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private static ClipContext.Fluid aquaticaces$modifyFluidForEntity(ClipContext.Fluid fluid) {
        return aquaticaces$overrideFluid(fluid);
    }

    @ModifyVariable(
        method = "<init>(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/phys/shapes/CollisionContext;)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private static ClipContext.Fluid aquaticaces$modifyFluidForCollision(ClipContext.Fluid fluid) {
        return aquaticaces$overrideFluid(fluid);
    }

    private static ClipContext.Fluid aquaticaces$overrideFluid(ClipContext.Fluid fluid) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) {
            return fluid;
        }
        if (LiquidInteract.isActive()) {
            return ClipContext.Fluid.ANY;
        }
        return fluid;
    }
}
