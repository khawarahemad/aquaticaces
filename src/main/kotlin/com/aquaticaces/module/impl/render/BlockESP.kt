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
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import org.lwjgl.opengl.GL11

class BlockESP : Module("BlockESP", "Highlights valuable blocks.", Category.RENDER) {
    val color = ColorSetting("Color", 0xFFFFD700.toInt())
    init { addSettings(color) }

    private val targets = setOf(
        Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
        Blocks.ANCIENT_DEBRIS, Blocks.CHEST, Blocks.TRAPPED_CHEST,
        Blocks.SPAWNER, Blocks.END_PORTAL_FRAME
    )

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        val cam = mc.gameRenderer.mainCamera.position
        val radius = 48
        val origin = BlockPos.containing(player.position())
        RenderSystem.enableBlend()
        RenderSystem.disableDepthTest()
        GL11.glLineWidth(1.5f)
        val buf = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
        for (x in -radius..radius) for (y in -radius..radius) for (z in -radius..radius) {
            val pos = origin.offset(x, y, z)
            if (!targets.contains(level.getBlockState(pos).block)) continue
            drawBox(buf, pos, cam, color.value)
        }
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buf.buildOrThrow())
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }

    private fun drawBox(buf: com.mojang.blaze3d.vertex.BufferBuilder, pos: BlockPos, cam: net.minecraft.world.phys.Vec3, argb: Int) {
        val a = ((argb shr 24) and 0xFF) / 255f
        val r = ((argb shr 16) and 0xFF) / 255f
        val g = ((argb shr 8) and 0xFF) / 255f
        val b = (argb and 0xFF) / 255f
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
