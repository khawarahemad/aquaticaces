package com.aquaticaces.ui

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation

object BrandedUi {
    private val RAW_LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "textures/gui/logo.png")
    private val CLEAN_LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "branded_logo_clean")

    private var cleanLogo: ResourceLocation? = null
    private var prepareFailed = false

    private const val ACCENT = 0xFF00C6FF.toInt()
    private const val MUTED = 0xFF7A8A9A.toInt()
    private const val DIM = 0xFF556677.toInt()

    /** Strip solid / light / blue PNG backgrounds; keep original logo colors. */
    private fun ensureCleanLogo(): ResourceLocation? {
        cleanLogo?.let { return it }
        if (prepareFailed) return RAW_LOGO
        try {
            val mc = Minecraft.getInstance()
            val resource = mc.resourceManager.getResource(RAW_LOGO).orElse(null) ?: return null
            val image = resource.open().use { NativeImage.read(it) }
            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val px = image.getPixelRGBA(x, y)
                    val a = (px ushr 24) and 0xFF
                    val r = px and 0xFF
                    val g = (px ushr 8) and 0xFF
                    val b = (px ushr 16) and 0xFF
                    if (isBackground(r, g, b, a)) {
                        image.setPixelRGBA(x, y, 0)
                    } else {
                        image.setPixelRGBA(x, y, (a shl 24) or (r shl 16) or (g shl 8) or b)
                    }
                }
            }
            mc.textureManager.register(CLEAN_LOGO, DynamicTexture(image))
            cleanLogo = CLEAN_LOGO
            return cleanLogo
        } catch (_: Throwable) {
            prepareFailed = true
            return RAW_LOGO
        }
    }

    private fun isBackground(r: Int, g: Int, b: Int, a: Int): Boolean {
        if (a < 20) return true
        if (r > 225 && g > 225 && b > 225) return true
        val lum = 0.299 * r + 0.587 * g + 0.114 * b
        if (lum > 240) return true
        // cyan / blue squares from the source PNG
        if (b > 160 && b > r + 25 && b > g + 15) return true
        return false
    }

    fun drawLogo(g: GuiGraphics, centerX: Int, y: Int, size: Int = 64) {
        val logo = ensureCleanLogo() ?: return
        try {
            g.setColor(1f, 1f, 1f, 1f)
            g.blit(logo, centerX - size / 2, y, 0f, 0f, size, size, size, size)
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
