package com.aquaticaces.config

import com.aquaticaces.AquaticAces
import com.aquaticaces.module.ModuleManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import net.minecraft.client.Minecraft
import java.io.File
import java.io.IOException

@Serializable
data class ModuleProfile(
    val enabled: Boolean,
    val keybind: Int,
    val settings: Map<String, String>
)

@Serializable
data class ClientProfile(
    val modules: Map<String, ModuleProfile>
)

/**
 * Handles saving and loading JSON-based configuration profiles.
 */
class ConfigManager {
    private val profilesDirectory: File

    init {
        val mcDir = Minecraft.getInstance().gameDirectory
        profilesDirectory = File(mcDir, "aquaticaces/profiles")
        if (!profilesDirectory.exists()) {
            profilesDirectory.mkdirs()
        }
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Saves only modules from a specific category.
     */
    fun saveCategory(profileName: String, category: com.aquaticaces.module.Category) {
        val moduleProfiles = mutableMapOf<String, ModuleProfile>()
        for (module in ModuleManager.getModulesByCategory(category)) {
            val settingsMap = module.settings.associate { it.name to it.getValueAsString() }
            moduleProfiles[module.name] = ModuleProfile(module.isEnabled, module.keybind, settingsMap)
        }
        val file = File(profilesDirectory, "$profileName-${category.name.lowercase()}.json")
        file.writeText(json.encodeToString(ClientProfile(moduleProfiles)))
        AquaticAces.logger.info("Saved ${category.name} profile $profileName")
    }

    /**
     * Loads only modules from a specific category.
     */
    fun loadCategory(profileName: String, category: com.aquaticaces.module.Category) {
        val file = File(profilesDirectory, "$profileName-${category.name.lowercase()}.json")
        if (!file.exists()) return
        applyProfile(json.decodeFromString<ClientProfile>(file.readText()), category)
    }

    private fun applyProfile(profile: ClientProfile, category: com.aquaticaces.module.Category? = null) {
        for (module in ModuleManager.modules) {
            if (category != null && module.category != category) continue
            val modProfile = profile.modules[module.name] ?: continue
            module.keybind = modProfile.keybind
            for ((settingName, settingValue) in modProfile.settings) {
                module.settings.firstOrNull { it.name.equals(settingName, ignoreCase = true) }
                    ?.setValueFromString(settingValue)
            }
            module.isEnabled = modProfile.enabled
        }
    }

    /**
     * Serializes all module states to a JSON file.
     */
    fun saveConfig(profileName: String) {
        val moduleProfiles = mutableMapOf<String, ModuleProfile>()

        for (module in ModuleManager.modules) {
            val settingsMap = module.settings.associate { it.name to it.getValueAsString() }
            moduleProfiles[module.name] = ModuleProfile(module.isEnabled, module.keybind, settingsMap)
        }

        val profile = ClientProfile(moduleProfiles)
        val file = File(profilesDirectory, "$profileName.json")
        try {
            val jsonStr = json.encodeToString(profile)
            file.writeText(jsonStr)
            AquaticAces.logger.info("Successfully saved profile $profileName")
        } catch (e: IOException) {
            AquaticAces.logger.error("Failed to save profile $profileName", e)
        }
    }

    /**
     * Loads a profile from JSON and maps configuration parameters back to modules.
     */
    fun loadConfig(profileName: String) {
        val file = File(profilesDirectory, "$profileName.json")
        if (!file.exists()) {
            generateDefaultProfile(profileName)
            return
        }

        try {
            val jsonStr = file.readText()
            val profile = json.decodeFromString<ClientProfile>(jsonStr)

            applyProfile(profile)
            AquaticAces.logger.info("Successfully loaded profile $profileName")
        } catch (e: Exception) {
            AquaticAces.logger.error("Failed to load profile $profileName", e)
        }
    }

    /**
     * Seeding logic for default configurations.
     */
    private fun generateDefaultProfile(profileName: String) {
        if (profileName.equals("Ghost", ignoreCase = true)) {
            for (module in ModuleManager.modules) {
                module.isEnabled = false
                if (module.name.equals("KillAura", ignoreCase = true)) {
                    module.settings.firstOrNull { it.name.equals("Range", ignoreCase = true) }?.setValueFromString("3.1")
                } else if (module.name.equals("Velocity", ignoreCase = true)) {
                    module.settings.firstOrNull { it.name.equals("Horizontal", ignoreCase = true) }?.setValueFromString("0.9")
                    module.settings.firstOrNull { it.name.equals("Vertical", ignoreCase = true) }?.setValueFromString("1.0")
                }
            }
            saveConfig("Ghost")
        } else if (profileName.equals("Blatant", ignoreCase = true)) {
            for (module in ModuleManager.modules) {
                module.isEnabled = false
                if (module.name.equals("KillAura", ignoreCase = true)) {
                    module.settings.firstOrNull { it.name.equals("Range", ignoreCase = true) }?.setValueFromString("6.0")
                } else if (module.name.equals("Velocity", ignoreCase = true)) {
                    module.settings.firstOrNull { it.name.equals("Horizontal", ignoreCase = true) }?.setValueFromString("0.0")
                    module.settings.firstOrNull { it.name.equals("Vertical", ignoreCase = true) }?.setValueFromString("0.0")
                }
            }
            saveConfig("Blatant")
        }
    }
}
