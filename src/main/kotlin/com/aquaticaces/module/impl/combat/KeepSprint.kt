package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventAttack
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module

class KeepSprint : Module("KeepSprint", "Maintains sprint after attacking.", Category.COMBAT) {
    @Subscribe
    fun onAttack(event: EventAttack) {
        if (!canRun()) return
        mc.player?.setSprinting(true)
    }
}
