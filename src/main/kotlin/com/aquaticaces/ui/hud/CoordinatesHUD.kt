package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.ui.UiStyle
import net.minecraft.client.Minecraft

/**
 * Minimal bottom-left coordinates readout as clean shadowed text.
 */
class CoordinatesHUD {
    private val mc = Minecraft.getInstance()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return
        if (!HudSettings.isEnabled("coordinates")) return
        val player = mc.player ?: return

        val g = event.guiGraphics
        val font = mc.font
        val facing = player.direction.name.lowercase().replaceFirstChar { it.uppercase() }
        val coords = String.format("%.0f, %.0f, %.0f", player.x, player.y, player.z)
        val y = mc.window.guiScaledHeight - 12

        var x = 6
        g.drawString(font, "XYZ", x, y, UiStyle.ACCENT, true)
        x += font.width("XYZ ")
        g.drawString(font, coords, x, y, UiStyle.TEXT, true)
        x += font.width(coords) + 6
        g.drawString(font, facing, x, y, UiStyle.MUTED, true)
    }
}
