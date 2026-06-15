package com.aquaticaces.module.impl.world

import com.aquaticaces.core.SchematicManager
import com.aquaticaces.event.Subscribe
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
import net.minecraft.core.BlockPos
import org.lwjgl.opengl.GL11

class SchematicPreview : Module("SchematicPreview", "Renders loaded schematic ghosts.", Category.WORLD) {
    val color = ColorSetting("Color", 0x8800C6FF.toInt())
    val offsetY = NumberSetting("Offset Y", 0.0, -32.0, 32.0, 1.0)
    init { addSettings(color, offsetY) }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun()) return
        val schematic = SchematicManager.active ?: return
        val cam = mc.gameRenderer.mainCamera.position
        val origin = BlockPos(
            schematic.originX,
            schematic.originY + offsetY.value.toInt(),
            schematic.originZ
        )

        RenderSystem.enableBlend()
        RenderSystem.disableDepthTest()
        GL11.glLineWidth(1.2f)
        val buf = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
        val a = ((color.value shr 24) and 0xFF) / 255f
        val r = ((color.value shr 16) and 0xFF) / 255f
        val g = ((color.value shr 8) and 0xFF) / 255f
        val b = (color.value and 0xFF) / 255f

        for (block in schematic.blocks) {
            val pos = SchematicManager.previewOffset(block, origin)
            drawBox(buf, pos, cam, r, g, b, a.coerceAtLeast(0.35f))
        }
        BufferUploader.drawWithShader(buf.buildOrThrow())
        GL11.glLineWidth(1f)
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }

    private fun drawBox(buf: com.mojang.blaze3d.vertex.BufferBuilder, pos: BlockPos, cam: net.minecraft.world.phys.Vec3, r: Float, g: Float, b: Float, a: Float) {
        val x1 = pos.x - cam.x; val y1 = pos.y - cam.y; val z1 = pos.z - cam.z
        val x2 = x1 + 1.0; val y2 = y1 + 1.0; val z2 = z1 + 1.0
        fun line(xa: Double, ya: Double, za: Double, xb: Double, yb: Double, zb: Double) {
            buf.addVertex(xa.toFloat(), ya.toFloat(), za.toFloat()).setColor(r, g, b, a)
            buf.addVertex(xb.toFloat(), yb.toFloat(), zb.toFloat()).setColor(r, g, b, a)
        }
        line(x1,y1,z1,x2,y1,z1); line(x2,y1,z1,x2,y1,z2); line(x2,y1,z2,x1,y1,z2); line(x1,y1,z2,x1,y1,z1)
        line(x1,y2,z1,x2,y2,z1); line(x2,y2,z1,x2,y2,z2); line(x2,y2,z2,x1,y2,z2); line(x1,y2,z2,x1,y2,z1)
        line(x1,y1,z1,x1,y2,z1); line(x2,y1,z1,x2,y2,z1); line(x2,y1,z2,x2,y2,z2); line(x1,y1,z2,x1,y2,z2)
    }
}
