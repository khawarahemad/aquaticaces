package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RespawnAnchorBlock
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW

/**
 * AutoAnchor module.
 * Automatically places, charges (with Glowstone), and detonates Respawn Anchors
 * in the Overworld to deal massive combat damage to targets.
 */
class AutoAnchor : Module("AutoAnchor", "Places and explodes Respawn Anchors.", Category.COMBAT) {

    val range = NumberSetting("Range", 5.0, 1.0, 6.0, 0.1)

    init {
        addSettings(range)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        // 1. Find target
        var target: LivingEntity? = null
        var closestDist = range.value
        for (entity in level.entitiesForRendering()) {
            if (entity === player || entity !is LivingEntity || !entity.isAlive) continue
            val dist = player.distanceTo(entity).toDouble()
            if (dist < closestDist) {
                closestDist = dist
                target = entity
            }
        }

        if (target == null) return

        val playerPos = player.blockPosition()
        val radius = range.value.toInt()

        // 2. Scan for placed Respawn Anchors or find a spot to place one
        var anchorPos: BlockPos? = null
        var canPlace = false
        var placementBaseBlock: BlockPos? = null

        outer@ for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                for (dz in -radius..radius) {
                    val pos = playerPos.offset(dx, dy, dz)
                    val state = level.getBlockState(pos)
                    
                    if (state.block == Blocks.RESPAWN_ANCHOR) {
                        anchorPos = pos
                        break@outer
                    } else if (state.isAir && !level.getBlockState(pos.below()).isAir) {
                        // Candidate placement spot
                        if (player.position().distanceTo(Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)) <= range.value) {
                            placementBaseBlock = pos.below()
                            anchorPos = pos
                            canPlace = true
                        }
                    }
                }
            }
        }

        val anchor = anchorPos ?: return

        if (canPlace && placementBaseBlock != null) {
            // Equip Respawn Anchor in hand
            val anchorHand = getHandWithItem(Items.RESPAWN_ANCHOR) ?: return
            
            // Place anchor
            val hitResult = BlockHitResult(
                Vec3(anchor.x + 0.5, anchor.y.toDouble(), anchor.z + 0.5),
                Direction.UP,
                placementBaseBlock,
                false
            )
            player.connection.send(ServerboundUseItemOnPacket(anchorHand, hitResult, 0))
            player.swing(anchorHand)
        } else {
            val state = level.getBlockState(anchor)
            if (state.block == Blocks.RESPAWN_ANCHOR) {
                val charge = state.getValue(RespawnAnchorBlock.CHARGE)
                if (charge == 0) {
                    // Charge Respawn Anchor with Glowstone
                    val glowstoneHand = getHandWithItem(Items.GLOWSTONE) ?: return
                    val hitResult = BlockHitResult(
                        Vec3(anchor.x + 0.5, anchor.y + 0.5, anchor.z + 0.5),
                        Direction.UP,
                        anchor,
                        false
                    )
                    player.connection.send(ServerboundUseItemOnPacket(glowstoneHand, hitResult, 0))
                    player.swing(glowstoneHand)
                } else {
                    // Click anchor with anything other than Glowstone/Anchor to detonate it
                    val clickHand = getHandWithoutItem(Items.GLOWSTONE, Items.RESPAWN_ANCHOR)
                    val hitResult = BlockHitResult(
                        Vec3(anchor.x + 0.5, anchor.y + 0.5, anchor.z + 0.5),
                        Direction.UP,
                        anchor,
                        false
                    )
                    player.connection.send(ServerboundUseItemOnPacket(clickHand, hitResult, 0))
                    player.swing(clickHand)
                }
            }
        }
    }

    private fun getHandWithItem(item: net.minecraft.world.item.Item): InteractionHand? {
        val player = mc.player ?: return null
        return when (item) {
            player.mainHandItem.item -> InteractionHand.MAIN_HAND
            player.offhandItem.item -> InteractionHand.OFF_HAND
            else -> null
        }
    }

    private fun getHandWithoutItem(vararg items: net.minecraft.world.item.Item): InteractionHand {
        val player = mc.player ?: return InteractionHand.MAIN_HAND
        val mainItem = player.mainHandItem.item
        if (items.none { it == mainItem }) {
            return InteractionHand.MAIN_HAND
        }
        return InteractionHand.OFF_HAND
    }
}
