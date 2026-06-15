package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import com.aquaticaces.util.PacketUtils
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW

/**
 * Freecam module.
 * Decouples the client camera viewpoint from the player's physical body,
 * allowing flying scouting while maintaining static coordinates on the server.
 */
class Freecam : Module("Freecam", "Enables free camera movement.", Category.RENDER) {

    val speed = NumberSetting("Speed", 1.0, 0.2, 4.0, 0.2)

    private var startPos = Vec3.ZERO
    private var startYaw = 0f
    private var startPitch = 0f
    private var startFlying = false

    init {
        addSettings(speed)
    }

    override fun onEnable() {
        super.onEnable()
        val player = mc.player ?: return
        startPos = player.position()
        startYaw = player.yRot
        startPitch = player.xRot
        startFlying = player.abilities.flying
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return

        // Bypass wall collisions and enable flight locally
        player.noPhysics = true
        player.abilities.flying = true

        val spd = speed.value
        val yawRad = Math.toRadians(player.yRot.toDouble())
        val input = player.input

        var moveX = 0.0
        var moveZ = 0.0
        val forward = input.forwardImpulse
        val sideways = input.leftImpulse

        if (forward != 0f || sideways != 0f) {
            val forwardAngle = if (forward > 0f) 0.0 else if (forward < 0f) Math.PI else 0.0
            val sidewaysAngle = if (sideways > 0f) -Math.PI / 2.0 else if (sideways < 0f) Math.PI / 2.0 else 0.0
            val angle = yawRad + forwardAngle + sidewaysAngle

            moveX = -Math.sin(angle) * spd
            moveZ = Math.cos(angle) * spd
        }

        var moveY = 0.0
        if (input.jumping) {
            moveY = spd
        } else if (input.shiftKeyDown) {
            moveY = -spd
        }

        player.deltaMovement = Vec3(moveX, moveY, moveZ)
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        val packet = event.packet
        if (packet is ServerboundMovePlayerPacket) {
            PacketUtils.setPositionAndRotation(packet, startPos.x, startPos.y, startPos.z, startYaw, startPitch)
        }
    }

    override fun onDisable() {
        super.onDisable()
        val player = mc.player ?: return
        
        // Restore physical state and teleport camera back
        player.noPhysics = false
        player.abilities.flying = startFlying
        player.setPos(startPos.x, startPos.y, startPos.z)
        player.yRot = startYaw
        player.xRot = startPitch
    }
}
