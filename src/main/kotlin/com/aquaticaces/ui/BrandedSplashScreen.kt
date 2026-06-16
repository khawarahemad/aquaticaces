package com.aquaticaces.ui

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

/**
 * Short branded splash shown after resources finish loading.
 * Vanilla loading logic already completed — this is purely cosmetic.
 */
class BrandedSplashScreen : Screen(Component.empty()) {

    private var ticks = 0
    private var progress = 0f

    private val modVersion: String by lazy {
        FabricLoader.getInstance().getModContainer("aquaticaces")
            .map { it.metadata.version.friendlyString }
            .orElse("dev")
    }

    override fun render(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        ticks++
        progress = (progress + 0.06f).coerceAtMost(1f)
        BrandedUi.drawFakeLoadingSplash(g, minecraft!!.font, width, height, progress, modVersion, 1f)
        if (ticks >= 28 && progress >= 1f) {
            minecraft!!.setScreen(MainMenuScreen())
        }
    }

    override fun tick() {
        if (ticks >= 28 && progress >= 1f) {
            minecraft?.setScreen(MainMenuScreen())
        }
    }

    override fun shouldCloseOnEsc(): Boolean = false
}
