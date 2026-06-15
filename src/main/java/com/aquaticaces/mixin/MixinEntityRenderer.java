package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.Nametags;
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
    private Component modifyNameTag(Component original, Entity entity) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return original;

        if (entity instanceof LivingEntity && Nametags.isActive()) {
            LivingEntity living = (LivingEntity) entity;
            float hp = living.getHealth();
            
            String colorCode = hp > 12f ? "§a" : (hp > 6f ? "§e" : "§c");
            String healthLabel = String.format(" %s[%.1f HP]", colorCode, hp);
            
            // Build armor label text list (LiquidBounce style)
            StringBuilder armorBuilder = new StringBuilder(" §7[");
            boolean first = true;
            for (net.minecraft.world.item.ItemStack armorPiece : living.getArmorSlots()) {
                if (!armorPiece.isEmpty()) {
                    if (!first) {
                        armorBuilder.append(", ");
                    }
                    armorBuilder.append(armorPiece.getHoverName().getString());
                    first = false;
                }
            }
            armorBuilder.append("]");
            String armorLabel = first ? "" : armorBuilder.toString();
            
            return Component.literal(original.getString() + healthLabel + armorLabel);
        }
        return original;
    }
}
