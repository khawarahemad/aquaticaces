package com.aquaticaces.module.impl.ghost

import com.aquaticaces.core.RotationManager
import com.aquaticaces.core.RotationMode
import com.aquaticaces.core.TargetValidator
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventAttack
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.NumberSetting
import com.aquaticaces.util.PacketUtils
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Ghost AimBot.
 * Subtle head assistance only while left-click is held and a valid enemy is already in the crosshair.
 * Designed for legit-looking PvP: low correction strength plus optional silent attack rotations.
 */
class AimBot : Module(
    "AimBot",
    "Ghost head assist while attacking crosshair targets.",
    Category.GHOST
) {
    val range = NumberSetting("Range", 3.0, 3.0, 6.0, 0.1)
    val strength = NumberSetting("Strength", 5.0, 1.0, 20.0, 0.5)
    val headOffset = NumberSetting("HeadOffset", 0.12, 0.0, 0.4, 0.01)
    val silentAttack = BooleanSetting("SilentAttack", true)
    val playersOnly = BooleanSetting("PlayersOnly", true)

    private var silentYaw = 0f
    private var silentPitch = 0f
    private var silentActive = false

    init {
        addSettings(range, strength, headOffset, silentAttack, playersOnly)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        silentActive = false

        if (!isAttackKeyDown()) return

        val player = mc.player ?: return
        val target = getCrosshairTarget() ?: return
        if (!TargetValidator.isValidCombatTarget(target, playersOnly = playersOnly.value)) return
        if (player.distanceTo(target) > range.value) return

        val head = headPosition(target)
        val smooth = (1.0 - (strength.value / 100.0)).toFloat().coerceIn(0.8f, 0.99f)
        val (yaw, pitch) = rotationTo(head)

        RotationManager.lookAt(
            head.x,
            head.y,
            head.z,
            priority = 60,
            smooth = smooth,
            mode = RotationMode.CLIENT
        )

        silentYaw = yaw
        silentPitch = pitch
        silentActive = silentAttack.value
    }

    @Subscribe
    fun onAttack(event: EventAttack) {
        if (!canRun() || !isAttackKeyDown()) return
        val target = event.target as? LivingEntity ?: return
        if (!TargetValidator.isValidCombatTarget(target, playersOnly = playersOnly.value)) return

        val player = mc.player ?: return
        if (player.distanceTo(target) > range.value) return

        val head = headPosition(target)
        val (yaw, pitch) = rotationTo(head)
        silentYaw = yaw
        silentPitch = pitch
        silentActive = silentAttack.value
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        if (!silentActive) return
        val packet = event.packet
        if (packet is ServerboundMovePlayerPacket) {
            PacketUtils.setRotations(packet, silentYaw, silentPitch)
        }
    }

    override fun onDisable() {
        super.onDisable()
        silentActive = false
        RotationManager.reset()
    }

    private fun isAttackKeyDown(): Boolean {
        val window = mc.window.window
        return GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS
    }

    private fun getCrosshairTarget(): LivingEntity? {
        val hit = mc.hitResult ?: return null
        if (hit !is EntityHitResult) return null
        return hit.entity as? LivingEntity
    }

    private fun headPosition(entity: LivingEntity): Vec3 {
        val box = entity.boundingBox
        val centerX = (box.minX + box.maxX) * 0.5
        val centerZ = (box.minZ + box.maxZ) * 0.5
        val headY = box.maxY - headOffset.value
        return Vec3(centerX, headY, centerZ)
    }

    private fun rotationTo(target: Vec3): Pair<Float, Float> {
        val player = mc.player ?: return 0f to 0f
        val eye = player.eyePosition
        val dx = target.x - eye.x
        val dy = target.y - eye.y
        val dz = target.z - eye.z
        val xz = sqrt(dx * dx + dz * dz)
        val yaw = Math.toDegrees(atan2(dz, dx)).toFloat() - 90f
        val pitch = (-Math.toDegrees(atan2(dy, xz))).toFloat().coerceIn(-90f, 90f)
        return yaw to pitch
    }
}
