package com.aquaticaces.core

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import java.util.ArrayDeque

object BacktrackStore {
    data class Snapshot(val pos: Vec3, val time: Long)

    private val history = mutableMapOf<Int, ArrayDeque<Snapshot>>()

    fun record(entity: Entity) {
        val id = entity.id
        val deque = history.getOrPut(id) { ArrayDeque() }
        deque.addLast(Snapshot(entity.position(), System.currentTimeMillis()))
        while (deque.size > 20) deque.removeFirst()
    }

    fun getDelayedPosition(entity: Entity, delayMs: Long): Vec3? {
        val deque = history[entity.id] ?: return null
        val targetTime = System.currentTimeMillis() - delayMs
        var result: Vec3? = null
        for (snap in deque) {
            if (snap.time <= targetTime) result = snap.pos
            else break
        }
        return result ?: deque.firstOrNull()?.pos
    }

    fun clear() = history.clear()
}
