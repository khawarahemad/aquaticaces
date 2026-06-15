package com.aquaticaces.keybind

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventKeyInput
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.ui.ClickGUI
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

class KeybindManager {

    @Subscribe
    fun onKeyInput(event: EventKeyInput) {
        if (event.action != GLFW.GLFW_PRESS) return
        val mc = Minecraft.getInstance()
        val screen = mc.screen
        if (screen != null && screen !is ClickGUI) return

        val pressedKey = event.key
        if (pressedKey == GLFW.GLFW_KEY_UNKNOWN) return

        for (module in ModuleManager.modules) {
            if (module.keybind == pressedKey) module.toggle()
        }
    }
}
