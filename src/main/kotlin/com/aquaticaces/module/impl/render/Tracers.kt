package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.world.entity.player.Player
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

/**
 * Tracers module.
 * Renders lines in 3D space from the player's crosshair to nearby player targets.
 */
class Tracers : Module("Tracers", "Draws lines to other players.", Category.RENDER) {

    val color = ColorSetting("Color", 0xFF00FF55.toInt())

    init {
        addSettings(color)
    }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        val player = mc.player ?: return
        val level = mc.level ?: return
        val camera = mc.gameRenderer.mainCamera
        val cameraPos = camera.position

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()

        val tesselator = Tesselator.getInstance()
        val bufferBuilder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)

        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val a = color.alpha / 255f

        // Draw tracers from the center of camera view (0, 0, 0 in relative coords) to entities
        val lookVec = player.getLookAngle()
        val startX = lookVec.x * 0.5
        val startY = player.eyeHeight.toDouble() - 0.1
        val startZ = lookVec.z * 0.5

        for (entity in level.entitiesForRendering()) {
            if (entity === player || entity !is Player || !entity.isAlive) continue

            // Translated eye coordinates of target
            val targetX = entity.x - cameraPos.x
            val targetY = entity.eyeY - cameraPos.y
            val targetZ = entity.z - cameraPos.z

            // Base offset line from center of camera view
            bufferBuilder.addVertex(0f, 0f, 0f).setColor(r, g, b, a)
            bufferBuilder.addVertex(targetX.toFloat(), targetY.toFloat(), targetZ.toFloat()).setColor(r, g, b, a)
        }

        GL11.glLineWidth(1.5f)
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
        GL11.glLineWidth(1.0f)

        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
    }
}
