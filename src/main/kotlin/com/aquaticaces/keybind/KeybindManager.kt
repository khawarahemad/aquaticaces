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

        // ClickGUI opens reliably and directly, independent of the module
        // toggle/notification machinery. Falls back to Right Shift if its
        // bind was lost/cleared by an old config.
        val clickGuiModule = ModuleManager.getModuleByName("ClickGUI") as? ClickGUIModule
        val openKey = clickGuiModule?.keybind ?: GLFW.GLFW_KEY_RIGHT_SHIFT
        if (pressedKey == openKey || (openKey <= 0 && pressedKey == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            mc.execute { mc.setScreen(ClickGUI()) }
            event.cancel()
            return
        }

        for (module in ModuleManager.modules) {
            if (module === clickGuiModule) continue
            if (module.keybind == pressedKey) module.toggle()
        }
    }
}
