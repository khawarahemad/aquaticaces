package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ThreadLocalRandom

/**
 * TriggerBot module.
 * Automatically swings and attacks the targeted entity when looking directly at it.
 */
class TriggerBot : Module("TriggerBot", "Attacks entities in crosshair.", Category.COMBAT) {

    val minCPS = NumberSetting("MinCPS", 8.0, 1.0, 20.0, 1.0)
    val maxCPS = NumberSetting("MaxCPS", 12.0, 1.0, 20.0, 1.0)

    private var lastAttackTime = 0L
    private var nextAttackDelay = 0L

    init {
        addSettings(minCPS, maxCPS)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val hit = mc.hitResult ?: return

        if (hit is EntityHitResult) {
            val entity = hit.entity
            if (entity is LivingEntity && entity.isAlive && entity != player) {
                val now = System.currentTimeMillis()
                if (now - lastAttackTime >= nextAttackDelay) {
                    val interactPacket = ServerboundInteractPacket.createAttackPacket(entity, player.isShiftKeyDown)
                    player.connection.send(interactPacket)
                    player.swing(InteractionHand.MAIN_HAND)

                    val min = minCPS.value.coerceAtMost(maxCPS.value)
                    val max = maxCPS.value.coerceAtLeast(minCPS.value)
                    val targetCPS = ThreadLocalRandom.current().nextDouble(min, max + 0.1)
                    nextAttackDelay = (1000.0 / targetCPS).toLong()
                    lastAttackTime = now
                }
            }
        }
    }
}
