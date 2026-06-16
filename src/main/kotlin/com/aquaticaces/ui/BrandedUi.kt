package com.aquaticaces.ui

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation

object BrandedUi {
    /** Raw brand art shipped in the jar (black mark on a white background). */
    private val RAW_LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "textures/gui/logo.png")
    /** Processed mark: white background removed, silhouette tinted light. */
    private val PROCESSED_LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "branded_logo_processed")

    private var processed: ResourceLocation? = null
    private var processFailed = false

    const val BG_TOP = 0xFF080A14.toInt()
    const val BG_BOTTOM = 0xFF140D26.toInt()
    const val ACCENT = 0x00C6FF
    const val MUTED = 0x88AACC

    /**
     * Lazily builds a transparent, light-tinted version of the logo so it reads
     * cleanly on dark UI (no white box). Returns null if it can't be built yet.
     */
    private fun ensureProcessedLogo(): ResourceLocation? {
        processed?.let { return it }
        if (processFailed) return null
        try {
            val mc = Minecraft.getInstance()
            val resource = mc.resourceManager.getResource(RAW_LOGO).orElse(null) ?: return null
            val image = resource.open().use { NativeImage.read(it) }
            val w = image.width
            val h = image.height
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val px = image.getPixelRGBA(x, y)
                    val r = px and 0xFF
                    val g = (px ushr 8) and 0xFF
                    val b = (px ushr 16) and 0xFF
                    val lum = 0.299 * r + 0.587 * g + 0.114 * b
                    // dark mark -> opaque; white background -> transparent; fuzzy edges -> smooth
                    val alpha = (255.0 - lum).toInt().coerceIn(0, 255)
                    // tint the silhouette to a clean cool white
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

    fun drawBackground(guiGraphics: GuiGraphics, width: Int, height: Int) {
        guiGraphics.fillGradient(0, 0, width, height, BG_TOP, BG_BOTTOM)
    }

    fun drawLogo(guiGraphics: GuiGraphics, centerX: Int, y: Int, size: Int = 64) {
        val logo = ensureProcessedLogo() ?: return
        try {
            guiGraphics.setColor(1f, 1f, 1f, 1f)
            guiGraphics.blit(logo, centerX - size / 2, y, 0f, 0f, size, size, size, size)
        } catch (_: Exception) {
            // Logo is optional.
        }
    }

    fun drawTitle(guiGraphics: GuiGraphics, font: Font, centerX: Int, y: Int) {
        guiGraphics.drawCenteredString(font, "AQUATIC ACES", centerX, y, ACCENT)
    }

    fun drawFooter(guiGraphics: GuiGraphics, font: Font, centerX: Int, height: Int, versionLine: String) {
        guiGraphics.drawCenteredString(font, versionLine, centerX, height - 28, MUTED)
        guiGraphics.drawCenteredString(font, "discord.gg/GMDf9vWeuQ", centerX, height - 16, 0x556677)
    }

    fun drawLoadingBadge(guiGraphics: GuiGraphics, font: Font, width: Int, height: Int, status: String) {
        drawBackground(guiGraphics, width, height)
        val centerX = width / 2
        val logoY = height / 2 - 52
        drawLogo(guiGraphics, centerX, logoY, 72)
        drawTitle(guiGraphics, font, centerX, logoY + 78)
        guiGraphics.drawCenteredString(font, status, centerX, logoY + 96, MUTED)
    }

    fun drawProgressBar(guiGraphics: GuiGraphics, width: Int, height: Int, progress: Float) {
        val barW = (width * 0.55f).toInt().coerceAtMost(320)
        val barH = 4
        val x = (width - barW) / 2
        val y = height / 2 + 28
        guiGraphics.fill(x, y, x + barW, y + barH, 0xFF1A1F2E.toInt())
        val filled = (barW * progress.coerceIn(0f, 1f)).toInt()
        if (filled > 0) {
            guiGraphics.fill(x, y, x + filled, y + barH, 0xFF00C6FF.toInt())
        }
    }
}
