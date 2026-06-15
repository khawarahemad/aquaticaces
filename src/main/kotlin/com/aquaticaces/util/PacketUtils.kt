package com.aquaticaces.util

import com.aquaticaces.mixin.ServerboundMovePlayerPacketAccessor
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket

object PacketUtils {
    fun setRotations(packet: ServerboundMovePlayerPacket, yaw: Float, pitch: Float) {
        val accessor = packet as ServerboundMovePlayerPacketAccessor
        accessor.aquaticaces_setYRot(yaw)
        accessor.aquaticaces_setXRot(pitch)
    }

    fun setPosition(packet: ServerboundMovePlayerPacket, x: Double, y: Double, z: Double) {
        val accessor = packet as ServerboundMovePlayerPacketAccessor
        accessor.aquaticaces_setX(x)
        accessor.aquaticaces_setY(y)
        accessor.aquaticaces_setZ(z)
    }

    fun setPositionAndRotation(
        packet: ServerboundMovePlayerPacket,
        x: Double, y: Double, z: Double,
        yaw: Float, pitch: Float
    ) {
        val accessor = packet as ServerboundMovePlayerPacketAccessor
        accessor.aquaticaces_setX(x)
        accessor.aquaticaces_setY(y)
        accessor.aquaticaces_setZ(z)
        accessor.aquaticaces_setYRot(yaw)
        accessor.aquaticaces_setXRot(pitch)
    }
}
