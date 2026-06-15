package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW

/**
 * Jesus module.
 * Neutralizes liquid sinking physics so the player can walk on water and lava surfaces.
 */
class Jesus : Module("Jesus", "Allows walking on liquids.", Category.MOVEMENT) {

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        // 1. Check if the block directly below the player is water/lava
        val posBelow = BlockPos.containing(player.x, player.y - 0.1, player.z)
        val fluidState = level.getFluidState(posBelow)

        if (!fluidState.isEmpty) {
            // Keep the player afloat on liquid surface
            val velocity = player.deltaMovement
            player.deltaMovement = Vec3(velocity.x, 0.0, velocity.z)
            
            // Allow client-side ground status updates
            player.setOnGround(true)
            
            // Slowly ascend if jumping
            if (player.input.jumping) {
                player.deltaMovement = Vec3(velocity.x, 0.15, velocity.z)
            }
        }
    }
}
