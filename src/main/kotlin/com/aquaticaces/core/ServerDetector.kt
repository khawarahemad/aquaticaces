package com.aquaticaces.core

import net.minecraft.client.Minecraft

enum class ServerType {
    UNKNOWN, SINGLEPLAYER, VANILLA, HYPIXEL, MINEHUT, CONSTANTIAM, OTHER
}

object ServerDetector {
    private val mc get() = Minecraft.getInstance()

    fun currentType(): ServerType {
        val server = mc.currentServer ?: return if (mc.hasSingleplayerServer()) ServerType.SINGLEPLAYER else ServerType.UNKNOWN
        val address = server.ip.lowercase()
        val brand = mc.player?.connection?.serverBrand()?.lowercase() ?: ""
        return when {
            address.contains("hypixel") || brand.contains("hypixel") -> ServerType.HYPIXEL
            address.contains("minehut") -> ServerType.MINEHUT
            address.contains("constantiam") -> ServerType.CONSTANTIAM
            brand.contains("vanilla") || brand.isBlank() -> ServerType.VANILLA
            else -> ServerType.OTHER
        }
    }

    fun isInGame(): Boolean = mc.player != null && mc.level != null

    fun isOnServer(): Boolean = mc.currentServer != null

    fun shouldRunCombat(): Boolean = isInGame() && currentType() != ServerType.UNKNOWN

    fun isLobby(): Boolean {
        val scoreboard = mc.level?.scoreboard ?: return false
        val objective = scoreboard.getDisplayObjective(net.minecraft.world.scores.DisplaySlot.SIDEBAR) ?: return false
        val title = objective.displayName.string.lowercase()
        return title.contains("lobby") || title.contains("hub")
    }
}
