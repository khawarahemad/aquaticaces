package com.aquaticaces.ui.hud

import com.aquaticaces.core.HudLayout
import com.aquaticaces.core.HudSettings
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.module.impl.combat.KillAura
import com.aquaticaces.ui.UiStyle
import net.minecraft.client.Minecraft

/**
 * Minimal combat target readout: target name, health and distance as clean
 * shadowed text — no panel.
 */
class TargetHUD {
    private val mc = Minecraft.getInstance()

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (!HudSettings.isEnabled("target")) return
        if (mc.options.hideGui) return
        val killAura = com.aquaticaces.AquaticAces.moduleManager.getModuleByName("KillAura") as? KillAura
        val target = killAura?.target ?: return
        if (!target.isAlive) return

        val g = event.guiGraphics
        val font = mc.font

        val name = target.name.string.let { if (it.length > 16) it.take(15) + "…" else it }
        val hp = target.health.coerceAtLeast(0f)
        val dist = mc.player?.distanceTo(target) ?: 0f
        val hpCol = if (hp < target.maxHealth * 0.3f) 0xFFFF4466.toInt() else UiStyle.SUCCESS

        val x = HudLayout.resolveX(HudLayout.positions.targetHudX, mc.window.guiScaledWidth.toFloat()).toInt()
        val y = HudLayout.resolveY(HudLayout.positions.targetHudY, mc.window.guiScaledHeight.toFloat(), 0f).toInt()

        var cx = x
        g.drawString(font, name, cx, y, UiStyle.TEXT, true)
        cx += font.width("$name  ")
        val hpStr = String.format("%.1f hp", hp)
        g.drawString(font, hpStr, cx, y, hpCol, true)
        cx += font.width("$hpStr  ")
        g.drawString(font, String.format("%.1fm", dist), cx, y, UiStyle.MUTED, true)
    }
}
