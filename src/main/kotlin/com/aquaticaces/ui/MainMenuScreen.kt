package com.aquaticaces.ui

import com.mojang.realmsclient.RealmsMainScreen
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.network.chat.Component
import kotlin.math.sin

/**
 * Full-screen Aquatic Aces main menu — same layout language as the multiplayer screen:
 * edge-to-edge backdrop, top header bar, large action tiles, bottom action strip.
 */
class MainMenuScreen : Screen(Component.literal("Aquatic Aces")) {

    private data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun has(mx: Double, my: Double) = mx >= x && mx <= x + w && my >= y && my <= y + h
    }

    private data class MenuTile(
        val id: String,
        val title: String,
        val subtitle: String,
        val primary: Boolean = false,
        val accent: Int = UiStyle.ACCENT,
        val action: () -> Unit,
    ) {
        var hover = 0f
    }

    private val tiles = mutableListOf<MenuTile>()
    private val tileRects = LinkedHashMap<String, Rect>()
    private val barButtons = LinkedHashMap<String, Rect>()
    private var menuTick = 0f

    private val pad get() = 20f
    private val headerY get() = 56f
    private val footerH get() = 34f
    private val contentH get() = height - headerY - footerH - 20f

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
        tiles.add(MenuTile("singleplayer", "Singleplayer", "Create or play a local world", primary = true) {
            minecraft?.setScreen(CustomSingleplayerScreen(this))
        })
        tiles.add(MenuTile("multiplayer", "Multiplayer", "Browse and join online servers", primary = true, accent = UiStyle.PURPLE) {
            minecraft?.setScreen(CustomMultiplayerScreen(this))
        })
        tiles.add(MenuTile("realms", "Minecraft Realms", "Play on a personal Realm", accent = UiStyle.SUCCESS) {
            minecraft?.setScreen(RealmsMainScreen(this))
        })
        tiles.add(MenuTile("clickgui", "ClickGUI", "Configure modules and settings", accent = UiStyle.ACCENT) {
            minecraft?.setScreen(ClickGUI())
        })
        if (hasModMenu) {
            tiles.add(MenuTile("mods", "Mods", "View installed Fabric mods", accent = 0xFF66AAFF.toInt()) {
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
        tileRects.clear()
        barButtons.clear()

        UiStyle.backdrop(g, width, height, t)

        // ---- Header (matches multiplayer) ----
        UiStyle.logoMark(g, 32, 14, 26)
        g.drawString(font, "AQUATIC ACES", 52, 16, UiStyle.ACCENT, true)
        g.drawString(
            font,
            "Fabric ${SharedConstants.getCurrentVersion().name}  |  v$modVersion  |  60+ modules",
            52, 28, UiStyle.MUTED, true
        )
        g.fill(pad.toInt(), 46, (gw - pad).toInt(), 47, UiStyle.BORDER)

        layoutTiles(gw)
        drawTiles(g, font, mouseX, mouseY)

        // ---- Bottom bar ----
        val barY = gh - footerH - 10f
        val barBtnH = 24f
        val gap = 8f
        val quitW = 72f
        val optW = 80f
        val discordW = 120f
        var bx = pad

        barButtons["options"] = Rect(bx, barY, optW, barBtnH)
        drawBarButton(g, font, "Options", barButtons["options"]!!, mouseX, mouseY)
        bx += optW + gap

        barButtons["discord"] = Rect(bx, barY, discordW, barBtnH)
        drawBarButton(g, font, "Discord", barButtons["discord"]!!, mouseX, mouseY)
        bx = gw - pad - quitW

        barButtons["quit"] = Rect(bx, barY, quitW, barBtnH)
        drawBarButton(g, font, "Quit", barButtons["quit"]!!, mouseX, mouseY, danger = true)

        // live pulse dot in header area
        val pulse = (0.5f + 0.5f * sin(t * 0.12f))
        val dotCol = UiStyle.withAlpha(UiStyle.SUCCESS, (0x88 + pulse * 0x77).toInt())
        g.fill((gw - pad - 8).toInt(), 18, (gw - pad - 4).toInt(), 22, dotCol)
        g.drawString(font, "ONLINE", (gw - pad - font.width("ONLINE") - 14).toInt(), 16, UiStyle.SUCCESS, false)
    }

    private fun layoutTiles(gw: Float) {
        val contentW = gw - pad * 2f
        val gap = 10f
        val y0 = headerY

        val primaryH = (contentH * 0.42f).coerceIn(64f, 96f)
        val secondaryCount = if (hasModMenu) 3 else 2
        val secondaryH = ((contentH - primaryH - gap * 2f) / 2f).coerceIn(44f, 58f)
        val heroH = contentH - primaryH - secondaryH - gap * 2f

        // Hero strip — branding fills remaining top space on wide screens
        if (heroH >= 36f) {
            tileRects["hero"] = Rect(pad, y0, contentW, heroH)
        }

        val row1Y = y0 + if (heroH >= 36f) heroH + gap else 0f
        val halfW = (contentW - gap) / 2f
        tileRects["singleplayer"] = Rect(pad, row1Y, halfW, primaryH)
        tileRects["multiplayer"] = Rect(pad + halfW + gap, row1Y, halfW, primaryH)

        val row2Y = row1Y + primaryH + gap
        val secW = (contentW - gap * (secondaryCount - 1)) / secondaryCount
        var sx = pad
        tileRects["realms"] = Rect(sx, row2Y, secW, secondaryH); sx += secW + gap
        tileRects["clickgui"] = Rect(sx, row2Y, secW, secondaryH); sx += secW + gap
        if (hasModMenu) {
            tileRects["mods"] = Rect(sx, row2Y, secW, secondaryH)
        }
    }

    private fun drawTiles(g: GuiGraphics, font: net.minecraft.client.gui.Font, mx: Int, my: Int) {
        tileRects["hero"]?.let { r ->
            val cx = (r.x + r.w / 2f).toInt()
            UiStyle.logoMark(g, cx, (r.y + 4f).toInt(), 40)
            UiStyle.title(g, font, cx, r.y + 52f, 1.6f)
            g.drawCenteredString(font, "Precision combat. Buttery movement.", cx, (r.y + 76f).toInt(), UiStyle.MUTED)
        }

        for (tile in tiles) {
            val rect = tileRects[tile.id] ?: continue
            val hovered = rect.has(mx.toDouble(), my.toDouble())
            tile.hover += ((if (hovered) 1f else 0f) - tile.hover) * 0.18f
            drawTile(g, font, tile, rect, mx, my)
        }
    }

    private fun drawTile(
        g: GuiGraphics,
        font: net.minecraft.client.gui.Font,
        tile: MenuTile,
        r: Rect,
        mx: Int,
        my: Int,
    ) {
        val x1 = r.x.toInt(); val y1 = r.y.toInt()
        val x2 = (r.x + r.w).toInt(); val y2 = (r.y + r.h).toInt()
        val hover = tile.hover
        val accent = tile.accent

        g.fill(x1, y1, x2, y2, UiStyle.withAlpha(0x10131A, (0xCC + hover * 0x22).toInt().coerceAtMost(0xFF)))
        if (tile.primary || hover > 0.05f) {
            g.fill(x1, y1, x2, y2, UiStyle.withAlpha(accent, ((if (tile.primary) 0x18 else 0) + hover * 0x20).toInt()))
        }
        UiStyle.outline(
            g, x1, y1, x2, y2,
            if (hover > 0.05f) UiStyle.withAlpha(accent, 0xCC) else UiStyle.BORDER
        )
        g.fill(x1, y1, x1 + 3, y2, accent)

        val titleY = if (tile.primary) (r.y + r.h / 2f - 14f).toInt() else (r.y + 12f).toInt()
        g.drawString(font, tile.title, (r.x + 14f).toInt(), titleY,
            if (hover > 0.05f) accent else UiStyle.TEXT, false)
        g.drawString(font, tile.subtitle, (r.x + 14f).toInt(), titleY + 13, UiStyle.MUTED, false)

        if (tile.primary && hover > 0.05f) {
            g.drawString(font, ">", (x2 - 16), titleY + 2, accent, false)
        }
    }

    private fun drawBarButton(
        g: GuiGraphics,
        font: net.minecraft.client.gui.Font,
        label: String,
        r: Rect,
        mx: Int,
        my: Int,
        danger: Boolean = false,
    ) {
        val x1 = r.x.toInt(); val y1 = r.y.toInt()
        val x2 = (r.x + r.w).toInt(); val y2 = (r.y + r.h).toInt()
        val cx = (r.x + r.w / 2f).toInt()
        val cy = (r.y + r.h / 2f - 4f).toInt()
        val hovered = r.has(mx.toDouble(), my.toDouble())
        val accent = if (danger) 0xFFFF4466.toInt() else UiStyle.ACCENT

        if (hovered) g.fill(x1 - 1, y1 - 1, x2 + 1, y2 + 1, UiStyle.withAlpha(accent, 0x44))
        g.fill(x1, y1, x2, y2, if (hovered) UiStyle.withAlpha(0x1A2230, 0xCC) else 0x99131521.toInt())
        UiStyle.outline(g, x1, y1, x2, y2, if (hovered) UiStyle.withAlpha(accent, 0x88) else UiStyle.BORDER)
        g.drawCenteredString(font, label, cx, cy, if (hovered) accent else UiStyle.TEXT)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)

        for (tile in tiles) {
            tileRects[tile.id]?.let { if (it.has(mouseX, mouseY)) { tile.action(); return true } }
        }
        when {
            barButtons["options"]?.has(mouseX, mouseY) == true -> {
                minecraft!!.setScreen(OptionsScreen(this, minecraft!!.options)); return true
            }
            barButtons["discord"]?.has(mouseX, mouseY) == true -> {
                try {
                    net.minecraft.Util.getPlatform().openUri("https://discord.gg/GMDf9vWeuQ")
                } catch (_: Exception) {}
                return true
            }
            barButtons["quit"]?.has(mouseX, mouseY) == true -> {
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
