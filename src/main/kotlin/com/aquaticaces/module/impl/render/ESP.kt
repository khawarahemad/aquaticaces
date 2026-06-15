package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.ModeSetting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.world.entity.player.Player
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

/**
 * ESP module.
 * Draws glowing 3D box outlines around player models.
 */
class ESP : Module("ESP", "Renders visuals through blocks.", Category.RENDER) {

    val mode = ModeSetting("Mode", "Box", listOf("Box", "Wireframe"))
    val color = ColorSetting("Color", 0xFF00C6FF.toInt())

    init {
        addSettings(mode, color)
    }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        val player = mc.player ?: return
        val level = mc.level ?: return
        val camera = mc.gameRenderer.mainCamera
        val cameraPos = camera.position

        // Configure OpenGL rendering states for outline draw
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

        for (entity in level.entitiesForRendering()) {
            if (entity === player || entity !is Player || !entity.isAlive) continue

            // Compute translated box coords relative to camera
            val box = entity.boundingBox.move(-cameraPos.x, -cameraPos.y, -cameraPos.z)

            // Add lines for box bounds
            // Bottom Face
            bufferBuilder.addVertex(box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)

            // Top Face
            bufferBuilder.addVertex(box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)

            // Vertical Pillars
            bufferBuilder.addVertex(box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)

            bufferBuilder.addVertex(box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)
            bufferBuilder.addVertex(box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).setColor(r, g, b, a)
        }

        // Draw vertices
        GL11.glLineWidth(2.0f)
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
        GL11.glLineWidth(1.0f)

        // Restore state
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
    }
}

// Dummy BufferUploader wrapper to compile cleanly across Fabric environments
object BufferUploader {
    fun drawWithShader(buffer: com.mojang.blaze3d.vertex.MeshData) {
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer)
    }
}
