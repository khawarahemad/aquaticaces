package com.aquaticaces.module.impl.render

import com.aquaticaces.core.WaypointManager
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender2D
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.ui.ClickGUI
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.world.phys.Vec3
import org.lwjgl.opengl.GL11

class Waypoints : Module("Waypoints", "Shows saved world waypoints.", Category.RENDER) {
    val beams = BooleanSetting("Beams", true)
    val labels = BooleanSetting("Labels", true)
    init { addSettings(beams, labels) }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun() || !beams.value) return
        val camera = mc.gameRenderer.mainCamera.position

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()

        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
        for (wp in WaypointManager.all()) {
            val r = ((wp.color shr 16) and 0xFF) / 255f
            val g = ((wp.color shr 8) and 0xFF) / 255f
            val b = (wp.color and 0xFF) / 255f
            val x = wp.x + 0.5 - camera.x
            val y = wp.y - camera.y
            val z = wp.z + 0.5 - camera.z
            buffer.addVertex(x.toFloat(), y.toFloat(), z.toFloat()).setColor(r, g, b, 0.9f)
            buffer.addVertex(x.toFloat(), (y + 3f).toFloat(), z.toFloat()).setColor(r, g, b, 0.2f)
        }
        GL11.glLineWidth(2f)
        BufferUploader.drawWithShader(buffer.buildOrThrow())
        GL11.glLineWidth(1f)
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
    }

    @Subscribe
    fun onRender2D(event: EventRender2D) {
        if (!canRun() || !labels.value) return
        val player = mc.player ?: return
        val font = ClickGUI.fontRenderer
        val vector = ClickGUI.vectorRenderer

        for (wp in WaypointManager.all()) {
            val dist = Vec3(wp.x + 0.5, wp.y.toDouble(), wp.z + 0.5).distanceTo(player.position())
            if (dist > 128) continue
            val text = "${wp.name} (${dist.toInt()}m)"
            val w = font.getStringWidth("outfit", text, 9f) + 8f
            val screenX = mc.window.guiScaledWidth / 2f - w / 2f
            val screenY = 30f + WaypointManager.all().indexOf(wp) * 14f
            vector.drawRoundedRect(screenX, screenY, w, 12f, 2f, 0xAA13141B.toInt())
            font.drawString("outfit", text, screenX + 4f, screenY + 1f, 9f, wp.color)
        }
    }
}
