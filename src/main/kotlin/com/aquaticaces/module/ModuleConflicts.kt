package com.aquaticaces.module

import com.aquaticaces.core.NotificationManager

object ModuleConflicts {
    private val conflicts: Map<String, List<String>> = mapOf(
        "Freecam" to listOf("Scaffold", "Surround", "Blink", "Flight"),
        "Blink" to listOf("Freecam", "Scaffold"),
        "Flight" to listOf("Freecam", "ElytraFly"),
        "ElytraFly" to listOf("Flight"),
        "Zoom" to listOf("Freecam"),
        "AimBot" to listOf("AimAssist", "SilentAim", "KillAura", "TriggerBot"),
        "AimAssist" to listOf("AimBot"),
        "SilentAim" to listOf("AimBot"),
        "KillAura" to listOf("AimBot"),
        "TriggerBot" to listOf("AimBot"),
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
