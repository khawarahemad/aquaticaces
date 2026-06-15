package com.aquaticaces.module.impl.utility

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.*
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ThreadLocalRandom

/**
 * ChestStealer module.
 * Automatically quick-moves high-value items (armor, weapons, golden apples, etc.)
 * from open chest containers into the player's inventory using randomized Gaussian delays.
 */
class ChestStealer : Module("ChestStealer", "Automatically steals items from containers.", Category.UTILITY) {

    val delay = NumberSetting("Delay", 80.0, 20.0, 200.0, 10.0)

    private var lastClickTime = 0L
    private var nextDelay = 0L

    init {
        addSettings(delay)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val screen = mc.screen ?: return

        // 1. Verify container screen
        if (screen is AbstractContainerScreen<*>) {
            val menu = screen.menu
            val chestSlotCount = menu.slots.size - 36 // 36 slots at the end belong to player inventory

            if (chestSlotCount <= 0) return

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < nextDelay) return

            var lootedItem = false

            // 2. Loop chest slots to transfer valuable items
            for (i in 0 until chestSlotCount) {
                val slot = menu.slots[i]
                val stack = slot.item

                if (!stack.isEmpty && isHighValue(stack)) {
                    mc.gameMode?.handleInventoryMouseClick(menu.containerId, i, 0, ClickType.QUICK_MOVE, player)
                    
                    lastClickTime = currentTime
                    // Compute next click delay using randomized Gaussian variance
                    val randomOffset = ThreadLocalRandom.current().nextGaussian() * 15.0
                    nextDelay = (delay.value + randomOffset).toLong().coerceAtLeast(0L)
                    
                    lootedItem = true
                    break
                }
            }

            // 3. Auto-close container if no high-value items remain
            if (!lootedItem) {
                var chestEmptyOfValuables = true
                for (i in 0 until chestSlotCount) {
                    val stack = menu.slots[i].item
                    if (!stack.isEmpty && isHighValue(stack)) {
                        chestEmptyOfValuables = false
                        break
                    }
                }
                if (chestEmptyOfValuables) {
                    player.closeContainer()
                }
            }
        }
    }

    /**
     * Identifies valuable loot items.
     */
    private fun isHighValue(stack: ItemStack): Boolean {
        val item = stack.item
        return item is ArmorItem ||
               item is SwordItem ||
               item is AxeItem ||
               item is BowItem ||
               item is CrossbowItem ||
               item == Items.GOLDEN_APPLE ||
               item == Items.ENCHANTED_GOLDEN_APPLE ||
               item == Items.END_CRYSTAL ||
               item == Items.OBSIDIAN ||
               item == Items.TOTEM_OF_UNDYING ||
               item == Items.EXPERIENCE_BOTTLE ||
               (item is PotionItem)
    }

    override fun onEnable() {
        super.onEnable()
        lastClickTime = 0L
        nextDelay = 0L
    }
}
