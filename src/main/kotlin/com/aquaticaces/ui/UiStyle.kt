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

    const val SCREEN_PAD = 20
    const val HEADER_H = 48
    const val FOOTER_H = 58

    /** Top bar used on every full-screen menu (logo + title + subtitle + divider). */
    fun screenHeader(g: GuiGraphics, font: Font, width: Int, title: String, subtitle: String, accent: Int = ACCENT) {
        BrandedUi.drawLogo(g, SCREEN_PAD + 6, 12, 28)
        g.drawString(font, title, 56, 14, accent, true)
        g.drawString(font, subtitle, 56, 28, MUTED, false)
        g.fill(SCREEN_PAD, HEADER_H, width - SCREEN_PAD, HEADER_H + 1, BORDER)
    }

    fun footerStrip(g: GuiGraphics, width: Int, height: Int) {
        val top = height - FOOTER_H
        g.fill(0, top, width, height, 0xFF08090D.toInt())
        g.fill(SCREEN_PAD, top, width - SCREEN_PAD, top + 1, BORDER)
    }

    enum class HeroIcon { WORLD, SERVERS, PLAY, CREATE, MANAGE }

    /** Premium launcher tile — dark glass panel, icon column, left-aligned copy. */
    fun heroTile(
        g: GuiGraphics,
        font: Font,
        x: Int, y: Int, w: Int, h: Int,
        title: String,
        subtitle: String?,
        accent: Int,
        hover: Float,
        icon: HeroIcon,
    ) {
        if (hover > 0.04f) {
            g.fill(x - 1, y - 1, x + w + 1, y + h + 1, withAlpha(accent, (hover * 0x28).toInt()))
        }
        g.fill(x + 2, y + 3, x + w + 2, y + h + 3, 0x44000000)
        g.fillGradient(x, y, x + w, y + h, 0xFF161922.toInt(), 0xFF0D0F14.toInt())
        if (hover > 0.04f) g.fill(x, y, x + w, y + h, withAlpha(accent, (hover * 0x14).toInt()))
        outline(g, x, y, x + w, y + h, if (hover > 0.04f) withAlpha(accent, 0xAA) else BORDER)
        g.fill(x, y, x + w, y + 1, 0x28FFFFFF)

        val iconCol = 54
        g.fill(x, y, x + iconCol, y + h, withAlpha(accent, 0x14))
        g.fill(x, y, x + 3, y + h, accent)
        drawHeroIcon(g, x + (iconCol - 26) / 2, y + (h - 26) / 2, 26, icon, accent, hover)

        val tx = x + iconCol + 12
        val ty = y + h / 2 - (if (subtitle != null) 8 else 4)
        g.drawString(font, title, tx, ty, if (hover > 0.04f) accent else TEXT, false)
        subtitle?.let { g.drawString(font, it, tx, ty + 12, MUTED, false) }

        val barW = ((w - iconCol) * hover).toInt().coerceAtLeast(if (hover > 0.04f) 24 else 0)
        if (barW > 0) {
            g.fillGradient(x + iconCol, y + h - 2, x + iconCol + barW, y + h, accent, withAlpha(accent, 0x44))
        }

        if (hover > 0.15f) {
            g.drawString(font, ">", x + w - 14, y + h / 2 - 4, accent, false)
        }
    }

    private fun drawHeroIcon(g: GuiGraphics, x: Int, y: Int, size: Int, icon: HeroIcon, accent: Int, hover: Float) {
        g.fill(x - 1, y - 1, x + size + 1, y + size + 1, withAlpha(accent, if (hover > 0.04f) 0x55 else 0x33))
        g.fill(x, y, x + size, y + size, 0xFF0A0C10.toInt())
        outline(g, x, y, x + size, y + size, withAlpha(accent, 0x88))
        val s = size / 2
        val cx = x + s
        val cy = y + s
        when (icon) {
            HeroIcon.WORLD -> {
                g.fill(x + 4, y + 4, x + size - 4, y + s, 0xFF3DDC84.toInt())
                g.fill(x + 4, y + s, x + size - 4, y + size - 4, 0xFF8B6914.toInt())
            }
            HeroIcon.SERVERS -> {
                g.fill(cx - 3, cy - 7, cx + 3, cy - 1, accent)
                g.fill(cx - 7, cy + 1, cx - 1, cy + 7, accent)
                g.fill(cx + 1, cy + 1, cx + 7, cy + 7, accent)
                g.fill(cx - 1, cy - 1, cx + 1, cy + 1, 0xFF0A0C10.toInt())
            }
            HeroIcon.PLAY -> {
                g.fill(cx - 2, cy - 6, cx + 6, cy, accent)
                g.fill(cx - 2, cy, cx + 6, cy + 6, withAlpha(accent, 0x88))
            }
            HeroIcon.CREATE -> {
                g.fill(cx - 1, cy - 6, cx + 1, cy + 6, accent)
                g.fill(cx - 6, cy - 1, cx + 6, cy + 1, accent)
            }
            HeroIcon.MANAGE -> {
                g.fill(x + 5, y + 8, x + size - 5, y + 10, accent)
                g.fill(x + 5, y + 14, x + size - 5, y + 16, withAlpha(accent, 0xAA))
                g.fill(x + 5, y + 20, x + size - 5, y + 22, withAlpha(accent, 0x66))
            }
        }
    }

    /** Compact secondary row for Realms / ClickGUI / Mods. */
    fun menuRow(
        g: GuiGraphics,
        font: Font,
        x: Int, y: Int, w: Int, h: Int,
        title: String,
        subtitle: String?,
        accent: Int,
        hover: Float,
        primary: Boolean = false,
    ) {
        g.fillGradient(x, y, x + w, y + h, 0xFF141820.toInt(), 0xFF0E1016.toInt())
        if (hover > 0.05f) g.fill(x, y, x + w, y + h, withAlpha(accent, 0x18))
        outline(g, x, y, x + w, y + h, if (hover > 0.05f) withAlpha(accent, 0x99) else BORDER)
        g.fill(x, y, x + 3, y + h, accent)
        g.fill(x, y, x + w, y + 1, 0x22FFFFFF)
        g.drawString(font, title, x + 14, y + 10, if (hover > 0.05f) accent else TEXT, false)
        subtitle?.let {
            if (h >= 44) g.drawString(font, it, x + 14, y + 22, MUTED, false)
        }
        if (hover > 0.05f) {
            g.drawString(font, ">", x + w - 16, y + h / 2 - 4, accent, false)
        }
    }

    fun barButton(
        g: GuiGraphics,
        font: Font,
        x: Int, y: Int, w: Int, h: Int,
        label: String,
        hovered: Boolean,
        danger: Boolean = false,
    ) {
        val accent = if (danger) 0xFFFF4466.toInt() else ACCENT
        g.fill(x, y, x + w, y + h, if (hovered) 0xFF1A2230.toInt() else 0xFF131521.toInt())
        outline(g, x, y, x + w, y + h, if (hovered) withAlpha(accent, 0x88) else BORDER)
        g.drawCenteredString(font, label, x + w / 2, y + h / 2 - 4, if (hovered) accent else TEXT)
    }
}
