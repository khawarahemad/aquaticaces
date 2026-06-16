package com.aquaticaces.module.impl.render

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.effect.MobEffects

class Fullbright : Module("Fullbright", "Maximum brightness without XRay.", Category.RENDER) {
    override fun onEnable() {
        super.onEnable()
        mc.levelRenderer.allChanged()
    }

    override fun onDisable() {
        super.onDisable()
        mc.levelRenderer.allChanged()
    }
}
