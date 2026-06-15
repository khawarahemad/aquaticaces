package com.aquaticaces.module.impl.player

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack

class AutoArmor : Module("AutoArmor", "Equips best armor automatically.", Category.PLAYER) {
    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val menu = player.inventoryMenu
        for (armorSlot in 5..8) {
            val current = menu.slots[armorSlot].item
            var bestInv = -1
            var bestValue = armorValue(current)
            for (i in 9..44) {
                val stack = menu.slots[i].item
                if (stack.item !is ArmorItem) continue
                val v = armorValue(stack)
                if (v > bestValue) { bestValue = v; bestInv = i }
            }
            if (bestInv != -1) {
                mc.gameMode?.handleInventoryMouseClick(menu.containerId, bestInv, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, player)
            }
        }
    }

    private fun armorValue(stack: ItemStack): Int {
        val item = stack.item
        if (item !is ArmorItem) return 0
        return item.defense
    }
}
