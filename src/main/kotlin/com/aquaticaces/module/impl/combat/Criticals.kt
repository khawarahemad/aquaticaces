package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import org.lwjgl.glfw.GLFW

/**
 * Criticals module.
 * Forces critical hits on every attack by spoofing player falling states using
 * position packet offsets or micro-jumping.
 */
class Criticals : Module("Criticals", "Forces critical hits on attacks.", Category.COMBAT) {

    val mode = ModeSetting("Mode", "Packet", listOf("Packet", "Jump"))

    init {
        addSettings(mode)
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        val player = mc.player ?: return
        val connection = mc.connection ?: return
        val packet = event.packet

        // If packet is attacking an entity and player is on the ground
        if (packet is ServerboundInteractPacket && player.onGround() && !player.isInWater && !player.isInLava) {
            val isAttack = isAttackPacket(packet)
            if (isAttack) {
                when (mode.value) {
                    "Packet" -> {
                        val x = player.x
                        val y = player.y
                        val z = player.z
                        
                        // Send micro-offset positions to simulate a tiny jump and fall
                        connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.0625, z, false))
                        connection.send(ServerboundMovePlayerPacket.Pos(x, y, z, false))
                        connection.send(ServerboundMovePlayerPacket.Pos(x, y + 0.0125, z, false))
                        connection.send(ServerboundMovePlayerPacket.Pos(x, y, z, false))
                    }
                    "Jump" -> {
                        player.jumpFromGround()
                    }
                }
            }
        }
    }

    private fun isAttackPacket(packet: ServerboundInteractPacket): Boolean {
        return try {
            val actionField = ServerboundInteractPacket::class.java.getDeclaredField("action")
            actionField.isAccessible = true
            val action = actionField.get(packet)
            action.javaClass.simpleName.contains("Attack", ignoreCase = true)
        } catch (e: Exception) {
            // Fallback: search enum or action type representation
            packet.toString().contains("ATTACK", ignoreCase = true)
        }
    }
}
