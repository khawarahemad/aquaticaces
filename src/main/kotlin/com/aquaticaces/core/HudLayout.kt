package com.aquaticaces.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import java.io.File

@Serializable
data class HudPositions(
    var targetHudX: Float = 200f,
    var targetHudY: Float = 320f,
    var statsHudX: Float = 10f,
    var statsHudY: Float = -1f,
    var arrayListX: Float = -1f,
    var arrayListY: Float = 2f
)

object HudLayout {
    var positions = HudPositions()
    private val file: File
        get() = File(Minecraft.getInstance().gameDirectory, "aquaticaces/hud.json")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    /** Negative values anchor from the right/bottom edge of the screen. */
    fun resolveX(configX: Float, screenWidth: Float, elementWidth: Float = 0f): Float =
        if (configX < 0f) screenWidth + configX - elementWidth else configX

    fun resolveY(configY: Float, screenHeight: Float, elementHeight: Float = 0f): Float =
        if (configY < 0f) screenHeight + configY - elementHeight else configY

    fun load() {
        if (!file.exists()) return
        try { positions = json.decodeFromString<HudPositions>(file.readText()) } catch (_: Exception) {}
    }

    fun save() {
        file.parentFile?.mkdirs()
        file.writeText(json.encodeToString(positions))
    }
}
