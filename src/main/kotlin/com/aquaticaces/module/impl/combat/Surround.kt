package com.aquaticaces.module.impl.combat

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.impl.utility.BlockPlacer
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.core.BlockPos

class Surround : Module("Surround", "Places blocks around your feet.", Category.COMBAT) {
    val blocksPerTick = NumberSetting("Blocks/Tick", 2.0, 1.0, 8.0, 1.0)
    val center = BooleanSetting("Center", true)
    init { addSettings(blocksPerTick, center) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        if (BlockPlacer.findBlockHand(player) == null) return

        if (center.value && player.onGround()) {
            val centerPos = BlockPos.containing(player.x, player.y, player.z)
            val targetX = centerPos.x + 0.5
            val targetZ = centerPos.z + 0.5
            player.setPos(targetX, player.y, targetZ)
        }

        val feet = player.blockPosition()
        val offsets = listOf(
            BlockPos(0, 0, -1), BlockPos(0, 0, 1), BlockPos(-1, 0, 0), BlockPos(1, 0, 0),
            BlockPos(-1, 0, -1), BlockPos(1, 0, -1), BlockPos(-1, 0, 1), BlockPos(1, 0, 1)
        )

        var placed = 0
        for (offset in offsets) {
            if (placed >= blocksPerTick.value.toInt()) break
            val pos = feet.offset(offset)
            if (BlockPlacer.placeAt(level, player, pos)) placed++
        }
    }
}
