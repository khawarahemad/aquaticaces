package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudSettings
import com.aquaticaces.core.NotificationManager
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.ui.CubicBezier
import com.aquaticaces.ui.UiStyle
import net.minecraft.client.Minecraft

/**
 * Minimal bottom-right notifications: a thin accent bar and one clean line of
 * shadowed text per toast — no heavy cards.
 */
class NotificationHUD {
    private val mc = Minecraft.getInstance()
    private val slideProgress = mutableMapOf<String, Float>()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return
        if (!HudSettings.isEnabled("notifications")) return

        val g = event.guiGraphics
        val font = mc.font
        val guiWidth = mc.window.guiScaledWidth
        var y = mc.window.guiScaledHeight - 18

        for (note in NotificationManager.getActive()) {
            val key = "${note.title}:${note.message}:${note.createdAt}"
            val current = slideProgress.getOrDefault(key, 0f)
            slideProgress[key] = (current + 0.16f).coerceAtMost(1f)
            val slide = CubicBezier.easeOutBezier(slideProgress[key]!!)

            val age = System.currentTimeMillis() - note.createdAt
            val fade = (1f - (age.toFloat() / note.durationMs.toFloat())).coerceIn(0f, 1f)
            if (fade <= 0f) continue

            val text = "${note.title}  ${note.message}"
            val tw = font.width(text)
            val x = guiWidth - (tw + 16) * slide
            val xi = x.toInt()

            g.fill(xi, y - 1, xi + 2, y + 9, note.color)
            g.drawString(font, note.title, xi + 6, y, note.color, true)
            g.drawString(font, note.message, xi + 6 + font.width("${note.title} "), y, UiStyle.TEXT, true)
            y -= 12
        }

        slideProgress.keys.retainAll { key ->
            NotificationManager.getActive().any { "${it.title}:${it.message}:${it.createdAt}" == key }
        }
    }
}
