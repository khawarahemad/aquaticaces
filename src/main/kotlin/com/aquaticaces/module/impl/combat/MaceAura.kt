package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import java.util.concurrent.ThreadLocalRandom

/**
 * MaceAura — auto-attacks nearby targets with a mace, optionally jumping to
 * build fall distance for smash bonus damage (Minecraft 1.21+).
 */
class MaceAura : Module("MaceAura", "Auto mace smash attacks on nearby targets.", Category.COMBAT) {

    val range = NumberSetting("Range", 3.5, 2.0, 6.0, 0.1)
    val minFall = NumberSetting("MinFall", 1.5, 0.0, 10.0, 0.5)
    val minCPS = NumberSetting("MinCPS", 6.0, 1.0, 20.0, 1.0)
    val maxCPS = NumberSetting("MaxCPS", 10.0, 1.0, 20.0, 1.0)
    val autoSwitch = BooleanSetting("AutoSwitch", true)
    val autoJump = BooleanSetting("AutoJump", true)
    val smashOnly = BooleanSetting("SmashOnly", false)

    private var lastAttackTime = 0L
    private var nextAttackDelay = 0L

    init {
        addSettings(range, minFall, minCPS, maxCPS, autoSwitch, autoJump, smashOnly)
        minFall.dependsOn(smashOnly, true)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        if (autoSwitch.value && !switchToMace()) return
        if (!holdingMace()) return

        val target = findTarget(player) ?: return

        com.aquaticaces.core.RotationManager.lookAt(
            target.x, target.y + target.eyeHeight * 0.5, target.z,
            priority = 90,
            smooth = 0.35f,
            mode = com.aquaticaces.core.RotationMode.CLIENT
        )

        if (autoJump.value && player.onGround() && player.distanceTo(target) <= range.value + 0.5) {
            player.jumpFromGround()
        }

        if (smashOnly.value && player.fallDistance < minFall.value) return

        val now = System.currentTimeMillis()
        if (now - lastAttackTime < nextAttackDelay) return
        if (player.distanceTo(target) > range.value) return

        player.connection.send(ServerboundInteractPacket.createAttackPacket(target, player.isShiftKeyDown))
        if (!NoSwing.shouldHideSwing()) player.swing(InteractionHand.MAIN_HAND)

        val min = minCPS.value.coerceAtMost(maxCPS.value)
        val max = maxCPS.value.coerceAtLeast(minCPS.value)
        nextAttackDelay = (1000.0 / ThreadLocalRandom.current().nextDouble(min, max + 0.1)).toLong()
        lastAttackTime = now
    }

    private fun findTarget(player: net.minecraft.world.entity.player.Player): LivingEntity? {
        val level = mc.level ?: return null
        var best: LivingEntity? = null
        var bestDist = range.value + 1
        for (entity in level.entitiesForRendering()) {
            if (entity !is LivingEntity) continue
            if (!com.aquaticaces.core.TargetValidator.isValidCombatTarget(entity)) continue
            val dist = player.distanceTo(entity).toDouble()
            if (dist <= range.value && dist < bestDist) {
                bestDist = dist
                best = entity
            }
        }
        return best
    }

    private fun switchToMace(): Boolean {
        val player = mc.player ?: return false
        if (holdingMace()) return true
        for (i in 0 until 9) {
            if (player.inventory.getItem(i).`is`(Items.MACE)) {
                player.inventory.selected = i
                return true
            }
        }
        return false
    }

    private fun holdingMace(): Boolean = mc.player?.mainHandItem?.`is`(Items.MACE) == true
}
