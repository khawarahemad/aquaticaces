package com.aquaticaces.module.impl.utility

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.client.gui.screens.ConnectScreen
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.resolver.ServerAddress

class AutoReconnect : Module(
    "AutoReconnect",
    "Reconnects after disconnect.",
    Category.UTILITY,
    onlyInGame = false
) {
    val delay = NumberSetting("Delay", 5.0, 1.0, 30.0, 1.0)
    private var lastServer: ServerData? = null
    private var reconnectAt = 0L

    init { addSettings(delay) }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return

        // Remember the last server while connected.
        mc.currentServer?.let { lastServer = ServerData(it.name, it.ip, ServerData.Type.OTHER) }

        if (mc.player != null) {
            reconnectAt = 0L
            return
        }

        // Only reconnect when we were in a multiplayer session.
        val server = lastServer ?: return
        if (reconnectAt == 0L) {
            reconnectAt = System.currentTimeMillis() + (delay.value * 1000).toLong()
        }
        if (System.currentTimeMillis() >= reconnectAt) {
            reconnectAt = 0L
            ConnectScreen.startConnecting(null, mc, ServerAddress.parseString(server.ip), server, false, null)
        }
    }
}
