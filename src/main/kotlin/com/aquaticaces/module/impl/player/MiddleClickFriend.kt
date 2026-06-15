package com.aquaticaces.module.impl.player

import com.aquaticaces.core.FriendsManager
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.entity.player.Player
import org.lwjgl.glfw.GLFW

class MiddleClickFriend : Module("MiddleClickFriend", "Middle click player to friend.", Category.PLAYER) {
    private var wasPressed = false

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val window = mc.window.window
        val pressed = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS
        if (pressed && !wasPressed) {
            val hit = mc.hitResult
            if (hit is net.minecraft.world.phys.EntityHitResult) {
                val entity = hit.entity
                if (entity is Player) FriendsManager.toggle(entity.gameProfile.name)
            }
        }
        wasPressed = pressed
    }
}
