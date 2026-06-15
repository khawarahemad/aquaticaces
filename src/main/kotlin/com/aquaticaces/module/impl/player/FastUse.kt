package com.aquaticaces.module.impl.player

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.mixin.LivingEntityAccessor
import com.aquaticaces.module.setting.NumberSetting

class FastUse : Module("FastUse", "Uses items faster.", Category.PLAYER) {
    val delay = NumberSetting("Delay", 0.0, 0.0, 4.0, 1.0)
    init { addSettings(delay) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        if (player.isUsingItem && player.useItemRemainingTicks > delay.value.toInt()) {
            (player as LivingEntityAccessor).aquaticaces_setUseItemRemaining(delay.value.toInt().coerceAtLeast(0))
        }
    }
}
