package com.aquaticaces.module

import com.aquaticaces.core.NotificationManager

object ModuleConflicts {
    private val conflicts: Map<String, List<String>> = mapOf(
        "Freecam" to listOf("Scaffold", "AirScaffold", "Surround", "Blink", "Flight"),
        "Blink" to listOf("Freecam", "Scaffold", "AirScaffold"),
        "Flight" to listOf("Freecam", "ElytraFly"),
        "ElytraFly" to listOf("Flight"),
        "Zoom" to listOf("Freecam"),
        "Scaffold" to listOf("AirScaffold"),
        "AirScaffold" to listOf("Scaffold", "Freecam", "Blink"),
        "WallHit" to listOf("KillAura", "TriggerBot"),
        "KillAura" to listOf("AimBot", "WallHit", "MaceAura"),
        "MaceAura" to listOf("KillAura", "TriggerBot"),
        "BedAura" to listOf("AutoAnchor", "AutoCrystal"),
        "AutoAnchor" to listOf("BedAura"),
        "AutoCrystal" to listOf("BedAura"),
        "AimBot" to listOf("AimAssist", "SilentAim", "KillAura", "TriggerBot"),
        "AimAssist" to listOf("AimBot"),
        "SilentAim" to listOf("AimBot"),
        "TriggerBot" to listOf("AimBot", "WallHit"),
    )

    fun disableConflicting(module: Module) {
        val names = conflicts[module.name] ?: return
        for (name in names) {
            val other = ModuleManager.getModuleByName(name) ?: continue
            if (other.isEnabled) {
                other.isEnabled = false
                NotificationManager.warn(module.name, "Disabled $name (conflict)")
            }
        }
    }

    fun getConflicts(name: String): List<String> = conflicts[name] ?: emptyList()
}
