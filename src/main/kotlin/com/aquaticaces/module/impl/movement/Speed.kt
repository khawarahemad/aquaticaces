package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ModeSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Speed module.
 * Enhances player movement velocities via Bhop or Strafe logic.
 */
class Speed : Module("Speed", "Increases movement speed.", Category.MOVEMENT, GLFW.GLFW_KEY_V) {

    val mode = ModeSetting("Mode", "Bhop", listOf("Bhop", "Strafe"))
    val multiplier = NumberSetting("Multiplier", 1.2, 1.0, 3.0, 0.1)

    init {
        addSettings(mode, multiplier)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return

        val input = player.input
        val isMoving = input.forwardImpulse != 0f || input.leftImpulse != 0f
        if (!isMoving) return

        when (mode.value) {
            "Bhop" -> {
                if (player.onGround()) {
                    player.jumpFromGround()
                } else {
                    // Boost horizontal speed when in mid-air
                    val currentVel = player.deltaMovement
                    val currentSpeed = sqrt(currentVel.x * currentVel.x + currentVel.z * currentVel.z)
                    if (currentSpeed < 0.6) {
                        val yawRad = Math.toRadians(player.yRot.toDouble())
                        val forward = input.forwardImpulse
                        val sideways = input.leftImpulse
                        
                        var angle = yawRad
                        if (forward != 0f || sideways != 0f) {
                            val forwardAngle = if (forward > 0f) 0.0 else if (forward < 0f) Math.PI else 0.0
                            val sidewaysAngle = if (sideways > 0f) -Math.PI / 2.0 else if (sideways < 0f) Math.PI / 2.0 else 0.0
                            angle += (forwardAngle + sidewaysAngle)
                        }

                        val targetX = -sin(angle) * (currentSpeed * multiplier.value)
                        val targetZ = cos(angle) * (currentSpeed * multiplier.value)
                        player.deltaMovement = Vec3(targetX, currentVel.y, targetZ)
                    }
                }
            }
            "Strafe" -> {
                // Instantly redirect horizontal speed vector towards input keys
                val currentVel = player.deltaMovement
                val currentSpeed = sqrt(currentVel.x * currentVel.x + currentVel.z * currentVel.z)
                val yawRad = Math.toRadians(player.yRot.toDouble())
                val forward = input.forwardImpulse
                val sideways = input.leftImpulse
                
                var angle = yawRad
                if (forward != 0f || sideways != 0f) {
                    val forwardAngle = if (forward > 0f) 0.0 else if (forward < 0f) Math.PI else 0.0
                    val sidewaysAngle = if (sideways > 0f) -Math.PI / 2.0 else if (sideways < 0f) Math.PI / 2.0 else 0.0
                    angle += (forwardAngle + sidewaysAngle)
                }

                val targetX = -sin(angle) * currentSpeed
                val targetZ = cos(angle) * currentSpeed
                player.deltaMovement = Vec3(targetX, currentVel.y, targetZ)
            }
        }
    }
}
