package com.aquaticaces.util

import kotlin.math.abs

object ColorUtils {
    data class Hsv(val h: Float, val s: Float, val v: Float)

    fun rgbToHsv(r: Int, g: Int, b: Int): Hsv {
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f
        val max = maxOf(rf, gf, bf)
        val min = minOf(rf, gf, bf)
        val delta = max - min
        val h = when {
            delta < 0.00001f -> 0f
            max == rf -> ((gf - bf) / delta) % 6f
            max == gf -> ((bf - rf) / delta) + 2f
            else -> ((rf - gf) / delta) + 4f
        } * 60f
        val s = if (max < 0.00001f) 0f else delta / max
        return Hsv(if (h < 0) h + 360f else h, s, max)
    }

    fun hsvToArgb(h: Float, s: Float, v: Float, alpha: Int = 255): Int {
        val c = v * s
        val x = c * (1 - abs((h / 60f) % 2 - 1))
        val m = v - c
        val (rp, gp, bp) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        val r = ((rp + m) * 255).toInt().coerceIn(0, 255)
        val g = ((gp + m) * 255).toInt().coerceIn(0, 255)
        val b = ((bp + m) * 255).toInt().coerceIn(0, 255)
        return ((alpha and 0xFF) shl 24) or (r shl 16) or (g shl 8) or b
    }
}
