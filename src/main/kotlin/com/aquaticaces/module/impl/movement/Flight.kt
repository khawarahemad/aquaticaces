package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

/**
 * Flight module.
 * Bypasses gravity vectors to float, hover, or fly in three modes.
 */
class Flight : Module("Flight", "Enables player flight capabilities.", Category.MOVEMENT, GLFW.GLFW_KEY_F) {

    val mode = ModeSetting("Mode", "Vanilla", listOf("Vanilla", "Motion", "Packet"))
    val speed = NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1)

    init {
        addSettings(mode, speed)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val connection = mc.connection ?: return

        when (mode.value) {
            "Vanilla" -> {
                player.abilities.flying = true
                player.abilities.flyingSpeed = speed.value.toFloat() * 0.05f
            }
            "Motion" -> {
                player.abilities.flying = false
                val speedVal = speed.value
                val yawRad = Math.toRadians(player.yRot.toDouble())
                val input = player.input

                var moveX = 0.0
                var moveZ = 0.0
                
                val forward = input.forwardImpulse
                val sideways = input.leftImpulse

                if (forward != 0f || sideways != 0f) {
                    val forwardAngle = if (forward > 0f) 0.0 else if (forward < 0f) Math.PI else 0.0
                    val sidewaysAngle = if (sideways > 0f) -Math.PI / 2.0 else if (sideways < 0f) Math.PI / 2.0 else 0.0
                    val finalAngle = yawRad + forwardAngle + sidewaysAngle

                    moveX = -sin(finalAngle) * speedVal
                    moveZ = cos(finalAngle) * speedVal
                }

                var moveY = 0.0
                if (input.jumping) {
                    moveY = speedVal
                } else if (input.shiftKeyDown) {
                    moveY = -speedVal
                }

                player.deltaMovement = Vec3(moveX, moveY, moveZ)
            }
            "Packet" -> {
                player.abilities.flying = false
                player.deltaMovement = Vec3(0.0, 0.0, 0.0)

                val x = player.x
                val y = player.y
                val z = player.z

                val posPacketUp = ServerboundMovePlayerPacket.Pos(x, y + 0.045, z, false)
                val posPacketDown = ServerboundMovePlayerPacket.Pos(x, y, z, true)
                connection.send(posPacketUp)
                connection.send(posPacketDown)
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
        val player = mc.player ?: return
        player.abilities.flying = false
        player.abilities.flyingSpeed = 0.05f
    }
}
