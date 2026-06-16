package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudLayout
import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.ui.ClickGUI
import net.minecraft.client.Minecraft

/**
 * Minimal top-right enabled-module list: clean shadowed text colored per
 * category with a thin accent edge. No background boxes.
 */
class ArrayListHUD {
    private val mc = Minecraft.getInstance()
    private val slideMap = mutableMapOf<String, Float>()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (mc.options.hideGui) return
        if (!HudSettings.isEnabled("arraylist")) return

        val g = event.guiGraphics
        val font = mc.font
        val guiWidth = mc.window.guiScaledWidth

        for (module in ModuleManager.modules) {
            val target = if (module.isEnabled) 1f else 0f
            val current = slideMap.getOrDefault(module.name, 0f)
            if (current != target) {
                slideMap[module.name] = if (current < target) (current + 0.2f).coerceAtMost(target)
                else (current - 0.2f).coerceAtLeast(target)
            }
        }

        val visible = ModuleManager.modules
            .filter { slideMap.getOrDefault(it.name, 0f) > 0.01f }
            .sortedByDescending { font.width(it.name) }
        if (visible.isEmpty()) return

        var y = HudLayout.positions.arrayListY.toInt().coerceAtLeast(4)
        for (module in visible) {
            val progress = slideMap.getOrDefault(module.name, 0f)
            val accent = ClickGUI.catColor(module.category)
            val textW = font.width(module.name)
            val x = guiWidth - 6 - (textW * progress).toInt()
            g.drawString(font, module.name, x, y, accent, true)
            g.fill(guiWidth - 2, y - 1, guiWidth, y + 9, accent)
            y += 11
        }

        slideMap.keys.retainAll { name -> ModuleManager.modules.any { it.name == name } }
    }
}
