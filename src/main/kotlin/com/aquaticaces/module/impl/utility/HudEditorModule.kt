package com.aquaticaces.module.impl.utility

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.ui.HudEditorScreen

class HudEditorModule : Module("HUD Editor", "HUD toggle and position editor.", Category.UTILITY) {
    override fun onEnable() {
        super.onEnable()
        mc.execute { mc.setScreen(com.aquaticaces.ui.HudToggleScreen(mc.screen)) }
        isEnabled = false
    }
}
