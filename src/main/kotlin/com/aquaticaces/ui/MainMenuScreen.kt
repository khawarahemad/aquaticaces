package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

/**
 * MainMenuScreen.
 * Sleek, futuristic main menu replacement utilizing custom Vector graphics
 * and responsive animations.
 */
class MainMenuScreen : Screen(Component.literal("MainMenu")) {

    private class MenuButton(val label: String, val onClick: () -> Unit) {
        var hoverProgress = 0f
    }

    private val buttons = mutableListOf<MenuButton>()
    private var fadeProgress = 0.0f

    init {
        buttons.add(MenuButton("Singleplayer") {
            MinecraftClientHolder.mc.setScreen(SelectWorldScreen(this))
        })
        buttons.add(MenuButton("Multiplayer") {
            MinecraftClientHolder.mc.setScreen(JoinMultiplayerScreen(this))
        })
        buttons.add(MenuButton("Options") {
            MinecraftClientHolder.mc.setScreen(OptionsScreen(this, MinecraftClientHolder.mc.options))
        })
        buttons.add(MenuButton("Exit Game") {
            MinecraftClientHolder.mc.close()
        })
    }

    override fun init() {
        super.init()
        fadeProgress = 0f
    }

    override fun tick() {
        fadeProgress = (fadeProgress + 0.05f).coerceAtMost(1.0f)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val vectorRenderer = ClickGUI.vectorRenderer
        val fontRenderer = ClickGUI.fontRenderer

        val scale = minecraft!!.window.guiScale.toFloat()
        val guiWidth = minecraft!!.window.guiScaledWidth.toFloat()
        val guiHeight = minecraft!!.window.guiScaledHeight.toFloat()

        // Begin frame
        vectorRenderer.begin(guiWidth, guiHeight, scale)

        // 1. Draw premium deep indigo/violet gradient background
        val leftColor = 0xFF080A14.toInt()
        val rightColor = 0xFF140D26.toInt()
        vectorRenderer.drawLinearGradientRect(
            0f, 0f, guiWidth, guiHeight, 0f,
            0f, 0f, guiWidth, guiHeight,
            leftColor, rightColor
        )

        // 2. Draw animated Title Logo
        val titleText = "AQUATIC ACES"
        val titleSize = 38f
        val titleW = fontRenderer.getStringWidth("outfit", titleText, titleSize)
        val titleX = guiWidth / 2f - titleW / 2f
        val titleY = guiHeight / 4f - 20f

        // Subtle gradient shift glow on title
        val time = System.currentTimeMillis()
        val hue = ((time / 20) % 360) / 360f
        val titleColor = java.awt.Color.getHSBColor(hue, 0.6f, 1.0f).rgb

        // Outline glow dropshadow
        val shadowAlpha = (fadeProgress * 0x77).toInt()
        vectorRenderer.drawDropShadow(titleX, titleY, titleW, titleSize, 12f, 24f, (shadowAlpha shl 24) or (titleColor and 0xFFFFFF))
        
        fontRenderer.drawString(
            "outfit",
            titleText,
            titleX,
            titleY,
            titleSize,
            (0xFF shl 24) or (titleColor and 0xFFFFFF)
        )

        // 3. Render buttons list
        val btnW = 160f
        val btnH = 26f
        val startY = guiHeight / 2f - 40f
        val btnX = guiWidth / 2f - btnW / 2f

        buttons.forEachIndexed { index, button ->
            val y = startY + index * (btnH + 10f)

            // Update button hover state animations
            val isHovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= y && mouseY <= y + btnH
            val targetHover = if (isHovered) 1.0f else 0.0f
            button.hoverProgress += (targetHover - button.hoverProgress) * 0.2f

            // Compute background and border transition layouts
            val hoverFactor = button.hoverProgress
            val bgAlpha = ((0x2A + (0x1F * hoverFactor)).toInt() * fadeProgress).toInt()
            val buttonBg = (bgAlpha shl 24) or 0x131521

            val borderAlpha = ((0x3C + (0x7F * hoverFactor)).toInt() * fadeProgress).toInt()
            val buttonBorder = (borderAlpha shl 24) or 0x00C6FF

            // Draw vector card
            vectorRenderer.drawRoundedRect(btnX, y, btnW, btnH, 4f, buttonBg)
            vectorRenderer.drawMultiPassOutline(btnX, y, btnW, btnH, 4f, 1.0f, buttonBorder, (0x10 shl 24) or 0x00C6FF)

            // Draw label centering text
            val textW = fontRenderer.getStringWidth("outfit", button.label, 12f)
            val labelColor = ((0xBB + (0x44 * hoverFactor)).toInt() shl 24) or 0xFFFFFF
            fontRenderer.drawString(
                "outfit",
                button.label,
                btnX + btnW / 2f - textW / 2f,
                y + btnH / 2f - 6f,
                12f,
                labelColor
            )
        }

        vectorRenderer.end()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val guiWidth = minecraft!!.window.guiScaledWidth.toFloat()
        val guiHeight = minecraft!!.window.guiScaledHeight.toFloat()
        
        val btnW = 160f
        val btnH = 26f
        val startY = guiHeight / 2f - 40f
        val btnX = guiWidth / 2f - btnW / 2f

        buttons.forEachIndexed { index, item ->
            val y = startY + index * (btnH + 10f)
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= y && mouseY <= y + btnH) {
                item.onClick()
                return true
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldCloseOnEsc(): Boolean = false
}

// Global class helper to safely retrieve Minecraft client across compilations
object MinecraftClientHolder {
    val mc: net.minecraft.client.Minecraft get() = net.minecraft.client.Minecraft.getInstance()
}
