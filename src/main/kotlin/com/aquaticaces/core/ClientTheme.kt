package com.aquaticaces.core

object ClientTheme {
    var accentLeft = 0xFF00C6FF.toInt()
    var accentRight = 0xFF0072FF.toInt()
    var panelBg = 0xEE1A1C23.toInt()
    var blurStrength = 9.0f

    fun syncFromClickGUI() {
        val module = com.aquaticaces.module.ModuleManager.getModuleByName("ClickGUI") as? com.aquaticaces.module.impl.render.ClickGUIModule
            ?: return
        module.syncToTheme()
    }
}
