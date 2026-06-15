package com.aquaticaces.command

import com.aquaticaces.AquaticAces
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventChatInput
import com.aquaticaces.module.ModuleManager
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import java.util.Locale

/**
 * Handles checking chat input prefixes, parsing parameters, and routing executing commands.
 */
class CommandManager {
    val prefix = "."
    val commands = mutableListOf<Command>()

    init {
        commands.add(HelpCommand())
        commands.add(FriendCommand())
        commands.add(AltCommand())
        commands.add(HudCommand())
        commands.add(BindCommand())
        commands.add(ToggleCommand())
        commands.add(ConfigCommand())
        commands.add(SchematicCommand())
        commands.add(WaypointCommand())
    }

    @Subscribe
    fun handleChatInput(event: EventChatInput) {
        if (!event.message.startsWith(prefix)) return
        
        // Prevent sending chat packet to the server
        event.cancel()

        val rawInput = event.message.substring(prefix.length)
        if (rawInput.isBlank()) {
            printToChat("§cSyntax error: prefix alone is invalid.")
            return
        }

        val parts = rawInput.split(" ").filter { it.isNotBlank() }
        val cmdName = parts[0].lowercase(Locale.ROOT)
        val args = parts.drop(1).toTypedArray()

        val matchedCommand = commands.firstOrNull {
            it.name.equals(cmdName, ignoreCase = true) || it.aliases.contains(cmdName)
        }

        if (matchedCommand != null) {
            try {
                matchedCommand.execute(args)
            } catch (e: Exception) {
                printToChat("§cExecution failure: ${e.message}")
            }
        } else {
            printToChat("§cCommand not recognized. Type help or try other shortcuts.")
        }
    }

    private fun printToChat(msgStr: String) {
        Minecraft.getInstance().player?.sendSystemMessage(
            Component.literal("§d[Aquatic Aces] $msgStr")
        )
    }

    private class HelpCommand : Command("help", "Lists available commands", "help", listOf("h", "?")) {
        override fun execute(args: Array<String>) {
            info("§7Commands:")
            for (cmd in AquaticAces.commandManager.commands) {
                info("§d.${cmd.name}§7 — ${cmd.description} §8(${cmd.syntax})")
            }
        }
    }

    private class FriendCommand : Command("friend", "Manages friends list", "friend <add/remove/list> [name]", listOf("friends")) {
        override fun execute(args: Array<String>) {
            if (args.isEmpty()) {
                info("§cUsage: $syntax")
                return
            }
            when (args[0].lowercase(Locale.ROOT)) {
                "add" -> {
                    if (args.size < 2) { info("§cUsage: friend add <name>"); return }
                    com.aquaticaces.core.FriendsManager.add(args[1])
                }
                "remove", "del" -> {
                    if (args.size < 2) { info("§cUsage: friend remove <name>"); return }
                    com.aquaticaces.core.FriendsManager.remove(args[1])
                    info("§aRemoved ${args[1]}")
                }
                "list" -> {
                    val all = com.aquaticaces.core.FriendsManager.all()
                    info(if (all.isEmpty()) "§7No friends saved." else "§aFriends: ${all.joinToString(", ")}")
                }
                else -> info("§cUsage: $syntax")
            }
        }
    }

    private class AltCommand : Command("alt", "Manages alt accounts", "alt <add/remove/list/switch> [name]", listOf("alts")) {
        override fun execute(args: Array<String>) {
            if (args.isEmpty()) {
                mc.execute { mc.setScreen(com.aquaticaces.ui.AltManagerScreen(mc.screen)) }
                return
            }
            when (args[0].lowercase(Locale.ROOT)) {
                "add" -> {
                    if (args.size < 2) { info("§cUsage: alt add <name>"); return }
                    com.aquaticaces.core.AltManager.add(args[1])
                }
                "remove", "del" -> {
                    if (args.size < 2) { info("§cUsage: alt remove <name>"); return }
                    com.aquaticaces.core.AltManager.remove(args[1])
                    info("§aRemoved ${args[1]}")
                }
                "list" -> {
                    val all = com.aquaticaces.core.AltManager.all()
                    info(if (all.isEmpty()) "§7No alts saved." else "§aAlts: ${all.joinToString { it.username }}")
                }
                "switch" -> {
                    if (args.size < 2) { info("§cUsage: alt switch <name>"); return }
                    if (!com.aquaticaces.core.AltManager.switchTo(args[1])) info("§cFailed to switch.")
                }
                else -> mc.execute { mc.setScreen(com.aquaticaces.ui.AltManagerScreen(mc.screen)) }
            }
        }
    }

    private class HudCommand : Command("hud", "Opens HUD settings screen", "hud [edit]", listOf("hudeditor")) {
        override fun execute(args: Array<String>) {
            when (args.getOrNull(0)?.lowercase(Locale.ROOT)) {
                "edit", "pos", "positions" -> mc.execute { mc.setScreen(com.aquaticaces.ui.HudEditorScreen(mc.screen)) }
                else -> mc.execute { mc.setScreen(com.aquaticaces.ui.HudToggleScreen(mc.screen)) }
            }
        }
    }

    private class BindCommand : Command("bind", "Updates module keybind mappings", "bind <module> <key>") {
        override fun execute(args: Array<String>) {
            if (args.size < 2) {
                info("§cUsage: $syntax")
                return
            }

            val targetName = args[0]
            val keyStr = args[1].uppercase(Locale.ROOT)

            val module = ModuleManager.getModuleByName(targetName)
            if (module == null) {
                info("§cModule not found.")
                return
            }

            val keyCode = resolveKey(keyStr)
            if (keyCode == -1) {
                info("§cKey not found.")
                return
            }

            module.keybind = keyCode
            info("§aSuccess: mapped ${module.name} to key $keyStr")
        }

        private fun resolveKey(str: String): Int {
            if (str.equals("NONE", ignoreCase = true)) return GLFW.GLFW_KEY_UNKNOWN
            return try {
                val field = GLFW::class.java.getField("GLFW_KEY_$str")
                field.getInt(null)
            } catch (e: NoSuchFieldException) {
                str.toIntOrNull() ?: -1
            }
        }
    }

    /**
     * .toggle <module>
     */
    private class ToggleCommand : Command("toggle", "Toggles active mod state", "toggle <module>", listOf("t")) {
        override fun execute(args: Array<String>) {
            if (args.isEmpty()) {
                info("§cUsage: $syntax")
                return
            }

            val targetName = args[0]
            val module = ModuleManager.getModuleByName(targetName)
            if (module == null) {
                info("§cModule not found.")
                return
            }

            module.toggle()
            val stateColor = if (module.isEnabled) "§aENABLED" else "§cDISABLED"
            info("§d${module.name}§7 is now $stateColor")
        }
    }

    /**
     * .config <load/save> <profile>
     */
    private class ConfigCommand : Command("config", "Configures load/save configs", "config <load/save> [profile] [category]") {
        override fun execute(args: Array<String>) {
            if (args.isEmpty()) {
                info("§cUsage: $syntax")
                return
            }

            val action = args[0].lowercase(Locale.ROOT)
            val profile = args.getOrNull(1) ?: "Ghost"
            val categoryName = args.getOrNull(2)

            when (action) {
                "load" -> {
                    if (categoryName != null) {
                        val cat = com.aquaticaces.module.Category.entries.firstOrNull {
                            it.name.equals(categoryName, ignoreCase = true)
                        }
                        if (cat == null) { info("§cUnknown category."); return }
                        AquaticAces.configManager.loadCategory(profile, cat)
                        info("§aLoaded $profile (${cat.name})")
                    } else {
                        AquaticAces.configManager.loadConfig(profile)
                        info("§aLoaded profile: $profile")
                    }
                }
                "save" -> {
                    if (categoryName != null) {
                        val cat = com.aquaticaces.module.Category.entries.firstOrNull {
                            it.name.equals(categoryName, ignoreCase = true)
                        }
                        if (cat == null) { info("§cUnknown category."); return }
                        AquaticAces.configManager.saveCategory(profile, cat)
                        info("§aSaved $profile (${cat.name})")
                    } else {
                        AquaticAces.configManager.saveConfig(profile)
                        info("§aSaved profile: $profile")
                    }
                }
                else -> info("§cUsage: $syntax")
            }
        }
    }

    private class WaypointCommand : Command("wp", "Manages waypoints", "wp <add/remove/list> [name]", listOf("waypoint")) {
        override fun execute(args: Array<String>) {
            when (args.getOrNull(0)?.lowercase(Locale.ROOT)) {
                "add" -> {
                    val name = args.getOrNull(1) ?: "Point"
                    com.aquaticaces.core.WaypointManager.addAtPlayer(name)
                }
                "remove", "del" -> {
                    if (args.size < 2) { info("§cUsage: wp remove <name>"); return }
                    com.aquaticaces.core.WaypointManager.remove(args[1])
                    info("§aRemoved ${args[1]}")
                }
                "list" -> {
                    val all = com.aquaticaces.core.WaypointManager.all()
                    info(if (all.isEmpty()) "§7No waypoints." else all.joinToString { "${it.name} @ ${it.x},${it.y},${it.z}" })
                }
                else -> info("§cUsage: $syntax")
            }
        }
    }

    private class SchematicCommand : Command("schematic", "Capture and load schematics", "schematic <save/load/list/clear> [name] [radius]", listOf("schem")) {
        override fun execute(args: Array<String>) {
            when (args.getOrNull(0)?.lowercase(Locale.ROOT)) {
                "save" -> {
                    if (args.size < 2) { info("§cUsage: schematic save <name> [radius]"); return }
                    val player = mc.player ?: return
                    val radius = args.getOrNull(2)?.toIntOrNull()?.coerceIn(1, 32) ?: 8
                    com.aquaticaces.core.SchematicManager.capture(args[1], player.blockPosition(), radius)
                }
                "load" -> {
                    if (args.size < 2) { info("§cUsage: schematic load <name>"); return }
                    if (!com.aquaticaces.core.SchematicManager.load(args[1])) info("§cSchematic not found.")
                }
                "list" -> {
                    val all = com.aquaticaces.core.SchematicManager.list()
                    info(if (all.isEmpty()) "§7No schematics saved." else "§aSchematics: ${all.joinToString()}")
                }
                "clear" -> {
                    com.aquaticaces.core.SchematicManager.clear()
                    info("§aCleared active schematic.")
                }
                else -> info("§cUsage: $syntax")
            }
        }
    }
}
