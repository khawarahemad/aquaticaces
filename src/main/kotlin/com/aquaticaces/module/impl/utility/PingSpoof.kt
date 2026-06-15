package com.aquaticaces.module.impl.utility

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting

class PingSpoof : Module("PingSpoof", "Displays spoofed ping value.", Category.UTILITY) {
    val ping = NumberSetting("Ping", 50.0, 0.0, 1000.0, 10.0)
    init { addSettings(ping) }

    companion object {
        @JvmStatic
        fun displayPing(real: Int): Int {
            val m = com.aquaticaces.module.ModuleManager.getModuleByName("PingSpoof") as? PingSpoof
            return if (m != null && m.isEnabled) m.ping.value.toInt() else real
        }
    }
}
