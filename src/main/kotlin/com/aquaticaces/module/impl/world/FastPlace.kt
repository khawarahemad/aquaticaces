package com.aquaticaces.module.impl.world

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import org.lwjgl.glfw.GLFW

/**
 * FastPlace module.
 * Bypasses the default client-side block placement delay timer using reflection.
 */
class FastPlace : Module("FastPlace", "Removes block placement delay.", Category.WORLD) {

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        resetRightClickDelay()
    }

    private fun resetRightClickDelay() {
        try {
            val delayField = mc.javaClass.getDeclaredField("rightClickDelay")
            delayField.isAccessible = true
            delayField.setInt(mc, 0)
        } catch (e: Exception) {
            try {
                val delayField = mc.javaClass.getDeclaredField("rightClickDelayTimer")
                delayField.isAccessible = true
                delayField.setInt(mc, 0)
            } catch (ex: Exception) {
                // Fallback traversing int fields of Minecraft instance
                for (field in mc.javaClass.declaredFields) {
                    if (field.type == Int::class.javaPrimitiveType) {
                        field.isAccessible = true
                        val value = field.getInt(mc)
                        if (value in 1..4) {
                            field.setInt(mc, 0)
                        }
                    }
                }
            }
        }
    }
}
