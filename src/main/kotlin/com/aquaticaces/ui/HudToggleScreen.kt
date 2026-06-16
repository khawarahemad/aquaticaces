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

    private var menuTick = 0f

    override fun tick() { menuTick += 1f }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        guiGraphics.fillGradient(0, 0, width, height, 0xCC06070B.toInt(), 0xDD0D0E14.toInt())
        UiStyle.grid(guiGraphics, width, height)

        val panelW = 240
        val panelH = 40 + entries.size * 24 + 40
        val px = width / 2 - panelW / 2
        val py = height / 2 - panelH / 2
        UiStyle.card(guiGraphics, px, py, px + panelW, py + panelH)

        UiStyle.logoMark(guiGraphics, width / 2, py + 12, 22)
        guiGraphics.drawCenteredString(font, "HUD SETTINGS", width / 2, py + 38, UiStyle.ACCENT)

        var rowY = py + 54
        for (entry in entries) {
            val enabled = entry.getter()
            val rx = px + 12
            val rw = panelW - 24
            val hovered = mouseX >= rx && mouseX <= rx + rw && mouseY >= rowY && mouseY <= rowY + 18
            val bg = if (enabled) UiStyle.withAlpha(UiStyle.ACCENT, if (hovered) 0x33 else 0x22) else if (hovered) 0x22FFFFFF else 0x33000000
            guiGraphics.fill(rx, rowY, rx + rw, rowY + 18, bg)
            UiStyle.outline(guiGraphics, rx, rowY, rx + rw, rowY + 18, if (enabled) UiStyle.withAlpha(UiStyle.ACCENT, 0x88) else UiStyle.BORDER)
            guiGraphics.drawString(font, entry.label, rx + 8, rowY + 5, if (enabled) UiStyle.TEXT else UiStyle.MUTED, false)
            val pill = if (enabled) "ON" else "OFF"
            guiGraphics.drawString(font, pill, rx + rw - 8 - font.width(pill), rowY + 5, if (enabled) UiStyle.SUCCESS else UiStyle.DIM, false)
            rowY += 24
        }

        val editY = py + panelH - 30
        val ex = px + 12
        val ew = panelW - 24
        val editHovered = mouseX >= ex && mouseX <= ex + ew && mouseY >= editY && mouseY <= editY + 20
        if (editHovered) guiGraphics.fill(ex - 2, editY - 2, ex + ew + 2, editY + 22, UiStyle.withAlpha(UiStyle.ACCENT, 0x55))
        guiGraphics.fillGradient(ex, editY, ex + ew, editY + 20, UiStyle.ACCENT, UiStyle.ACCENT_2)
        guiGraphics.drawCenteredString(font, "Edit Positions...", width / 2, editY + 6, 0xFFFFFFFF.toInt())

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        val panelW = 240
        val panelH = 40 + entries.size * 24 + 40
        val px = width / 2 - panelW / 2
        val py = height / 2 - panelH / 2
        val rx = px + 12
        val rw = panelW - 24

        var rowY = py + 54
        for (entry in entries) {
            if (mouseX >= rx && mouseX <= rx + rw && mouseY >= rowY && mouseY <= rowY + 18) {
                entry.setter(!entry.getter())
                HudSettings.save()
                return true
            }
            rowY += 24
        }

        val editY = py + panelH - 30
        if (mouseX >= rx && mouseX <= rx + rw && mouseY >= editY && mouseY <= editY + 20) {
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
