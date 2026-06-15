package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.ViewModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {

    @Inject(method = "applyItemArmTransform", at = @At("HEAD"))
    private void onApplyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float swingProgress, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        ViewModel.applyTransform(poseStack);
    }
}
