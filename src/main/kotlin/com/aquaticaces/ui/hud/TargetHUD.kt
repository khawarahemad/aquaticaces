package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudLayout
import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.module.impl.combat.KillAura
import com.aquaticaces.ui.ClickGUI
import com.aquaticaces.ui.CubicBezier
import net.minecraft.client.Minecraft
import kotlin.math.roundToInt

/**
 * Custom heads-up display overlay rendering information on the current combat target.
 */
class TargetHUD {
    private val mc = Minecraft.getInstance()
    private var fadeAlpha = 0f
    private var animatedHealth = 0f

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (!HudSettings.isEnabled("target")) return
        val killAura = com.aquaticaces.AquaticAces.moduleManager.getModuleByName("KillAura") as? KillAura
        val activeTarget = killAura?.target

        // Determine if target exists, is alive, and visible in current world context
        val isVisible = activeTarget != null && activeTarget.isAlive && mc.player?.hasLineOfSight(activeTarget) == true
        fadeAlpha += if (isVisible) 0.10f else -0.10f
        fadeAlpha = fadeAlpha.coerceIn(0f, 1f)

        if (fadeAlpha <= 0f) return

        val targetEntity = activeTarget ?: return

        // Smoothly ease health ticks
        val maxHealth = targetEntity.maxHealth
        val currentHealth = targetEntity.health.coerceIn(0f, maxHealth)
        if (animatedHealth <= 0.0f || animatedHealth > maxHealth) {
            animatedHealth = currentHealth
        }
        animatedHealth += (currentHealth - animatedHealth) * 0.15f

        val vectorRenderer = ClickGUI.vectorRenderer
        val fontRenderer = ClickGUI.fontRenderer

        // Coordinates for HUD position placement
        val guiWidth = mc.window.guiScaledWidth.toFloat()
        val guiHeight = mc.window.guiScaledHeight.toFloat()
        val w = 150f
        val h = 42f
        val x = HudLayout.resolveX(HudLayout.positions.targetHudX, guiWidth)
        val y = HudLayout.resolveY(HudLayout.positions.targetHudY, guiHeight, h)

        // Dynamically compute ARGB hex colors scaled by active alpha fade
        val alphaScale = (fadeAlpha * 255).roundToInt()
        val shadowColor = (alphaScale shl 24) or 0x000000
        val bgColor = ((fadeAlpha * 0xDD).roundToInt() shl 24) or 0x13141B
        val borderColors = (alphaScale shl 24) or 0x2A2E3D
        val glowColor = ((fadeAlpha * 0x1A).roundToInt() shl 24) or 0x00C6FF

        // 1. Draw card backdrop, glow, and outlines
        vectorRenderer.drawDropShadow(x, y, w, h, 6f, 12f, shadowColor)
        vectorRenderer.drawRoundedRect(x, y, w, h, 6f, bgColor)
        vectorRenderer.drawMultiPassOutline(x, y, w, h, 6f, 1.0f, borderColors, glowColor)

        // 2. Draw Target Avatar Box
        val avatarSize = 26f
        val ax = x + 8f
        val ay = y + 8f
        val avatarBoxColor = ((fadeAlpha * 0x30).roundToInt() shl 24) or 0x00C6FF
        vectorRenderer.drawRoundedRect(ax, ay, avatarSize, avatarSize, 4f, avatarBoxColor)
        fontRenderer.drawString(
            "outfit",
            "T",
            ax + 9f,
            ay + 6f,
            13f,
            (alphaScale shl 24) or 0xFFFFFF
        )

        // 3. Draw Target Name text
        fontRenderer.drawString(
            "outfit",
            targetEntity.name.string,
            x + 40f,
            y + 6f,
            11f,
            (alphaScale shl 24) or 0xFFFFFF
        )

        // 4. Draw distance and precise health stats
        val distance = mc.player?.distanceTo(targetEntity) ?: 0f
        val statsText = String.format("%.1f HP • %.1fm", currentHealth, distance)
        fontRenderer.drawString(
            "outfit",
            statsText,
            x + 40f,
            y + 17f,
            9f,
            (alphaScale shl 24) or 0x9898A6
        )

        // 5. Draw Eased HP Bar
        val barX = x + 40f
        val barY = y + 29f
        val barW = w - 48f
        val barH = 4f
        val trackColor = (alphaScale shl 24) or 0x242630

        vectorRenderer.drawRoundedRect(barX, barY, barW, barH, 2f, trackColor)

        val healthPercent = (animatedHealth / maxHealth).coerceIn(0f, 1f)
        if (healthPercent > 0.01f) {
            val fillWidth = barW * CubicBezier.easeInOutBezier(healthPercent)
            val fillLeft = (alphaScale shl 24) or 0x00C6FF
            val fillRight = (alphaScale shl 24) or 0x0072FF

            vectorRenderer.drawLinearGradientRect(
                barX, barY, fillWidth, barH, 2f,
                barX, barY, barX + barW, barY,
                fillLeft, fillRight
            )
        }
    }
}
