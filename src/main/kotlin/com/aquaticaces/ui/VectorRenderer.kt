package com.aquaticaces.ui

import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint

/**
 * Modern hardware-accelerated vector rendering framework powered by LWJGL NanoVG (OpenGL 3 backend).
 */
class VectorRenderer {
    private var vg: Long = 0
    private val colorAlloc = NVGColor.create()
    private val paintAlloc = NVGPaint.create()

    /**
     * Initializes the NanoVG context with hardware anti-aliasing and stencil testing enabled.
     */
    fun init() {
        vg = nvgCreate(NVG_ANTIALIAS or NVG_STENCIL_STROKES)
        if (vg == 0L) {
            throw IllegalStateException("Failed to create NanoVG GL3 rendering context.")
        }
    }

    /**
     * Begins frame rendering context.
     */
    fun begin(windowWidth: Float, windowHeight: Float, devicePixelRatio: Float) {
        nvgBeginFrame(vg, windowWidth, windowHeight, devicePixelRatio)
    }

    /**
     * Ends frame rendering context, flushing commands to GPU.
     */
    fun end() {
        nvgEndFrame(vg)
    }

    /**
     * Draws a solid rounded rectangle with configurable corner radius.
     */
    fun drawRoundedRect(x: Float, y: Float, w: Float, h: Float, radius: Float, argbColor: Int) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        nvgFillColor(vg, allocateColor(argbColor))
        nvgFill(vg)
    }

    /**
     * Draws a single-pass rounded rectangle outline.
     */
    fun drawRoundedOutline(x: Float, y: Float, w: Float, h: Float, radius: Float, strokeWidth: Float, argbColor: Int) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        nvgStrokeColor(vg, allocateColor(argbColor))
        nvgStrokeWidth(vg, strokeWidth)
        nvgStroke(vg)
    }

    /**
     * Draws a multi-pass anti-aliased outline to create a smooth high-contrast border.
     */
    fun drawMultiPassOutline(x: Float, y: Float, w: Float, h: Float, radius: Float, strokeWidth: Float, baseColor: Int, glowColor: Int) {
        // Pass 1: Draw outer glowing border
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x - 0.5f, y - 0.5f, w + 1f, h + 1f, radius + 0.5f)
        nvgStrokeColor(vg, allocateColor(glowColor))
        nvgStrokeWidth(vg, strokeWidth + 1f)
        nvgStroke(vg)

        // Pass 2: Draw inner primary border
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        nvgStrokeColor(vg, allocateColor(baseColor))
        nvgStrokeWidth(vg, strokeWidth)
        nvgStroke(vg)
    }

    /**
     * Draws a rectangle filled with a linear color gradient.
     */
    fun drawLinearGradientRect(
        x: Float, y: Float, w: Float, h: Float, radius: Float,
        sx: Float, sy: Float, ex: Float, ey: Float,
        startColor: Int, endColor: Int
    ) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        
        val paint = nvgLinearGradient(vg, sx, sy, ex, ey, allocateColor(startColor), allocateColor(endColor), paintAlloc)
        nvgFillPaint(vg, paint)
        nvgFill(vg)
    }

    /**
     * Draws a rectangle filled with a radial color gradient.
     */
    fun drawRadialGradientRect(
        x: Float, y: Float, w: Float, h: Float, radius: Float,
        cx: Float, cy: Float, innerRadius: Float, outerRadius: Float,
        innerColor: Int, outerColor: Int
    ) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        
        val paint = nvgRadialGradient(vg, cx, cy, innerRadius, outerRadius, allocateColor(innerColor), allocateColor(outerColor), paintAlloc)
        nvgFillPaint(vg, paint)
        nvgFill(vg)
    }

    /**
     * Renders a glowing drop shadow utilizing box gradients.
     */
    fun drawDropShadow(x: Float, y: Float, w: Float, h: Float, radius: Float, feather: Float, argbColor: Int) {
        nvgBeginPath(vg)
        
        val shadowPaint = nvgBoxGradient(
            vg,
            x, y, w, h,
            radius, feather,
            allocateColor(argbColor),
            allocateColor(0x00000000), // Transparent edge
            paintAlloc
        )
        
        nvgRoundedRect(vg, x - feather, y - feather, w + feather * 2, h + feather * 2, radius + feather)
        nvgFillPaint(vg, shadowPaint)
        nvgFill(vg)
    }

    /**
     * Returns the internal NanoVG context handle.
     */
    fun getContext(): Long = vg

    /**
     * Converts ARGB format color to NanoVG native struct.
     */
    private fun allocateColor(argb: Int): NVGColor {
        val a = ((argb shr 24) and 0xFF) / 255.0f
        val r = ((argb shr 16) and 0xFF) / 255.0f
        val g = ((argb shr 8) and 0xFF) / 255.0f
        val b = (argb and 0xFF) / 255.0f
        
        colorAlloc.r(r)
        colorAlloc.g(g)
        colorAlloc.b(b)
        colorAlloc.a(a)
        return colorAlloc
    }

    /**
     * Native memory cleanup. Deletes context on client exit.
     */
    fun cleanup() {
        if (vg != 0L) {
            nvgDelete(vg)
            vg = 0L
        }
    }
}
