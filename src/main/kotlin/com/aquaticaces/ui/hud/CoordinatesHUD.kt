package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.ui.ClickGUI
import net.minecraft.client.Minecraft

class CoordinatesHUD {
    private val mc = Minecraft.getInstance()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return
        if (!HudSettings.isEnabled("coordinates")) return
        val player = mc.player ?: return
        val font = ClickGUI.fontRenderer
        val vector = ClickGUI.vectorRenderer

        val text = String.format("XYZ: %.1f / %.1f / %.1f  Facing: %s", player.x, player.y, player.z, player.direction.name)
        val w = font.getStringWidth("outfit", text, 9f) + 12f
        val x = 8f
        val y = mc.window.guiScaledHeight - 22f

        vector.drawRoundedRect(x, y, w, 14f, 3f, 0xAA13141B.toInt())
        font.drawString("outfit", text, x + 6f, y + 2f, 9f, 0xFFCCCCCC.toInt())
    }
}
