package com.aquaticaces.module.impl.utility

import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

object BlockPlacer {
    fun findBlockHand(player: LocalPlayer): InteractionHand? {
        if (player.mainHandItem.item is BlockItem) return InteractionHand.MAIN_HAND
        if (player.offhandItem.item is BlockItem) return InteractionHand.OFF_HAND
        return null
    }

    fun placeAt(level: Level, player: LocalPlayer, targetPos: BlockPos): Boolean {
        if (!level.getBlockState(targetPos).isAir) return false

        var against: BlockPos? = null
        var face: Direction? = null
        for (dir in Direction.entries) {
            val adj = targetPos.relative(dir)
            if (!level.getBlockState(adj).isAir) {
                against = adj
                face = dir.opposite
                break
            }
        }
        val solid = against ?: return false
        val direction = face ?: return false
        val hand = findBlockHand(player) ?: return false

        val hitVec = Vec3(
            solid.x + 0.5 + direction.stepX * 0.5,
            solid.y + 0.5 + direction.stepY * 0.5,
            solid.z + 0.5 + direction.stepZ * 0.5
        )
        val hit = BlockHitResult(hitVec, direction, solid, false)
        player.connection.send(ServerboundUseItemOnPacket(hand, hit, 0))
        player.swing(hand)
        return true
    }
}
