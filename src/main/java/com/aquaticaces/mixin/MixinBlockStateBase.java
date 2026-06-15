package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.XRay;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase {

    @Shadow
    public abstract Block getBlock();

    @Inject(
        method = "skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onSkipRendering(BlockState adjacentState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        if (XRay.Companion.isXrayEnabled()) {
            cir.setReturnValue(!XRay.Companion.shouldRender(getBlock()));
        }
    }
}
