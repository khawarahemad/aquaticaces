package com.aquaticaces.ui.components

import com.aquaticaces.core.NotificationManager
import com.aquaticaces.module.Module
import com.aquaticaces.ui.CubicBezier
import com.aquaticaces.ui.VectorRenderer
import com.aquaticaces.ui.FontRenderer
import com.aquaticaces.module.setting.*
import com.aquaticaces.core.ClientTheme
import com.aquaticaces.util.KeybindUtils
import org.lwjgl.glfw.GLFW

class ModuleButton(
    val module: Module,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) : UIComponent(x, y, width, height) {

    var isExpanded = false
    var settingsProgress = 0.0f

    private var hoverFactor = 0f
    private var toggledFactor = 0f

    val settingComponents = mutableListOf<UIComponent>()

    init {
        for (setting in module.settings) {
            when (setting) {
                is BooleanSetting -> settingComponents.add(BooleanComponent(setting, 0f, 0f, width, 16f))
                is NumberSetting -> settingComponents.add(NumberComponent(setting, 0f, 0f, width, 16f))
                is ModeSetting -> settingComponents.add(ModeComponent(setting, 0f, 0f, width, 18f))
                is ColorSetting -> settingComponents.add(ColorComponent(setting, 0f, 0f, width, 78f))
            }
        }
    }

    override fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val headerHeight = 20f
        val isHovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight

        hoverFactor += (if (isHovered) 0.15f else -0.15f)
        hoverFactor = hoverFactor.coerceIn(0f, 1f)

        toggledFactor += (if (module.isEnabled) 0.15f else -0.15f)
        toggledFactor = toggledFactor.coerceIn(0f, 1f)

        val target = if (isExpanded) 1.0f else 0.0f
        settingsProgress += (target - settingsProgress) * 0.15f
        val easedSettings = CubicBezier.easeInOutBezier(settingsProgress)

        val baseColor = 0xDD111215.toInt()
        val hoverColor = 0x2A2E3342.toInt()
        val currentBg = CubicBezier.interpolateColor(baseColor, hoverColor, CubicBezier.easeInOutBezier(hoverFactor))
        vectorRenderer.drawRoundedRect(x, y, width, headerHeight, 0f, currentBg)

        if (toggledFactor > 0.01f) {
            vectorRenderer.drawLinearGradientRect(
                x + 2f, y + 2f, (width - 4f) * CubicBezier.easeInOutBezier(toggledFactor), 16f, 3f,
                x, y, x + width, y,
                ClientTheme.accentLeft, ClientTheme.accentRight
            )
        }

        val textColor = CubicBezier.interpolateColor(0xFF888896.toInt(), 0xFFFFFFFF.toInt(), CubicBezier.easeInOutBezier(toggledFactor))
        fontRenderer.drawString("outfit", module.name, x + 10f, y + 4f, 11f, textColor)

        if (module.keybind != 0 && module.keybind != GLFW.GLFW_KEY_UNKNOWN) {
            val keyText = KeybindUtils.name(module.keybind)
            val kw = fontRenderer.getStringWidth("outfit", keyText, 8f)
            fontRenderer.drawString("outfit", keyText, x + width - kw - 8f, y + 5f, 8f, 0xFF666688.toInt())
        }

        var yOffset = headerHeight
        var calculatedHeight = headerHeight

        if (easedSettings > 0.01f) {
            for (comp in settingComponents) {
                val setting = getSettingFromComponent(comp)
                if (setting != null && !setting.isVisible()) continue
                comp.x = x
                comp.y = y + yOffset
                comp.render(vectorRenderer, fontRenderer, mouseX, mouseY, partialTicks)
                yOffset += comp.height
            }
            calculatedHeight += (yOffset - headerHeight) * easedSettings
        }

        height = calculatedHeight
    }

    private fun getSettingFromComponent(comp: UIComponent): Setting<*>? = when (comp) {
        is BooleanComponent -> comp.setting
        is NumberComponent -> comp.setting
        is ModeComponent -> comp.setting
        is ColorComponent -> comp.setting
        else -> null
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val headerHeight = 20f
        val isHeaderHit = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight

        if (isHeaderHit) {
            when (button) {
                0 -> { module.toggle(); return true }
                1 -> { isExpanded = !isExpanded; return true }
                2 -> {
                    module.keybind = GLFW.GLFW_KEY_UNKNOWN
                    NotificationManager.info(module.name, "Press a key to bind")
                    KeybindCapture.request(module)
                    return true
                }
            }
        }

        if (isExpanded && settingsProgress > 0.9f) {
            for (comp in settingComponents) {
                val setting = getSettingFromComponent(comp)
                if (setting != null && !setting.isVisible()) continue
                if (comp.mouseClicked(mouseX, mouseY, button)) return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean {
        settingComponents.forEach { it.mouseReleased(mouseX, mouseY, button) }
        return false
    }

    override fun keyTyped(keyCode: Int): Boolean {
        for (comp in settingComponents) if (comp.keyTyped(keyCode)) return true
        return false
    }
}
