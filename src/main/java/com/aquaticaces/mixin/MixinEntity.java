package com.aquaticaces.mixin;

import com.aquaticaces.module.impl.ghost.Hitboxes;
import com.aquaticaces.module.impl.movement.Step;
import com.aquaticaces.module.Module;
import com.aquaticaces.module.ModuleManager;
import com.aquaticaces.module.setting.ModeSetting;
import com.aquaticaces.module.setting.NumberSetting;
import com.aquaticaces.module.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

    /** Expand enemy hitboxes for easier targeting. */
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void aquaticaces$expandHitboxes(CallbackInfoReturnable<AABB> cir) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        Entity self = (Entity) (Object) this;
        if (!(self instanceof LivingEntity) || self == Minecraft.getInstance().player) return;

        float expand = Hitboxes.expansion();
        if (expand > 0f) {
            cir.setReturnValue(cir.getReturnValue().inflate(expand, expand, expand));
        }
    }

    @Inject(method = "maxUpStep", at = @At("HEAD"), cancellable = true)
    private void onMaxUpStep(CallbackInfoReturnable<Float> cir) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        Entity self = (Entity) (Object) this;
        if (self != Minecraft.getInstance().player) return;

        Module stepModule = ModuleManager.INSTANCE.getModuleByName("Step");
        if (stepModule == null || !stepModule.isEnabled() || !(stepModule instanceof Step)) return;

        Step step = (Step) stepModule;
        ModeSetting modeSetting = null;
        NumberSetting heightSetting = null;
        for (Setting<?> setting : step.getSettings()) {
            if (setting instanceof ModeSetting mode) {
                modeSetting = mode;
            } else if (setting instanceof NumberSetting number) {
                heightSetting = number;
            }
        }

        if (modeSetting == null || heightSetting == null) return;

        if ("Vanilla".equals(modeSetting.getValue())) {
            cir.setReturnValue(heightSetting.getValue().floatValue());
        } else {
            cir.setReturnValue(0.6f);
        }
    }
}
