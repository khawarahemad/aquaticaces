package com.aquaticaces.ui.components

import com.aquaticaces.core.ClientTheme
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.ui.FontRenderer
import com.aquaticaces.ui.VectorRenderer
import com.aquaticaces.util.ColorUtils

class ColorComponent(
    val setting: ColorSetting,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) : UIComponent(x, y, width, height) {

    private var draggingHue = false
    private var draggingSv = false
    private var draggingAlpha = false

    private var hue = 0f
    private var sat = 1f
    private var value = 1f

    init { syncFromSetting() }

    private fun syncFromSetting() {
        val hsv = ColorUtils.rgbToHsv(setting.red, setting.green, setting.blue)
        hue = hsv.h
        sat = hsv.s
        value = hsv.v
    }

    private fun applyColor() {
        setting.value = ColorUtils.hsvToArgb(hue, sat, value, setting.alpha)
        ClientTheme.syncFromClickGUI()
    }

    override fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val boxX = x + 12f
        val boxW = width - 24f
        var rowY = y

        fontRenderer.drawString("outfit", setting.name, boxX, rowY + 1f, 9f, 0xFFBBBBBB.toInt())
        vectorRenderer.drawRoundedRect(x + width - 34f, rowY, 18f, 12f, 3f, setting.value)
        rowY += 14f

        val svH = 36f
        val svY = rowY
        vectorRenderer.drawRoundedRect(boxX, svY, boxW, svH, 3f, ColorUtils.hsvToArgb(hue, 1f, 1f))
        vectorRenderer.drawLinearGradientRect(boxX, svY, boxW, svH, 3f, boxX, svY, boxX + boxW, svY, 0xFFFFFFFF.toInt(), 0x00FFFFFF)
        vectorRenderer.drawLinearGradientRect(boxX, svY, boxW, svH, 3f, boxX, svY + svH, boxX, svY, 0x00000000, 0xFF000000.toInt())

        if (draggingSv) {
            sat = ((mouseX - boxX) / boxW).coerceIn(0f, 1f)
            value = 1f - ((mouseY - svY) / svH).coerceIn(0f, 1f)
            applyColor()
        }

        val cx = boxX + sat * boxW - 2f
        val cy = svY + (1f - value) * svH - 2f
        vectorRenderer.drawRoundedRect(cx, cy, 4f, 4f, 2f, 0xFFFFFFFF.toInt())

        rowY += svH + 4f
        val hueH = 8f
        for (i in 0 until boxW.toInt()) {
            val h = (i / boxW) * 360f
            vectorRenderer.drawRoundedRect(boxX + i, rowY, 1f, hueH, 0f, ColorUtils.hsvToArgb(h, 1f, 1f))
        }
        if (draggingHue) {
            hue = ((mouseX - boxX) / boxW * 360f).coerceIn(0f, 359.9f)
            applyColor()
        }
        vectorRenderer.drawRoundedRect(boxX + (hue / 360f) * boxW - 1f, rowY - 1f, 2f, hueH + 2f, 1f, 0xFFFFFFFF.toInt())

        rowY += hueH + 4f
        val alphaY = rowY
        vectorRenderer.drawRoundedRect(boxX, alphaY, boxW, 4f, 2f, 0xFF2B2D38.toInt())
        vectorRenderer.drawLinearGradientRect(boxX, alphaY, boxW * (setting.alpha / 255f), 4f, 2f, boxX, alphaY, boxX + boxW, alphaY, setting.value, setting.value and 0x00FFFFFF)
        if (draggingAlpha) {
            val a = ((mouseX - boxX) / boxW * 255f).toInt().coerceIn(0, 255)
            setting.setColor(setting.red, setting.green, setting.blue, a)
            ClientTheme.syncFromClickGUI()
        }

        height = 78f
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button != 0) return false
        val boxX = x + 12f
        val boxW = width - 24f
        val svY = y + 14f
        val svH = 36f
        if (mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= svY && mouseY <= svY + svH) {
            draggingSv = true
            return true
        }
        val hueY = svY + svH + 4f
        if (mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= hueY && mouseY <= hueY + 8f) {
            draggingHue = true
            return true
        }
        val alphaY = hueY + 12f
        if (mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= alphaY && mouseY <= alphaY + 4f) {
            draggingAlpha = true
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0) {
            draggingHue = false
            draggingSv = false
            draggingAlpha = false
        }
        return false
    }

    override fun keyTyped(keyCode: Int): Boolean = false
}
