package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import org.lwjgl.glfw.GLFW

/**
 * NoSlowdown module.
 * Neutralizes slow down penalties from blocking shields, drawing bows, or eating food items.
 */
class NoSlowdown : Module("NoSlowdown", "Prevents slowdown when using items.", Category.MOVEMENT) {

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return

        // If using item (eating, block, bow charging), counteract the 0.2x speed penalty
        if (player.isUsingItem) {
            player.input.leftImpulse *= 5.0f
            player.input.forwardImpulse *= 5.0f
        }
    }
}
