package com.aquaticaces.module.impl.world

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import org.lwjgl.glfw.GLFW

/**
 * Timer module.
 * Alters client game speed by running additional game tick cycles or introducing delays.
 */
class Timer : Module("Timer", "Speeds up or slows down the client.", Category.WORLD) {

    val speed = NumberSetting("Speed", 1.2, 0.2, 5.0, 0.1)
    
    private var ticking = false
    private var skipCount = 0

    init {
        addSettings(speed)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        if (ticking) return
        val player = mc.player ?: return

        val targetSpeed = speed.value
        if (targetSpeed > 1.0) {
            // Speed up: run additional game loop ticks
            val extraTicks = (targetSpeed - 1.0)
            val wholeTicks = extraTicks.toInt()
            val fractionalTick = extraTicks - wholeTicks

            ticking = true
            try {
                for (i in 0 until wholeTicks) {
                    mc.tick()
                }
                if (Math.random() < fractionalTick) {
                    mc.tick()
                }
            } finally {
                ticking = false
            }
        } else if (targetSpeed < 1.0) {
            // Slow down: skip ticks by introducing short sleeps
            val sleepMs = ((1.0 / targetSpeed - 1.0) * 50.0).toLong().coerceIn(0L, 200L)
            if (sleepMs > 0) {
                try {
                    Thread.sleep(sleepMs)
                } catch (e: InterruptedException) {
                    // Ignore
                }
            }
        }
    }
}
