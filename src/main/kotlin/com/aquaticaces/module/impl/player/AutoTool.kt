package com.aquaticaces.module.impl.player

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.level.block.state.BlockState

class AutoTool : Module("AutoTool", "Selects best tool for block.", Category.PLAYER) {
    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val hit = mc.hitResult as? net.minecraft.world.phys.BlockHitResult ?: return
        val state: BlockState = mc.level?.getBlockState(hit.blockPos) ?: return
        var best = 0
        var bestSpeed = 1f
        for (i in 0..8) {
            val stack = player.inventory.getItem(i)
            val speed = stack.getDestroySpeed(state)
            if (speed > bestSpeed) { bestSpeed = speed; best = i }
        }
        if (bestSpeed > 1f) player.inventory.selected = best
    }
}
