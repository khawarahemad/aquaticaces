package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket

class NoFall : Module("NoFall", "Prevents fall damage.", Category.MOVEMENT) {
    val mode = ModeSetting("Mode", "Packet", listOf("Packet", "OnGround"))
    
    private var clientFallDistance = 0f
    private var lastY = 0.0

    init { addSettings(mode) }

    override fun onEnable() {
        super.onEnable()
        val player = mc.player
        if (player != null) {
            lastY = player.y
        }
        clientFallDistance = 0f
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        
        if (player.onGround()) {
            clientFallDistance = 0f
        } else {
            val dy = player.y - lastY
            if (dy < 0) {
                clientFallDistance += -dy.toFloat()
            }
        }
        lastY = player.y
    }

    @Subscribe
    fun onPacket(event: EventPacketSend) {
        if (!canRun()) return
        val player = mc.player ?: return
        
        if (player.onGround()) {
            clientFallDistance = 0f
        } else {
            val dy = player.y - lastY
            if (dy < 0) {
                clientFallDistance += -dy.toFloat()
            }
        }
        lastY = player.y

        if (clientFallDistance < 2f) return

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

        if (mode.value == "Packet") {
            player.fallDistance = 0f
            clientFallDistance = 0f
        }
    }
}
