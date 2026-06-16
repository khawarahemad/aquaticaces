package com.aquaticaces.module.impl.combat

import com.aquaticaces.accessor.ServerboundInteractPacketAccess
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket

class Criticals : Module("Criticals", "Forces critical hits on attacks.", Category.COMBAT) {

    val mode = ModeSetting("Mode", "Packet", listOf("Packet", "Jump"))
    init { addSettings(mode) }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        val player = mc.player ?: return
        val connection = mc.connection ?: return
        val packet = event.packet

        if (packet !is ServerboundInteractPacket) return
        if (!(packet as ServerboundInteractPacketAccess).aquaticaces_isAttack()) return
        if (!player.onGround() || player.isInWater || player.isInLava) return

        when (mode.value) {
            "Packet" -> {
                val x = player.x; val y = player.y; val z = player.z
                connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.0625, z, false))
                connection.send(ServerboundMovePlayerPacket.Pos(x, y, z, false))
                connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.0125, z, false))
                connection.send(ServerboundMovePlayerPacket.Pos(x, y, z, false))
            }
            "Jump" -> player.jumpFromGround()
        }
    }
}
