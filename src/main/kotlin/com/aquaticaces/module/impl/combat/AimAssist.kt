package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.entity.LivingEntity
import org.lwjgl.glfw.GLFW
import kotlin.math.*

/**
 * AimAssist module.
 * Smoothly drags the player's crosshair towards targets within reach and fov guidelines.
 */
class AimAssist : Module("AimAssist", "Smoothly pulls target onto crosshair.", Category.COMBAT) {

    val range = NumberSetting("Range", 4.0, 1.0, 6.0, 0.1)
    val fov = NumberSetting("FOV", 60.0, 10.0, 360.0, 5.0)
    val strength = NumberSetting("Strength", 5.0, 1.0, 20.0, 1.0)

    init {
        addSettings(range, fov, strength)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        var bestTarget: LivingEntity? = null
        var bestFovDiff = fov.value

        for (entity in level.entitiesForRendering()) {
            if (entity === player || entity !is LivingEntity || !entity.isAlive) continue
            if (player.distanceTo(entity) > range.value) continue

            // Compute ideal rotations to target
            val diffX = entity.x - player.x
            val diffY = entity.eyeY - player.eyeY
            val diffZ = entity.z - player.z
            val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)

            val targetYaw = Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f
            val yawDiff = wrapAngle(targetYaw - player.yRot)

            if (abs(yawDiff) < bestFovDiff) {
                bestFovDiff = abs(yawDiff).toDouble()
                bestTarget = entity
            }
        }

        val target = bestTarget ?: return

        // Smoothly pull crosshair
        val diffX = target.x - player.x
        val diffY = target.eyeY - player.eyeY
        val diffZ = target.z - player.z
        val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)

        val targetYaw = Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f
        val targetPitch = (-Math.toDegrees(atan2(diffY, diffXZ))).toFloat()

        val yawDiff = wrapAngle(targetYaw - player.yRot)
        val pitchDiff = targetPitch - player.xRot

        // Scale pulls by strength setting
        val speed = strength.value * 0.1f
        player.yRot += (yawDiff * speed).toFloat()
        player.xRot += (pitchDiff * speed).toFloat()
        player.xRot = player.xRot.coerceIn(-90f, 90f)
    }

    private fun wrapAngle(angle: Float): Float {
        var wrapped = angle % 360.0f
        if (wrapped >= 180.0f) wrapped -= 360.0f
        if (wrapped < -180.0f) wrapped += 360.0f
        return wrapped
    }
}
