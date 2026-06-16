package com.aquaticaces.ui.components

import com.aquaticaces.core.ClientTheme
import com.aquaticaces.ui.FontRenderer
import com.aquaticaces.ui.UiEngine
import com.aquaticaces.ui.VectorRenderer

class VectorMenuButton(
    val label: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val enabled: Boolean = true,
    val onClick: () -> Unit
) {
    var hoverProgress = 0f

    fun contains(mouseX: Double, mouseY: Double): Boolean {
        return enabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }

    fun tickHovered(hovered: Boolean) {
        val target = if (hovered) 1f else 0f
        hoverProgress += (target - hoverProgress) * 0.22f
    }

    fun render(vr: VectorRenderer, fr: FontRenderer) {
        val hover = hoverProgress
        val bgAlpha = if (enabled) ((0x28 + hover * 0x38) * 255).toInt().coerceIn(0, 255) else 0x18
        val bg = (bgAlpha shl 24) or (if (hover > 0.1f) 0x0072FF else 0x131521)
        val border = ((0x44 + hover * 0xBB).toInt() shl 24) or (ClientTheme.accentLeft and 0xFFFFFF)

        vr.drawDropShadow(x, y, width, height, 4f, 8f, (0x55000000))
        vr.drawRoundedRect(x, y, width, height, 4f, bg)
        vr.drawMultiPassOutline(x, y, width, height, 4f, 1f, border, (0x22 shl 24) or (ClientTheme.accentLeft and 0xFFFFFF))

        val font = UiEngine.fontName
        val textW = fr.getStringWidth(font, label, 11f)
        val textColor = when {
            !enabled -> 0xFF585864.toInt()
            hover > 0.1f -> 0xFF00C6FF.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
        fr.drawString(font, label, x + width / 2f - textW / 2f, y + height / 2f - 5f, 11f, textColor)
    }

    fun click() {
        if (enabled) onClick()
    }
}
