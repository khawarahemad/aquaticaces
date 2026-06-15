package com.aquaticaces.ui.components

import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.core.ClientTheme
import com.aquaticaces.module.setting.ModeSetting
import com.aquaticaces.module.setting.NumberSetting
import com.aquaticaces.ui.CubicBezier
import com.aquaticaces.ui.VectorRenderer
import com.aquaticaces.ui.FontRenderer

/**
 * Visual checkbox representing BooleanSetting.
 */
class BooleanComponent(
    val setting: BooleanSetting,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) : UIComponent(x, y, width, height) {

    private var switchProgress = if (setting.value) 1.0f else 0.0f

    override fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val target = if (setting.value) 1.0f else 0.0f
        switchProgress += (target - switchProgress) * 0.2f
        val eased = CubicBezier.easeInOutBezier(switchProgress)

        // Draw property name
        fontRenderer.drawString("outfit", setting.name, x + 12f, y + 3f, 10f, 0xFFBBBBBB.toInt())

        // Toggle slot frame bounds
        val switchW = 18f
        val switchH = 9f
        val sx = x + width - 26f
        val sy = y + 4f

        val frameColor = CubicBezier.interpolateColor(0xFF2B2D38.toInt(), 0xFF0072FF.toInt(), eased)
        vectorRenderer.drawRoundedRect(sx, sy, switchW, switchH, 4.5f, frameColor)

        // Slide knob
        val knobSize = 7f
        val knobX = sx + 1f + (switchW - knobSize - 2f) * eased
        val knobY = sy + 1f
        vectorRenderer.drawRoundedRect(knobX, knobY, knobSize, knobSize, 3.5f, 0xFFFFFFFF.toInt())
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val inBounds = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
        if (inBounds && button == 0) {
            setting.value = !setting.value
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean = false
    override fun keyTyped(keyCode: Int): Boolean = false
}

/**
 * Slide ruler representing NumberSetting.
 */
class NumberComponent(
    val setting: NumberSetting,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) : UIComponent(x, y, width, height) {

    private var isDragging = false

    override fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val sliderX = x + 12f
        val sliderWidth = width - 24f
        val sliderY = y + 10f
        val sliderHeight = 3f

        // Handle active sliding motions
        if (isDragging) {
            val offset = (mouseX - sliderX).coerceIn(0f, sliderWidth)
            val percentage = offset / sliderWidth
            val raw = setting.min + percentage * (setting.max - setting.min)
            val rounded = Math.round(raw / setting.increment) * setting.increment
            setting.value = rounded.coerceIn(setting.min, setting.max)
        }

        val fillPercent = ((setting.value - setting.min) / (setting.max - setting.min)).toFloat().coerceIn(0f, 1f)

        // Draw property details
        fontRenderer.drawString("outfit", setting.name, x + 12f, y + 0f, 9f, 0xFFBBBBBB.toInt())
        val valStr = String.format("%.1f", setting.value)
        val valWidth = fontRenderer.getStringWidth("outfit", valStr, 9f)
        fontRenderer.drawString("outfit", valStr, x + width - 12f - valWidth, y + 0f, 9f, 0xFF00C6FF.toInt())

        // Slider track
        vectorRenderer.drawRoundedRect(sliderX, sliderY, sliderWidth, sliderHeight, 1.5f, 0xFF2B2D38.toInt())

        // Slider fill
        if (fillPercent > 0f) {
            val activeColorLeft = 0xFF00C6FF.toInt()
            val activeColorRight = 0xFF0072FF.toInt()
            vectorRenderer.drawLinearGradientRect(
                sliderX, sliderY, sliderWidth * fillPercent, sliderHeight, 1.5f,
                sliderX, sliderY, sliderX + sliderWidth, sliderY,
                activeColorLeft, activeColorRight
            )
        }

        // Render thumb handle if hovered/dragging
        val isHovered = mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= y && mouseY <= y + height
        if (isHovered || isDragging) {
            val knobX = sliderX + sliderWidth * fillPercent - 2f
            val knobY = sliderY - 1.5f
            vectorRenderer.drawRoundedRect(knobX, knobY, 4f, 6f, 2f, 0xFFFFFFFF.toInt())
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val sliderX = x + 12f
        val sliderWidth = width - 24f
        val inBounds = mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= y && mouseY <= y + height
        if (inBounds && button == 0) {
            isDragging = true
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0) {
            if (isDragging) ClientTheme.syncFromClickGUI()
            isDragging = false
        }
        return false
    }

    override fun keyTyped(keyCode: Int): Boolean = false
}

/**
 * Dropdown box representing ModeSetting.
 */
class ModeComponent(
    val setting: ModeSetting,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) : UIComponent(x, y, width, height) {

    private var isOpen = false
    private var openProgress = 0.0f

    override fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val boxX = x + 12f
        val boxWidth = width - 24f
        val boxY = y + 2f
        val boxHeight = 13f

        // Draw property name
        fontRenderer.drawString("outfit", setting.name, x + 12f, y + 4f, 10f, 0xFFBBBBBB.toInt())

        // Draw box frame
        val boxBg = 0xFF1E2028.toInt()
        vectorRenderer.drawRoundedRect(boxX, boxY, boxWidth, boxHeight, 3f, boxBg)
        vectorRenderer.drawRoundedOutline(boxX, boxY, boxWidth, boxHeight, 3f, 1f, 0xFF2B2D38.toInt())

        // Center active string
        val activeVal = setting.value
        val valW = fontRenderer.getStringWidth("outfit", activeVal, 9f)
        fontRenderer.drawString("outfit", activeVal, boxX + (boxWidth - valW) / 2f, boxY + 2f, 9f, 0xFF00C6FF.toInt())

        // Expand dropdown menu
        val target = if (isOpen) 1.0f else 0.0f
        openProgress += (target - openProgress) * 0.2f
        val eased = CubicBezier.easeInOutBezier(openProgress)

        var totalHeight = height
        if (eased > 0.01f) {
            val dropdownY = boxY + boxHeight + 1f
            val itemHeight = 12f
            val menuHeight = (setting.modes.size * itemHeight) * eased

            // Dropdown menu frame
            vectorRenderer.drawRoundedRect(boxX, dropdownY, boxWidth, menuHeight, 3f, 0xFF14151B.toInt())
            vectorRenderer.drawRoundedOutline(boxX, dropdownY, boxWidth, menuHeight, 3f, 1f, 0xFF2B2D38.toInt())

            setting.modes.forEachIndexed { idx, mode ->
                val modeItemY = dropdownY + (idx * itemHeight)
                if (modeItemY + itemHeight <= dropdownY + menuHeight) {
                    val isHovered = mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= modeItemY && mouseY <= modeItemY + itemHeight
                    val isSelected = mode == setting.value
                    
                    val bg = when {
                        isSelected -> 0x4000C6FF.toInt()
                        isHovered -> 0x20FFFFFF.toInt()
                        else -> 0x00000000
                    }
                    if (bg != 0) {
                        vectorRenderer.drawRoundedRect(boxX + 1f, modeItemY, boxWidth - 2f, itemHeight, 2f, bg)
                    }

                    val color = if (isSelected) 0xFF00C6FF.toInt() else 0xFF888896.toInt()
                    fontRenderer.drawString("outfit", mode, boxX + 6f, modeItemY + 1.5f, 8.5f, color)
                }
            }
            totalHeight += (setting.modes.size * itemHeight) * eased
        }

        height = totalHeight
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val boxX = x + 12f
        val boxWidth = width - 24f
        val boxY = y + 2f
        val boxHeight = 13f

        if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight) {
            if (button == 0) {
                isOpen = !isOpen
                return true
            }
        }

        if (isOpen && openProgress > 0.9f) {
            val dropdownY = boxY + boxHeight + 1f
            val itemHeight = 12f
            
            setting.modes.forEachIndexed { idx, mode ->
                val modeItemY = dropdownY + (idx * itemHeight)
                if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= modeItemY && mouseY <= modeItemY + itemHeight) {
                    setting.value = mode
                    isOpen = false
                    return true
                }
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean = false
    override fun keyTyped(keyCode: Int): Boolean = false
}
