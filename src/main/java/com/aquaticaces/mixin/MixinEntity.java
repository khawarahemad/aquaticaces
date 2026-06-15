package com.aquaticaces.mixin;

import com.aquaticaces.module.Module;
import com.aquaticaces.module.ModuleManager;
import com.aquaticaces.module.impl.movement.Step;
import com.aquaticaces.module.setting.ModeSetting;
import com.aquaticaces.module.setting.NumberSetting;
import com.aquaticaces.module.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "getPickRadius", at = @At("HEAD"), cancellable = true)
    private void onGetPickRadius(CallbackInfoReturnable<Float> cir) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;

        Entity self = (Entity) (Object) this;
        if (self instanceof LivingEntity && self != Minecraft.getInstance().player) {
            Module hitboxes = ModuleManager.INSTANCE.getModuleByName("Hitboxes");
            if (hitboxes != null && hitboxes.isEnabled()) {
                // The first setting of Hitboxes is the expansion size
                if (!hitboxes.getSettings().isEmpty()) {
                    NumberSetting sizeSetting = (NumberSetting) hitboxes.getSettings().get(0);
                    cir.setReturnValue(sizeSetting.getValue().floatValue());
                }
            }
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
