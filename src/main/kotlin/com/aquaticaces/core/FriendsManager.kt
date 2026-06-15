package com.aquaticaces.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import java.io.File

@Serializable
data class FriendsFile(val friends: MutableList<String> = mutableListOf())

object FriendsManager {
    private val friends = linkedSetOf<String>()
    private val file: File
        get() = File(Minecraft.getInstance().gameDirectory, "aquaticaces/friends.json")

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun init() = load()

    fun isFriend(name: String): Boolean = friends.any { it.equals(name, ignoreCase = true) }

    fun add(name: String) {
        if (name.isBlank()) return
        friends.add(name)
        save()
        NotificationManager.success("Friends", "Added $name")
    }

    fun remove(name: String) {
        friends.removeIf { it.equals(name, ignoreCase = true) }
        save()
    }

    fun toggle(name: String) {
        if (isFriend(name)) remove(name) else add(name)
    }

    fun all(): Set<String> = friends.toSet()

    private fun load() {
        friends.clear()
        if (!file.exists()) return
        try {
            val data = json.decodeFromString<FriendsFile>(file.readText())
            friends.addAll(data.friends)
        } catch (_: Exception) {}
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(json.encodeToString(FriendsFile(friends.toMutableList())))
    }
}
