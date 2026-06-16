package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class CustomSingleplayerScreen(private val parent: Screen?) : Screen(Component.literal("Singleplayer")) {

    private data class ActionCard(
        val title: String,
        val subtitle: String,
        val action: () -> Unit,
        val primary: Boolean = false,
        var x: Int = 0,
        var y: Int = 0,
        var w: Int = 0,
        var h: Int = 0,
        var hover: Float = 0f
    )

    private val actions = mutableListOf<ActionCard>()
    private var menuTick = 0f

    override fun init() {
        actions.clear()
        actions.add(ActionCard("Play a World", "Open your saved worlds and jump back in.", action = {
            minecraft?.setScreen(CustomWorldSelectScreen(this))
        }, primary = true))
        actions.add(ActionCard("Create New World", "Generate a fresh world with custom settings.", action = {
            minecraft?.setScreen(CustomCreateWorldScreen(this))
        }))
        actions.add(ActionCard("Manage Worlds", "Play, create or delete your existing worlds.", action = {
            minecraft?.setScreen(CustomWorldSelectScreen(this))
        }))
        actions.add(ActionCard("Back", "Return to the Aquatic Aces main menu.", action = {
            minecraft?.setScreen(parent ?: MainMenuScreen())
        }))
    }

    override fun tick() { menuTick += 1f }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        val t = menuTick + partialTick
        UiStyle.backdrop(guiGraphics, width, height, t)

        val n = actions.size
        val headerH = 82
        val cardW = (width - 80).coerceIn(220, 360)

        // fit the action cards to the available height
        var ch = 48
        var gap = 10
        var totalH = headerH + n * (ch + gap)
        if (totalH + 28 > height) {
            val avail = (height - 28 - headerH).coerceAtLeast(120)
            val per = (avail / n).coerceAtLeast(26)
            ch = (per - 6).coerceIn(28, 48)
            gap = (per - ch).coerceIn(4, 10)
            totalH = headerH + n * (ch + gap)
        }

        val cardX = width / 2 - cardW / 2
        val cardTopY = ((height - totalH) / 2).coerceAtLeast(14)

        UiStyle.card(guiGraphics, cardX - 14, cardTopY - 16, cardX + cardW + 14, cardTopY + totalH + 2)

        val centerX = width / 2
        UiStyle.logoMark(guiGraphics, centerX, cardTopY - 4, 28)
        guiGraphics.drawCenteredString(font, "SINGLEPLAYER", centerX, cardTopY + 30, UiStyle.ACCENT)
        guiGraphics.drawCenteredString(font, "World management hub", centerX, cardTopY + 42, UiStyle.MUTED)

        val startY = cardTopY + headerH
        actions.forEachIndexed { index, card ->
            card.x = cardX
            card.y = startY + index * (ch + gap)
            card.w = cardW
            card.h = ch
            val hovered = mouseX >= card.x && mouseX <= card.x + card.w && mouseY >= card.y && mouseY <= card.y + card.h
            card.hover += ((if (hovered) 1f else 0f) - card.hover) * 0.25f
            drawCard(guiGraphics, card)
        }
    }

    private fun drawCard(g: GuiGraphics, card: ActionCard) {
        val font = minecraft!!.font
        val hover = card.hover
        val accent = if (card.primary) UiStyle.ACCENT else UiStyle.PURPLE
        val bg = UiStyle.withAlpha(0x10131A, (0xCC + hover * 0x33).toInt().coerceAtMost(0xFF))
        g.fill(card.x, card.y, card.x + card.w, card.y + card.h, bg)
        if (hover > 0.05f) g.fill(card.x, card.y, card.x + card.w, card.y + card.h, UiStyle.withAlpha(accent, (hover * 0x1A).toInt()))
        UiStyle.outline(g, card.x, card.y, card.x + card.w, card.y + card.h, if (hover > 0.05f) UiStyle.withAlpha(accent, 0x99) else UiStyle.BORDER)
        val barW = (3 + hover * 3f).toInt()
        g.fill(card.x, card.y, card.x + barW, card.y + card.h, accent)

        val titleY = card.y + 8
        val subY = card.y + card.h - 13
        g.drawString(font, card.title, card.x + 16, titleY, if (hover > 0.05f) accent else UiStyle.TEXT, false)
        if (subY > titleY + 9) {
            g.drawString(font, card.subtitle, card.x + 16, subY, UiStyle.MUTED, false)
        }
        val cxr = card.x + card.w - 18
        val cyr = card.y + card.h / 2
        g.drawString(font, ">", cxr, cyr - 4, if (hover > 0.05f) accent else UiStyle.DIM, false)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            actions.firstOrNull {
                mouseX >= it.x && mouseX <= it.x + it.w && mouseY >= it.y && mouseY <= it.y + it.h
            }?.let {
                it.action()
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldCloseOnEsc(): Boolean = true

    override fun onClose() {
        minecraft?.setScreen(parent ?: MainMenuScreen())
    }
}
