package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.phys.Vec3
import java.util.ArrayDeque

class Breadcrumbs : Module("Breadcrumbs", "Draws a trail of recent positions.", Category.RENDER) {
    val color = ColorSetting("Color", 0xFF00C6FF.toInt())
    val maxPoints = NumberSetting("Points", 40.0, 10.0, 120.0, 5.0)
    private val trail = ArrayDeque<Vec3>()

    init { addSettings(color, maxPoints) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        if (player.tickCount % 2 != 0) return
        trail.addLast(player.position())
        while (trail.size > maxPoints.value.toInt()) trail.removeFirst()
    }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun() || trail.size < 2) return
        val camera = mc.gameRenderer.mainCamera.position

        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val baseA = color.alpha / 255f

        RenderUtil.begin(2.5f)
        val buffer = RenderUtil.lines()
        val total = trail.size
        var index = 0
        var prev: Vec3? = null
        for (point in trail) {
            if (prev != null) {
                // older points (front of the deque) fade out, newest is brightest
                val aPrev = baseA * (index.toFloat() / total)
                val aCur = baseA * ((index + 1).toFloat() / total)
                buffer.addVertex((prev.x - camera.x).toFloat(), (prev.y - camera.y).toFloat(), (prev.z - camera.z).toFloat()).setColor(r, g, b, aPrev)
                buffer.addVertex((point.x - camera.x).toFloat(), (point.y - camera.y).toFloat(), (point.z - camera.z).toFloat()).setColor(r, g, b, aCur)
            }
            prev = point
            index++
        }
        RenderUtil.draw(buffer)
        RenderUtil.end()
    }

    override fun onDisable() {
        super.onDisable()
        trail.clear()
    }
}
