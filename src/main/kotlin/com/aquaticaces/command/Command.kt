package com.aquaticaces.command

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

/**
 * Base template class for client-side chat commands.
 */
abstract class Command(
    val name: String,
    val description: String,
    val syntax: String,
    val aliases: List<String> = emptyList()
) {
    protected val mc: Minecraft = Minecraft.getInstance()

    /**
     * Executes the command parsing raw arguments.
     */
    abstract fun execute(args: Array<String>)

    /**
     * Sends a prefix notification in client chat.
     */
    protected fun info(message: String) {
        mc.player?.sendSystemMessage(
            Component.literal("§d[Aquatic Aces] §7$message")
        )
    }
}
