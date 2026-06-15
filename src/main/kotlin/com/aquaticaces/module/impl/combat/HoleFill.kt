package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.impl.utility.BlockPlacer
import com.aquaticaces.module.setting.BooleanSetting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

class HoleFill : Module("HoleFill", "Fills the hole you are standing in.", Category.COMBAT) {
    val whileMoving = BooleanSetting("While Moving", false)
    init { addSettings(whileMoving) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        if (!whileMoving.value && (player.deltaMovement.horizontalDistanceSqr() > 0.001)) return
        if (BlockPlacer.findBlockHand(player) == null) return

        val feet = player.blockPosition()
        if (!level.getBlockState(feet).isAir) return

        var walls = 0
        for (dir in Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(feet.relative(dir)).isAir) walls++
        }
        if (walls < 3) return

        BlockPlacer.placeAt(level, player, feet)
    }
}
