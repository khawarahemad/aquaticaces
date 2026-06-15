package com.aquaticaces.event

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Annotation to mark methods as event handlers.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val priority: Int = Priority.NORMAL)

/**
 * Priorities for event dispatching. Higher execution values run first.
 */
object Priority {
    const val HIGHEST = 200
    const val HIGH = 100
    const val NORMAL = 0
    const val LOW = -100
    const val LOWEST = -200
}

/**
 * High-performance, reflection-cached, thread-safe Event Bus.
 */
class EventBus {
    
    // Subscriber representation containing the instance, target method, and priority weight.
    private class Subscriber(
        val instance: Any,
        val method: Method,
        val priority: Int
    )

    // Cached metadata structure to map a subscriber class to its subscribed method metadata
    private class MethodData(
        val method: Method,
        val eventType: Class<out Event>,
        val priority: Int
    )

    // Concurrent registries for fast thread-safe runtime operations
    private val registry = ConcurrentHashMap<Class<out Event>, CopyOnWriteArrayList<Subscriber>>()
    private val classCache = ConcurrentHashMap<Class<*>, List<MethodData>>()

    /**
     * Registers all annotated methods of the subscriber instance.
     */
    fun register(subscriber: Any) {
        val subscriberClass = subscriber::class.java
        
        // Retrieve or compute scanned method cache for the class to prevent redundant reflection scanning
        val methodsData = classCache.computeIfAbsent(subscriberClass) { clazz ->
            val list = mutableListOf<MethodData>()
            var currentClass: Class<*>? = clazz
            while (currentClass != null) {
                for (method in currentClass.declaredMethods) {
                    if (method.isAnnotationPresent(Subscribe::class.java)) {
                        val annotation = method.getAnnotation(Subscribe::class.java)
                        if (method.parameterCount == 1 && Event::class.java.isAssignableFrom(method.parameterTypes[0])) {
                            @Suppress("UNCHECKED_CAST")
                            val eventType = method.parameterTypes[0] as Class<out Event>
                            method.isAccessible = true
                            list.add(MethodData(method, eventType, annotation.priority))
                        }
                    }
                }
                currentClass = currentClass.superclass
            }
            list
        }

        // Add scanned methods to active event registers
        for (data in methodsData) {
            val subscribers = registry.computeIfAbsent(data.eventType) { CopyOnWriteArrayList() }
            
            // Check for duplicates before registering
            val exists = subscribers.any { it.instance === subscriber && it.method == data.method }
            if (!exists) {
                val newSubscriber = Subscriber(subscriber, data.method, data.priority)
                subscribers.add(newSubscriber)
                // Sort descending so highest priority subscribers run first
                subscribers.sortByDescending { it.priority }
            }
        }
    }

    /**
     * Unregisters the subscriber instance from all event registrations.
     */
    fun unregister(subscriber: Any) {
        for (subscribers in registry.values) {
            subscribers.removeIf { it.instance === subscriber }
        }
    }

    /**
     * Posts an event to all registered listening methods.
     * Returns true if the event was cancelled by any handler.
     */
    fun post(event: Event): Boolean {
        var currentClass: Class<*>? = event::class.java
        
        // Propagate the event to listeners matching the class and all its parent Event types
        while (currentClass != null && Event::class.java.isAssignableFrom(currentClass)) {
            val subscribers = registry[currentClass]
            if (subscribers != null) {
                for (subscriber in subscribers) {
                    try {
                        subscriber.method.invoke(subscriber.instance, event)
                    } catch (e: Exception) {
                        System.err.println("EventBus error: failed invoking ${subscriber.method.name} on ${subscriber.instance::class.java.simpleName}")
                        e.printStackTrace()
                    }
                    if (event.isCancelled) {
                        return true
                    }
                }
            }
            currentClass = currentClass.superclass
        }
        return event.isCancelled
    }

    /**
     * Clears all registered subscribers and class caches.
     */
    fun clear() {
        registry.clear()
        classCache.clear()
    }
}
