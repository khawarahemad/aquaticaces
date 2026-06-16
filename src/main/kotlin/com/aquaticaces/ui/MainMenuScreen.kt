package com.aquaticaces.ui

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.network.chat.Component
import kotlin.math.sin

/** Full-screen Aquatic Aces main menu. */
class MainMenuScreen : Screen(Component.literal("Aquatic Aces")) {

    private data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun hit(mx: Double, my: Double) = mx >= x && mx <= x + w && my >= y && my <= y + h
    }

    private data class Tile(
        val id: String,
        val title: String,
        val subtitle: String?,
        val accent: Int = UiStyle.ACCENT,
        val icon: UiStyle.HeroIcon? = null,
        val action: () -> Unit,
    ) {
        var hover = 0f
        var rect = Rect(0f, 0f, 0f, 0f)
    }

    private val tiles = mutableListOf<Tile>()
    private val barRects = LinkedHashMap<String, Rect>()
    private var menuTick = 0f

    private val modVersion: String by lazy {
        FabricLoader.getInstance().getModContainer("aquaticaces")
            .map { it.metadata.version.friendlyString }
            .orElse("dev")
    }

    private val hasModMenu: Boolean by lazy {
        FabricLoader.getInstance().isModLoaded("modmenu")
    }

    override fun init() {
        tiles.clear()
        tiles.add(Tile(
            "singleplayer", "Singleplayer", "Create or play a local world",
            icon = UiStyle.HeroIcon.WORLD
        ) {
            minecraft?.setScreen(CustomSingleplayerScreen(this))
        })
        tiles.add(Tile(
            "multiplayer", "Multiplayer", "Browse and join online servers",
            accent = UiStyle.PURPLE, icon = UiStyle.HeroIcon.SERVERS
        ) {
            minecraft?.setScreen(CustomMultiplayerScreen(this))
        })
        tiles.add(Tile("realms", "Minecraft Realms", "Play on a personal Realm", accent = UiStyle.SUCCESS) {
            minecraft?.setScreen(com.mojang.realmsclient.RealmsMainScreen(this))
        })
        tiles.add(Tile("clickgui", "ClickGUI", "Configure modules and settings") {
            minecraft?.setScreen(ClickGUI())
        })
        if (hasModMenu) {
            tiles.add(Tile("mods", "Mods", "View installed Fabric mods", accent = 0xFF66AAFF.toInt()) {
                openModsScreen()
            })
        }
    }

    override fun tick() {
        menuTick += 1f
    }

    override fun render(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        val gw = width.toFloat()
        val gh = height.toFloat()
        val t = menuTick + partialTick
        barRects.clear()

        UiStyle.backdrop(g, width, height, t)
        drawMainHeader(g, font, gw, t)
        layoutTiles(gw, gh)

        for (tile in tiles) {
            val hovered = tile.rect.hit(mouseX.toDouble(), mouseY.toDouble())
            tile.hover += ((if (hovered) 1f else 0f) - tile.hover) * 0.2f
            val r = tile.rect
            if (tile.icon != null) {
                UiStyle.heroTile(
                    g, font,
                    r.x.toInt(), r.y.toInt(), r.w.toInt(), r.h.toInt(),
                    tile.title, tile.subtitle, tile.accent, tile.hover, tile.icon
                )
            } else {
                UiStyle.menuRow(
                    g, font,
                    r.x.toInt(), r.y.toInt(), r.w.toInt(), r.h.toInt(),
                    tile.title, tile.subtitle, tile.accent, tile.hover
                )
            }
        }

        drawFooter(g, font, gw, gh, mouseX, mouseY)
    }

    private fun drawMainHeader(g: GuiGraphics, font: net.minecraft.client.gui.Font, gw: Float, t: Float) {
        BrandedUi.drawLogo(g, UiStyle.SCREEN_PAD + 6, 12, 28)
        g.drawString(font, "AQUATIC ACES", 56, 14, UiStyle.ACCENT, true)
        g.drawString(
            font,
            "Fabric ${SharedConstants.getCurrentVersion().name}  ·  v$modVersion  ·  60+ modules",
            56, 28, UiStyle.MUTED, false
        )
        g.fill(UiStyle.SCREEN_PAD, UiStyle.HEADER_H, (gw - UiStyle.SCREEN_PAD).toInt(), UiStyle.HEADER_H + 1, UiStyle.BORDER)

        val pulse = 0.5f + 0.5f * sin(t * 0.12f)
        g.fill((gw - UiStyle.SCREEN_PAD - 8).toInt(), 16, (gw - UiStyle.SCREEN_PAD - 4).toInt(), 20,
            UiStyle.withAlpha(UiStyle.SUCCESS, (0x88 + pulse * 0x77).toInt()))
        g.drawString(font, "ONLINE", (gw - UiStyle.SCREEN_PAD - font.width("ONLINE") - 14).toInt(), 14, UiStyle.SUCCESS, false)
    }

    private fun layoutTiles(gw: Float, gh: Float) {
        val pad = UiStyle.SCREEN_PAD.toFloat()
        val contentW = gw - pad * 2f
        val gap = 10f
        var y = UiStyle.HEADER_H + 16f
        val maxBottom = gh - UiStyle.FOOTER_H

        val heroH = 76f
        tile("singleplayer").rect = Rect(pad, y, contentW, heroH)
        y += heroH + gap
        tile("multiplayer").rect = Rect(pad, y, contentW, heroH)
        y += heroH + 14f

        val secondaryCount = if (hasModMenu) 3 else 2
        val secondaryH = 52f
        val secW = (contentW - gap * (secondaryCount - 1)) / secondaryCount
        var sx = pad
        tile("realms").rect = Rect(sx, y, secW, secondaryH); sx += secW + gap
        tile("clickgui").rect = Rect(sx, y, secW, secondaryH); sx += secW + gap
        if (hasModMenu) tile("mods").rect = Rect(sx, y, secW, secondaryH)

        if (y + secondaryH > maxBottom) {
            val scale = (maxBottom - (UiStyle.HEADER_H + 16f) - 14f - secondaryH) / (heroH * 2 + gap)
            if (scale < 1f) {
                val h = (heroH * scale).coerceAtLeast(58f)
                y = UiStyle.HEADER_H + 16f
                tile("singleplayer").rect = Rect(pad, y, contentW, h)
                y += h + gap
                tile("multiplayer").rect = Rect(pad, y, contentW, h)
            }
        }
    }

    private fun tile(id: String) = tiles.first { it.id == id }

    private fun drawFooter(g: GuiGraphics, font: net.minecraft.client.gui.Font, gw: Float, gh: Float, mx: Int, my: Int) {
        UiStyle.footerStrip(g, gw.toInt(), gh.toInt())
        val barY = gh - UiStyle.FOOTER_H + 8f
        val gap = 8f
        val pad = UiStyle.SCREEN_PAD.toFloat()

        barRects["options"] = Rect(pad, barY, 80f, 24f)
        barRects["discord"] = Rect(pad + 80f + gap, barY, 120f, 24f)
        barRects["quit"] = Rect(gw - pad - 72f, barY, 72f, 24f)

        for ((key, r) in barRects) {
            val hovered = r.hit(mx.toDouble(), my.toDouble())
            UiStyle.barButton(g, font, r.x.toInt(), r.y.toInt(), r.w.toInt(), r.h.toInt(), key.replaceFirstChar { it.uppercase() }, hovered, key == "quit")
        }
        g.drawCenteredString(font, "discord.gg/GMDf9vWeuQ", (gw / 2f).toInt(), (gh - 10f).toInt(), UiStyle.DIM)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        for (tile in tiles) {
            if (tile.rect.hit(mouseX, mouseY)) {
                tile.action()
                return true
            }
        }
        when {
            barRects["options"]?.hit(mouseX, mouseY) == true -> {
                minecraft!!.setScreen(OptionsScreen(this, minecraft!!.options)); return true
            }
            barRects["discord"]?.hit(mouseX, mouseY) == true -> {
                try { net.minecraft.Util.getPlatform().openUri("https://discord.gg/GMDf9vWeuQ") } catch (_: Exception) {}
                return true
            }
            barRects["quit"]?.hit(mouseX, mouseY) == true -> {
                minecraft!!.stop(); return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldCloseOnEsc(): Boolean = false

    override fun renderBackground(g: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}

    private fun openModsScreen() {
        try {
            val screenClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen")
            val screen = screenClass.getConstructor(Screen::class.java).newInstance(this) as Screen
            minecraft?.setScreen(screen)
        } catch (_: Exception) {}
    }
}
