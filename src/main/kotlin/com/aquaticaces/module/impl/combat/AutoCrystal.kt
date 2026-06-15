package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.boss.enderdragon.EndCrystal
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import kotlin.math.sqrt

/**
 * AutoCrystal module.
 * Scans, places, and explodes end crystals based on real-time damage calculations
 * to optimize damage dealt while mitigating self-damage.
 */
class AutoCrystal : Module("AutoCrystal", "Automatically places and detonates crystals.", Category.COMBAT) {

    val range = NumberSetting("Range", 5.0, 1.0, 6.0, 0.1)
    val targetRange = NumberSetting("TargetRange", 12.0, 5.0, 15.0, 0.5)
    val maxSelfDamage = NumberSetting("MaxSelf", 6.0, 1.0, 20.0, 0.5)
    val minEnemyDamage = NumberSetting("MinEnemy", 8.0, 1.0, 20.0, 0.5)

    init {
        addSettings(range, targetRange, maxSelfDamage, minEnemyDamage)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        // 1. Locate closest enemy entity
        var closestTarget: LivingEntity? = null
        var closestDist = targetRange.value
        for (entity in level.entitiesForRendering()) {
            if (entity === player || entity !is LivingEntity || !entity.isAlive) continue
            val dist = player.distanceTo(entity).toDouble()
            if (dist < closestDist) {
                closestDist = dist
                closestTarget = entity
            }
        }

        val target = closestTarget ?: return

        // 2. Break Existing Crystals
        var crystalToBreak: EndCrystal? = null
        var bestBreakDamage = 0.0
        for (entity in level.entitiesForRendering()) {
            if (entity !is EndCrystal || !entity.isAlive) continue
            val dist = player.distanceTo(entity)
            if (dist > range.value) continue

            val crystalPos = entity.position()
            val enemyDamage = calculateDamage(crystalPos, target)
            val selfDamage = calculateDamage(crystalPos, player)

            if (selfDamage <= maxSelfDamage.value && enemyDamage >= minEnemyDamage.value) {
                if (enemyDamage > bestBreakDamage) {
                    bestBreakDamage = enemyDamage
                    crystalToBreak = entity
                }
            }
        }

        if (crystalToBreak != null) {
            val interactPacket = ServerboundInteractPacket.createAttackPacket(crystalToBreak, player.isShiftKeyDown)
            player.connection.send(interactPacket)
            player.swing(InteractionHand.MAIN_HAND)
            return // Skip placing in the same tick if we just detonated one
        }

        // 3. Scan & Place Crystals
        val crystalHand = when (Items.END_CRYSTAL) {
            player.mainHandItem.item -> InteractionHand.MAIN_HAND
            player.offhandItem.item -> InteractionHand.OFF_HAND
            else -> null
        } ?: return // Require holding a crystal

        val playerPos = player.blockPosition()
        val radius = 5
        var bestBlock: BlockPos? = null
        var bestPlaceDamage = 0.0

        for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                for (dz in -radius..radius) {
                    val pos = playerPos.offset(dx, dy, dz)
                    val block = level.getBlockState(pos).block
                    if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
                        // Ensure there is space above the block for the crystal
                        if (level.getBlockState(pos.above()).isAir) {
                            val crystalPos = Vec3(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5)
                            val distToPlayer = player.position().distanceTo(crystalPos)
                            if (distToPlayer > range.value) continue

                            val enemyDamage = calculateDamage(crystalPos, target)
                            val selfDamage = calculateDamage(crystalPos, player)

                            if (selfDamage <= maxSelfDamage.value && enemyDamage >= minEnemyDamage.value) {
                                if (enemyDamage > bestPlaceDamage) {
                                    bestPlaceDamage = enemyDamage
                                    bestBlock = pos
                                }
                            }
                        }
                    }
                }
            }
        }

        val targetBlock = bestBlock ?: return
        val hitResult = BlockHitResult(
            Vec3(targetBlock.x + 0.5, targetBlock.y + 1.0, targetBlock.z + 0.5),
            Direction.UP,
            targetBlock,
            false
        )

        // Send placement packet
        val placePacket = ServerboundUseItemOnPacket(crystalHand, hitResult, 0)
        player.connection.send(placePacket)
        player.swing(crystalHand)
    }

    /**
     * Compute explosion physics damage to an entity from explosion source position.
     */
    private fun calculateDamage(explosionPos: Vec3, entity: LivingEntity): Double {
        val dist = entity.position().distanceTo(explosionPos)
        val maxDist = 12.0 // Crystal explosion radius (power 6.0 * 2)
        if (dist > maxDist) return 0.0

        val impact = 1.0 - (dist / maxDist)
        val rawDamage = ((impact * impact + impact) / 2.0 * 7.0 * maxDist + 1.0)

        // For simplicity, verify line-of-sight exposure
        val hasLineOfSight = entity.level().clip(
            net.minecraft.world.level.ClipContext(
                explosionPos,
                entity.eyePosition,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                entity
            )
        ).type == net.minecraft.world.phys.HitResult.Type.MISS

        val exposure = if (hasLineOfSight) 1.0 else 0.5
        return rawDamage * exposure
    }
}
