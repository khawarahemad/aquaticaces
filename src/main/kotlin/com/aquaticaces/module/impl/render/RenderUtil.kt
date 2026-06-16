package com.aquaticaces.module.impl.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.GameRenderer
import org.lwjgl.opengl.GL11

/**
 * Shared world-space render helpers so every render module shares one polished,
 * consistent look: filled gradient boxes with bright glowing outlines.
 */
object RenderUtil {

    fun begin(lineWidth: Float = 2f) {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        RenderSystem.depthMask(false)
        RenderSystem.setShader(GameRenderer::getPositionColorShader)
        GL11.glLineWidth(lineWidth)
    }

    fun end() {
        GL11.glLineWidth(1f)
        RenderSystem.depthMask(true)
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
    }

    fun quads(): BufferBuilder =
        Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

    fun lines(): BufferBuilder =
        Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)

    /** Safe draw — silently ignores empty buffers instead of throwing. */
    fun draw(buf: BufferBuilder) {
        val mesh = buf.build() ?: return
        BufferUploader.drawWithShader(mesh)
    }

    /** Filled box with a vertical alpha gradient (brighter at the bottom). */
    fun addFilledBox(
        buf: BufferBuilder,
        x1: Double, y1: Double, z1: Double,
        x2: Double, y2: Double, z2: Double,
        r: Float, g: Float, b: Float,
        aBottom: Float, aTop: Float
    ) {
        val xa = x1.toFloat(); val ya = y1.toFloat(); val za = z1.toFloat()
        val xb = x2.toFloat(); val yb = y2.toFloat(); val zb = z2.toFloat()
        fun v(x: Float, y: Float, z: Float, a: Float) = buf.addVertex(x, y, z).setColor(r, g, b, a)
        // bottom
        v(xa, ya, za, aBottom); v(xb, ya, za, aBottom); v(xb, ya, zb, aBottom); v(xa, ya, zb, aBottom)
        // top
        v(xa, yb, za, aTop); v(xa, yb, zb, aTop); v(xb, yb, zb, aTop); v(xb, yb, za, aTop)
        // north
        v(xa, ya, za, aBottom); v(xa, yb, za, aTop); v(xb, yb, za, aTop); v(xb, ya, za, aBottom)
        // south
        v(xa, ya, zb, aBottom); v(xb, ya, zb, aBottom); v(xb, yb, zb, aTop); v(xa, yb, zb, aTop)
        // west
        v(xa, ya, za, aBottom); v(xa, ya, zb, aBottom); v(xa, yb, zb, aTop); v(xa, yb, za, aTop)
        // east
        v(xb, ya, za, aBottom); v(xb, yb, za, aTop); v(xb, yb, zb, aTop); v(xb, ya, zb, aBottom)
    }

    /** 12-edge outline of a box. */
    fun addBoxOutline(
        buf: BufferBuilder,
        x1: Double, y1: Double, z1: Double,
        x2: Double, y2: Double, z2: Double,
        r: Float, g: Float, b: Float, a: Float
    ) {
        val xa = x1.toFloat(); val ya = y1.toFloat(); val za = z1.toFloat()
        val xb = x2.toFloat(); val yb = y2.toFloat(); val zb = z2.toFloat()
        fun line(ax: Float, ay: Float, az: Float, bx: Float, by: Float, bz: Float) {
            buf.addVertex(ax, ay, az).setColor(r, g, b, a)
            buf.addVertex(bx, by, bz).setColor(r, g, b, a)
        }
        // bottom ring
        line(xa, ya, za, xb, ya, za); line(xb, ya, za, xb, ya, zb)
        line(xb, ya, zb, xa, ya, zb); line(xa, ya, zb, xa, ya, za)
        // top ring
        line(xa, yb, za, xb, yb, za); line(xb, yb, za, xb, yb, zb)
        line(xb, yb, zb, xa, yb, zb); line(xa, yb, zb, xa, yb, za)
        // pillars
        line(xa, ya, za, xa, yb, za); line(xb, ya, za, xb, yb, za)
        line(xb, ya, zb, xb, yb, zb); line(xa, ya, zb, xa, yb, zb)
    }

    fun red(argb: Int) = ((argb shr 16) and 0xFF) / 255f
    fun green(argb: Int) = ((argb shr 8) and 0xFF) / 255f
    fun blue(argb: Int) = (argb and 0xFF) / 255f
    fun alpha(argb: Int) = ((argb shr 24) and 0xFF) / 255f
}
