package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.ui.ClickGUI
import com.aquaticaces.ui.CubicBezier
import net.minecraft.client.Minecraft
import java.util.ArrayDeque
import kotlin.math.roundToInt

class PerformanceHUD {
    private val mc = Minecraft.getInstance()
    private val fpsHistory = ArrayDeque<Int>()
    private var lastFpsSample = 0L

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return
        if (!HudSettings.isEnabled("performance")) return
        val now = System.currentTimeMillis()
        if (now - lastFpsSample > 250) {
            fpsHistory.addLast(mc.fps)
            while (fpsHistory.size > 40) fpsHistory.removeFirst()
            lastFpsSample = now
        }

        val vector = ClickGUI.vectorRenderer
        val font = ClickGUI.fontRenderer
        val guiW = mc.window.guiScaledWidth.toFloat()
        val x = guiW - 130f
        val y = 8f
        val w = 120f
        val h = 52f

        vector.drawRoundedRect(x, y, w, h, 5f, 0xDD13141B.toInt())
        vector.drawMultiPassOutline(x, y, w, h, 5f, 1f, 0xFF2A2E3D.toInt(), 0x1A00C6FF)

        val runtime = Runtime.getRuntime()
        val usedMb = ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)).toInt()
        val fps = mc.fps
        font.drawString("outfit", "FPS: $fps", x + 8f, y + 6f, 10f, 0xFFFFFFFF.toInt())
        font.drawString("outfit", "RAM: ${usedMb}MB", x + 8f, y + 18f, 9f, 0xFFAAAAAA.toInt())

        val graphX = x + 8f
        val graphY = y + 30f
        val graphW = w - 16f
        val graphH = 16f
        vector.drawRoundedRect(graphX, graphY, graphW, graphH, 2f, 0xFF242630.toInt())

        if (fpsHistory.isNotEmpty()) {
            val max = (fpsHistory.maxOrNull() ?: 60).coerceAtLeast(30)
            var px = graphX
            val step = graphW / fpsHistory.size.coerceAtLeast(1)
            var prevY = graphY + graphH
            for (sample in fpsHistory) {
                val pct = (sample.toFloat() / max).coerceIn(0f, 1f)
                val py = graphY + graphH - graphH * CubicBezier.easeInOutBezier(pct)
                vector.drawRoundedRect(px, py, step.coerceAtLeast(1f), graphH - (py - graphY), 0f, 0xFF00C6FF.toInt())
                px += step
            }
        }
    }
}
