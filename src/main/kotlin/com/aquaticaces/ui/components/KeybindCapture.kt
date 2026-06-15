package com.aquaticaces.ui.components

import com.aquaticaces.core.NotificationManager
import com.aquaticaces.module.Module
import com.aquaticaces.util.KeybindUtils
import org.lwjgl.glfw.GLFW

object KeybindCapture {
    var pending: Module? = null

    fun request(module: Module) { pending = module }

    fun handle(key: Int): Boolean {
        val module = pending ?: return false
        pending = null
        module.keybind = if (key == GLFW.GLFW_KEY_ESCAPE) 0 else key
        NotificationManager.success(module.name, if (module.keybind == 0) "Keybind cleared" else "Bound to ${KeybindUtils.name(module.keybind)}")
        return true
    }
}
