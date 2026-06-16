package com.aquaticaces.ui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

object BrandedUi {
    private val RAW_LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "textures/gui/logo.png")

    private const val ACCENT = 0xFF00C6FF.toInt()
    private const val MUTED = 0xFF7A8A9A.toInt()
    private const val DIM = 0xFF556677.toInt()

    fun drawLogo(g: GuiGraphics, centerX: Int, y: Int, size: Int = 64) {
        try {
            g.setColor(1f, 1f, 1f, 1f)
            g.blit(RAW_LOGO, centerX - size / 2, y, 0f, 0f, size, size, size, size)
        } catch (_: Exception) {}
    }

    /** Real Minecraft reload — logo only, no text (font may not be ready yet). */
    @JvmOverloads
    fun drawRealLoadingSplash(
        g: GuiGraphics,
        width: Int,
        height: Int,
        alpha: Float = 1f,
    ) {
        val a = alpha.coerceIn(0f, 1f)
        if (a <= 0f) return

        g.fillGradient(0, 0, width, height, applyAlpha(UiStyle.BG_TOP, a), applyAlpha(UiStyle.BG_BOT, a))
        drawLogo(g, width / 2, height / 2 - 38, 88)
    }

    /** Post-load fake splash — full branded UI with text and progress bar. */
    @JvmOverloads
    fun drawFakeLoadingSplash(
        g: GuiGraphics,
        font: Font,
        width: Int,
        height: Int,
        progress: Float,
        version: String,
        alpha: Float = 1f,
    ) {
        val a = alpha.coerceIn(0f, 1f)
        if (a <= 0f) return

        g.fillGradient(0, 0, width, height, applyAlpha(UiStyle.BG_TOP, a), applyAlpha(UiStyle.BG_BOT, a))
        UiStyle.grid(g, width, height)

        val cx = width / 2
        val logoY = height / 2 - 70
        drawLogo(g, cx, logoY, 76)

        val left = "AQUATIC "
        val right = "ACES"
        val titleW = font.width(left) + font.width(right)
        val titleX = cx - titleW / 2
        val titleY = logoY + 88
        g.drawString(font, left, titleX, titleY, applyAlpha(ACCENT, a), true)
        g.drawString(font, right, titleX + font.width(left), titleY, applyAlpha(UiStyle.PURPLE, a), true)

        val pct = (progress.coerceIn(0f, 1f) * 100).toInt()
        g.drawCenteredString(font, "Loading $pct", cx, logoY + 118, applyAlpha(MUTED, a))

        val barW = (width * 0.4f).toInt().coerceIn(160, 340)
        val barH = 4
        val barX = cx - barW / 2
        val barY = logoY + 132
        g.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, applyAlpha(0xFF1A1F2E.toInt(), a))
        val filled = (barW * progress.coerceIn(0f, 1f)).toInt()
        if (filled > 0) {
            g.fillGradient(barX, barY, barX + filled, barY + barH, applyAlpha(ACCENT, a), applyAlpha(UiStyle.ACCENT_2, a))
        }

        val safeVersion = version.filter { it.isLetterOrDigit() || it == '.' || it == '-' }
        g.drawCenteredString(font, "v$safeVersion - MC 1.21", cx, height - 34, applyAlpha(DIM, a))
        g.drawCenteredString(font, "discord.gg/GMDf9vWeuQ", cx, height - 22, applyAlpha(DIM, a))
    }

    private fun applyAlpha(color: Int, alpha: Float): Int {
        val a = (alpha.coerceIn(0f, 1f) * 255).toInt()
        return (color and 0xFFFFFF) or (a shl 24)
    }
}
