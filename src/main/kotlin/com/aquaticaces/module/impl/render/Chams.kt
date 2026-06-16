package com.aquaticaces.module.impl.render

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.ColorSetting
import org.lwjgl.glfw.GLFW

/**
 * Chams module.
 * Exposes state flags and color uniform variables accessed by entity rendering mixins.
 */
class Chams : Module("Chams", "Overrides entity rendering models through walls.", Category.RENDER) {

    val color = ColorSetting("Color", 0xFFFF0077.toInt())
    val flat = BooleanSetting("Flat", true)

    companion object {
        @JvmField var chamsActive = false
        @JvmField var r = 1.0f
        @JvmField var g = 0.0f
        @JvmField var b = 0.5f
        @JvmField var a = 0.6f

        @JvmStatic
        fun syncColors() {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("Chams") as? Chams ?: return
            if (!module.isEnabled) return
            r = module.color.red / 255f
            g = module.color.green / 255f
            b = module.color.blue / 255f
            a = module.color.alpha / 255f
        }
    }

    init {
        addSettings(color, flat)
    }

    override fun onEnable() {
        super.onEnable()
        chamsActive = true
        syncColors()
    }

    override fun onDisable() {
        super.onDisable()
        chamsActive = false
    }
}
