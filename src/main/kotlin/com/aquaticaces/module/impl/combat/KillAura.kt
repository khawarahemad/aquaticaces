package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.ModeSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*

/**
 * KillAura combat module.
 * Automatically rotates and attacks nearby entities within range with multiple target modes.
 */
class KillAura : Module("KillAura", "Automatically attacks nearby entities.", Category.COMBAT, GLFW.GLFW_KEY_R) {

    val targetMode = ModeSetting("Mode", "Single", listOf("Single", "Switch", "Multi"))
    val targets = ModeSetting("Targets", "Players", listOf("Players", "Mobs", "All"))
    val range = NumberSetting("Range", 3.8, 1.0, 6.0, 0.1)
    val minCPS = NumberSetting("MinCPS", 10.0, 1.0, 20.0, 1.0)
    val maxCPS = NumberSetting("MaxCPS", 14.0, 1.0, 20.0, 1.0)
    val rotationSmoothness = NumberSetting("Smoothness", 10.0, 0.0, 100.0, 1.0)
    val raytrace = BooleanSetting("Raytrace", true)
    val switchDelay = NumberSetting("SwitchDelay", 300.0, 50.0, 1000.0, 50.0)

    var target: LivingEntity? = null
        private set
    private val targetsList = mutableListOf<LivingEntity>()
    private var targetIndex = 0
    private var lastSwitchTime = 0L
    private var lastAttackTime = 0L
    private var nextAttackDelay = 0L

    init {
        addSettings(targetMode, targets, range, minCPS, maxCPS, rotationSmoothness, raytrace, switchDelay)
        switchDelay.dependsOn(targetMode, "Switch")
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        // 1. Gather valid targets within range
        targetsList.clear()
        val entities = level.entitiesForRendering()
        for (entity in entities) {
            if (entity !is LivingEntity || !entity.isAlive) continue
            if (!isValidTarget(entity)) continue
            val distCheck = combatDistance(player, entity)
            if (distCheck <= range.value) targetsList.add(entity)
        }

        if (targetsList.isEmpty()) {
            target = null
            return
        }

        // 2. Select target based on mode
        when (targetMode.value) {
            "Single" -> {
                // Keep the closest target
                targetsList.sortBy { player.distanceTo(it) }
                target = targetsList.first()
            }
            "Switch" -> {
                val now = System.currentTimeMillis()
                if (now - lastSwitchTime >= switchDelay.value) {
                    targetIndex = (targetIndex + 1) % targetsList.size
                    lastSwitchTime = now
                }
                target = targetsList.getOrNull(targetIndex) ?: targetsList.first()
            }
            "Multi" -> {
                // Cycle targets rapidly on every tick/attack frame
                targetIndex = (targetIndex + 1) % targetsList.size
                target = targetsList[targetIndex]
            }
        }

        val activeTarget = target ?: return

        // 3. Compute rotations (optionally toward backtracked position)
        val aimPos = Backtrack.delayedPosition(activeTarget)?.add(0.0, activeTarget.eyeHeight.toDouble(), 0.0)
            ?: activeTarget.eyePosition
        val playerEyePos = player.eyePosition

        val diffX = aimPos.x - playerEyePos.x
        val diffY = aimPos.y - playerEyePos.y
        val diffZ = aimPos.z - playerEyePos.z
        val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)

        var targetYaw = Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f
        var targetPitch = (-Math.toDegrees(atan2(diffY, diffXZ))).toFloat()

        // 4. Apply rotations via RotationManager
        val smooth = (rotationSmoothness.value / 100.0).toFloat()
        com.aquaticaces.core.RotationManager.lookAt(
            aimPos.x, aimPos.y, aimPos.z,
            priority = 100,
            smooth = smooth,
            mode = com.aquaticaces.core.RotationMode.CLIENT
        )

        // 5. Raytrace Check
        if (raytrace.value) {
            val reach = range.value
            val lookVec = player.getLookAngle()
            val endVec = playerEyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach)
            val searchBox = player.boundingBox.expandTowards(lookVec.scale(reach)).inflate(1.0)
            
            var raycastHit = false
            val colliders = level.getEntities(player, searchBox) { it is LivingEntity && it.isAlive }
            for (collider in colliders) {
                val hitAABB = collider.boundingBox.inflate(collider.pickRadius.toDouble())
                if (hitAABB.clip(playerEyePos, endVec).isPresent) {
                    if (collider === activeTarget) {
                        raycastHit = true
                        break
                    }
                }
            }
            if (!raycastHit) return
        }

        // 6. Attack Delay and Packet Action
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAttackTime >= nextAttackDelay) {
            mc.gameMode?.attack(player, activeTarget)
            if (!NoSwing.shouldHideSwing()) player.swing(InteractionHand.MAIN_HAND)

            val min = minCPS.value.coerceAtMost(maxCPS.value)
            val max = maxCPS.value.coerceAtLeast(minCPS.value)
            val targetCPS = ThreadLocalRandom.current().nextDouble(min, max + 0.1)
            nextAttackDelay = (1000.0 / targetCPS).toLong()
            lastAttackTime = currentTime
        }
    }

    private fun isValidTarget(entity: LivingEntity): Boolean {
        return when (targets.value) {
            "Players" -> com.aquaticaces.core.TargetValidator.isValidCombatTarget(entity, playersOnly = true)
            "Mobs" -> entity !is Player && com.aquaticaces.core.TargetValidator.isValidCombatTarget(entity, playersOnly = false)
            else -> com.aquaticaces.core.TargetValidator.isValidCombatTarget(entity, playersOnly = false)
        }
    }

    private fun combatDistance(player: Player, entity: LivingEntity): Double {
        val delayed = Backtrack.delayedPosition(entity)
        if (delayed != null) {
            val dx = delayed.x - player.x
            val dy = delayed.y - player.y
            val dz = delayed.z - player.z
            return sqrt(dx * dx + dy * dy + dz * dz)
        }
        return player.distanceTo(entity).toDouble()
    }

    private fun wrapAngle(angle: Float): Float {
        var wrapped = angle % 360.0f
        if (wrapped >= 180.0f) wrapped -= 360.0f
        if (wrapped < -180.0f) wrapped += 360.0f
        return wrapped
    }

    override fun onDisable() {
        super.onDisable()
        target = null
        targetsList.clear()
    }
}
