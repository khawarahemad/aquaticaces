package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.Nametags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

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
}
