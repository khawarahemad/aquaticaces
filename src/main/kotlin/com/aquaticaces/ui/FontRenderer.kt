package com.aquaticaces.ui

import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.system.MemoryUtil
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Custom Vector-based Font Renderer using NanoVG's built-in stb_truetype bindings.
 * Guarantees crisp, scale-independent, anti-aliased text rendering.
 */
class FontRenderer(private val renderer: VectorRenderer) {
    private val registeredFonts = mutableSetOf<String>()

    /**
     * Loads a TTF/OTF font file from classpath resources into NanoVG.
     */
    fun loadFont(fontName: String, resourcePath: String) {
        val vg = renderer.getContext()
        val inputStream: InputStream = this::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Font resource could not be resolved at: $resourcePath")
        
        val bytes = inputStream.readBytes()
        val buffer: ByteBuffer = MemoryUtil.memAlloc(bytes.size)
        buffer.put(bytes)
        buffer.flip()
        
        // Pass 1 as freeData so NanoVG handles native memory liberation
        val fontHandle = nvgCreateFontMem(vg, fontName, buffer, true)
        if (fontHandle == -1) {
            MemoryUtil.memFree(buffer)
            throw IllegalStateException("Failed to load and register custom font: $fontName")
        }
        registeredFonts.add(fontName)
    }

    /**
     * Draws anti-aliased text using vector glyph coordinates.
     */
    fun drawString(fontName: String, text: String, x: Float, y: Float, size: Float, argbColor: Int) {
        val vg = renderer.getContext()
        nvgFontSize(vg, size)
        nvgFontFace(vg, fontName)
        nvgTextAlign(vg, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)
        
        // Color separation
        val a = ((argbColor shr 24) and 0xFF) / 255.0f
        val r = ((argbColor shr 16) and 0xFF) / 255.0f
        val g = ((argbColor shr 8) and 0xFF) / 255.0f
        val b = (argbColor and 0xFF) / 255.0f
        
        val color = org.lwjgl.nanovg.NVGColor.calloc()
        color.r(r).g(g).b(b).a(a)
        
        nvgFillColor(vg, color)
        nvgText(vg, x, y, text)
        color.free()
    }

    /**
     * Calculates text width.
     */
    fun getStringWidth(fontName: String, text: String, size: Float): Float {
        val vg = renderer.getContext()
        nvgFontSize(vg, size)
        nvgFontFace(vg, fontName)
        val bounds = FloatArray(4)
        nvgTextBounds(vg, 0f, 0f, text, bounds)
        return bounds[2] - bounds[0]
    }

    /**
     * Calculates text height.
     */
    fun getStringHeight(fontName: String, text: String, size: Float): Float {
        val vg = renderer.getContext()
        nvgFontSize(vg, size)
        nvgFontFace(vg, fontName)
        val bounds = FloatArray(4)
        nvgTextBounds(vg, 0f, 0f, text, bounds)
        return bounds[3] - bounds[1]
    }
}
