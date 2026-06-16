package com.aquaticaces.mixin;

import com.aquaticaces.AquaticAces;
import com.aquaticaces.event.impl.EventRender3D;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Inject(
        method = "renderLevel",
        at = @At("RETURN")
    )
    private void onRenderLevel(
        DeltaTracker deltaTracker, 
        boolean renderBlockOutline, 
        Camera camera, 
        GameRenderer gameRenderer, 
        LightTexture lightTexture, 
        Matrix4f projectionMatrix, 
        Matrix4f modelViewMatrix, 
        CallbackInfo ci
    ) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(camera.rotation());
        org.joml.Matrix4f modelViewRotationOnly = new org.joml.Matrix4f(poseStack.last().pose());
        
        // Save matrices globally in RenderUtil for backward-compatible rendering modules
        com.aquaticaces.module.impl.render.RenderUtil.activeProj = projectionMatrix;
        com.aquaticaces.module.impl.render.RenderUtil.activeModelView = modelViewRotationOnly;

        // Post EventRender3D for custom 3D visuals
        AquaticAces.INSTANCE.getEventBus().post(new EventRender3D(poseStack, projectionMatrix, modelViewRotationOnly, partialTick));
    }
}
