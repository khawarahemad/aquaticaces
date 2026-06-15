package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventAttack
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module

class NoSwing : Module("NoSwing", "Hides attack swing animation.", Category.GHOST) {
    @Subscribe
    fun onAttack(event: EventAttack) {
        if (!canRun()) return
        // Swing is client-side; cancel follow-up by not swinging in KillAura when this is on
    }

    companion object {
        @JvmStatic
        fun shouldHideSwing(): Boolean {
            val m = com.aquaticaces.module.ModuleManager.getModuleByName("NoSwing")
            return m != null && m.isEnabled
        }
    }
}
