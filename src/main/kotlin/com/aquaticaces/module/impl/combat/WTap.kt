package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventAttack
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting

class WTap : Module("WTap", "Resets sprint on hit for extra knockback.", Category.COMBAT) {
    val onlyPlayers = BooleanSetting("PlayersOnly", true)
    init { addSettings(onlyPlayers) }

    @Subscribe
    fun onAttack(event: EventAttack) {
        if (!canRun()) return
        if (onlyPlayers.value && event.target !is net.minecraft.world.entity.player.Player) return
        val player = mc.player ?: return
        player.setSprinting(false)
        player.setSprinting(true)
    }
}
