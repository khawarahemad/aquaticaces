package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting

class Sprint : Module("Sprint", "Always sprints when moving.", Category.MOVEMENT) {
    val omni = BooleanSetting("Omni", false)
    init { addSettings(omni) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val input = player.input
        val moving = input.forwardImpulse != 0f || input.leftImpulse != 0f
        if (moving || omni.value) player.setSprinting(true)
    }
}
