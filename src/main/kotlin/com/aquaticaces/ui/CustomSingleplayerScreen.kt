package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

/** Full-screen singleplayer hub — matches main menu / multiplayer layout. */
class CustomSingleplayerScreen(private val parent: Screen?) : Screen(Component.literal("Singleplayer")) {

    private data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun hit(mx: Double, my: Double) = mx >= x && mx <= x + w && my >= y && my <= y + h
    }

    private data class Action(
        val title: String,
        val subtitle: String,
        val accent: Int = UiStyle.ACCENT,
        val icon: UiStyle.HeroIcon,
        val action: () -> Unit,
    ) {
        var hover = 0f
        var rect = Rect(0f, 0f, 0f, 0f)
    }

    private val actions = mutableListOf<Action>()
    private var backRect = Rect(0f, 0f, 0f, 0f)
    private var menuTick = 0f

    override fun init() {
        actions.clear()
        actions.add(Action(
            "Play a World", "Open your saved worlds and jump back in",
            icon = UiStyle.HeroIcon.PLAY
        ) {
            minecraft?.setScreen(CustomWorldSelectScreen(this))
        })
        actions.add(Action(
            "Create New World", "Generate a fresh world with custom settings",
            accent = UiStyle.PURPLE, icon = UiStyle.HeroIcon.CREATE
        ) {
            minecraft?.setScreen(CustomCreateWorldScreen(this))
        })
        actions.add(Action(
            "Manage Worlds", "Play, create or delete your existing worlds",
            icon = UiStyle.HeroIcon.MANAGE
        ) {
            minecraft?.setScreen(CustomWorldSelectScreen(this))
        })
    }

    override fun tick() {
        menuTick += 1f
    }

    override fun render(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        val gw = width.toFloat()
        val gh = height.toFloat()

        UiStyle.backdrop(g, width, height, menuTick + partialTick)
        UiStyle.screenHeader(g, font, width, "SINGLEPLAYER", "World management hub")

        layoutRows(gw, gh)
        for (action in actions) {
            val hovered = action.rect.hit(mouseX.toDouble(), mouseY.toDouble())
            action.hover += ((if (hovered) 1f else 0f) - action.hover) * 0.2f
            val r = action.rect
            UiStyle.heroTile(
                g, font,
                r.x.toInt(), r.y.toInt(), r.w.toInt(), r.h.toInt(),
                action.title, action.subtitle, action.accent, action.hover, action.icon
            )
        }

        UiStyle.footerStrip(g, width, height)
        val backHovered = backRect.hit(mouseX.toDouble(), mouseY.toDouble())
        UiStyle.barButton(
            g, font,
            backRect.x.toInt(), backRect.y.toInt(), backRect.w.toInt(), backRect.h.toInt(),
            "Back", backHovered
        )
    }

    private fun layoutRows(gw: Float, gh: Float) {
        val pad = UiStyle.SCREEN_PAD.toFloat()
        val contentW = gw - pad * 2f
        val gap = 10f
        val y0 = UiStyle.HEADER_H + 16f
        val maxBottom = gh - UiStyle.FOOTER_H
        val available = maxBottom - y0

        val rowCount = actions.size
        var rowH = ((available - gap * (rowCount - 1)) / rowCount).coerceIn(68f, 80f)
        if (rowH * rowCount + gap * (rowCount - 1) > available) {
            rowH = (available - gap * (rowCount - 1)) / rowCount
        }

        var y = y0
        for (action in actions) {
            action.rect = Rect(pad, y, contentW, rowH)
            y += rowH + gap
        }

        backRect = Rect(pad, gh - UiStyle.FOOTER_H + 8f, 72f, 24f)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        if (backRect.hit(mouseX, mouseY)) {
            minecraft?.setScreen(parent ?: MainMenuScreen())
            return true
        }
        for (action in actions) {
            if (action.rect.hit(mouseX, mouseY)) {
                action.action()
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldCloseOnEsc(): Boolean = true

    override fun onClose() {
        minecraft?.setScreen(parent ?: MainMenuScreen())
    }

    override fun renderBackground(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}
}
