package com.aquaticaces.module.impl.world

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW

/**
 * GhostHand module.
 * Allows right-click interactions with chest containers, levers, and buttons
 * directly through solid walls by scanning along player look angles.
 */
class GhostHand : Module("GhostHand", "Interact with blocks through walls.", Category.WORLD) {

    private var wasUseKeyDown = false

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        val useKeyDown = mc.options.keyUse.isDown
        if (useKeyDown && !wasUseKeyDown) {
            // Right click pressed: scan look vector for interactable block behind walls
            val eyePos = player.eyePosition
            val lookVec = player.getLookAngle()
            
            for (i in 0..50) {
                val dist = i * 0.1
                val checkPos = BlockPos.containing(
                    eyePos.x + lookVec.x * dist,
                    eyePos.y + lookVec.y * dist,
                    eyePos.z + lookVec.z * dist
                )
                
                val state = level.getBlockState(checkPos)
                val block = state.block
                
                // Interactable targets list
                val isInteractable = block == Blocks.CHEST || 
                                     block == Blocks.TRAPPED_CHEST || 
                                     block == Blocks.BARREL || 
                                     block == Blocks.FURNACE ||
                                     block == Blocks.LEVER || 
                                     block == Blocks.STONE_BUTTON || 
                                     block == Blocks.OAK_BUTTON
                                     
                if (isInteractable) {
                    val hitResult = BlockHitResult(
                        Vec3(checkPos.x + 0.5, checkPos.y + 0.5, checkPos.z + 0.5),
                        Direction.UP,
                        checkPos,
                        false
                    )
                    
                    // Click block directly bypassing obstruction collisions
                    player.connection.send(ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hitResult, 0))
                    player.swing(InteractionHand.MAIN_HAND)
                    
                    // Suppress normal click actions
                    mc.options.keyUse.isDown = false
                    break
                }
            }
        }
        wasUseKeyDown = useKeyDown
    }

    override fun onDisable() {
        super.onDisable()
        wasUseKeyDown = false
    }
}
