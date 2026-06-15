package com.aquaticaces.ui.components

import com.aquaticaces.core.ClientTheme
import com.aquaticaces.core.NotificationManager
import com.aquaticaces.module.impl.render.ClickGUIModule
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.NumberSetting
import com.aquaticaces.ui.CubicBezier
import com.aquaticaces.ui.FontRenderer
import com.aquaticaces.ui.VectorRenderer
import com.aquaticaces.util.KeybindUtils

class ThemePanel(
    private val clickGuiModule: ClickGUIModule,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) : UIComponent(x, y, width, height) {

    private var isDragging = false
    private var dragX = 0f
    private var dragY = 0f
    private var waitingKeybind = false

    private val components = mutableListOf<UIComponent>()

    init {
        rebuildComponents()
    }

    private fun rebuildComponents() {
        components.clear()
        for (setting in clickGuiModule.settings) {
            when (setting) {
                is ColorSetting -> components.add(ColorComponent(setting, 0f, 0f, width, 52f))
                is NumberSetting -> components.add(NumberComponent(setting, 0f, 0f, width, 16f))
                is BooleanSetting -> components.add(BooleanComponent(setting, 0f, 0f, width, 16f))
            }
        }
    }

    override fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (isDragging) {
            x = (mouseX - dragX).coerceAtLeast(0f)
            y = (mouseY - dragY).coerceAtLeast(0f)
        }

        var bodyH = 36f
        var offset = height
        for (comp in components) {
            val setting = when (comp) {
                is ColorComponent -> comp.setting
                is NumberComponent -> comp.setting
                is BooleanComponent -> comp.setting
                else -> null
            }
            if (setting != null && !setting.isVisible()) continue
            comp.x = x
            comp.y = y + offset
            comp.render(vectorRenderer, fontRenderer, mouseX, mouseY, partialTicks)
            offset += comp.height
            bodyH += comp.height
        }

        val totalH = bodyH
        vectorRenderer.drawDropShadow(x, y, width, totalH, 6f, 10f, 0x90000000.toInt())
        vectorRenderer.drawRoundedRect(x, y, width, totalH, 5f, ClientTheme.panelBg)
        vectorRenderer.drawMultiPassOutline(x, y, width, totalH, 5f, 1.2f, 0xFF3A3D4D.toInt(), ClientTheme.accentLeft and 0x33FFFFFF)

        fontRenderer.drawString("outfit", "Theme & ClickGUI", x + 10f, y + 5f, 12f, 0xFFFFFFFF.toInt())

        val keyY = y + 20f
        val keyLabel = "Open Key: ${KeybindUtils.name(clickGuiModule.keybind)}"
        val keyHovered = mouseX >= x + 8 && mouseX <= x + width - 8 && mouseY >= keyY && mouseY <= keyY + 14
        val keyBg = if (waitingKeybind) 0x6600C6FF else if (keyHovered) 0x332A2E3D else 0x221A1C23
        vectorRenderer.drawRoundedRect(x + 8f, keyY, width - 16f, 14f, 3f, keyBg)
        fontRenderer.drawString("outfit", if (waitingKeybind) "Press a key..." else keyLabel, x + 14f, keyY + 2f, 9f, 0xFF00C6FF.toInt())
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val keyY = y + 20f
        if (button == 0 && mouseX >= x + 8 && mouseX <= x + width - 8 && mouseY >= keyY && mouseY <= keyY + 14) {
            waitingKeybind = true
            NotificationManager.info("ClickGUI", "Press a key to bind (ESC = clear)")
            KeybindCapture.request(clickGuiModule)
            return true
        }

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if (button == 0) {
                isDragging = true
                dragX = mouseX - x
                dragY = mouseY - y
            }
        }

        for (comp in components) {
            if (comp.mouseClicked(mouseX, mouseY, button)) return true
        }
        return isDragging
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0) isDragging = false
        waitingKeybind = false
        for (comp in components) comp.mouseReleased(mouseX, mouseY, button)
        return false
    }

    override fun keyTyped(keyCode: Int): Boolean {
        for (comp in components) if (comp.keyTyped(keyCode)) return true
        return false
    }
}
