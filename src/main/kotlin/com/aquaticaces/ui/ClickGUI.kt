package com.aquaticaces.ui

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.module.impl.render.ClickGUIModule
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.ModeSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * ClickGUI styled to match the Aquatic Aces website module browser:
 * centered glass card, category sidebar with counts, per-category accent
 * colors, module chips and an inline settings panel. Rendered entirely with
 * Minecraft's GuiGraphics for reliability across systems.
 */
class ClickGUI : Screen(Component.literal("ClickGUI")) {

    companion object {
        val vectorRenderer get() = UiEngine.vectorRenderer
        val fontRenderer get() = UiEngine.fontRenderer
        val shaderManager get() = UiEngine.shaderManager
        var searchQuery: String
            get() = UiEngine.searchQuery
            set(value) { UiEngine.searchQuery = value }

        @JvmStatic
        fun ensureInitialized(): Boolean = UiEngine.ensureInitialized()

        // --- Website palette (ARGB) ---
        private const val BG_TOP = 0xFF06070B.toInt()
        private const val BG_BOT = 0xFF0D0E14.toInt()
        private const val CARD_TOP = 0xFF15171F.toInt()
        private const val CARD_BOT = 0xFF0B0C12.toInt()
        private const val BORDER = 0xFF2A2E3D.toInt()
        private const val SIDEBAR_BG = 0x40000000
        private const val ACCENT = 0xFF00C6FF.toInt()
        private const val ACCENT_2 = 0xFF0072FF.toInt()
        private const val TEXT = 0xFFF0F0F8.toInt()
        private const val MUTED = 0xFF7A7A94.toInt()
        private const val DIM = 0xFF55556A.toInt()
        private const val SUCCESS = 0xFF00FF88.toInt()

        fun catColor(c: Category): Int = when (c) {
            Category.COMBAT -> 0xFFFF4466.toInt()
            Category.MOVEMENT -> 0xFF00C6FF.toInt()
            Category.RENDER -> 0xFFA97BFF.toInt()
            Category.WORLD -> 0xFF00FF88.toInt()
            Category.EXPLOIT -> 0xFFFFAA00.toInt()
            Category.PLAYER -> 0xFFFF88CC.toInt()
            Category.UTILITY -> 0xFF66AAFF.toInt()
            Category.GHOST -> 0xFF888899.toInt()
        }

        private fun withAlpha(color: Int, alpha: Int): Int =
            ((alpha and 0xFF) shl 24) or (color and 0xFFFFFF)
    }

    private data class ChipRect(val module: Module, val x: Int, val y: Int, val w: Int, val h: Int)
    private data class CatRect(val category: Category, val x: Int, val y: Int, val w: Int, val h: Int)
    private data class ToggleRect(val setting: BooleanSetting, val x: Int, val y: Int, val w: Int, val h: Int)
    private data class ModeRect(val setting: ModeSetting, val x: Int, val y: Int, val w: Int, val h: Int)
    private data class SliderRect(val setting: NumberSetting, val x: Int, val y: Int, val w: Int, val h: Int)

    private var activeCategory: Category = Category.COMBAT
    private var selected: Module? = null
    private var gridScroll = 0f
    private var maxGridScroll = 0f
    private var draggingSlider: NumberSetting? = null
    private var draggingBarX = 0
    private var draggingBarW = 0

    private val chipRects = mutableListOf<ChipRect>()
    private val catRects = mutableListOf<CatRect>()
    private val toggleRects = mutableListOf<ToggleRect>()
    private val modeRects = mutableListOf<ModeRect>()
    private val sliderRects = mutableListOf<SliderRect>()
    private var enableX = 0
    private var enableY = 0
    private var enableW = 0
    private var enableH = 0

    private lateinit var searchBox: EditBox

    // Card geometry
    private var cardX = 0
    private var cardY = 0
    private var cardW = 0
    private var cardH = 0
    private val topBarH = 50
    private val sidebarW = 128

    override fun init() {
        super.init()
        cardW = (width - 60).coerceAtMost(680)
        cardH = (height - 60).coerceAtMost(440)
        cardX = (width - cardW) / 2
        cardY = (height - cardH) / 2

        val boxW = 158
        val boxX = cardX + cardW - boxW - 14
        val boxY = cardY + 17
        searchBox = EditBox(minecraft!!.font, boxX, boxY, boxW, 18, Component.literal("Search"))
        searchBox.setBordered(false)
        searchBox.setHint(Component.literal("Filter modules..."))
        searchBox.setResponder {
            searchQuery = it.lowercase()
            gridScroll = 0f
        }
        searchBox.value = searchQuery
        addRenderableWidget(searchBox)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        drawScene(guiGraphics, mouseX, mouseY)
        // search box background (drawn under the EditBox widget)
        guiGraphics.fill(searchBox.x - 8, searchBox.y - 6, searchBox.x + searchBox.width + 8, searchBox.y + 18, 0x66000000)
        guiGraphics.fill(searchBox.x - 8, searchBox.y + 12, searchBox.x + searchBox.width + 8, searchBox.y + 13, withAlpha(ACCENT, 0x55))
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    private fun drawScene(g: GuiGraphics, mouseX: Int, mouseY: Int) {
        val font = minecraft!!.font

        // Backdrop — crisp dark gradient + grid, no haze
        g.fillGradient(0, 0, width, height, BG_TOP, BG_BOT)
        UiStyle.grid(g, width, height)
        g.fillGradient(0, 0, width, height / 6, 0x66000000, 0x00000000)
        g.fillGradient(0, height * 5 / 6, width, height, 0x00000000, 0x66000000)

        // Card
        roundShadow(g, cardX, cardY, cardX + cardW, cardY + cardH)
        g.fillGradient(cardX, cardY, cardX + cardW, cardY + cardH, CARD_TOP, CARD_BOT)
        outline(g, cardX, cardY, cardX + cardW, cardY + cardH, BORDER)
        // top accent hairline
        g.fillGradient(cardX, cardY, cardX + cardW, cardY + 1, ACCENT, ACCENT_2)

        // ---- Top bar ----
        // logo mark
        val markX = cardX + 16
        val markY = cardY + 14
        g.fillGradient(markX, markY, markX + 22, markY + 22, ACCENT, ACCENT_2)
        g.drawString(font, "A", markX + 8, markY + 7, 0xFF02030A.toInt(), false)
        g.drawString(font, "AQUATIC ACES", markX + 32, markY + 3, ACCENT, false)
        g.drawString(font, "Module configuration", markX + 32, markY + 14, MUTED, false)
        g.fill(cardX, cardY + topBarH, cardX + cardW, cardY + topBarH + 1, BORDER)

        // ---- Sidebar ----
        g.fill(cardX, cardY + topBarH + 1, cardX + sidebarW, cardY + cardH, SIDEBAR_BG)
        g.fill(cardX + sidebarW, cardY + topBarH + 1, cardX + sidebarW + 1, cardY + cardH, BORDER)
        catRects.clear()
        val cats = Category.values()
        val catTop = cardY + topBarH + 10
        val catRowH = ((cardH - topBarH - 20) / cats.size).coerceAtMost(36)
        cats.forEachIndexed { i, cat ->
            val ry = catTop + i * catRowH
            val rx = cardX + 8
            val rw = sidebarW - 16
            val rh = catRowH - 5
            val active = cat == activeCategory
            val hovered = mouseX in rx..(rx + rw) && mouseY in ry..(ry + rh)
            val col = catColor(cat)
            if (active) {
                g.fill(rx, ry, rx + rw, ry + rh, withAlpha(col, 0x22))
                outline(g, rx, ry, rx + rw, ry + rh, withAlpha(col, 0x66))
                g.fill(rx, ry, rx + 3, ry + rh, col)
            } else if (hovered) {
                g.fill(rx, ry, rx + rw, ry + rh, 0x14FFFFFF)
            }
            val count = ModuleManager.modules.count { it.category == cat }
            val nameCol = if (active) TEXT else if (hovered) TEXT else MUTED
            g.drawString(font, prettyCat(cat), rx + 10, ry + (rh - 8) / 2, nameCol, false)
            // count badge
            val badge = count.toString()
            val bw = font.width(badge) + 8
            val bx = rx + rw - bw - 6
            val by = ry + (rh - 11) / 2
            g.fill(bx, by, bx + bw, by + 11, if (active) withAlpha(col, 0x33) else 0x18FFFFFF)
            g.drawString(font, badge, bx + 4, by + 2, if (active) col else DIM, false)
            catRects.add(CatRect(cat, rx, ry, rw, rh))
        }

        // ---- Main area ----
        val mainX = cardX + sidebarW + 14
        val mainRight = cardX + cardW - 14
        val mainW = mainRight - mainX
        val headerY = cardY + topBarH + 12
        val catCol = catColor(activeCategory)
        g.drawString(font, prettyCat(activeCategory), mainX, headerY, catCol, false)
        val modulesAll = ModuleManager.modules.filter { it.category == activeCategory }
        val q = searchQuery
        val modules = if (q.isBlank()) modulesAll else modulesAll.filter { it.name.lowercase().contains(q) }
        g.drawString(font, "${modules.size} modules  ·  left-click toggle  ·  right-click settings", mainX, headerY + 12, DIM, false)

        // settings panel reserve
        val sel = selected
        val detailH = if (sel != null) 120 else 0
        val gridTop = headerY + 28
        val gridBottom = cardY + cardH - 12 - (if (detailH > 0) detailH + 8 else 0)

        // ---- Module chips (scissored, scrollable) ----
        chipRects.clear()
        g.enableScissor(mainX, gridTop, mainRight, gridBottom)
        var cx = mainX
        var cy = gridTop - gridScroll.toInt()
        val chipH = 18
        val gapX = 6
        val gapY = 6
        var rowMaxBottom = cy
        for (module in modules) {
            val label = module.name
            val cw = font.width(label) + 22
            if (cx + cw > mainRight) {
                cx = mainX
                cy += chipH + gapY
            }
            val enabled = module.isEnabled
            val isSel = module == sel
            val hovered = mouseX in cx..(cx + cw) && mouseY in cy..(cy + chipH) && mouseY in gridTop..gridBottom
            val bg = when {
                enabled -> withAlpha(catCol, 0xCC)
                isSel -> withAlpha(catCol, 0x33)
                hovered -> withAlpha(catCol, 0x1A)
                else -> 0x59000000
            }
            val bdr = when {
                enabled -> withAlpha(catCol, 0xFF)
                isSel -> withAlpha(catCol, 0x88)
                else -> withAlpha(catCol, 0x33)
            }
            g.fill(cx, cy, cx + cw, cy + chipH, bg)
            outline(g, cx, cy, cx + cw, cy + chipH, bdr)
            // status dot
            val dotCol = if (enabled) 0xFF02030A.toInt() else catCol
            g.fill(cx + 7, cy + chipH / 2 - 2, cx + 11, cy + chipH / 2 + 2, dotCol)
            val txtCol = if (enabled) 0xFF02030A.toInt() else TEXT
            g.drawString(font, label, cx + 16, cy + (chipH - 8) / 2, txtCol, false)
            chipRects.add(ChipRect(module, cx, cy, cw, chipH))
            cx += cw + gapX
            rowMaxBottom = cy + chipH
        }
        if (modules.isEmpty()) {
            g.drawString(font, "No modules match your filter", mainX, gridTop + 6, DIM, false)
        }
        g.disableScissor()

        // scroll bounds
        val contentBottom = rowMaxBottom + gridScroll.toInt()
        val visible = gridBottom - gridTop
        maxGridScroll = max(0f, (contentBottom - gridTop - visible).toFloat() + 4f)
        gridScroll = gridScroll.coerceIn(0f, maxGridScroll)
        if (maxGridScroll > 0f) {
            val trackX = mainRight - 2
            val sbH = (visible * visible / (visible + maxGridScroll)).toInt().coerceAtLeast(20)
            val sbY = gridTop + ((visible - sbH) * (gridScroll / maxGridScroll)).toInt()
            g.fill(trackX, gridTop, trackX + 2, gridBottom, 0x22FFFFFF)
            g.fill(trackX, sbY, trackX + 2, sbY + sbH, withAlpha(catCol, 0xAA))
        }

        // ---- Settings detail panel ----
        toggleRects.clear()
        modeRects.clear()
        sliderRects.clear()
        if (sel != null) {
            val dx = mainX
            val dy = gridBottom + 8
            val dw = mainRight - mainX
            val dh = detailH
            g.fillGradient(dx, dy, dx + dw, dy + dh, withAlpha(catCol, 0x14), 0x66000000)
            outline(g, dx, dy, dx + dw, dy + dh, withAlpha(catCol, 0x44))
            g.fill(dx, dy, dx + dw, dy + 1, withAlpha(catCol, 0xAA))

            g.drawString(font, sel.name, dx + 10, dy + 8, catCol, false)
            val desc = font.plainSubstrByWidth(sel.description, dw - 90)
            g.drawString(font, desc, dx + 10, dy + 20, MUTED, false)

            // enable toggle pill (top-right)
            enableW = 52
            enableH = 16
            enableX = dx + dw - enableW - 10
            enableY = dy + 8
            val on = sel.isEnabled
            g.fill(enableX, enableY, enableX + enableW, enableY + enableH, if (on) withAlpha(SUCCESS, 0xCC) else 0x44000000)
            outline(g, enableX, enableY, enableX + enableW, enableY + enableH, if (on) SUCCESS else BORDER)
            val lbl = if (on) "ENABLED" else "OFF"
            g.drawString(font, lbl, enableX + (enableW - font.width(lbl)) / 2, enableY + 4, if (on) 0xFF02030A.toInt() else MUTED, false)

            // settings rows (scissored)
            val sy0 = dy + 36
            g.enableScissor(dx, sy0, dx + dw, dy + dh)
            var sy = sy0
            val rowH = 16
            for (setting in sel.settings) {
                if (!setting.isVisible()) continue
                if (sy + rowH > dy + dh) break
                when (setting) {
                    is BooleanSetting -> {
                        g.drawString(font, setting.name, dx + 12, sy + 3, TEXT, false)
                        val pw = 34
                        val px = dx + dw - pw - 12
                        val v = setting.value
                        g.fill(px, sy, px + pw, sy + 13, if (v) withAlpha(catCol, 0xCC) else 0x33000000)
                        outline(g, px, sy, px + pw, sy + 13, if (v) catCol else BORDER)
                        val t = if (v) "ON" else "OFF"
                        g.drawString(font, t, px + (pw - font.width(t)) / 2, sy + 3, if (v) 0xFF02030A.toInt() else MUTED, false)
                        toggleRects.add(ToggleRect(setting, px, sy, pw, 13))
                        sy += rowH
                    }
                    is ModeSetting -> {
                        g.drawString(font, setting.name, dx + 12, sy + 3, TEXT, false)
                        val text = setting.value
                        val pw = font.width(text) + 16
                        val px = dx + dw - pw - 12
                        g.fill(px, sy, px + pw, sy + 13, withAlpha(catCol, 0x22))
                        outline(g, px, sy, px + pw, sy + 13, withAlpha(catCol, 0x66))
                        g.drawString(font, text, px + 8, sy + 3, catCol, false)
                        modeRects.add(ModeRect(setting, px, sy, pw, 13))
                        sy += rowH
                    }
                    is NumberSetting -> {
                        val valTxt = formatNum(setting.value)
                        g.drawString(font, setting.name, dx + 12, sy + 1, TEXT, false)
                        g.drawString(font, valTxt, dx + dw - font.width(valTxt) - 12, sy + 1, catCol, false)
                        val barX = dx + 12
                        val barW = dw - 24
                        val barY = sy + 11
                        g.fill(barX, barY, barX + barW, barY + 3, 0x44000000)
                        val ratio = ((setting.value - setting.min) / (setting.max - setting.min)).toFloat().coerceIn(0f, 1f)
                        val fillW = (barW * ratio).toInt()
                        g.fillGradient(barX, barY, barX + fillW, barY + 3, ACCENT, ACCENT_2)
                        g.fill(barX + fillW - 1, barY - 2, barX + fillW + 2, barY + 5, TEXT)
                        sliderRects.add(SliderRect(setting, barX, barY - 3, barW, 9))
                        sy += rowH + 6
                    }
                    is ColorSetting -> {
                        g.drawString(font, setting.name, dx + 12, sy + 3, TEXT, false)
                        val sw = 30
                        val px = dx + dw - sw - 12
                        g.fill(px, sy, px + sw, sy + 13, setting.value)
                        outline(g, px, sy, px + sw, sy + 13, BORDER)
                        sy += rowH
                    }
                    else -> {
                        g.drawString(font, setting.name, dx + 12, sy + 3, MUTED, false)
                        sy += rowH
                    }
                }
            }
            if (sel.settings.none { it.isVisible() }) {
                g.drawString(font, "No configurable settings.", dx + 12, sy0 + 2, DIM, false)
            }
            g.disableScissor()
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mx = mouseX.toInt()
        val my = mouseY.toInt()

        // enable toggle
        val sel = selected
        if (sel != null && mx in enableX..(enableX + enableW) && my in enableY..(enableY + enableH)) {
            sel.toggle()
            return true
        }
        // boolean settings
        for (r in toggleRects) {
            if (mx in r.x..(r.x + r.w) && my in r.y..(r.y + r.h)) {
                r.setting.value = !r.setting.value
                return true
            }
        }
        // mode settings
        for (r in modeRects) {
            if (mx in r.x..(r.x + r.w) && my in r.y..(r.y + r.h)) {
                if (button == 1) {
                    // reverse cycle
                    val idx = r.setting.modes.indexOf(r.setting.value)
                    val prev = (idx - 1 + r.setting.modes.size) % r.setting.modes.size
                    r.setting.value = r.setting.modes[prev]
                } else r.setting.cycle()
                return true
            }
        }
        // sliders
        for (r in sliderRects) {
            if (mx in r.x..(r.x + r.w) && my in r.y..(r.y + r.h)) {
                draggingSlider = r.setting
                draggingBarX = r.x
                draggingBarW = r.w
                applySlider(r.setting, mx)
                return true
            }
        }
        // chips
        for (r in chipRects) {
            if (mx in r.x..(r.x + r.w) && my in r.y..(r.y + r.h)) {
                if (button == 1) {
                    selected = if (selected == r.module) null else r.module
                } else {
                    r.module.toggle()
                }
                return true
            }
        }
        // categories
        for (r in catRects) {
            if (mx in r.x..(r.x + r.w) && my in r.y..(r.y + r.h)) {
                if (r.category != activeCategory) {
                    activeCategory = r.category
                    selected = null
                    gridScroll = 0f
                }
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dx: Double, dy: Double): Boolean {
        val slider = draggingSlider
        if (slider != null) {
            applySlider(slider, mouseX.toInt())
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        draggingSlider = null
        return super.mouseReleased(mouseX, mouseY, button)
    }

    private fun applySlider(setting: NumberSetting, mx: Int) {
        val ratio = ((mx - draggingBarX).toFloat() / draggingBarW.coerceAtLeast(1)).coerceIn(0f, 1f)
        val raw = setting.min + ratio * (setting.max - setting.min)
        val stepped = (raw / setting.increment).roundToLong() * setting.increment
        setting.value = stepped.coerceIn(setting.min, setting.max)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        gridScroll = (gridScroll - scrollY.toFloat() * 18f).coerceIn(0f, maxGridScroll)
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (searchBox.isFocused && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers)
        }
        val clickGui = ModuleManager.getModuleByName("ClickGUI") as? ClickGUIModule
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == clickGui?.keybind) {
            minecraft?.setScreen(null)
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun shouldCloseOnEsc(): Boolean = false

    // --- drawing helpers ---
    private fun outline(g: GuiGraphics, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        g.fill(x1, y1, x2, y1 + 1, color)
        g.fill(x1, y2 - 1, x2, y2, color)
        g.fill(x1, y1, x1 + 1, y2, color)
        g.fill(x2 - 1, y1, x2, y2, color)
    }

    private fun roundShadow(g: GuiGraphics, x1: Int, y1: Int, x2: Int, y2: Int) {
        g.fill(x1 - 3, y1 - 3, x2 + 3, y2 + 3, 0x33000000)
        g.fill(x1 - 6, y1 - 6, x2 + 6, y2 + 6, 0x18000000)
    }

    private fun prettyCat(c: Category): String =
        c.name.substring(0, 1) + c.name.substring(1).lowercase()

    private fun formatNum(v: Double): String =
        if (v == v.toLong().toDouble()) v.toLong().toString() else String.format("%.2f", v)
}
