package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class AutoPot : Module("AutoPot", "Throws health potions when low.", Category.COMBAT) {
    val health = NumberSetting("Health", 14.0, 4.0, 20.0, 0.5)
    init { addSettings(health) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        if (player.health + player.absorptionAmount > health.value) return
        val slot = findSplashHeal()
        if (slot == -1) return
        val prev = player.inventory.selected
        player.inventory.selected = slot
        mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)
        player.inventory.selected = prev
    }

    private fun findSplashHeal(): Int {
        val inv = mc.player?.inventory ?: return -1
        for (i in 0..8) {
            val stack: ItemStack = inv.getItem(i)
            if (stack.item == Items.SPLASH_POTION && stack.hoverName.string.lowercase().contains("heal")) return i
        }
        return -1
    }
}
