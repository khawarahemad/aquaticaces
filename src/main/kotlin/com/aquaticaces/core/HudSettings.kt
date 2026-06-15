package com.aquaticaces.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import java.io.File

@Serializable
data class HudToggles(
    var targetHud: Boolean = true,
    var statsHud: Boolean = true,
    var arrayList: Boolean = true,
    var notifications: Boolean = true,
    var performance: Boolean = true,
    var coordinates: Boolean = true
)

object HudSettings {
    var toggles = HudToggles()
    private val file: File
        get() = File(Minecraft.getInstance().gameDirectory, "aquaticaces/hud-settings.json")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun isEnabled(key: String): Boolean = when (key) {
        "target" -> toggles.targetHud
        "stats" -> toggles.statsHud
        "arraylist" -> toggles.arrayList
        "notifications" -> toggles.notifications
        "performance" -> toggles.performance
        "coordinates" -> toggles.coordinates
        else -> true
    }

    fun toggle(key: String) {
        when (key) {
            "target" -> toggles.targetHud = !toggles.targetHud
            "stats" -> toggles.statsHud = !toggles.statsHud
            "arraylist" -> toggles.arrayList = !toggles.arrayList
            "notifications" -> toggles.notifications = !toggles.notifications
            "performance" -> toggles.performance = !toggles.performance
            "coordinates" -> toggles.coordinates = !toggles.coordinates
        }
        save()
    }

    fun load() {
        if (!file.exists()) return
        try { toggles = json.decodeFromString<HudToggles>(file.readText()) } catch (_: Exception) {}
    }

    fun save() {
        file.parentFile?.mkdirs()
        file.writeText(json.encodeToString(toggles))
    }
}
