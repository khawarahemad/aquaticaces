package com.aquaticaces.core

import net.minecraft.client.Minecraft
import kotlin.math.atan2
import kotlin.math.sqrt

enum class RotationMode { CLIENT, SILENT }

data class RotationRequest(
    val yaw: Float,
    val pitch: Float,
    val priority: Int,
    val mode: RotationMode = RotationMode.CLIENT
)

object RotationManager {
    private val mc get() = Minecraft.getInstance()
    private var activeRequest: RotationRequest? = null
    var silentYaw: Float? = null
    var silentPitch: Float? = null

    fun request(yaw: Float, pitch: Float, priority: Int, mode: RotationMode = RotationMode.CLIENT) {
        val current = activeRequest
        if (current == null || priority >= current.priority) {
            activeRequest = RotationRequest(yaw, pitch.coerceIn(-90f, 90f), priority, mode)
        }
    }

    fun lookAt(x: Double, y: Double, z: Double, priority: Int, smooth: Float = 0f, mode: RotationMode = RotationMode.CLIENT) {
        val player = mc.player ?: return
        val eye = player.eyePosition
        val dx = x - eye.x
        val dy = y - eye.y
        val dz = z - eye.z
        val xz = sqrt(dx * dx + dz * dz)
        val yaw = Math.toDegrees(atan2(dz, dx)).toFloat() - 90f
        val pitch = (-Math.toDegrees(atan2(dy, xz))).toFloat()
        if (smooth <= 0f) {
            request(yaw, pitch, priority, mode)
        } else {
            val playerYaw = player.yRot
            val playerPitch = player.xRot
            val factor = 1f - smooth.coerceIn(0f, 1f)
            request(playerYaw + wrap(yaw - playerYaw) * factor, playerPitch + (pitch - playerPitch) * factor, priority, mode)
        }
    }

    fun apply() {
        val player = mc.player ?: return
        val req = activeRequest ?: return
        when (req.mode) {
            RotationMode.CLIENT -> {
                player.yRot = req.yaw
                player.xRot = req.pitch
                silentYaw = null
                silentPitch = null
            }
            RotationMode.SILENT -> {
                silentYaw = req.yaw
                silentPitch = req.pitch
            }
        }
        activeRequest = null
    }

    fun reset() {
        activeRequest = null
        silentYaw = null
        silentPitch = null
    }

    private fun wrap(angle: Float): Float {
        var a = angle % 360f
        if (a >= 180f) a -= 360f
        if (a < -180f) a += 360f
        return a
    }
}
