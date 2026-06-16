package com.aquaticaces.ui

import com.mojang.realmsclient.RealmsMainScreen
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.network.chat.Component
import com.aquaticaces.ui.components.VectorMenuButton
import kotlin.math.sin

/**
 * Aquatic Aces main menu, styled to mirror the marketing website hero. The
 * layout is computed to fit any GUI scale so buttons and footer never overlap.
 */
class MainMenuScreen : Screen(Component.literal("Aquatic Aces")) {

    private val buttons = mutableListOf<VectorMenuButton>()
    private var menuTick = 0f

    private val primaryButtons = setOf("Singleplayer", "Multiplayer")

    private var cardTopY = 0
    private var cardBotY = 0
    private var headerH = 112

    private val modVersion: String by lazy {
        FabricLoader.getInstance().getModContainer("aquaticaces")
            .map { it.metadata.version.friendlyString }
            .orElse("1.3.0")
    }

    private val hasModMenu: Boolean by lazy {
        FabricLoader.getInstance().isModLoaded("modmenu")
    }

    override fun init() {
        buttons.clear()

        val btnW = 224f
        // rows: SP, MP, Realms, ClickGUI, [Mods], Options/Quit
        val rowCount = if (hasModMenu) 6 else 5
        val footerH = 22

        // fit the layout to the available height
        var btnH = 22f
        var gap = 6f
        val needed = headerH + rowCount * (btnH + gap) + footerH + 16
        if (needed > height) {
            val avail = (height - headerH - footerH - 16f).coerceAtLeast(120f)
            val per = (avail / rowCount).coerceAtLeast(16f)
            btnH = (per - 5f).coerceIn(14f, 22f)
            gap = (per - btnH).coerceIn(3f, 6f)
        }
        val rowH = btnH + gap

        val contentH = (headerH + rowCount * rowH).toInt()
        val cardH = contentH + 14
        cardTopY = ((height - cardH - footerH) / 2).coerceAtLeast(6)
        cardBotY = cardTopY + cardH

        val cx = width / 2f - btnW / 2f
        var y = cardTopY + headerH.toFloat()

        buttons.add(VectorMenuButton("Singleplayer", cx, y, btnW, btnH) {
            minecraft?.setScreen(CustomSingleplayerScreen(this))
        })
        y += rowH

        buttons.add(VectorMenuButton("Multiplayer", cx, y, btnW, btnH) {
            minecraft?.setScreen(CustomMultiplayerScreen(this))
        })
        y += rowH

        buttons.add(VectorMenuButton("Minecraft Realms", cx, y, btnW, btnH) {
            minecraft?.setScreen(RealmsMainScreen(this))
        })
        y += rowH

        buttons.add(VectorMenuButton("ClickGUI", cx, y, btnW, btnH) {
            minecraft?.setScreen(ClickGUI())
        })
        y += rowH

        if (hasModMenu) {
            buttons.add(VectorMenuButton("Mods", cx, y, btnW, btnH) {
                openModsScreen()
            })
            y += rowH
        }

        val halfW = (btnW - 6f) / 2f
        buttons.add(VectorMenuButton("Options", cx, y, halfW, btnH) {
            minecraft?.setScreen(OptionsScreen(this, minecraft!!.options))
        })
        buttons.add(VectorMenuButton("Quit", cx + btnW - halfW, y, halfW, btnH) {
            minecraft?.stop()
        })
    }

    override fun tick() {
        menuTick += 1f
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val mc = minecraft ?: return
        val font = mc.font
        val t = menuTick + partialTick

        UiStyle.backdrop(guiGraphics, width, height, t)

        val cardW = 300
        val cardX = width / 2 - cardW / 2
        UiStyle.card(guiGraphics, cardX, cardTopY, cardX + cardW, cardBotY)

        val centerX = width / 2
        val markY = cardTopY + 14
        UiStyle.logoMark(guiGraphics, centerX, markY, 32)

        val eyebrow = "FABRIC ${SharedConstants.getCurrentVersion().name} CLIENT"
        guiGraphics.drawCenteredString(font, eyebrow, centerX, markY + 38, UiStyle.ACCENT)

        UiStyle.title(guiGraphics, font, centerX, (markY + 48).toFloat(), 1.5f)

        guiGraphics.drawCenteredString(font, "Precision combat. Buttery movement.", centerX, markY + 68, UiStyle.MUTED)

        // live badge
        val badge = "60+ MODULES  ·  v$modVersion"
        val bw = font.width(badge) + 22
        val bx = centerX - bw / 2
        val by = markY + 82
        guiGraphics.fill(bx, by, bx + bw, by + 14, 0x1500FF88)
        UiStyle.outline(guiGraphics, bx, by, bx + bw, by + 14, 0x4400FF88)
        val pulse = (0.5f + 0.5f * sin(t * 0.12f))
        val dotAlpha = (0x66 + pulse * 0x99).toInt() shl 24
        guiGraphics.fill(bx + 7, by + 5, bx + 11, by + 9, dotAlpha or (UiStyle.SUCCESS and 0xFFFFFF))
        guiGraphics.drawString(font, badge, bx + 15, by + 3, UiStyle.SUCCESS, false)

        for (button in buttons) {
            button.tickHovered(button.contains(mouseX.toDouble(), mouseY.toDouble()))
            UiStyle.button(guiGraphics, font, button, primaryButtons.contains(button.label))
        }

        val footerY = (cardBotY + 9).coerceAtMost(height - 11)
        guiGraphics.drawCenteredString(font, "discord.gg/GMDf9vWeuQ", centerX, footerY, UiStyle.DIM)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            for (item in buttons) {
                if (item.contains(mouseX, mouseY)) {
                    item.click()
                    return true
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldCloseOnEsc(): Boolean = false

    private fun openModsScreen() {
        try {
            val screenClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen")
            val screen = screenClass.getConstructor(Screen::class.java).newInstance(this) as Screen
            minecraft?.setScreen(screen)
        } catch (_: Exception) {
        }
    }
}
