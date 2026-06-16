package com.aquaticaces.ui

import com.aquaticaces.ui.components.VectorMenuButton
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics

/**
 * Shared visual language for all Aquatic Aces screens, matching the marketing
 * website (aquaticaces.vercel.app). Everything renders with Minecraft's
 * GuiGraphics so it is reliable across systems.
 */
object UiStyle {
    const val BG_TOP = 0xFF06070B.toInt()
    const val BG_BOT = 0xFF0D0E14.toInt()
    const val CARD_TOP = 0xFF15171F.toInt()
    const val CARD_BOT = 0xFF0B0C12.toInt()
    const val BORDER = 0xFF2A2E3D.toInt()
    const val ACCENT = 0xFF00C6FF.toInt()
    const val ACCENT_2 = 0xFF0072FF.toInt()
    const val PURPLE = 0xFFA97BFF.toInt()
    const val TEXT = 0xFFF0F0F8.toInt()
    const val MUTED = 0xFF7A7A94.toInt()
    const val DIM = 0xFF55556A.toInt()
    const val SUCCESS = 0xFF00FF88.toInt()

    fun withAlpha(color: Int, alpha: Int): Int =
        ((alpha and 0xFF) shl 24) or (color and 0xFFFFFF)

    fun backdrop(g: GuiGraphics, width: Int, height: Int, tick: Float) {
        // crisp dark gradient — no translucent haze
        g.fillGradient(0, 0, width, height, BG_TOP, BG_BOT)
        grid(g, width, height)
        // subtle crisp vignette to darken the edges
        g.fillGradient(0, 0, width, height / 6, 0x66000000, 0x00000000)
        g.fillGradient(0, height * 5 / 6, width, height, 0x00000000, 0x66000000)
    }

    fun grid(g: GuiGraphics, width: Int, height: Int) {
        val spacing = 26
        val color = 0x0EFFFFFF
        var x = width / 2 % spacing
        while (x < width) {
            g.fill(x, 0, x + 1, height, color)
            x += spacing
        }
        var y = 0
        while (y < height) {
            g.fill(0, y, width, y + 1, color)
            y += spacing
        }
    }

    fun outline(g: GuiGraphics, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        g.fill(x1, y1, x2, y1 + 1, color)
        g.fill(x1, y2 - 1, x2, y2, color)
        g.fill(x1, y1, x1 + 1, y2, color)
        g.fill(x2 - 1, y1, x2, y2, color)
    }

    fun card(g: GuiGraphics, x1: Int, y1: Int, x2: Int, y2: Int) {
        g.fill(x1 - 6, y1 - 6, x2 + 6, y2 + 6, 0x33000000)
        g.fill(x1 - 3, y1 - 3, x2 + 3, y2 + 3, 0x22000000)
        g.fillGradient(x1, y1, x2, y2, CARD_TOP, CARD_BOT)
        outline(g, x1, y1, x2, y2, BORDER)
        g.fillGradient(x1, y1, x2, y1 + 1, ACCENT, ACCENT_2)
        g.fill(x1, y1, x1 + 1, y1 + 14, ACCENT)
        g.fill(x2 - 1, y2 - 14, x2, y2, ACCENT_2)
    }

    fun logoMark(g: GuiGraphics, centerX: Int, y: Int, size: Int) {
        val mx = centerX - size / 2
        g.fillGradient(mx, y, mx + size, y + size, ACCENT, ACCENT_2)
        g.fill(mx, y, mx + size, y + 1, 0x55FFFFFF)
        BrandedUi.drawLogo(g, centerX, y, size)
    }

    fun title(g: GuiGraphics, font: Font, centerX: Int, y: Float, scale: Float = 1.7f) {
        val left = "AQUATIC "
        val right = "ACES"
        val totalW = (font.width(left) + font.width(right)) * scale
        g.pose().pushPose()
        g.pose().translate(centerX - totalW / 2f, y, 0f)
        g.pose().scale(scale, scale, 1f)
        g.drawString(font, left, 0, 0, ACCENT, true)
        g.drawString(font, right, font.width(left), 0, PURPLE, true)
        g.pose().popPose()
    }

    /** Compact glass HUD panel: drop shadow, gradient body, border, accent hairline. */
    fun hudPanel(g: GuiGraphics, x: Int, y: Int, w: Int, h: Int, accentColor: Int = ACCENT) {
        g.fill(x - 2, y - 2, x + w + 2, y + h + 2, 0x33000000)
        g.fillGradient(x, y, x + w, y + h, 0xEE15171F.toInt(), 0xEE0B0C12.toInt())
        outline(g, x, y, x + w, y + h, BORDER)
        g.fillGradient(x, y, x + w, y + 1, accentColor, withAlpha(accentColor, 0x55))
        g.fill(x, y, x + 1, y + h, withAlpha(accentColor, 0xAA))
    }

    /** Rounded-ish progress bar with a track and a gradient fill. */
    fun bar(g: GuiGraphics, x: Int, y: Int, w: Int, h: Int, pct: Float, left: Int, right: Int) {
        g.fill(x, y, x + w, y + h, 0x55000000)
        outline(g, x, y, x + w, y + h, 0x33FFFFFF)
        val fill = (w * pct.coerceIn(0f, 1f)).toInt()
        if (fill > 0) {
            g.fillGradient(x, y, x + fill, y + h, left, right)
            g.fill(x, y, x + fill, y + 1, 0x44FFFFFF)
        }
    }

    /** Website-style button. Primary = cyan gradient, secondary = glass with growing accent bar. */
    fun button(g: GuiGraphics, font: Font, b: VectorMenuButton, primary: Boolean) {
        val x = b.x.toInt(); val y = b.y.toInt()
        val w = b.width.toInt(); val h = b.height.toInt()
        val hover = b.hoverProgress
        if (primary) {
            if (hover > 0.05f) g.fill(x - 2, y - 2, x + w + 2, y + h + 2, withAlpha(ACCENT, (hover * 0x55).toInt()))
            g.fillGradient(x, y, x + w, y + h, ACCENT, ACCENT_2)
            g.fill(x, y, x + w, y + 1, 0x44FFFFFF)
            g.drawCenteredString(font, b.label, x + w / 2, y + h / 2 - 4, 0xFFFFFFFF.toInt())
        } else {
            val bg = if (hover > 0.05f) withAlpha(0x1A2230, (0xAA + hover * 0x55).toInt().coerceAtMost(0xFF)) else 0x99131521.toInt()
            g.fill(x, y, x + w, y + h, bg)
            outline(g, x, y, x + w, y + h, if (hover > 0.05f) withAlpha(ACCENT, 0x88) else BORDER)
            val accentW = (1 + hover * 4f).toInt()
            g.fill(x, y, x + accentW, y + h, ACCENT)
            g.fill(x, y, x + w, y + 1, 0x22FFFFFF)
            g.drawCenteredString(font, b.label, x + w / 2, y + h / 2 - 4, if (hover > 0.05f) ACCENT else TEXT)
        }
    }
}
