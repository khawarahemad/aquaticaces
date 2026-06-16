package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket

class NoFall : Module("NoFall", "Prevents fall damage.", Category.MOVEMENT) {
    val mode = ModeSetting("Mode", "Packet", listOf("Packet", "OnGround"))
    init { addSettings(mode) }

    @Subscribe
    fun onPacket(event: EventPacketSend) {
        if (!canRun()) return
        val player = mc.player ?: return
        if (player.fallDistance < 2f) return

        val spoofGround = mode.value == "OnGround" || mode.value == "Packet"
        if (!spoofGround) return

        when (val packet = event.packet) {
            is ServerboundMovePlayerPacket.Pos ->
                event.replace(ServerboundMovePlayerPacket.Pos(player.x, player.y, player.z, true))
            is ServerboundMovePlayerPacket.PosRot ->
                event.replace(ServerboundMovePlayerPacket.PosRot(
                    player.x, player.y, player.z, player.yRot, player.xRot, true
                ))
            is ServerboundMovePlayerPacket.StatusOnly ->
                event.replace(ServerboundMovePlayerPacket.StatusOnly(true))
        }

        if (mode.value == "Packet") player.fallDistance = 0f
    }
}
