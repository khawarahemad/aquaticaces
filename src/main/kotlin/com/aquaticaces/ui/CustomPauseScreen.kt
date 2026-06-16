package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen
import net.minecraft.network.chat.Component
import com.aquaticaces.ui.components.VectorMenuButton

/**
 * Branded in-game pause menu — website-styled quick access to ClickGUI and HUD settings.
 */
class CustomPauseScreen(private val parent: Screen?) : Screen(Component.literal("Game Menu")) {

    private val buttons = mutableListOf<VectorMenuButton>()
    private val primary = setOf("Back to Game", "ClickGUI")
    private var menuTick = 0f
    private var firstBtnY = 0f
    private var lastBtnBottom = 0f

    override fun init() {
        buttons.clear()

        val btnW = 220f
        val btnH = 24f
        val gap = 28f
        val cx = width / 2f - btnW / 2f
        var y = height / 2f - 52f
        firstBtnY = y

        buttons.add(VectorMenuButton("Back to Game", cx, y, btnW, btnH) {
            minecraft?.setScreen(null)
        })
        y += gap

        buttons.add(VectorMenuButton("ClickGUI", cx, y, btnW, btnH) {
            minecraft?.setScreen(ClickGUI())
        })
        y += gap

        buttons.add(VectorMenuButton("HUD Settings", cx, y, btnW, btnH) {
            minecraft?.setScreen(HudToggleScreen(this))
        })
        y += gap

        buttons.add(VectorMenuButton("Advancements", cx, y, btnW, btnH) {
            val listener = minecraft?.connection?.advancements
            if (listener != null) minecraft?.setScreen(AdvancementsScreen(listener))
        })
        y += gap

        buttons.add(VectorMenuButton("Options", cx, y, btnW, btnH) {
            minecraft?.setScreen(OptionsScreen(this, minecraft!!.options))
        })
        y += gap

        val halfW = 108f
        buttons.add(VectorMenuButton("Disconnect", cx, y, halfW, btnH) {
            minecraft?.level?.disconnect()
            minecraft?.disconnect(MainMenuScreen())
        })
        buttons.add(VectorMenuButton("Save & Quit", cx + btnW - halfW, y, halfW, btnH) {
            minecraft?.stop()
        })
        lastBtnBottom = y + btnH
    }

    override fun tick() {
        menuTick += 1f
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val mc = minecraft ?: return
        val font = mc.font
        // dim the world behind
        guiGraphics.fillGradient(0, 0, width, height, 0xCC06070B.toInt(), 0xDD0D0E14.toInt())
        UiStyle.grid(guiGraphics, width, height)
        guiGraphics.fillGradient(width / 4, height / 6, width * 3 / 4, height * 5 / 6, 0x1500C6FF, 0x0006070B)

        val cardW = 280
        val cardX = width / 2 - cardW / 2
        val cardTopY = (firstBtnY - 64f).toInt()
        val cardBotY = (lastBtnBottom + 16f).toInt()
        UiStyle.card(guiGraphics, cardX, cardTopY, cardX + cardW, cardBotY)

        val centerX = width / 2
        UiStyle.logoMark(guiGraphics, centerX, cardTopY + 14, 26)
        guiGraphics.drawCenteredString(font, "GAME PAUSED", centerX, cardTopY + 44, UiStyle.ACCENT)

        for (button in buttons) {
            button.tickHovered(button.contains(mouseX.toDouble(), mouseY.toDouble()))
            UiStyle.button(guiGraphics, font, button, primary.contains(button.label))
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            for (item in buttons) {
                if (item.contains(mouseX, mouseY)) {
                    item.click()
                    return true
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldCloseOnEsc(): Boolean = true
}
