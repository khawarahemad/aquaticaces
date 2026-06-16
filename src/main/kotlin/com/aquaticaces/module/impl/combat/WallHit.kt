package com.aquaticaces.module.impl.combat

import com.aquaticaces.core.RotationManager
import com.aquaticaces.core.RotationMode
import com.aquaticaces.core.TargetValidator
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import java.util.concurrent.ThreadLocalRandom

/**
 * Attacks the closest valid target without line-of-sight checks — hits through walls.
 */
class WallHit : Module("WallHit", "Attacks enemies through walls.", Category.COMBAT) {

    val range = NumberSetting("Range", 4.0, 1.0, 6.0, 0.1)
    val minCPS = NumberSetting("MinCPS", 8.0, 1.0, 20.0, 1.0)
    val maxCPS = NumberSetting("MaxCPS", 12.0, 1.0, 20.0, 1.0)
    val rotations = BooleanSetting("Rotations", true)
    val playersOnly = BooleanSetting("PlayersOnly", true)

    private var lastAttackTime = 0L
    private var nextAttackDelay = 0L

    init {
        addSettings(range, minCPS, maxCPS, rotations, playersOnly)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        var closest: LivingEntity? = null
        var closestDist = range.value

        for (entity in level.entitiesForRendering()) {
            if (entity !is LivingEntity) continue
            if (!TargetValidator.isValidCombatTarget(entity, playersOnly.value)) continue
            val dist = player.distanceTo(entity).toDouble()
            if (dist <= closestDist) {
                closestDist = dist
                closest = entity
            }
        }

        val target = closest ?: return
        val aimPos = Backtrack.delayedPosition(target)?.add(0.0, target.eyeHeight * 0.5, 0.0)
            ?: target.position().add(0.0, target.eyeHeight * 0.5, 0.0)

        if (rotations.value) {
            RotationManager.lookAt(
                aimPos.x, aimPos.y, aimPos.z,
                priority = 90,
                smooth = 0.35f,
                mode = RotationMode.CLIENT
            )
        }

        val now = System.currentTimeMillis()
        if (now - lastAttackTime < nextAttackDelay) return

        player.connection.send(ServerboundInteractPacket.createAttackPacket(target, player.isShiftKeyDown))
        if (!NoSwing.shouldHideSwing()) player.swing(InteractionHand.MAIN_HAND)

        val min = minCPS.value.coerceAtMost(maxCPS.value)
        val max = maxCPS.value.coerceAtLeast(minCPS.value)
        val targetCPS = ThreadLocalRandom.current().nextDouble(min, max + 0.1)
        nextAttackDelay = (1000.0 / targetCPS).toLong()
        lastAttackTime = now
    }
}
