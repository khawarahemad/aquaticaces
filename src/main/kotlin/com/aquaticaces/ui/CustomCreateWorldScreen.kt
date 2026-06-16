package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.Difficulty
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.level.LevelSettings
import net.minecraft.world.level.WorldDataConfiguration
import net.minecraft.world.level.levelgen.WorldOptions
import net.minecraft.world.level.levelgen.presets.WorldPresets
import org.lwjgl.glfw.GLFW
import java.util.Random

/**
 * Minimal, fully custom "Create New World" screen styled for Aquatic Aces.
 * Collects a name, game mode and seed, then creates the world directly.
 */
class CustomCreateWorldScreen(private val parent: Screen?) : Screen(Component.literal("Create World")) {

    private data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun has(mx: Double, my: Double) = mx >= x && mx <= x + w && my >= y && my <= y + h
    }

    private enum class Mode(val label: String) { SURVIVAL("Survival"), CREATIVE("Creative"), HARDCORE("Hardcore") }

    private lateinit var nameInput: EditBox
    private lateinit var seedInput: EditBox
    private var mode = Mode.SURVIVAL
    private var menuTick = 0f
    private val buttons = LinkedHashMap<String, Rect>()

    private val cardW get() = (width - 80).coerceIn(240, 360)
    private val cardX get() = width / 2 - cardW / 2

    override fun init() {
        super.init()
        nameInput = EditBox(minecraft!!.font, 0, 0, 196, 16, Component.literal("Name")).apply {
            setBordered(false); setHint(Component.literal("New World")); setMaxLength(64); value = "New World"
        }
        seedInput = EditBox(minecraft!!.font, 0, 0, 196, 16, Component.literal("Seed")).apply {
            setBordered(false); setHint(Component.literal("leave blank for random")); setMaxLength(64)
        }
        addRenderableWidget(nameInput)
        addRenderableWidget(seedInput)
        nameInput.isFocused = true
    }

    override fun tick() { menuTick += 1f }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        buttons.clear()
        UiStyle.backdrop(guiGraphics, width, height, menuTick + partialTick)

        val cardTop = (height / 2 - 92).coerceAtLeast(20)
        val cardH = 184
        UiStyle.card(guiGraphics, cardX - 14, cardTop - 14, cardX + cardW + 14, cardTop + cardH)

        val cx = width / 2
        UiStyle.logoMark(guiGraphics, cx, cardTop - 2, 26)
        guiGraphics.drawCenteredString(font, "CREATE WORLD", cx, cardTop + 28, UiStyle.ACCENT)

        // Name field
        var fy = cardTop + 46
        guiGraphics.drawString(font, "World Name", cardX, fy, UiStyle.MUTED, false)
        field(guiGraphics, nameInput, cardX.toFloat(), (fy + 11).toFloat(), cardW.toFloat())

        // Seed field
        fy += 40
        guiGraphics.drawString(font, "Seed", cardX, fy, UiStyle.MUTED, false)
        field(guiGraphics, seedInput, cardX.toFloat(), (fy + 11).toFloat(), cardW.toFloat())

        // Mode cycler
        fy += 40
        guiGraphics.drawString(font, "Mode", cardX, fy, UiStyle.MUTED, false)
        val modeRect = Rect(cardX.toFloat(), (fy + 10).toFloat(), cardW.toFloat(), 20f)
        buttons["mode"] = modeRect
        drawButton(guiGraphics, font, mode.label, modeRect, true, mouseX, mouseY)

        // Bottom buttons
        val byy = (cardTop + cardH - 26).toFloat()
        val halfW = (cardW - 8) / 2f
        val createRect = Rect(cardX.toFloat(), byy, halfW, 22f)
        val cancelRect = Rect(cardX + halfW + 8f, byy, halfW, 22f)
        buttons["create"] = createRect
        buttons["cancel"] = cancelRect
        drawButton(guiGraphics, font, "Create", createRect, nameInput.value.isNotBlank(), mouseX, mouseY, primary = true)
        drawButton(guiGraphics, font, "Cancel", cancelRect, true, mouseX, mouseY)

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    private fun field(g: GuiGraphics, box: EditBox, x: Float, y: Float, w: Float) {
        g.fill(x.toInt(), y.toInt(), (x + w).toInt(), (y + 18f).toInt(), 0x66000000)
        UiStyle.outline(g, x.toInt(), y.toInt(), (x + w).toInt(), (y + 18f).toInt(), UiStyle.BORDER)
        box.setX((x + 5f).toInt())
        box.setY((y + 5f).toInt())
        box.width = (w - 10f).toInt()
        box.visible = true
    }

    private fun drawButton(g: GuiGraphics, font: net.minecraft.client.gui.Font, label: String, r: Rect, enabled: Boolean, mx: Int, my: Int, primary: Boolean = false) {
        val x1 = r.x.toInt(); val y1 = r.y.toInt(); val x2 = (r.x + r.w).toInt(); val y2 = (r.y + r.h).toInt()
        val cx = (r.x + r.w / 2f).toInt(); val cy = (r.y + r.h / 2f - 4f).toInt()
        val hovered = enabled && r.has(mx.toDouble(), my.toDouble())
        if (!enabled) {
            g.fill(x1, y1, x2, y2, 0x55131521)
            UiStyle.outline(g, x1, y1, x2, y2, 0x552A2E3D)
            g.drawCenteredString(font, label, cx, cy, 0xFF585864.toInt())
            return
        }
        if (primary) {
            if (hovered) g.fill(x1 - 2, y1 - 2, x2 + 2, y2 + 2, UiStyle.withAlpha(UiStyle.ACCENT, 0x55))
            g.fillGradient(x1, y1, x2, y2, UiStyle.ACCENT, UiStyle.ACCENT_2)
            g.fill(x1, y1, x2, y1 + 1, 0x44FFFFFF)
            g.drawCenteredString(font, label, cx, cy, 0xFFFFFFFF.toInt())
        } else {
            g.fill(x1, y1, x2, y2, if (hovered) UiStyle.withAlpha(0x1A2230, 0xCC) else 0x99131521.toInt())
            UiStyle.outline(g, x1, y1, x2, y2, if (hovered) UiStyle.withAlpha(UiStyle.ACCENT, 0x88) else UiStyle.BORDER)
            g.fill(x1, y1, x1 + 2, y2, UiStyle.ACCENT)
            g.drawCenteredString(font, label, cx, cy, if (hovered) UiStyle.ACCENT else UiStyle.TEXT)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        when {
            buttons["mode"]?.has(mouseX, mouseY) == true -> {
                mode = Mode.entries[(mode.ordinal + 1) % Mode.entries.size]
                return true
            }
            buttons["create"]?.has(mouseX, mouseY) == true && nameInput.value.isNotBlank() -> { createWorld(); return true }
            buttons["cancel"]?.has(mouseX, mouseY) == true -> { minecraft!!.setScreen(parent); return true }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { minecraft!!.setScreen(parent); return true }
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            val toName = !nameInput.isFocused
            nameInput.isFocused = toName
            seedInput.isFocused = !toName
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun parseSeed(text: String): Long {
        if (text.isBlank()) return Random().nextLong()
        return text.toLongOrNull() ?: text.hashCode().toLong()
    }

    private fun createWorld() {
        val mc = minecraft!!
        val hardcore = mode == Mode.HARDCORE
        val gameType = if (mode == Mode.CREATIVE) GameType.CREATIVE else GameType.SURVIVAL
        val difficulty = if (hardcore) Difficulty.HARD else Difficulty.NORMAL
        val allowCommands = mode == Mode.CREATIVE

        val gameRules = GameRules()
        val settings = LevelSettings(
            nameInput.value.trim(),
            gameType,
            hardcore,
            difficulty,
            allowCommands,
            gameRules,
            WorldDataConfiguration.DEFAULT
        )
        val options = WorldOptions(parseSeed(seedInput.value), true, false)

        mc.createWorldOpenFlows().createFreshLevel(
            nameInput.value.trim(),
            settings,
            options,
            { provider -> WorldPresets.createNormalWorldDimensions(provider) },
            this
        )
    }

    override fun shouldCloseOnEsc(): Boolean = false
}
