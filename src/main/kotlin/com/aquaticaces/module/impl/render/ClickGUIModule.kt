package com.aquaticaces.module.impl.render

import com.aquaticaces.core.ClientTheme
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.NumberSetting
import com.aquaticaces.ui.ClickGUI
import org.lwjgl.glfw.GLFW

/**
 * Opens the ClickGUI on keybind (default: Right Shift).
 * Theme colors and open key can be changed in the Theme panel inside ClickGUI.
 */
class ClickGUIModule : Module("ClickGUI", "Opens the interactive ClickGUI menu.", Category.RENDER, GLFW.GLFW_KEY_RIGHT_SHIFT) {

    val accentLeft = ColorSetting("Accent Left", ClientTheme.accentLeft)
    val accentRight = ColorSetting("Accent Right", ClientTheme.accentRight)
    val panelBg = ColorSetting("Panel BG", ClientTheme.panelBg)
    val blur = NumberSetting("Blur", ClientTheme.blurStrength.toDouble(), 0.0, 20.0, 0.5)

    init { addSettings(accentLeft, accentRight, panelBg, blur) }

    override fun onInit() {
        syncToTheme()
    }

    fun syncToTheme() {
        ClientTheme.accentLeft = accentLeft.value
        ClientTheme.accentRight = accentRight.value
        ClientTheme.panelBg = panelBg.value
        ClientTheme.blurStrength = blur.value.toFloat()
    }

    override fun onEnable() {
        super.onEnable()
        syncToTheme()
        mc.execute { mc.setScreen(ClickGUI()) }
        isEnabled = false
    }
}
