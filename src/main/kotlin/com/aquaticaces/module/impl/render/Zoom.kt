package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import org.lwjgl.glfw.GLFW

class Zoom : Module("Zoom", "Optifine-style camera zoom.", Category.RENDER, GLFW.GLFW_KEY_C) {
    val level = NumberSetting("Level", 3.0, 1.5, 10.0, 0.5)
    private var defaultFov = 70
    init { addSettings(level) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        mc.options.fov().set((70.0 / level.value).toInt())
    }

    override fun onEnable() {
        super.onEnable()
        defaultFov = mc.options.fov().get()
    }

    override fun onDisable() {
        super.onDisable()
        mc.options.fov().set(defaultFov)
    }
}
