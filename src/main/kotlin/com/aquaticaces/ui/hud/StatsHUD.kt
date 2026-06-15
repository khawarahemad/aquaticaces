package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudLayout
import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.ui.ClickGUI
import com.aquaticaces.ui.CubicBezier
import net.minecraft.client.Minecraft
import kotlin.math.roundToInt

/**
 * StatsHUD.
 * Displays a premium bottom-left HUD overlay tracking the player's own health,
 * absorption, and hunger levels with smooth gradient progress bars.
 */
class StatsHUD {
    private val mc = Minecraft.getInstance()
    
    private var animatedHP = 0f
    private var animatedHunger = 0f

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (!HudSettings.isEnabled("stats")) return
        val player = mc.player ?: return
        if (mc.options.hideGui) return

        val maxHP = player.maxHealth
        val currentHP = player.health.coerceIn(0f, maxHP)
        val absorption = player.absorptionAmount
        val foodLevel = player.foodData.foodLevel.toFloat().coerceIn(0f, 20f)

        // Smoothly ease stat animations
        if (animatedHP <= 0f || animatedHP > maxHP + absorption) {
            animatedHP = currentHP
        }
        animatedHP += (currentHP - animatedHP) * 0.15f

        if (animatedHunger <= 0f) {
            animatedHunger = foodLevel
        }
        animatedHunger += (foodLevel - animatedHunger) * 0.15f

        val vectorRenderer = ClickGUI.vectorRenderer
        val fontRenderer = ClickGUI.fontRenderer

        // Position coordinates (bottom-left)
        val guiHeight = mc.window.guiScaledHeight.toFloat()
        val guiWidth = mc.window.guiScaledWidth.toFloat()
        val w = 160f
        val h = 55f
        val x = HudLayout.resolveX(HudLayout.positions.statsHudX, guiWidth)
        val y = HudLayout.resolveY(HudLayout.positions.statsHudY, guiHeight, h)

        // Theme colors
        val shadowColor = 0x80000000.toInt()
        val bgColor = 0xDD13141B.toInt()
        val borderColor = 0xFF2A2E3D.toInt()
        val glowColor = 0x1A00C6FF.toInt()

        // 1. Draw card backdrop and shadows
        vectorRenderer.drawDropShadow(x, y, w, h, 6f, 12f, shadowColor)
        vectorRenderer.drawRoundedRect(x, y, w, h, 6f, bgColor)
        vectorRenderer.drawMultiPassOutline(x, y, w, h, 6f, 1.0f, borderColor, glowColor)

        // 2. Draw Health bar (Green gradient)
        val barX = x + 10f
        var barY = y + 10f
        val barW = w - 20f
        val barH = 5f
        val trackColor = 0xFF242630.toInt()

        fontRenderer.drawString(
            "outfit",
            String.format("HP: %.1f / %.1f", currentHP + absorption, maxHP),
            barX,
            barY,
            9f,
            0xFFFFFFFF.toInt()
        )
        barY += 12f

        vectorRenderer.drawRoundedRect(barX, barY, barW, barH, 2f, trackColor)
        val hpPercent = (animatedHP / maxHP).coerceIn(0f, 1f)
        if (hpPercent > 0.01f) {
            val fillWidth = barW * CubicBezier.easeInOutBezier(hpPercent)
            val fillLeft = 0xFF00FF55.toInt()
            val fillRight = 0xFF00AA33.toInt()
            vectorRenderer.drawLinearGradientRect(
                barX, barY, fillWidth, barH, 2f,
                barX, barY, barX + barW, barY,
                fillLeft, fillRight
            )
        }

        // 3. Draw Hunger bar (Orange gradient)
        barY += 10f
        fontRenderer.drawString(
            "outfit",
            String.format("Hunger: %d / 20", foodLevel.toInt()),
            barX,
            barY,
            9f,
            0xFFFFFFFF.toInt()
        )
        barY += 12f

        vectorRenderer.drawRoundedRect(barX, barY, barW, barH, 2f, trackColor)
        val hungerPercent = (animatedHunger / 20f).coerceIn(0f, 1f)
        if (hungerPercent > 0.01f) {
            val fillWidth = barW * CubicBezier.easeInOutBezier(hungerPercent)
            val fillLeft = 0xFFFF9900.toInt()
            val fillRight = 0xFFCC5500.toInt()
            vectorRenderer.drawLinearGradientRect(
                barX, barY, fillWidth, barH, 2f,
                barX, barY, barX + barW, barY,
                fillLeft, fillRight
            )
        }
    }
}
