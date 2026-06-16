package com.aquaticaces.module.impl.combat

import com.aquaticaces.core.TargetValidator
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

/**
 * Places and detonates beds near targets in the Nether or End.
 */
class BedAura : Module("BedAura", "Places and explodes beds on targets.", Category.COMBAT) {

    val range = NumberSetting("Range", 5.0, 2.0, 6.0, 0.1)
    val placeDelay = NumberSetting("Delay", 250.0, 0.0, 1000.0, 50.0)

    private var lastAction = 0L

    init {
        addSettings(range, placeDelay)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        if (!canBedExplode(level)) return

        val now = System.currentTimeMillis()
        if (now - lastAction < placeDelay.value) return

        val target = findTarget() ?: return
        val existingBed = findNearbyBed(player.blockPosition(), range.value.toInt())
        if (existingBed != null) {
            detonateBed(existingBed)
            lastAction = now
            return
        }

        val spot = findPlacementSpot(target) ?: return
        val hand = findBedHand() ?: return

        val hit = BlockHitResult(
            Vec3(spot.x + 0.5, spot.y.toDouble(), spot.z + 0.5),
            Direction.UP,
            spot.below(),
            false
        )
        player.connection.send(ServerboundUseItemOnPacket(hand, hit, 0))
        player.swing(hand)
        lastAction = now
    }

    private fun canBedExplode(level: Level): Boolean {
        val dim = level.dimension()
        return dim == Level.NETHER || dim == Level.END
    }

    private fun findTarget(): LivingEntity? {
        val player = mc.player ?: return null
        val level = mc.level ?: return null
        var closest: LivingEntity? = null
        var closestDist = range.value

        for (entity in level.entitiesForRendering()) {
            if (entity !is LivingEntity) continue
            if (!TargetValidator.isValidCombatTarget(entity)) continue
            val dist = player.distanceTo(entity).toDouble()
            if (dist < closestDist) {
                closestDist = dist
                closest = entity
            }
        }
        return closest
    }

    private fun findNearbyBed(origin: BlockPos, radius: Int): BlockPos? {
        val level = mc.level ?: return null
        for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                for (dz in -radius..radius) {
                    val pos = origin.offset(dx, dy, dz)
                    val state = level.getBlockState(pos)
                    if (state.block is BedBlock && state.getValue(BedBlock.PART) == BedPart.FOOT) {
                        return pos
                    }
                }
            }
        }
        return null
    }

    private fun findPlacementSpot(target: LivingEntity): BlockPos? {
        val level = mc.level ?: return null
        val player = mc.player ?: return null
        val targetPos = target.blockPosition()
        val radius = range.value.toInt()

        var best: BlockPos? = null
        var bestDist = Double.MAX_VALUE

        for (dx in -radius..radius) {
            for (dy in -1..1) {
                for (dz in -radius..radius) {
                    val foot = targetPos.offset(dx, dy, dz)
                    if (!canPlaceBedAt(foot)) continue
                    val dist = player.distanceToSqr(Vec3(foot.x + 0.5, foot.y + 0.5, foot.z + 0.5))
                    if (dist < bestDist) {
                        bestDist = dist
                        best = foot
                    }
                }
            }
        }
        return best
    }

    private fun canPlaceBedAt(foot: BlockPos): Boolean {
        val level = mc.level ?: return false
        if (!level.getBlockState(foot).isAir) return false
        if (level.getBlockState(foot.below()).isAir) return false

        for (facing in Direction.Plane.HORIZONTAL) {
            val head = foot.relative(facing)
            if (level.getBlockState(head).isAir) return true
        }
        return false
    }

    private fun detonateBed(foot: BlockPos) {
        val player = mc.player ?: return
        val hand = findBedHand() ?: InteractionHand.MAIN_HAND
        val hit = BlockHitResult(
            Vec3(foot.x + 0.5, foot.y + 0.5, foot.z + 0.5),
            Direction.UP,
            foot,
            false
        )
        player.connection.send(ServerboundUseItemOnPacket(hand, hit, 0))
        player.swing(hand)
    }

    private fun findBedHand(): InteractionHand? {
        val player = mc.player ?: return null
        if (player.mainHandItem.`is`(ItemTags.BEDS)) return InteractionHand.MAIN_HAND
        if (player.offhandItem.`is`(ItemTags.BEDS)) return InteractionHand.OFF_HAND
        return null
    }
}
