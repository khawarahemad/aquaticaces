package com.aquaticaces.module

import com.aquaticaces.AquaticAces
import com.aquaticaces.core.NotificationManager
import com.aquaticaces.core.ServerDetector
import com.aquaticaces.module.setting.Setting
import net.minecraft.client.Minecraft

open class Module(
    val name: String,
    val description: String,
    val category: Category,
    var keybind: Int = 0,
    var onlyInGame: Boolean = true,
    var pauseInLobby: Boolean = false
) {
    protected val mc: Minecraft = Minecraft.getInstance()
    val settings = mutableListOf<Setting<*>>()

    var isEnabled: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            if (value) onEnable() else onDisable()
            val state = if (value) "ENABLED" else "DISABLED"
            val color = if (value) 0xFF00FF88.toInt() else 0xFFFF4444.toInt()
            NotificationManager.push(name, state, color)
        }

    fun toggle() { isEnabled = !isEnabled }

    fun canRun(): Boolean {
        if (onlyInGame && !ServerDetector.isInGame()) return false
        if (pauseInLobby && ServerDetector.isLobby()) return false
        return true
    }

    open fun onEnable() {
        ModuleConflicts.disableConflicting(this)
        AquaticAces.eventBus.register(this)
        AquaticAces.logger.info("Module '$name' enabled.")
    }

    open fun onDisable() {
        AquaticAces.eventBus.unregister(this)
        AquaticAces.logger.info("Module '$name' disabled.")
    }

    fun addSettings(vararg settings: Setting<*>) { this.settings.addAll(settings) }
    open fun onInit() {}
    open fun onSave(settingsMap: MutableMap<String, String>) {}
    open fun onLoad(settingsMap: Map<String, String>) {}
}
