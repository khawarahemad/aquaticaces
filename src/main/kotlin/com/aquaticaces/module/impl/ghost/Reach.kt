package com.aquaticaces.module.impl.ghost

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.accessor.ServerboundInteractPacketAccess
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.entity.ai.attributes.Attributes
import org.lwjgl.glfw.GLFW

class Reach : Module("Reach", "Extends attack and interaction range.", Category.GHOST) {
    val distance = NumberSetting("Distance", 3.5, 3.0, 5.0, 0.1)
    private var sendingSpoof = false
    init { addSettings(distance) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)?.baseValue = distance.value
        player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE)?.baseValue = distance.value
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        if (sendingSpoof || !canRun()) return
        val player = mc.player ?: return
        val connection = mc.connection ?: return
        val packet = event.packet
        if (packet is ServerboundInteractPacket) {
            val accessor = packet as ServerboundInteractPacketAccess
            val entity = mc.level?.getEntity(accessor.aquaticaces_getEntityId()) ?: return
            val dist = player.distanceTo(entity)
            if (dist > 3.0f && dist <= distance.value) {
                event.cancel()
                sendingSpoof = true
                try {
                    val playerPos = player.position()
                    val entityPos = entity.position()
                    val dir = playerPos.subtract(entityPos).normalize()
                    val spoofedPos = entityPos.add(dir.scale(2.8))
                    connection.send(ServerboundMovePlayerPacket.Pos(spoofedPos.x, spoofedPos.y, spoofedPos.z, player.onGround()))
                    connection.send(packet)
                    connection.send(ServerboundMovePlayerPacket.Pos(playerPos.x, playerPos.y, playerPos.z, player.onGround()))
                } finally { sendingSpoof = false }
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
        val player = mc.player ?: return
        player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)?.baseValue = 3.0
        player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE)?.baseValue = 4.5
    }
}
