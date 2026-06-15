package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis

class ViewModel : Module("ViewModel", "Adjusts hand view position.", Category.RENDER) {
    val scale = NumberSetting("Scale", 1.0, 0.5, 2.0, 0.1)
    val x = NumberSetting("X", 0.0, -2.0, 2.0, 0.1)
    val y = NumberSetting("Y", 0.0, -2.0, 2.0, 0.1)
    val z = NumberSetting("Z", 0.0, -2.0, 2.0, 0.1)
    init { addSettings(scale, x, y, z) }

    companion object {
        @JvmStatic
        fun applyTransform(poseStack: PoseStack) {
            val m = com.aquaticaces.module.ModuleManager.getModuleByName("ViewModel") as? ViewModel ?: return
            if (!m.isEnabled) return
            poseStack.translate(m.x.value, m.y.value, m.z.value)
            val s = m.scale.value.toFloat()
            poseStack.scale(s, s, s)
        }
    }
}
