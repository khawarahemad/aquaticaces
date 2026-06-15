package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module

class SafeWalk : Module("SafeWalk", "Prevents walking off block edges.", Category.MOVEMENT) {
    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        if (!player.onGround()) return
        player.setShiftKeyDown(true)
    }
}
