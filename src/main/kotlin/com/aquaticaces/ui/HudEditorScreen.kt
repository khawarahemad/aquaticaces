package com.aquaticaces.ui

import com.aquaticaces.core.HudLayout
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import com.aquaticaces.ui.UiStyle.withAlpha

class HudEditorScreen(private val parent: Screen?) : Screen(Component.literal("HUD Editor")) {

    private enum class DragTarget(val label: String, val width: Float, val height: Float) {
        TARGET("Target HUD", 150f, 22f),
        STATS("Stats HUD", 120f, 12f),
        ARRAYLIST("ArrayList", 90f, 60f);

        fun x(screenW: Float): Float = when (this) {
            TARGET -> HudLayout.resolveX(HudLayout.positions.targetHudX, screenW)
            STATS -> HudLayout.resolveX(HudLayout.positions.statsHudX, screenW)
            ARRAYLIST -> {
                val pad = if (HudLayout.positions.arrayListX < 0f) -HudLayout.positions.arrayListX else 8f
                screenW - width - pad
            }
        }

        fun y(screenH: Float): Float = when (this) {
            TARGET -> HudLayout.resolveY(HudLayout.positions.targetHudY, screenH, height)
            STATS -> HudLayout.resolveY(HudLayout.positions.statsHudY, screenH, height)
            ARRAYLIST -> HudLayout.positions.arrayListY
        }

        fun setPosition(screenW: Float, screenH: Float, mouseX: Float, mouseY: Float) {
            when (this) {
                TARGET -> {
                    HudLayout.positions.targetHudX = mouseX.coerceIn(0f, screenW - width)
                    HudLayout.positions.targetHudY = mouseY.coerceIn(0f, screenH - height)
                }
                STATS -> {
                    HudLayout.positions.statsHudX = mouseX.coerceIn(0f, screenW - width)
                    HudLayout.positions.statsHudY = mouseY.coerceIn(0f, screenH - height)
                }
                ARRAYLIST -> {
                    HudLayout.positions.arrayListX = -(screenW - mouseX - width).coerceAtLeast(8f)
                    HudLayout.positions.arrayListY = mouseY.coerceIn(0f, screenH - height)
                }
            }
        }
    }

    private var dragging: DragTarget? = null

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        val guiW = width.toFloat()
        val guiH = height.toFloat()

        guiGraphics.fill(0, 0, width, height, 0xCC06070B.toInt())
        UiStyle.grid(guiGraphics, width, height)

        guiGraphics.drawString(font, "HUD Editor", 12, 10, UiStyle.ACCENT, true)
        guiGraphics.drawString(font, "Drag elements to reposition  ·  ESC to save & close", 12, 22, UiStyle.MUTED, true)

        if (dragging != null) {
            dragging!!.setPosition(guiW, guiH, mouseX.toFloat(), mouseY.toFloat())
        }

        for (target in DragTarget.entries) {
            val x = target.x(guiW).toInt()
            val y = target.y(guiH).toInt()
            val w = target.width.toInt()
            val h = target.height.toInt()
            val hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h
            val active = dragging == target || hovered
            val border = if (active) UiStyle.ACCENT else UiStyle.BORDER
            guiGraphics.fill(x, y, x + w, y + h, if (dragging == target) withAlpha(UiStyle.ACCENT, 0x33) else 0x66131521)
            UiStyle.outline(guiGraphics, x, y, x + w, y + h, border)
            guiGraphics.fill(x, y, x + w, y + 1, UiStyle.ACCENT)
            guiGraphics.drawString(font, target.label, x + 5, y + 3, if (active) UiStyle.TEXT else UiStyle.MUTED, true)
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        val guiW = width.toFloat()
        val guiH = height.toFloat()
        dragging = DragTarget.entries.firstOrNull { target ->
            val x = target.x(guiW)
            val y = target.y(guiH)
            mouseX >= x && mouseX <= x + target.width && mouseY >= y && mouseY <= y + target.height
        }
        return dragging != null || super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && dragging != null) {
            dragging = null
            HudLayout.save()
            return true
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (shouldCloseOnEsc() && minecraft!!.options.keyInventory.matches(keyCode, scanCode)) return false
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            HudLayout.save()
            minecraft!!.setScreen(parent)
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun isPauseScreen(): Boolean = false
}
