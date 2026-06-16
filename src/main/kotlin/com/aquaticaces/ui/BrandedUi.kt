package com.aquaticaces.ui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

object BrandedUi {
    val LOGO: ResourceLocation = ResourceLocation.fromNamespaceAndPath("aquaticaces", "textures/gui/logo.png")

    const val BG_TOP = 0xFF080A14.toInt()
    const val BG_BOTTOM = 0xFF140D26.toInt()
    const val ACCENT = 0x00C6FF
    const val MUTED = 0x88AACC

    fun drawBackground(guiGraphics: GuiGraphics, width: Int, height: Int) {
        guiGraphics.fillGradient(0, 0, width, height, BG_TOP, BG_BOTTOM)
    }

    fun drawLogo(guiGraphics: GuiGraphics, centerX: Int, y: Int, size: Int = 64) {
        try {
            guiGraphics.setColor(1f, 1f, 1f, 1f)
            guiGraphics.blit(LOGO, centerX - size / 2, y, 0f, 0f, size, size, size, size)
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
            guiGraphics.fill(x, y, x + filled, y + barH, ACCENT)
        }
    }
}
