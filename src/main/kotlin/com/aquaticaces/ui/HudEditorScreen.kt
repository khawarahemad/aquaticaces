package com.aquaticaces.ui

import com.aquaticaces.core.HudLayout
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class HudEditorScreen(private val parent: Screen?) : Screen(Component.literal("HUD Editor")) {

    private enum class DragTarget(val label: String, val width: Float, val height: Float) {
        TARGET("Target HUD", 150f, 42f),
        STATS("Stats HUD", 160f, 55f),
        ARRAYLIST("ArrayList", 120f, 80f);

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
        val vector = ClickGUI.vectorRenderer
        val font = ClickGUI.fontRenderer
        val scale = minecraft!!.window.guiScale.toFloat()
        val guiW = minecraft!!.window.guiScaledWidth.toFloat()
        val guiH = minecraft!!.window.guiScaledHeight.toFloat()

        vector.begin(guiW, guiH, scale)

        vector.drawRoundedRect(0f, 0f, guiW, guiH, 0f, 0x88000000.toInt())
        font.drawString("outfit", "HUD Editor — drag elements, ESC to save & close", 12f, 10f, 12f, 0xFFFFFFFF.toInt())
        font.drawString("outfit", "Target / Stats / ArrayList anchors", 12f, 24f, 9f, 0xFFAAAAAA.toInt())

        if (dragging != null) {
            dragging!!.setPosition(guiW, guiH, mouseX.toFloat(), mouseY.toFloat())
        }

        for (target in DragTarget.entries) {
            val x = target.x(guiW)
            val y = target.y(guiH)
            val hovered = mouseX >= x && mouseX <= x + target.width && mouseY >= y && mouseY <= y + target.height
            val border = if (dragging == target || hovered) 0xFF00C6FF.toInt() else 0xFF666688.toInt()
            val fill = if (dragging == target) 0x4400C6FF else 0x3313141B
            vector.drawRoundedRect(x, y, target.width, target.height, 6f, fill)
            vector.drawMultiPassOutline(x, y, target.width, target.height, 6f, 1.2f, border, 0x2200C6FF)
            font.drawString("outfit", target.label, x + 8f, y + 8f, 10f, 0xFFFFFFFF.toInt())
        }

        vector.end()
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        val guiW = minecraft!!.window.guiScaledWidth.toFloat()
        val guiH = minecraft!!.window.guiScaledHeight.toFloat()
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
