package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.render.Chams;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends net.minecraft.client.model.EntityModel<T>> {

    @Redirect(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private VertexConsumer aquaticaces$chamsRedirectGetBuffer(
        MultiBufferSource source, 
        RenderType type, 
        LivingEntity livingEntity
    ) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) {
            return source.getBuffer(type);
        }

        if (Chams.chamsActive && livingEntity != net.minecraft.client.Minecraft.getInstance().player) {
            Chams.syncColors();
            ResourceLocation texture = ((EntityRenderer<T>) (Object) this).getTextureLocation((T) livingEntity);
            RenderType chamsType = net.minecraft.client.renderer.ChamsRenderHelper.getChamsRenderType(texture);
            
            // Get module settings
            com.aquaticaces.module.Module module = com.aquaticaces.module.ModuleManager.INSTANCE.getModuleByName("Chams");
            boolean flat = false;
            if (module instanceof Chams) {
                flat = ((Chams) module).getFlat().getValue();
            }

            VertexConsumer originalConsumer = source.getBuffer(chamsType);
            return new ChamsVertexConsumer(originalConsumer, Chams.r, Chams.g, Chams.b, Chams.a, flat);
        }

        return source.getBuffer(type);
    }

    private static class ChamsVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float r, g, b, a;
        private final boolean flat;

        public ChamsVertexConsumer(VertexConsumer delegate, float r, float g, float b, float a, boolean flat) {
            this.delegate = delegate;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.flat = flat;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            delegate.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            delegate.setColor((int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
            return this;
        }

        @Override
        public VertexConsumer setColor(float red, float green, float blue, float alpha) {
            delegate.setColor(r, g, b, a);
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            delegate.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            delegate.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            if (flat) {
                delegate.setUv2(15, 15);
            } else {
                delegate.setUv2(u, v);
            }
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            delegate.setNormal(x, y, z);
            return this;
        }
    }
}
