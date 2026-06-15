package com.aquaticaces.ui.components

import com.aquaticaces.ui.VectorRenderer
import com.aquaticaces.ui.FontRenderer

/**
 * Base abstract class for reactive vector UI components.
 */
abstract class UIComponent(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) {
    /**
     * Renders the component onto the screen frame.
     */
    abstract fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float)

    /**
     * Triggers when a mouse button is pressed. Returns true if the event was consumed.
     */
    abstract fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean

    /**
     * Triggers when a mouse button is released.
     */
    abstract fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean

    /**
     * Triggers when a keyboard character key is pressed.
     */
    abstract fun keyTyped(keyCode: Int): Boolean
}
