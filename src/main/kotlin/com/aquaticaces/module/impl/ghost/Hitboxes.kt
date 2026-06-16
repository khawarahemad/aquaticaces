package com.aquaticaces.module.impl.ghost

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import org.lwjgl.glfw.GLFW

/**
 * Hitboxes — expands enemy collision boxes for easier hits.
 * Actual AABB inflation is handled in MixinEntity.
 */
class Hitboxes : Module("Hitboxes", "Expands targeted enemy bounding boxes.", Category.GHOST) {

    val size = NumberSetting("Size", 0.2, 0.0, 1.0, 0.05)

    init { addSettings(size) }

    companion object {
        @JvmStatic
        fun expansion(): Float {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("Hitboxes") as? Hitboxes
            return if (module != null && module.isEnabled) module.size.value.toFloat() else 0f
        }
    }
}
