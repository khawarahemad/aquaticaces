package com.aquaticaces.keybind

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventKeyInput
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.module.impl.render.ClickGUIModule
import com.aquaticaces.ui.ClickGUI
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

class KeybindManager {

    @Subscribe
    fun onKeyInput(event: EventKeyInput) {
        if (event.action != GLFW.GLFW_PRESS) return
        val mc = Minecraft.getInstance()
        val screen = mc.screen
        if (screen is ClickGUI) {
            val clickGui = ModuleManager.getModuleByName("ClickGUI") as? ClickGUIModule
            if (event.key == GLFW.GLFW_KEY_ESCAPE || event.key == clickGui?.keybind) {
                mc.execute { mc.setScreen(null) }
                event.cancel()
            }
            return
        }
        if (screen != null) return

        val pressedKey = event.key
        if (pressedKey == GLFW.GLFW_KEY_UNKNOWN) return

        for (module in ModuleManager.modules) {
            if (module.keybind == pressedKey) module.toggle()
        }
    }
}
