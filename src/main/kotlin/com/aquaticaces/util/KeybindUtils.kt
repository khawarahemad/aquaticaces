package com.aquaticaces.util

import org.lwjgl.glfw.GLFW

object KeybindUtils {
    fun name(key: Int): String {
        if (key == 0 || key == GLFW.GLFW_KEY_UNKNOWN) return "NONE"
        return when (key) {
            GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT"
            GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT"
            GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL"
            GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL"
            GLFW.GLFW_KEY_RIGHT_ALT -> "RALT"
            GLFW.GLFW_KEY_LEFT_ALT -> "LALT"
            GLFW.GLFW_KEY_SPACE -> "SPACE"
            GLFW.GLFW_KEY_ESCAPE -> "ESC"
            GLFW.GLFW_KEY_TAB -> "TAB"
            GLFW.GLFW_KEY_INSERT -> "INSERT"
            GLFW.GLFW_KEY_DELETE -> "DELETE"
            GLFW.GLFW_KEY_HOME -> "HOME"
            GLFW.GLFW_KEY_END -> "END"
            GLFW.GLFW_KEY_PAGE_UP -> "PGUP"
            GLFW.GLFW_KEY_PAGE_DOWN -> "PGDN"
            in GLFW.GLFW_KEY_F1..GLFW.GLFW_KEY_F12 -> "F${key - GLFW.GLFW_KEY_F1 + 1}"
            in GLFW.GLFW_KEY_A..GLFW.GLFW_KEY_Z -> ('A'.code + (key - GLFW.GLFW_KEY_A)).toChar().toString()
            in GLFW.GLFW_KEY_0..GLFW.GLFW_KEY_9 -> (key - GLFW.GLFW_KEY_0).toString()
            else -> GLFW.glfwGetKeyName(key, 0)?.uppercase() ?: "KEY_$key"
        }
    }
}
