package com.aquaticaces.module.impl.player

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW

/**
 * InventoryManager module.
 * Automatically equips the best armor from inventory and purges garbage items (dirt, seeds, etc.).
 */
class InventoryManager : Module("InventoryManager", "Manages armor and drops garbage.", Category.PLAYER) {

    private var lastActionTime = 0L
    private val delay = 100L // Delay in ms between inventory actions

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        if (mc.screen != null) return // Only organize when no menu is open

        val now = System.currentTimeMillis()
        if (now - lastActionTime < delay) return

        val menu = player.inventoryMenu
        val containerId = menu.containerId

        // 1. Equip empty armor slots
        // Armor slots are 5 (head), 6 (chest), 7 (legs), 8 (feet)
        for (armorSlot in 5..8) {
            if (menu.slots[armorSlot].item.isEmpty) {
                // Find matching armor piece in inventory (slots 9 to 44)
                for (invSlot in 9..44) {
                    val stack = menu.slots[invSlot].item
                    val item = stack.item
                    if (item is ArmorItem) {
                        val matchesSlot = when (armorSlot) {
                            5 -> item.type == ArmorItem.Type.HELMET
                            6 -> item.type == ArmorItem.Type.CHESTPLATE
                            7 -> item.type == ArmorItem.Type.LEGGINGS
                            8 -> item.type == ArmorItem.Type.BOOTS
                            else -> false
                        }
                        if (matchesSlot) {
                            mc.gameMode?.handleInventoryMouseClick(containerId, invSlot, 0, ClickType.QUICK_MOVE, player)
                            lastActionTime = now
                            return
                        }
                    }
                }
            }
        }

        // 2. Drop garbage items (cobblestone, dirt, seeds, rotten flesh, etc.)
        val garbageItems = setOf(
            Items.COBBLESTONE, Items.DIRT, Items.GRAVEL, Items.ROTTEN_FLESH,
            Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS
        )

        for (invSlot in 9..44) {
            val stack = menu.slots[invSlot].item
            if (!stack.isEmpty && garbageItems.contains(stack.item)) {
                // Throw slot stack
                mc.gameMode?.handleInventoryMouseClick(containerId, invSlot, 1, ClickType.THROW, player)
                lastActionTime = now
                return
            }
        }
    }
}
