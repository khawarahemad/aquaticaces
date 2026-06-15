package com.aquaticaces.module.impl.render

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.NumberSetting
import org.lwjgl.glfw.GLFW

/**
 * Nametags module.
 * Exposes settings for customizing player nametag overlays, health labels,
 * and text scales during rendering.
 */
class Nametags : Module("Nametags", "Renders detailed name tags above players.", Category.RENDER) {

    val health = BooleanSetting("Health", true)
    val distance = BooleanSetting("Distance", true)
    val scale = NumberSetting("Scale", 1.5, 0.5, 3.0, 0.1)

    companion object {
        @JvmStatic
        fun isActive(): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("Nametags")
            return module != null && module.isEnabled
        }

        @JvmStatic
        fun getScaleMultiplier(): Float {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("Nametags") as? Nametags
            return module?.scale?.value?.toFloat() ?: 1.0f
        }
    }

    init {
        addSettings(health, distance, scale)
    }
}
