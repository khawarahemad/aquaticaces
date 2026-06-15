package com.aquaticaces.module.impl.combat

import com.aquaticaces.core.BacktrackStore
import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.entity.LivingEntity

class Backtrack : Module("Backtrack", "Targets past enemy positions.", Category.COMBAT) {
    val delay = NumberSetting("Delay", 200.0, 50.0, 500.0, 25.0)
    init { addSettings(delay) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val level = mc.level ?: return
        for (entity in level.entitiesForRendering()) {
            if (entity is LivingEntity && entity.isAlive) BacktrackStore.record(entity)
        }
    }

    override fun onDisable() {
        super.onDisable()
        BacktrackStore.clear()
    }

    companion object {
        @JvmStatic
        fun delayedPosition(entity: LivingEntity): net.minecraft.world.phys.Vec3? {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("Backtrack") as? Backtrack ?: return null
            if (!module.isEnabled) return null
            return BacktrackStore.getDelayedPosition(entity, module.delay.value.toLong())
        }
    }
}
