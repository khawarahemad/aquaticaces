package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventPacketReceive
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.network.protocol.game.ClientboundExplodePacket
import org.lwjgl.glfw.GLFW

/**
 * Velocity module.
 * Reduces or cancels knockback forces sent by the server.
 */
class Velocity : Module("Velocity", "Reduces player knockback velocity.", Category.COMBAT, GLFW.GLFW_KEY_Z) {

    val horizontal = NumberSetting("Horizontal", 0.0, 0.0, 100.0, 1.0)
    val vertical = NumberSetting("Vertical", 0.0, 0.0, 100.0, 1.0)

    init {
        addSettings(horizontal, vertical)
    }

    @Subscribe
    fun onPacketReceive(event: EventPacketReceive) {
        val player = mc.player ?: return
        val packet = event.packet

        if (packet is ClientboundSetEntityMotionPacket) {
            if (packet.getId() == player.id) {
                val hMult = horizontal.value / 100.0
                val vMult = vertical.value / 100.0

                if (hMult == 0.0 && vMult == 0.0) {
                    event.cancel()
                } else {
                    val motionX = (packet.getXa() / 8000.0) * hMult
                    val motionY = (packet.getYa() / 8000.0) * vMult
                    val motionZ = (packet.getZa() / 8000.0) * hMult
                    player.lerpMotion(motionX, motionY, motionZ)
                    event.cancel()
                }
            }
        }

        if (packet is ClientboundExplodePacket) {
            val knockbackX = packet.getKnockbackX().toDouble()
            val knockbackY = packet.getKnockbackY().toDouble()
            val knockbackZ = packet.getKnockbackZ().toDouble()
            if (knockbackX == 0.0 && knockbackY == 0.0 && knockbackZ == 0.0) return

            val hMult = horizontal.value / 100.0
            val vMult = vertical.value / 100.0

            val subX = knockbackX * (1.0 - hMult)
            val subY = knockbackY * (1.0 - vMult)
            val subZ = knockbackZ * (1.0 - hMult)

            val current = player.deltaMovement
            player.deltaMovement = net.minecraft.world.phys.Vec3(
                current.x - subX,
                current.y - subY,
                current.z - subZ
            )
        }
    }
}
