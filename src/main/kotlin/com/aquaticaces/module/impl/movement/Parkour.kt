package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.core.BlockPos

class Parkour : Module("Parkour", "Auto jumps at block edges.", Category.MOVEMENT) {
    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        if (!player.onGround() || player.input.jumping) return
        val ahead = BlockPos.containing(player.x + -Math.sin(Math.toRadians(player.yRot.toDouble())) * 0.6, player.y - 0.5, player.z + Math.cos(Math.toRadians(player.yRot.toDouble())) * 0.6)
        val belowAhead = ahead.below()
        if (level.getBlockState(ahead).isAir && level.getBlockState(belowAhead).isAir) {
            player.jumpFromGround()
        }
    }
}
