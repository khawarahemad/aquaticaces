package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.Nametags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Unique
    private boolean aquaticaces$nametagScaled;

    @ModifyVariable(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private Component aquaticaces$modifyNameTag(Component original, Entity entity) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return original;
        if (!(entity instanceof LivingEntity living) || !Nametags.isActive()) return original;

        StringBuilder label = new StringBuilder(original.getString());

        if (Nametags.showHealth()) {
            float hp = living.getHealth();
            String colorCode = hp > 12f ? "§a" : (hp > 6f ? "§e" : "§c");
            label.append(String.format(" %s%.1f HP", colorCode, hp));
        }

        if (Nametags.showDistance()) {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                label.append(String.format(" §7%.1fm", player.distanceTo(living)));
            }
        }

        return Component.literal(label.toString());
    }

    @Inject(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At("HEAD")
    )
    private void aquaticaces$scaleNametagHead(
        Entity entity,
        Component displayName,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        float partialTick,
        CallbackInfo ci
    ) {
        aquaticaces$nametagScaled = false;
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        if (!(entity instanceof LivingEntity) || !Nametags.isActive()) return;

        float scale = Nametags.getScaleMultiplier();
        if (scale != 1.0f) {
            aquaticaces$nametagScaled = true;
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
        }
    }

    @Inject(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At("RETURN")
    )
    private void aquaticaces$scaleNametagReturn(PoseStack poseStack, CallbackInfo ci) {
        if (aquaticaces$nametagScaled) {
            poseStack.popPose();
            aquaticaces$nametagScaled = false;
        }
    }
}
