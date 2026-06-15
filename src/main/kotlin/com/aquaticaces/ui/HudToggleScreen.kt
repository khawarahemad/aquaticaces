package com.aquaticaces.ui

import com.aquaticaces.core.HudSettings
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class HudToggleScreen(private val parent: Screen?) : Screen(Component.literal("HUD Settings")) {

    private data class HudEntry(val key: String, val label: String, val getter: () -> Boolean, val setter: (Boolean) -> Unit)

    private val entries = listOf(
        HudEntry("target", "Target HUD", { HudSettings.toggles.targetHud }) { HudSettings.toggles.targetHud = it },
        HudEntry("stats", "Stats HUD", { HudSettings.toggles.statsHud }) { HudSettings.toggles.statsHud = it },
        HudEntry("arraylist", "ArrayList", { HudSettings.toggles.arrayList }) { HudSettings.toggles.arrayList = it },
        HudEntry("notifications", "Notifications", { HudSettings.toggles.notifications }) { HudSettings.toggles.notifications = it },
        HudEntry("performance", "Performance", { HudSettings.toggles.performance }) { HudSettings.toggles.performance = it },
        HudEntry("coordinates", "Coordinates", { HudSettings.toggles.coordinates }) { HudSettings.toggles.coordinates = it }
    )

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val vector = ClickGUI.vectorRenderer
        val font = ClickGUI.fontRenderer
        val guiW = minecraft!!.window.guiScaledWidth.toFloat()
        val guiH = minecraft!!.window.guiScaledHeight.toFloat()
        val scale = minecraft!!.window.guiScale.toFloat()

        vector.begin(guiW, guiH, scale)
        vector.drawRoundedRect(0f, 0f, guiW, guiH, 0f, 0x99000000.toInt())

        val panelW = 220f
        val panelH = 200f
        val px = guiW / 2f - panelW / 2f
        val py = guiH / 2f - panelH / 2f
        vector.drawRoundedRect(px, py, panelW, panelH, 8f, 0xEE13141B.toInt())
        vector.drawMultiPassOutline(px, py, panelW, panelH, 8f, 1.2f, 0xFF2A2E3D.toInt(), 0x3300C6FF)

        font.drawString("outfit", "HUD Settings", px + 12f, py + 10f, 13f, 0xFFFFFFFF.toInt())
        font.drawString("outfit", "Click to toggle elements", px + 12f, py + 24f, 8f, 0xFF888888.toInt())

        var rowY = py + 40f
        for (entry in entries) {
            val enabled = entry.getter()
            val hovered = mouseX >= px + 10 && mouseX <= px + panelW - 10 && mouseY >= rowY && mouseY <= rowY + 18
            val bg = when {
                enabled && hovered -> 0x4400C6FF
                enabled -> 0x2200C6FF
                hovered -> 0x332A2E3D
                else -> 0x221A1C23
            }
            vector.drawRoundedRect(px + 10f, rowY, panelW - 20f, 18f, 4f, bg)
            font.drawString("outfit", entry.label, px + 18f, rowY + 4f, 10f, if (enabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            font.drawString("outfit", if (enabled) "ON" else "OFF", px + panelW - 38f, rowY + 4f, 9f, if (enabled) 0xFF00FF88.toInt() else 0xFFFF6666.toInt())
            rowY += 22f
        }

        val editY = py + panelH - 32f
        val editHovered = mouseX >= px + 10 && mouseX <= px + panelW - 10 && mouseY >= editY && mouseY <= editY + 22
        vector.drawRoundedRect(px + 10f, editY, panelW - 20f, 22f, 4f, if (editHovered) 0x440072FF else 0x330072FF)
        font.drawString("outfit", "Edit Positions...", px + 18f, editY + 6f, 10f, 0xFF00C6FF.toInt())

        vector.end()
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        val guiW = minecraft!!.window.guiScaledWidth.toFloat()
        val guiH = minecraft!!.window.guiScaledHeight.toFloat()
        val panelW = 220f
        val panelH = 200f
        val px = guiW / 2f - panelW / 2f
        val py = guiH / 2f - panelH / 2f

        var rowY = py + 40f
        for (entry in entries) {
            if (mouseX >= px + 10 && mouseX <= px + panelW - 10 && mouseY >= rowY && mouseY <= rowY + 18) {
                entry.setter(!entry.getter())
                HudSettings.save()
                return true
            }
            rowY += 22f
        }

        val editY = py + panelH - 32f
        if (mouseX >= px + 10 && mouseX <= px + panelW - 10 && mouseY >= editY && mouseY <= editY + 22) {
            minecraft!!.setScreen(HudEditorScreen(this))
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            HudSettings.save()
            minecraft!!.setScreen(parent)
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun isPauseScreen(): Boolean = false
}
