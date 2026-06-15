package com.aquaticaces.module.impl.player

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.util.PacketUtils
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import org.lwjgl.glfw.GLFW

/**
 * Derp module.
 * Rapidly rotates player packet yaw and pitch angles to create a spinning effect to other players.
 */
class Derp : Module("Derp", "Spins player rotations head-spinningly.", Category.PLAYER) {

    private var derpYaw = 0f
    private var derpPitch = 0f

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        
        // Cycle rotation angles rapidly
        derpYaw = (derpYaw + 35f) % 360f
        derpPitch = if (derpPitch >= 90f) -90f else derpPitch + 15f
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        val packet = event.packet
        if (packet is ServerboundMovePlayerPacket) {
            PacketUtils.setRotations(packet, derpYaw, derpPitch)
        }
    }
}
