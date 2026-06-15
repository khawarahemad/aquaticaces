package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem

class ShieldBreaker : Module("ShieldBreaker", "Breaks enemy shields with axe.", Category.COMBAT) {
    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        for (entity in level.entitiesForRendering()) {
            if (entity !is Player || entity === player) continue
            if (!entity.isUsingItem) continue
            if (player.distanceTo(entity) > 3.5f) continue
            val axeSlot = findAxeSlot()
            if (axeSlot == -1) continue
            player.inventory.selected = axeSlot
            player.connection.send(net.minecraft.network.protocol.game.ServerboundInteractPacket.createAttackPacket(entity, player.isShiftKeyDown))
            player.swing(InteractionHand.MAIN_HAND)
            break
        }
    }

    private fun findAxeSlot(): Int {
        val inv = mc.player?.inventory ?: return -1
        for (i in 0..8) if (inv.getItem(i).item is AxeItem) return i
        return -1
    }
}
