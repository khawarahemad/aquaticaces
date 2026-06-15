package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import com.aquaticaces.util.PacketUtils
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.entity.LivingEntity
import org.lwjgl.glfw.GLFW
import kotlin.math.*

/**
 * SilentAim module.
 * Modifies outgoing packets to aim directly at nearest enemy target while leaving client camera intact.
 */
class SilentAim : Module("SilentAim", "Aims silently without turning camera.", Category.COMBAT) {

    val range = NumberSetting("Range", 4.0, 1.0, 6.0, 0.1)
    val fov = NumberSetting("FOV", 90.0, 10.0, 360.0, 5.0)

    private var targetYaw = 0f
    private var targetPitch = 0f
    private var hasTarget = false

    init {
        addSettings(range, fov)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        var closestTarget: LivingEntity? = null
        var bestFovDiff = fov.value

        for (entity in level.entitiesForRendering()) {
            if (entity === player || entity !is LivingEntity || !entity.isAlive) continue
            if (player.distanceTo(entity) > range.value) continue

            val diffX = entity.x - player.x
            val diffY = entity.eyeY - player.eyeY
            val diffZ = entity.z - player.z
            val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)

            val idealYaw = Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f
            val yawDiff = wrapAngle(idealYaw - player.yRot)

            if (abs(yawDiff) < bestFovDiff) {
                bestFovDiff = abs(yawDiff).toDouble()
                closestTarget = entity
            }
        }

        val target = closestTarget
        if (target != null) {
            val diffX = target.x - player.x
            val diffY = target.eyeY - player.eyeY
            val diffZ = target.z - player.z
            val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)

            targetYaw = (Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f)
            targetPitch = (-Math.toDegrees(atan2(diffY, diffXZ))).toFloat().coerceIn(-90f, 90f)
            hasTarget = true
        } else {
            hasTarget = false
        }
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        val packet = event.packet
        if (packet is ServerboundMovePlayerPacket && hasTarget) {
            PacketUtils.setRotations(packet, targetYaw, targetPitch)
        }
    }

    private fun wrapAngle(angle: Float): Float {
        var wrapped = angle % 360.0f
        if (wrapped >= 180.0f) wrapped -= 360.0f
        if (wrapped < -180.0f) wrapped += 360.0f
        return wrapped
    }

    override fun onDisable() {
        super.onDisable()
        hasTarget = false
    }
}
