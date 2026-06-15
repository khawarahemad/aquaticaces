package com.aquaticaces.module.impl.player

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW

/**
 * AutoEat module.
 * Automatically switches to and consumes food items from the hotbar when hunger drops.
 */
class AutoEat : Module("AutoEat", "Automatically eats food from hotbar.", Category.PLAYER) {

    val hungerThreshold = NumberSetting("Hunger", 14.0, 5.0, 19.0, 1.0)

    private var originalSlot = -1
    private var isEating = false

    init {
        addSettings(hungerThreshold)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return

        val foodData = player.foodData
        val hunger = foodData.foodLevel

        if (isEating) {
            // Check if eating completed
            if (!player.isUsingItem || hunger >= 20) {
                isEating = false
                if (originalSlot != -1) {
                    player.inventory.selected = originalSlot
                    originalSlot = -1
                }
                mc.options.keyUse.isDown = false
            } else {
                // Keep holding right click
                mc.options.keyUse.isDown = true
            }
            return
        }

        if (hunger <= hungerThreshold.value.toInt()) {
            // Find food in hotbar (slots 0..8)
            var foodSlot = -1
            for (i in 0..8) {
                val stack = player.inventory.getItem(i)
                if (!stack.isEmpty && isFoodItem(stack)) {
                    foodSlot = i
                    break
                }
            }

            if (foodSlot != -1) {
                originalSlot = player.inventory.selected
                player.inventory.selected = foodSlot
                isEating = true
                
                // Trigger right-click block use
                mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)
                mc.options.keyUse.isDown = true
            }
        }
    }

    private fun isFoodItem(stack: ItemStack): Boolean {
        val item = stack.item
        return item == Items.COOKED_BEEF ||
               item == Items.COOKED_PORKCHOP ||
               item == Items.COOKED_CHICKEN ||
               item == Items.COOKED_MUTTON ||
               item == Items.COOKED_SALMON ||
               item == Items.COOKED_COD ||
               item == Items.GOLDEN_CARROT ||
               item == Items.BREAD ||
               item == Items.BAKED_POTATO ||
               item == Items.APPLE
    }

    override fun onDisable() {
        super.onDisable()
        if (isEating && originalSlot != -1) {
            mc.player?.inventory?.selected = originalSlot
        }
        isEating = false
        originalSlot = -1
        mc.options.keyUse.isDown = false
    }
}
