package com.aquaticaces.module.setting

/**
 * Base abstract class for modular configuration settings.
 */
abstract class Setting<T>(
    val name: String,
    var value: T
) {
    // Dynamic visibility hook. Evaluates if this setting should be drawn in the UI.
    var isVisible: () -> Boolean = { true }

    fun dependsOn(parent: ModeSetting, value: String) {
        isVisible = { parent.value == value }
    }

    fun dependsOn(parent: BooleanSetting, value: Boolean = true) {
        isVisible = { parent.value == value }
    }

    abstract fun getValueAsString(): String
    abstract fun setValueFromString(valueStr: String)
}

/**
 * Setting wrapper for toggles (Boolean values).
 */
class BooleanSetting(
    name: String,
    defaultValue: Boolean
) : Setting<Boolean>(name, defaultValue) {
    
    override fun getValueAsString(): String = value.toString()
    
    override fun setValueFromString(valueStr: String) {
        value = valueStr.toBoolean()
    }
}

/**
 * Setting wrapper for numerical ranges (Double values) with clamping boundaries.
 */
class NumberSetting(
    name: String,
    defaultValue: Double,
    val min: Double,
    val max: Double,
    val increment: Double
) : Setting<Double>(name, defaultValue) {

    init {
        value = clamp(defaultValue)
    }

    private fun clamp(valToClamp: Double): Double {
        val clamped = valToClamp.coerceIn(min, max)
        return Math.round(clamped / increment) * increment
    }

    override fun getValueAsString(): String = value.toString()

    override fun setValueFromString(valueStr: String) {
        val parsed = valueStr.toDoubleOrNull()
        if (parsed != null) {
            value = clamp(parsed)
        }
    }
}

/**
 * Setting wrapper for list modes (String selections).
 */
class ModeSetting(
    name: String,
    defaultValue: String,
    val modes: List<String>
) : Setting<String>(name, defaultValue) {

    init {
        if (!modes.contains(defaultValue)) {
            value = modes.firstOrNull() ?: defaultValue
        }
    }

    /**
     * Cycles to the next mode option in the list.
     */
    fun cycle() {
        if (modes.isNotEmpty()) {
            val nextIndex = (modes.indexOf(value) + 1) % modes.size
            value = modes[nextIndex]
        }
    }

    override fun getValueAsString(): String = value

    override fun setValueFromString(valueStr: String) {
        if (modes.contains(valueStr)) {
            value = valueStr
        }
    }
}

/**
 * Setting wrapper for packed color values (ARGB).
 */
class ColorSetting(
    name: String,
    defaultValue: Int
) : Setting<Int>(name, defaultValue) {

    val red: Int get() = (value shr 16) and 0xFF
    val green: Int get() = (value shr 8) and 0xFF
    val blue: Int get() = value and 0xFF
    val alpha: Int get() = (value shr 24) and 0xFF

    fun setColor(r: Int, g: Int, b: Int, a: Int) {
        value = ((a and 0xFF) shl 24) or ((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or (b and 0xFF)
    }

    override fun getValueAsString(): String = value.toString()

    override fun setValueFromString(valueStr: String) {
        val parsed = valueStr.toIntOrNull()
        if (parsed != null) {
            value = parsed
        }
    }
}
