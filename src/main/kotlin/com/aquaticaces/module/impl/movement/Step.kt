package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket

/**
 * Step module.
 * Automatically ascends block heights without needing to jump.
 */
class Step : Module("Step", "Allows the player to step up full blocks instantly.", Category.MOVEMENT) {

    val height = NumberSetting("Height", 1.5, 1.0, 2.5, 0.5)
    val mode = ModeSetting("Mode", "Vanilla", listOf("Vanilla", "Packet"))

    init {
        addSettings(height, mode)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return

        if (mode.value != "Packet") return

        if (player.horizontalCollision && player.onGround() && !player.input.jumping) {
            val input = player.input
            if (input.forwardImpulse != 0f || input.leftImpulse != 0f) {
                val heightVal = height.value
                val connection = mc.connection ?: return

                val x = player.x
                val y = player.y
                val z = player.z

                if (heightVal <= 1.0) {
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.42, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.75, z, false))
                    player.setPos(x, y + 1.0, z)
                } else if (heightVal <= 1.5) {
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.42, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.75, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 1.00, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 1.16, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 1.23, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 1.20, z, false))
                    player.setPos(x, y + 1.5, z)
                } else if (heightVal <= 2.0) {
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.42, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.78, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 1.20, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 1.42, z, false))
                    connection.send(ServerboundMovePlayerPacket.Pos(x, y + 1.75, z, false))
                    player.setPos(x, y + 2.0, z)
                }
            }
        }
    }
}
