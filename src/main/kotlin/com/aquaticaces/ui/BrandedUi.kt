package com.aquaticaces.ui

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation

object BrandedUi {
    private val RAW_LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "textures/gui/logo.png")
    private val PROCESSED_LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "branded_logo_processed")

    private var processed: ResourceLocation? = null
    private var processFailed = false

    /** Full ARGB colors — never use RGB-only values with GuiGraphics text. */
    private const val ACCENT = 0xFF00C6FF.toInt()
    private const val MUTED = 0xFF7A8A9A.toInt()
    private const val DIM = 0xFF556677.toInt()

    private fun ensureProcessedLogo(): ResourceLocation? {
        processed?.let { return it }
        if (processFailed) return null
        try {
            val mc = Minecraft.getInstance()
            val resource = mc.resourceManager.getResource(RAW_LOGO).orElse(null) ?: return null
            val image = resource.open().use { NativeImage.read(it) }
            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val px = image.getPixelRGBA(x, y)
                    val r = px and 0xFF
                    val g = (px ushr 8) and 0xFF
                    val b = (px ushr 16) and 0xFF
                    val lum = 0.299 * r + 0.587 * g + 0.114 * b
                    val alpha = (255.0 - lum).toInt().coerceIn(0, 255)
                    image.setPixelRGBA(x, y, (alpha shl 24) or (0xFF shl 16) or (0xF6 shl 8) or 0xEA)
                }
            }
            mc.textureManager.register(PROCESSED_LOGO, DynamicTexture(image))
            processed = PROCESSED_LOGO
            return processed
        } catch (_: Throwable) {
            processFailed = true
            return null
        }
    }

    fun drawLogo(g: GuiGraphics, centerX: Int, y: Int, size: Int = 64) {
        val logo = ensureProcessedLogo() ?: return
        try {
            g.setColor(1f, 1f, 1f, 1f)
            g.blit(logo, centerX - size / 2, y, 0f, 0f, size, size, size, size)
        } catch (_: Exception) {}
    }

    /**
     * Full-screen branded loading splash. Uses ASCII-only strings so the
     * default Minecraft font never renders missing-glyph boxes.
     */
    fun drawLoadingScreen(g: GuiGraphics, font: Font, width: Int, height: Int, progress: Float, version: String) {
        UiStyle.backdrop(g, width, height, progress * 20f)

        val cx = width / 2
        val logoY = height / 2 - 72
        drawLogo(g, cx, logoY, 80)

        g.pose().pushPose()
        g.pose().translate(cx.toFloat(), (logoY + 92).toFloat(), 0f)
        g.pose().scale(1.35f, 1.35f, 1f)
        g.drawCenteredString(font, "AQUATIC", 0, 0, ACCENT)
        g.pose().popPose()

        g.pose().pushPose()
        g.pose().translate(cx.toFloat(), (logoY + 108).toFloat(), 0f)
        g.pose().scale(1.35f, 1.35f, 1f)
        g.drawCenteredString(font, "ACES", 0, 0, UiStyle.PURPLE)
        g.pose().popPose()

        val pct = (progress.coerceIn(0f, 1f) * 100).toInt()
        g.drawCenteredString(font, "Loading... $pct%", cx, logoY + 132, MUTED)

        val barW = (width * 0.42f).toInt().coerceIn(160, 360)
        val barH = 5
        val barX = cx - barW / 2
        val barY = logoY + 148
        g.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF1A1F2E.toInt())
        val filled = (barW * progress.coerceIn(0f, 1f)).toInt()
        if (filled > 0) {
            g.fillGradient(barX, barY, barX + filled, barY + barH, ACCENT, UiStyle.ACCENT_2)
            g.fill(barX, barY, barX + filled, barY + 1, 0x44FFFFFF)
        }

        g.drawCenteredString(font, "v$version  |  Minecraft 1.21", cx, height - 36, DIM)
        g.drawCenteredString(font, "discord.gg/GMDf9vWeuQ", cx, height - 22, DIM)
    }
}
