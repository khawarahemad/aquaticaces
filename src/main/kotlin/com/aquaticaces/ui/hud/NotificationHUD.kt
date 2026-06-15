package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudSettings
import com.aquaticaces.core.NotificationManager
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.ui.ClickGUI
import com.aquaticaces.ui.CubicBezier
import net.minecraft.client.Minecraft
import kotlin.math.roundToInt

class NotificationHUD {
    private val mc = Minecraft.getInstance()
    private val slideProgress = mutableMapOf<String, Float>()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return
        if (!HudSettings.isEnabled("notifications")) return
        val vectorRenderer = ClickGUI.vectorRenderer
        val fontRenderer = ClickGUI.fontRenderer
        val guiWidth = mc.window.guiScaledWidth.toFloat()
        var y = 8f

        for (note in NotificationManager.getActive()) {
            val key = "${note.title}:${note.message}:${note.createdAt}"
            val current = slideProgress.getOrDefault(key, 0f)
            slideProgress[key] = (current + 0.12f).coerceAtMost(1f)
            val slide = CubicBezier.easeOutBezier(slideProgress[key]!!)

            val age = System.currentTimeMillis() - note.createdAt
            val fade = (1f - (age.toFloat() / note.durationMs.toFloat())).coerceIn(0f, 1f)
            val text = "${note.title}: ${note.message}"
            val w = fontRenderer.getStringWidth("outfit", text, 10f) + 24f
            val x = guiWidth - (w + 8f) * slide
            val alpha = (fade * 220).roundToInt()
            val bg = (alpha shl 24) or 0x13141B
            val border = (alpha shl 24) or (note.color and 0xFFFFFF)

            vectorRenderer.drawRoundedRect(x, y, w, 20f, 4f, bg)
            vectorRenderer.drawRoundedRect(x, y + 4f, 3f, 12f, 1f, (alpha shl 24) or (note.color and 0xFFFFFF))
            vectorRenderer.drawMultiPassOutline(x, y, w, 20f, 4f, 1f, border, (alpha / 8 shl 24) or (note.color and 0xFFFFFF))
            fontRenderer.drawString("outfit", text, x + 10f, y + 5f, 10f, (alpha shl 24) or 0xFFFFFF)
            y += 24f
        }

        slideProgress.keys.retainAll { key ->
            NotificationManager.getActive().any { "${it.title}:${it.message}:${it.createdAt}" == key }
        }
    }
}
