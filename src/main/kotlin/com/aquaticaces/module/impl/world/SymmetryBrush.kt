package com.aquaticaces.module.impl.world

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class SymmetryBrush : Module("SymmetryBrush", "Mirrors block placements across an axis.", Category.WORLD) {
    val axis = ModeSetting("Axis", "X", listOf("X", "Y", "Z"))
    private var origin = BlockPos.ZERO
    private var sendingMirror = false

    init { addSettings(axis) }

    override fun onEnable() {
        super.onEnable()
        val player = mc.player ?: return
        origin = player.blockPosition()
        com.aquaticaces.core.NotificationManager.info("Symmetry", "Origin set at ${origin.x}, ${origin.y}, ${origin.z}")
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        if (sendingMirror || !canRun()) return
        val packet = event.packet
        if (packet !is ServerboundUseItemOnPacket) return
        val connection = mc.connection ?: return

        val hit = packet.getHitResult()
        val placed = hit.blockPos.relative(hit.direction)
        val mirroredPos = mirror(placed)
        val mirroredDir = mirrorDirection(hit.direction)
        if (mirroredPos == placed) return

        val mirrorHit = BlockHitResult(
            Vec3(mirroredPos.x + 0.5, mirroredPos.y + 0.5, mirroredPos.z + 0.5),
            mirroredDir,
            mirroredPos.relative(mirroredDir.opposite),
            false
        )

        sendingMirror = true
        try {
            connection.send(ServerboundUseItemOnPacket(packet.getHand(), mirrorHit, packet.getSequence()))
        } finally {
            sendingMirror = false
        }
    }

    private fun mirror(pos: BlockPos): BlockPos = when (axis.value) {
        "X" -> BlockPos(2 * origin.x - pos.x, pos.y, pos.z)
        "Y" -> BlockPos(pos.x, 2 * origin.y - pos.y, pos.z)
        else -> BlockPos(pos.x, pos.y, 2 * origin.z - pos.z)
    }

    private fun mirrorDirection(dir: Direction): Direction = when (axis.value) {
        "X" -> when (dir) {
            Direction.EAST -> Direction.WEST
            Direction.WEST -> Direction.EAST
            else -> dir
        }
        "Y" -> when (dir) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            else -> dir
        }
        else -> when (dir) {
            Direction.NORTH -> Direction.SOUTH
            Direction.SOUTH -> Direction.NORTH
            else -> dir
        }
    }
}
