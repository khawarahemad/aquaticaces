package com.aquaticaces.module.impl.ghost

import com.aquaticaces.AquaticAces
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.ui.ClickGUI
import org.lwjgl.glfw.GLFW

/**
 * SelfDestruct module.
 * Wipes the client completely from memory, unregisters all event listeners,
 * clears module list, flushes keybinds/commands, removes mod ID container from Fabric,
 * and forces JVM Garbage Collector to clean traces.
 */
class SelfDestruct : Module("SelfDestruct", "Wipes client traces from the JVM.", Category.GHOST) {

    companion object {
        @JvmField
        var destructed = false
    }

    override fun onEnable() {
        destructed = true

        // 1. Disable all modules except this one
        for (module in ModuleManager.modules) {
            if (module !== this && module.isEnabled) {
                module.isEnabled = false
            }
        }

        // 2. Clear GUI screens
        mc.execute {
            if (mc.screen is ClickGUI) {
                mc.setScreen(null)
            }
        }

        // 3. Unregister everything from EventBus
        AquaticAces.eventBus.clear()

        // 4. Clear registries
        ModuleManager.modules.clear()
        AquaticAces.commandManager.commands.clear()

        // 5. Unregister from Fabric Loader using reflection
        try {
            val loader = net.fabricmc.loader.api.FabricLoader.getInstance()
            val modsField = loader.javaClass.getDeclaredField("mods")
            modsField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val mods = modsField.get(loader) as? MutableMap<String, *>
            if (mods != null) {
                mods.remove("aquaticaces")
            }
        } catch (e: Exception) {
            try {
                val loader = net.fabricmc.loader.api.FabricLoader.getInstance()
                for (field in loader.javaClass.declaredFields) {
                    if (Map::class.java.isAssignableFrom(field.type)) {
                        field.isAccessible = true
                        val map = field.get(loader) as? MutableMap<*, *>
                        if (map != null) {
                            map.remove("aquaticaces")
                        }
                    }
                }
            } catch (ex: Exception) {
                // Ignore failure
            }
        }

        // 6. Request JVM GC to erase garbage from heap
        System.gc()
    }
}
