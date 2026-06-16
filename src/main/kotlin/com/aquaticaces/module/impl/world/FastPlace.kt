package com.aquaticaces.module.impl.world

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module

class FastPlace : Module("FastPlace", "Removes block placement delay.", Category.WORLD) {

    companion object {
        @JvmStatic
        fun isActive(): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("FastPlace")
            return module != null && module.isEnabled && module.canRun()
        }
    }
}
