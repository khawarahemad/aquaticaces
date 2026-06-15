package com.aquaticaces

import com.aquaticaces.core.AltManager
import com.aquaticaces.core.FriendsManager
import com.aquaticaces.core.WaypointManager
import com.aquaticaces.core.HudLayout
import com.aquaticaces.core.HudSettings
import com.aquaticaces.core.SchematicManager
import com.aquaticaces.core.RotationManager
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory
import com.aquaticaces.event.EventBus
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.command.CommandManager
import com.aquaticaces.config.ConfigManager
import com.aquaticaces.keybind.KeybindManager

object AquaticAces : ClientModInitializer {
    val logger = LoggerFactory.getLogger("AquaticAces")

    lateinit var eventBus: EventBus
        private set
    val moduleManager get() = ModuleManager
    lateinit var commandManager: CommandManager
        private set
    lateinit var keybindManager: KeybindManager
        private set
    lateinit var configManager: ConfigManager
        private set

    private val tickHook = object {
        @Subscribe
        fun onTick(event: EventClientTick) {
            if (event.phase == EventClientTick.Phase.POST) RotationManager.apply()
        }
    }

    override fun onInitializeClient() {
        logger.info("Initializing Aquatic Aces Client...")

        eventBus = EventBus()
        FriendsManager.init()
        AltManager.init()
        WaypointManager.init()
        HudLayout.load()
        HudSettings.load()
        SchematicManager.init()

        keybindManager = KeybindManager()
        eventBus.register(keybindManager)

        commandManager = CommandManager()
        eventBus.register(commandManager)

        configManager = ConfigManager()
        configManager.loadConfig("Ghost")

        eventBus.register(tickHook)
        eventBus.register(com.aquaticaces.ui.hud.TargetHUD())
        eventBus.register(com.aquaticaces.ui.hud.StatsHUD())
        eventBus.register(com.aquaticaces.ui.hud.ArrayListHUD())
        eventBus.register(com.aquaticaces.ui.hud.NotificationHUD())
        eventBus.register(com.aquaticaces.ui.hud.PerformanceHUD())
        eventBus.register(com.aquaticaces.ui.hud.CoordinatesHUD())

        for (module in ModuleManager.modules) module.onInit()

        logger.info("Aquatic Aces Client Initialization Complete!")
    }
}
