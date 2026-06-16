package com.aquaticaces.module.impl.render

import com.aquaticaces.core.WaypointManager
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import net.minecraft.world.phys.Vec3

class Waypoints : Module("Waypoints", "Shows saved world waypoints.", Category.RENDER) {
    val beams = BooleanSetting("Beams", true)
    val labels = BooleanSetting("Labels", true)
    init { addSettings(beams, labels) }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun() || !beams.value) return
        val camera = mc.gameRenderer.mainCamera.position
        val all = WaypointManager.all()
        if (all.isEmpty()) return

        RenderUtil.begin(1.5f)

        // glowing beam columns
        val fill = RenderUtil.quads()
        for (wp in all) {
            val r = ((wp.color shr 16) and 0xFF) / 255f
            val g = ((wp.color shr 8) and 0xFF) / 255f
            val b = (wp.color and 0xFF) / 255f
            val x = wp.x + 0.5 - camera.x
            val y = wp.y - camera.y
            val z = wp.z + 0.5 - camera.z
            val s = 0.12
            RenderUtil.addFilledBox(fill, x - s, y, z - s, x + s, y + 4.0, z + s, r, g, b, 0.45f, 0.02f)
        }
        RenderUtil.draw(fill)

        RenderUtil.end()
    }

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (!canRun() || !labels.value) return
        val player = mc.player ?: return
        val g = event.guiGraphics
        val font = mc.font
        val cx = mc.window.guiScaledWidth / 2

        WaypointManager.all().forEachIndexed { i, wp ->
            val dist = Vec3(wp.x + 0.5, wp.y.toDouble(), wp.z + 0.5).distanceTo(player.position())
            if (dist > 256) return@forEachIndexed
            val text = "${wp.name}  ${dist.toInt()}m"
            val y = 30 + i * 11
            g.drawString(font, text, cx - font.width(text) / 2, y, wp.color, true)
        }
    }
}
