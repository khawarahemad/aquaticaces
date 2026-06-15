package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.NumberSetting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.world.phys.Vec3
import org.lwjgl.opengl.GL11
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

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()

        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val a = color.alpha / 255f

        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
        var prev: Vec3? = null
        for (point in trail) {
            if (prev != null) {
                buffer.addVertex((prev.x - camera.x).toFloat(), (prev.y - camera.y).toFloat(), (prev.z - camera.z).toFloat()).setColor(r, g, b, a)
                buffer.addVertex((point.x - camera.x).toFloat(), (point.y - camera.y).toFloat(), (point.z - camera.z).toFloat()).setColor(r, g, b, a)
            }
            prev = point
        }

        GL11.glLineWidth(2f)
        BufferUploader.drawWithShader(buffer.buildOrThrow())
        GL11.glLineWidth(1f)

        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }

    override fun onDisable() {
        super.onDisable()
        trail.clear()
    }
}
