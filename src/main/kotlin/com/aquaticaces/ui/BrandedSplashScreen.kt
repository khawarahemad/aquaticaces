package com.aquaticaces.ui

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

/** Post-load branded splash — transitions to main menu in tick() only. */
class BrandedSplashScreen : Screen(Component.empty()) {

    private var ticks = 0
    private var progress = 0f
    private var openedMenu = false

    private val modVersion: String by lazy {
        FabricLoader.getInstance().getModContainer("aquaticaces")
            .map { it.metadata.version.friendlyString }
            .orElse("dev")
    }

    override fun init() {
        super.init()
        ticks = 0
        progress = 0f
        openedMenu = false
    }

    override fun render(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        BrandedUi.drawFakeLoadingSplash(g, minecraft!!.font, width, height, progress, modVersion, 1f)
    }

    override fun tick() {
        ticks++
        progress = (progress + 0.06f).coerceAtMost(1f)
        if (!openedMenu && ticks >= 28 && progress >= 1f) {
            openedMenu = true
            minecraft?.setScreen(MainMenuScreen())
        }
    }

    override fun renderBackground(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}

    override fun shouldCloseOnEsc(): Boolean = false
}
