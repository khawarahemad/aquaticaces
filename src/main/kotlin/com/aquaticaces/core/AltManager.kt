package com.aquaticaces.core

import com.aquaticaces.mixin.MinecraftUserAccessor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.client.User
import net.minecraft.client.gui.screens.TitleScreen
import java.io.File
import java.util.Optional
import java.util.UUID

@Serializable
data class AltEntry(val username: String)

@Serializable
data class AltsFile(val alts: MutableList<AltEntry> = mutableListOf(), var selected: String? = null)

object AltManager {
    private val alts = mutableListOf<AltEntry>()
    var selectedUsername: String? = null
        private set

    private val file: File
        get() = File(Minecraft.getInstance().gameDirectory, "aquaticaces/alts.json")

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun init() = load()

    fun all(): List<AltEntry> = alts.toList()

    fun add(username: String) {
        val name = username.trim()
        if (name.isBlank() || name.length > 16) return
        if (alts.any { it.username.equals(name, ignoreCase = true) }) return
        alts.add(AltEntry(name))
        save()
        NotificationManager.success("Alts", "Added $name")
    }

    fun remove(username: String) {
        alts.removeIf { it.username.equals(username, ignoreCase = true) }
        if (selectedUsername?.equals(username, ignoreCase = true) == true) selectedUsername = null
        save()
    }

    fun switchTo(username: String): Boolean {
        val name = username.trim()
        if (name.isBlank()) return false
        val mc = Minecraft.getInstance()

        if (mc.player != null) {
            mc.execute { mc.disconnect(TitleScreen()) }
        }

        val uuid = UUID.nameUUIDFromBytes("OfflinePlayer:$name".toByteArray(Charsets.UTF_8))
        val user = User(name, uuid, "0", Optional.empty(), Optional.empty(), User.Type.LEGACY)
        (mc as MinecraftUserAccessor).aquaticaces_setUser(user)
        selectedUsername = name
        save()
        NotificationManager.success("Alts", "Switched to $name")
        return true
    }

    fun switchToIndex(index: Int): Boolean {
        val entry = alts.getOrNull(index) ?: return false
        return switchTo(entry.username)
    }

    private fun load() {
        alts.clear()
        if (!file.exists()) return
        try {
            val data = json.decodeFromString<AltsFile>(file.readText())
            alts.addAll(data.alts)
            selectedUsername = data.selected
        } catch (_: Exception) {}
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(json.encodeToString(AltsFile(alts, selectedUsername)))
    }
}
