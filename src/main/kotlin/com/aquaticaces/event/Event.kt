package com.aquaticaces.event

/**
 * Base class for all events in the Aquatic Aces client.
 */
abstract class Event {
    var isCancelled: Boolean = false
        protected set

    /**
     * Cancels the event, preventing further propagation and blocking vanilla execution where supported.
     */
    fun cancel() {
        if (isCancellable()) {
            isCancelled = true
        }
    }

    /**
     * Specifies whether this event type is allowed to be cancelled.
     */
    open fun isCancellable(): Boolean = true
}
