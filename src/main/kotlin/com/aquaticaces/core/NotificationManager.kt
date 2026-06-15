package com.aquaticaces.core

import java.util.concurrent.CopyOnWriteArrayList

data class Notification(
    val title: String,
    val message: String,
    val color: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val durationMs: Long = 2500L
)

object NotificationManager {
    private val notifications = CopyOnWriteArrayList<Notification>()

    fun info(title: String, message: String) = push(title, message, 0xFF00C6FF.toInt())
    fun success(title: String, message: String) = push(title, message, 0xFF00FF88.toInt())
    fun warn(title: String, message: String) = push(title, message, 0xFFFFAA00.toInt())
    fun error(title: String, message: String) = push(title, message, 0xFFFF4444.toInt())

    fun push(title: String, message: String, color: Int, durationMs: Long = 2500L) {
        notifications.add(0, Notification(title, message, color, durationMs = durationMs))
        while (notifications.size > 8) notifications.removeAt(notifications.lastIndex)
    }

    fun getActive(): List<Notification> {
        val now = System.currentTimeMillis()
        notifications.removeIf { now - it.createdAt > it.durationMs }
        return notifications.toList()
    }
}
