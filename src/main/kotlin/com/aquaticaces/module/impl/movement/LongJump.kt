package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventJump
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import kotlin.math.cos
import kotlin.math.sin

class LongJump : Module("LongJump", "Boosts horizontal jump distance.", Category.MOVEMENT) {
    val boost = NumberSetting("Boost", 1.5, 0.5, 3.0, 0.1)
    init { addSettings(boost) }

    @Subscribe
    fun onJump(event: EventJump) {
        if (!canRun()) return
        val player = mc.player ?: return
        val yaw = Math.toRadians(player.yRot.toDouble())
        val forward = player.input.forwardImpulse
        val strafe = player.input.leftImpulse
        if (forward == 0f && strafe == 0f) return

        val speed = boost.value
        val motionX = -sin(yaw) * forward + cos(yaw) * strafe
        val motionZ = cos(yaw) * forward + sin(yaw) * strafe
        val len = kotlin.math.sqrt(motionX * motionX + motionZ * motionZ).coerceAtLeast(0.001)
        val vel = player.deltaMovement
        player.setDeltaMovement(
            vel.x + (motionX / len) * speed,
            vel.y,
            vel.z + (motionZ / len) * speed
        )
    }
}
