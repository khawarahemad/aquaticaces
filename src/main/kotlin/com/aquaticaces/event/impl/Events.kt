package com.aquaticaces.event.impl

import com.aquaticaces.event.Event
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.protocol.Packet
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

class EventClientTick(val phase: Phase) : Event() {
    enum class Phase { PRE, POST }
    override fun isCancellable(): Boolean = false
}

class EventRender3D(val poseStack: PoseStack, val partialTick: Float) : Event() {
    override fun isCancellable(): Boolean = false
}

class EventRender2D(val guiGraphics: GuiGraphics, val partialTick: Float) : Event() {
    override fun isCancellable(): Boolean = false
}

class EventPacketSend(var packet: Packet<*>) : Event() {
    override fun isCancellable(): Boolean = true
    fun replace(newPacket: Packet<*>) { packet = newPacket }
}

class EventPacketReceive(var packet: Packet<*>) : Event() {
    override fun isCancellable(): Boolean = true
    fun replace(newPacket: Packet<*>) { packet = newPacket }
}

class EventChatInput(var message: String) : Event() {
    override fun isCancellable(): Boolean = true
}

class EventKeyInput(
    val key: Int,
    val scanCode: Int,
    val action: Int,
    val modifiers: Int
) : Event() {
    override fun isCancellable(): Boolean = true
}

class EventMove(var x: Double, var y: Double, var z: Double) : Event() {
    override fun isCancellable(): Boolean = true
}

class EventJump : Event() {
    override fun isCancellable(): Boolean = true
}

class EventAttack(val target: Entity) : Event() {
    override fun isCancellable(): Boolean = true
}

class EventSlowdown(var multiplier: Float) : Event() {
    override fun isCancellable(): Boolean = true
}

class EventScreenOpen(val screen: Screen?) : Event() {
    override fun isCancellable(): Boolean = false
}

class EventWorldChange : Event() {
    override fun isCancellable(): Boolean = false
}
