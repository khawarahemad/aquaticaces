package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.module.impl.utility.PingSpoof
import com.aquaticaces.ui.UiStyle
import net.minecraft.client.Minecraft

/**
 * Minimal top-right performance readout: FPS, ping and memory as clean
 * shadowed text — no panels or graphs.
 */
class PerformanceHUD {
    private val mc = Minecraft.getInstance()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return
        if (!HudSettings.isEnabled("performance")) return

        val g = event.guiGraphics
        val font = mc.font
        val right = mc.window.guiScaledWidth - 6
        var y = 6

        val fps = mc.fps
        val fpsCol = if (fps >= 60) UiStyle.SUCCESS else if (fps >= 30) UiStyle.ACCENT else 0xFFFF4466.toInt()
        line(g, font, "$fps fps", right, y, fpsCol); y += 11

        val ping = pingMs()
        if (ping >= 0) {
            val pingCol = if (ping < 80) UiStyle.SUCCESS else if (ping < 200) UiStyle.ACCENT else 0xFFFF4466.toInt()
            line(g, font, "$ping ms", right, y, pingCol); y += 11
        }

        val usedMb = ((Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }) / (1024 * 1024)).toInt()
        line(g, font, "$usedMb mb", right, y, UiStyle.MUTED)
    }

    private fun line(g: net.minecraft.client.gui.GuiGraphics, font: net.minecraft.client.gui.Font, text: String, right: Int, y: Int, color: Int) {
        g.drawString(font, text, right - font.width(text), y, color, true)
    }

    private fun pingMs(): Int {
        val player = mc.player ?: return -1
        val info = mc.connection?.getPlayerInfo(player.uuid) ?: return -1
        return PingSpoof.displayPing(info.latency)
    }
}
