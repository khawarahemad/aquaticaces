package com.aquaticaces.module.impl.ghost

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import org.lwjgl.glfw.GLFW

/**
 * Hitboxes module.
 * Holds size setting for expanding targeted entity collision boxes (AABBs).
 * Actual bounding box expansion is handled in MixinEntity.
 */
class Hitboxes : Module("Hitboxes", "Expands targeted enemy bounding boxes.", Category.GHOST) {

    val size = NumberSetting("Size", 0.2, 0.1, 0.5, 0.05)

    init {
        addSettings(size)
    }
}
