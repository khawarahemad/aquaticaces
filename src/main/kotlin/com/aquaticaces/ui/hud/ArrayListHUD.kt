package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudLayout
import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.ui.ClickGUI
import net.minecraft.client.Minecraft

/**
 * ArrayListHUD.
 * Displays all enabled modules in the top-right corner of the screen, sorted by name width.
 * Features smooth slide animations and a shifting color theme.
 */
class ArrayListHUD {
    private val mc = Minecraft.getInstance()
    private val slideMap = mutableMapOf<String, Double>()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return

        if (!HudSettings.isEnabled("arraylist")) return
        val guiWidth = mc.window.guiScaledWidth.toFloat()
        val fontRenderer = ClickGUI.fontRenderer
        val vectorRenderer = ClickGUI.vectorRenderer

        // 1. Update slide animation progress for all registered modules
        for (module in ModuleManager.modules) {
            val target = if (module.isEnabled) 1.0 else 0.0
            val current = slideMap.getOrDefault(module.name, 0.0)
            
            if (current != target) {
                val step = 0.15
                val next = if (current < target) {
                    (current + step).coerceAtMost(target)
                } else {
                    (current - step).coerceAtLeast(target)
                }
                slideMap[module.name] = next
            }
        }

        // 2. Filter modules that are at least partially visible
        val visibleModules = ModuleManager.modules.filter { 
            slideMap.getOrDefault(it.name, 0.0) > 0.01 
        }.sortedByDescending { 
            fontRenderer.getStringWidth("outfit", it.name, 11f) 
        }

        if (visibleModules.isEmpty()) return

        // 3. Render modules list
        val scale = mc.window.guiScale.toFloat()
        val guiHeight = mc.window.guiScaledHeight.toFloat()
        vectorRenderer.begin(guiWidth, guiHeight, scale)

        var yOffset = HudLayout.positions.arrayListY
        val rightPad = if (HudLayout.positions.arrayListX < 0f) -HudLayout.positions.arrayListX else 8f
        val time = System.currentTimeMillis()

        for (module in visibleModules) {
            val progress = slideMap.getOrDefault(module.name, 0.0)
            val name = module.name
            val textWidth = fontRenderer.getStringWidth("outfit", name, 11f)
            
            // X position slides in from right edge
            val x = guiWidth - (textWidth + rightPad) * progress.toFloat()

            // Compute dynamic HSL color shift
            val hue = ((time / 15 + yOffset.toInt()) % 360) / 360f
            val color = java.awt.Color.getHSBColor(hue, 0.7f, 1.0f).rgb

            // Background rect with alpha transparency
            val alphaScale = (progress * 0xAA).toInt()
            val rectColor = (alphaScale shl 24) or 0x0F1015

            // Draw clean module label card
            vectorRenderer.drawRoundedRect(x, yOffset, textWidth + 8f, 12f, 2f, rectColor)
            fontRenderer.drawString(
                "outfit",
                name,
                x + 3f,
                yOffset + 1f,
                11f,
                (0xFF shl 24) or (color and 0xFFFFFF)
            )

            // Shifting colored right border accent
            vectorRenderer.drawRoundedRect(guiWidth - 2f, yOffset, 2f, 12f, 0f, (0xFF shl 24) or (color and 0xFFFFFF))

            yOffset += 14f
        }

        vectorRenderer.end()
    }
}
