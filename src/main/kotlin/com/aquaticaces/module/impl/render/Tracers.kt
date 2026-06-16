package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.ColorSetting
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

/**
 * Tracers — draws lines from the screen center to nearby targets. The line
 * fades from transparent at the origin to full color at the target, and can
 * tint by distance for quick threat reading.
 */
class Tracers : Module("Tracers", "Draws fading lines to nearby targets.", Category.RENDER) {

    val color = ColorSetting("Color", 0xFF00C6FF.toInt())
    val playersOnly = BooleanSetting("Players Only", true)
    val distanceColor = BooleanSetting("Distance Color", true)

    init {
        addSettings(color, playersOnly, distanceColor)
    }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        val player = mc.player ?: return
        val level = mc.level ?: return
        val cam = mc.gameRenderer.mainCamera.position

        RenderUtil.begin(1.8f)
        val buf = RenderUtil.lines()

        val look = player.lookAngle
        val sx = (look.x * 0.6).toFloat()
        val sy = (player.eyeHeight - 0.15).toFloat()
        val sz = (look.z * 0.6).toFloat()

        for (entity in level.entitiesForRendering()) {
            if (entity === player || !entity.isAlive) continue
            if (playersOnly.value && entity !is Player) continue
            if (!playersOnly.value && entity !is LivingEntity) continue

            val tx = (entity.x - cam.x).toFloat()
            val ty = (entity.eyeY - cam.y).toFloat()
            val tz = (entity.z - cam.z).toFloat()

            var r = RenderUtil.red(color.value)
            var g = RenderUtil.green(color.value)
            var b = RenderUtil.blue(color.value)
            if (distanceColor.value) {
                val dist = player.distanceTo(entity).coerceIn(0f, 32f) / 32f
                // near = red/danger, far = accent
                r = 1f - dist * (1f - RenderUtil.red(color.value))
                g = dist * RenderUtil.green(color.value)
                b = dist * RenderUtil.blue(color.value)
            }

            // origin: transparent, target: full
            buf.addVertex(sx, sy, sz).setColor(r, g, b, 0.0f)
            buf.addVertex(tx, ty, tz).setColor(r, g, b, 1.0f)
        }

        RenderUtil.draw(buf)
        RenderUtil.end()
    }
}
