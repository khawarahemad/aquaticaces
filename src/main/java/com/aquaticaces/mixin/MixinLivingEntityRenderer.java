package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.Chams;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD")
    )
    private void aquaticaces$chamsPre(LivingEntity livingEntity, float f, float g, PoseStack poseStack,
                                      MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        if (!Chams.chamsActive || livingEntity == net.minecraft.client.Minecraft.getInstance().player) return;

        Chams.syncColors();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(Chams.r, Chams.g, Chams.b, Chams.a);
    }

    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("RETURN")
    )
    private void aquaticaces$chamsPost(LivingEntity livingEntity, float f, float g, PoseStack poseStack,
                                       MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        if (!Chams.chamsActive || livingEntity == net.minecraft.client.Minecraft.getInstance().player) return;

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
