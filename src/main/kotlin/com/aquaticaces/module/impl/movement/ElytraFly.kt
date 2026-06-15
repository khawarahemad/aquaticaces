package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3

class ElytraFly : Module("ElytraFly", "Enhanced elytra flight control.", Category.MOVEMENT) {
    val speed = NumberSetting("Speed", 1.2, 0.5, 3.0, 0.1)
    init { addSettings(speed) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        if (!player.isFallFlying) return
        val look = player.lookAngle
        val mult = speed.value
        player.deltaMovement = Vec3(look.x * mult, look.y * mult, look.z * mult)
    }

    override fun onEnable() {
        super.onEnable()
        val player = mc.player ?: return
        if (player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).item != Items.ELYTRA) return
        player.startFallFlying()
    }
}
