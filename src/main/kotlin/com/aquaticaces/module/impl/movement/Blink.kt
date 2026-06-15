package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Blink module.
 * Blocks and buffers all outgoing player movement packets. When disabled,
 * flushes all buffered packets at once, simulating a client teleportation effect.
 */
class Blink : Module("Blink", "Delays outgoing movement updates.", Category.MOVEMENT) {

    private val packetQueue = ConcurrentLinkedQueue<Packet<*>>()

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        val packet = event.packet
        if (packet is ServerboundMovePlayerPacket) {
            packetQueue.add(packet)
            event.cancel()
        }
    }

    override fun onDisable() {
        super.onDisable()
        val connection = mc.connection ?: return
        
        while (!packetQueue.isEmpty()) {
            val packet = packetQueue.poll()
            connection.send(packet)
        }
    }
}
