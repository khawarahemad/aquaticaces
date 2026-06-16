package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
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

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        // Backup: counteract item-use input dampening if mixin misses.
        if (player.isUsingItem) {
            player.input.leftImpulse *= 5.0f
            player.input.forwardImpulse *= 5.0f
        }
    }
}
