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
import net.minecraft.world.item.Items
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import java.util.concurrent.ThreadLocalRandom

/**
 * MaceAura — auto-attacks nearby targets with a mace, optionally jumping to
 * build fall distance for smash bonus damage (Minecraft 1.21+).
 */
class MaceAura : Module("MaceAura", "Auto mace smash attacks on nearby targets.", Category.COMBAT) {

    val targets = ModeSetting("Targets", "Players", listOf("Players", "Mobs", "All"))
    val range = NumberSetting("Range", 3.5, 2.0, 6.0, 0.1)
    val instant = BooleanSetting("Instant", true)
    val spoofFall = NumberSetting("SpoofFall", 30.0, 3.0, 200.0, 1.0)
    val fullCharge = BooleanSetting("FullCharge", true)
    val minFall = NumberSetting("MinFall", 1.5, 0.0, 10.0, 0.5)
    val minCPS = NumberSetting("MinCPS", 6.0, 1.0, 20.0, 1.0)
    val maxCPS = NumberSetting("MaxCPS", 10.0, 1.0, 20.0, 1.0)
    val requireMace = BooleanSetting("RequireMace", true)
    val autoSwitch = BooleanSetting("AutoSwitch", true)
    val autoJump = BooleanSetting("AutoJump", false)
    val smashOnly = BooleanSetting("SmashOnly", false)

    private var lastAttackTime = 0L
    private var nextAttackDelay = 0L

    init {
        addSettings(targets, range, instant, spoofFall, fullCharge, minFall, minCPS, maxCPS, requireMace, autoSwitch, autoJump, smashOnly)
        minFall.dependsOn(smashOnly, true)
        autoSwitch.dependsOn(requireMace, true)
        spoofFall.dependsOn(instant, true)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        if (requireMace.value) {
            if (autoSwitch.value) switchToMace()
            if (!holdingMace()) return
        }

        val target = findTarget(player) ?: return

        com.aquaticaces.core.RotationManager.lookAt(
            target.x, target.y + target.eyeHeight * 0.5, target.z,
            priority = 90,
            smooth = 0.35f,
            mode = com.aquaticaces.core.RotationMode.CLIENT
        )

        if (autoJump.value && !instant.value && player.onGround() && player.distanceTo(target) <= range.value + 0.5) {
            player.jumpFromGround()
        }

        // Without Instant, fall back to the legacy "must actually be falling" behaviour.
        if (!instant.value && smashOnly.value && player.fallDistance < minFall.value) return

        val now = System.currentTimeMillis()
        if (now - lastAttackTime < nextAttackDelay) return
        if (player.distanceTo(target) > range.value) return

        // A full-charge swing is required for the mace to deal its max smash damage.
        if (fullCharge.value && player.getAttackStrengthScale(0f) < 0.95f) return

        // Instant one-shot: fake fall distance to the server so the smash bonus applies
        // even while standing still or flying. A successful smash resets the server-side
        // fall distance, so this does not cause self fall damage.
        if (instant.value) spoofFallDistance(player)

        // Use the vanilla attack path: it calls ensureHasSentCarriedItem() first, so the
        // server registers the hit with the mace actually equipped (required for the smash).
        mc.gameMode?.attack(player, target)
        if (!NoSwing.shouldHideSwing()) player.swing(InteractionHand.MAIN_HAND)

        val min = minCPS.value.coerceAtMost(maxCPS.value)
        val max = maxCPS.value.coerceAtLeast(minCPS.value)
        nextAttackDelay = (1000.0 / ThreadLocalRandom.current().nextDouble(min, max + 0.1)).toLong()
        lastAttackTime = now
    }

    /**
     * Sends paired up/down movement packets so the server accumulates fall distance
     * without the client actually moving. Each pair adds one [step] of fall distance;
     * steps stay under the server's "moved too quickly" threshold to avoid rubber-banding.
     */
    private fun spoofFallDistance(player: Player) {
        val connection = mc.connection ?: return
        val x = player.x
        val y = player.y
        val z = player.z
        val step = 8.0
        var remaining = spoofFall.value
        while (remaining > 0.0) {
            val s = minOf(step, remaining)
            connection.send(ServerboundMovePlayerPacket.Pos(x, y + s, z, false))
            connection.send(ServerboundMovePlayerPacket.Pos(x, y, z, false))
            remaining -= s
        }
    }

    private fun findTarget(player: Player): LivingEntity? {
        val level = mc.level ?: return null
        var best: LivingEntity? = null
        var bestDist = range.value + 1
        for (entity in level.entitiesForRendering()) {
            if (entity !is LivingEntity) continue
            if (!isValidTarget(entity)) continue
            val dist = player.distanceTo(entity).toDouble()
            if (dist <= range.value && dist < bestDist) {
                bestDist = dist
                best = entity
            }
        }
        return best
    }

    private fun isValidTarget(entity: LivingEntity): Boolean {
        return when (targets.value) {
            "Players" -> com.aquaticaces.core.TargetValidator.isValidCombatTarget(entity, playersOnly = true)
            "Mobs" -> entity !is Player && com.aquaticaces.core.TargetValidator.isValidCombatTarget(entity, playersOnly = false)
            else -> com.aquaticaces.core.TargetValidator.isValidCombatTarget(entity, playersOnly = false)
        }
    }

    /** Selects a mace from the hotbar and syncs the slot change to the server. */
    private fun switchToMace(): Boolean {
        val player = mc.player ?: return false
        if (holdingMace()) return true
        for (i in 0 until 9) {
            if (player.inventory.getItem(i).`is`(Items.MACE)) {
                if (player.inventory.selected != i) {
                    player.inventory.selected = i
                    player.connection.send(ServerboundSetCarriedItemPacket(i))
                }
                return true
            }
        }
        return false
    }

    private fun holdingMace(): Boolean = mc.player?.mainHandItem?.`is`(Items.MACE) == true
}
