package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.phys.Vec3

class Spider : Module("Spider", "Climbs walls like a spider.", Category.MOVEMENT) {
    val speed = NumberSetting("Speed", 0.2, 0.1, 0.5, 0.05)
    init { addSettings(speed) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        if (!player.horizontalCollision) return
        val vel = player.deltaMovement
        player.deltaMovement = Vec3(vel.x, speed.value, vel.z)
    }
}
