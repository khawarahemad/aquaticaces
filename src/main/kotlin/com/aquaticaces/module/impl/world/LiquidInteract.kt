package com.aquaticaces.module.impl.world

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import org.lwjgl.glfw.GLFW

/**
 * LiquidInteract module.
 * Exposes status state queried by raycasting methods to allow block placements
 * directly against liquid source faces.
 */
class LiquidInteract : Module("LiquidInteract", "Allows block placements on liquids.", Category.WORLD) {

    companion object {
        @JvmStatic
        fun isActive(): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("LiquidInteract")
            return module != null && module.isEnabled
        }
    }
}
