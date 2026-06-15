package com.aquaticaces.ui

/**
 * Cubic-bezier easing helpers for UI animations.
 */
object CubicBezier {
    fun easeInOutBezier(t: Float): Float {
        val clamped = t.coerceIn(0f, 1f)
        return if (clamped < 0.5f) {
            4f * clamped * clamped * clamped
        } else {
            val f = -2f * clamped + 2f
            1f - (f * f * f) / 2f
        }
    }

    fun easeOutBezier(t: Float): Float {
        val clamped = 1f - t.coerceIn(0f, 1f)
        return 1f - clamped * clamped * clamped
    }

    fun interpolateColor(startArgb: Int, endArgb: Int, t: Float): Int {
        val eased = easeInOutBezier(t)
        val a1 = (startArgb shr 24) and 0xFF
        val r1 = (startArgb shr 16) and 0xFF
        val g1 = (startArgb shr 8) and 0xFF
        val b1 = startArgb and 0xFF

        val a2 = (endArgb shr 24) and 0xFF
        val r2 = (endArgb shr 16) and 0xFF
        val g2 = (endArgb shr 8) and 0xFF
        val b2 = endArgb and 0xFF

        val a = (a1 + (a2 - a1) * eased).toInt()
        val r = (r1 + (r2 - r1) * eased).toInt()
        val g = (g1 + (g2 - g1) * eased).toInt()
        val b = (b1 + (b2 - b1) * eased).toInt()

        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
