package com.aquaticaces.module.impl.utility

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW

/**
 * AutoTotem module.
 * Automatically swaps a Totem of Undying into the player's offhand slot from
 * their inventory when health drops below the configured threshold.
 */
class AutoTotem : Module("AutoTotem", "Swaps totems to the offhand automatically.", Category.UTILITY) {

    val health = NumberSetting("Health", 10.0, 2.0, 20.0, 0.5)

    init {
        addSettings(health)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return

        val currentHP = player.health + player.absorptionAmount
        if (currentHP > health.value) return

        // 1. Verify offhand status
        if (player.offhandItem.item == Items.TOTEM_OF_UNDYING) return

        // 2. Find totem slot in player's inventory menu container
        val menu = player.inventoryMenu
        var totemSlotIndex = -1

        // Scan slots 9 to 44 (standard main inventory and hotbar)
        for (i in 9..44) {
            val stack = menu.slots[i].item
            if (stack.item == Items.TOTEM_OF_UNDYING) {
                totemSlotIndex = i
                break
            }
        }

        // 3. Perform the fast swap sequence if totem is found
        if (totemSlotIndex != -1) {
            val containerId = menu.containerId
            
            // Pick up the Totem
            mc.gameMode?.handleInventoryMouseClick(containerId, totemSlotIndex, 0, ClickType.PICKUP, player)
            // Swap with offhand slot (index 45)
            mc.gameMode?.handleInventoryMouseClick(containerId, 45, 0, ClickType.PICKUP, player)
            // Put down the replaced item back into the source slot
            mc.gameMode?.handleInventoryMouseClick(containerId, totemSlotIndex, 0, ClickType.PICKUP, player)
        }
    }
}
