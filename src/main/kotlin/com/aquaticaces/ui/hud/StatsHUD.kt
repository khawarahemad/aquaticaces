package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudLayout
import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.ui.UiStyle
import net.minecraft.client.Minecraft

/**
 * Minimal bottom-left player readout: health, armor and food as clean
 * shadowed text — no panels or bars.
 */
class StatsHUD {
    private val mc = Minecraft.getInstance()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (!HudSettings.isEnabled("stats")) return
        if (mc.options.hideGui) return
        val player = mc.player ?: return

        val g = event.guiGraphics
        val font = mc.font

        val hp = (player.health + player.absorptionAmount).toInt()
        val armor = player.armorValue
        val food = player.foodData.foodLevel
        val y = HudLayout.resolveY(HudLayout.positions.statsHudY, mc.window.guiScaledHeight.toFloat(), 12f).toInt()

        var x = HudLayout.resolveX(HudLayout.positions.statsHudX, mc.window.guiScaledWidth.toFloat()).toInt()
        x = pair(g, font, "HP", hp.toString(), x, y, UiStyle.SUCCESS)
        x = pair(g, font, "AR", armor.toString(), x, y, 0xFF7FA8FF.toInt())
        pair(g, font, "FD", food.toString(), x, y, 0xFFFFAA00.toInt())
    }

    private fun pair(g: net.minecraft.client.gui.GuiGraphics, font: net.minecraft.client.gui.Font, label: String, value: String, x: Int, y: Int, color: Int): Int {
        g.drawString(font, label, x, y, UiStyle.MUTED, true)
        var nx = x + font.width("$label ")
        g.drawString(font, value, nx, y, color, true)
        nx += font.width(value) + 8
        return nx
    }
}
