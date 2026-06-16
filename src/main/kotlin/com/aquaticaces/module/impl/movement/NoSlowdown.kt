package com.aquaticaces.module.impl.movement

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module

class NoSlowdown : Module("NoSlowdown", "Prevents slowdown when using items.", Category.MOVEMENT) {

    companion object {
        @JvmStatic
        fun isActive(): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("NoSlowdown")
            return module != null && module.isEnabled
        }
    }
}
