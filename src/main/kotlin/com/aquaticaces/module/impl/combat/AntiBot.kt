package com.aquaticaces.module.impl.combat

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class AntiBot : Module("AntiBot", "Filters fake player entities.", Category.COMBAT) {
    val tabCheck = BooleanSetting("TabCheck", true)
    init { addSettings(tabCheck) }

    companion object {
        private val mc get() = Minecraft.getInstance()

        fun isBot(entity: LivingEntity): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("AntiBot") as? AntiBot
            if (module == null || !module.isEnabled) return false
            if (entity !is Player) return false
            val name = entity.gameProfile.name
            if (name.isBlank() || name.length < 3) return true
            if (module.tabCheck.value) {
                val tab = mc.connection?.onlinePlayers?.any { it.profile.name.equals(name, true) } == true
                if (!tab) return true
            }
            if (entity.xRot == 0f && entity.yRot == 0f && entity.tickCount < 20) return true
            return false
        }
    }
}
