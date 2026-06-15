package com.aquaticaces.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import java.io.File

@Serializable
data class WaypointEntry(val name: String, val x: Int, val y: Int, val z: Int, val color: Int = 0xFF00C6FF.toInt())

@Serializable
data class WaypointsFile(val waypoints: MutableList<WaypointEntry> = mutableListOf())

object WaypointManager {
    private val waypoints = mutableListOf<WaypointEntry>()
    private val file: File
        get() = File(Minecraft.getInstance().gameDirectory, "aquaticaces/waypoints.json")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun init() = load()

    fun all(): List<WaypointEntry> = waypoints.toList()

    fun add(name: String, x: Int, y: Int, z: Int, color: Int = 0xFF00C6FF.toInt()) {
        waypoints.removeIf { it.name.equals(name, ignoreCase = true) }
        waypoints.add(WaypointEntry(name, x, y, z, color))
        save()
        NotificationManager.success("Waypoints", "Added $name")
    }

    fun addAtPlayer(name: String) {
        val player = Minecraft.getInstance().player ?: return
        add(name, player.blockX, player.blockY, player.blockZ)
    }

    fun remove(name: String) {
        waypoints.removeIf { it.name.equals(name, ignoreCase = true) }
        save()
    }

    fun clear() {
        waypoints.clear()
        save()
    }

    private fun load() {
        waypoints.clear()
        if (!file.exists()) return
        try {
            waypoints.addAll(json.decodeFromString<WaypointsFile>(file.readText()).waypoints)
        } catch (_: Exception) {}
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(json.encodeToString(WaypointsFile(waypoints)))
    }
}
