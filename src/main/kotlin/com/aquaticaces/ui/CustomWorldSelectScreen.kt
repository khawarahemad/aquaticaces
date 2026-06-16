package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.level.storage.LevelSummary
import org.lwjgl.glfw.GLFW
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Fully custom singleplayer world list, styled to match Aquatic Aces.
 * Supports play, create, delete and scrolling.
 */
class CustomWorldSelectScreen(private val parent: Screen?) : Screen(Component.literal("Select World")) {

    private data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun has(mx: Double, my: Double) = mx >= x && mx <= x + w && my >= y && my <= y + h
    }

    private class WorldEntry(val summary: LevelSummary) { var hover = 0f }

    private val entries = mutableListOf<WorldEntry>()
    private var loading = true
    private var loadError: String? = null
    private var selectedIndex = -1
    private var pendingDelete = -1
    private var menuTick = 0f
    private var scrollY = 0f
    private var maxScroll = 0f

    private val buttons = LinkedHashMap<String, Rect>()
    private val rowRects = mutableListOf<Rect>()
    private val dateFmt = SimpleDateFormat("MMM d, yyyy")

    private val rowH = 40f
    private val rowGap = 6f
    private val listX get() = 24f
    private val listY get() = 58f
    private val listW get() = width - 48f
    private val listH get() = height - 116f

    override fun init() {
        super.init()
        reload()
    }

    private fun reload() {
        loading = true
        loadError = null
        entries.clear()
        try {
            val source = minecraft!!.levelSource
            val candidates = source.findLevelCandidates()
            source.loadLevelSummaries(candidates).thenAccept { list ->
                minecraft!!.execute {
                    entries.clear()
                    list.sorted().forEach { entries.add(WorldEntry(it)) }
                    loading = false
                }
            }.exceptionally { e ->
                minecraft!!.execute { loading = false; loadError = e.message ?: "Failed to load worlds" }
                null
            }
        } catch (e: Exception) {
            loading = false
            loadError = e.message ?: "Failed to load worlds"
        }
    }

    override fun tick() { menuTick += 1f }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        val gw = width.toFloat(); val gh = height.toFloat()
        buttons.clear(); rowRects.clear()

        UiStyle.backdrop(guiGraphics, width, height, menuTick + partialTick)

        UiStyle.logoMark(guiGraphics, 32, 14, 26)
        guiGraphics.drawString(font, "SELECT WORLD", 52, 16, UiStyle.ACCENT, true)
        guiGraphics.drawString(font, if (loading) "Loading worlds…" else "${entries.size} worlds  ·  select one to play", 52, 28, UiStyle.MUTED, true)
        guiGraphics.fill(20, 46, width - 20, 47, UiStyle.BORDER)

        val contentH = entries.size * (rowH + rowGap)
        maxScroll = (contentH - listH).coerceAtLeast(0f)
        scrollY = scrollY.coerceIn(-maxScroll, 0f)

        if (loading) {
            guiGraphics.drawCenteredString(font, "Loading…", width / 2, (listY + listH / 2).toInt(), UiStyle.MUTED)
        } else if (loadError != null) {
            guiGraphics.drawCenteredString(font, loadError!!, width / 2, (listY + listH / 2).toInt(), 0xFFFF4466.toInt())
        } else if (entries.isEmpty()) {
            guiGraphics.drawCenteredString(font, "No worlds yet", width / 2, (listY + listH / 2 - 10).toInt(), UiStyle.TEXT)
            guiGraphics.drawCenteredString(font, "Click Create New World to start.", width / 2, (listY + listH / 2 + 4).toInt(), UiStyle.MUTED)
        }

        guiGraphics.enableScissor(listX.toInt(), listY.toInt(), (listX + listW).toInt(), (listY + listH).toInt())
        entries.forEachIndexed { index, entry ->
            val rowY = listY + index * (rowH + rowGap) + scrollY
            val rect = Rect(listX, rowY, listW, rowH)
            rowRects.add(rect)
            if (rowY + rowH < listY || rowY > listY + listH) return@forEachIndexed

            val selected = index == selectedIndex
            val hovered = rect.has(mouseX.toDouble(), mouseY.toDouble()) && mouseY >= listY && mouseY <= listY + listH
            entry.hover += ((if (hovered) 1f else 0f) - entry.hover) * 0.2f
            val accent = if (selected) UiStyle.ACCENT else UiStyle.PURPLE

            val x1 = listX.toInt(); val y1 = rowY.toInt()
            val x2 = (listX + listW).toInt(); val y2 = (rowY + rowH).toInt()
            guiGraphics.fill(x1, y1, x2, y2, UiStyle.withAlpha(0x10131A, (0xCC + entry.hover * 0x22).toInt().coerceAtMost(0xFF)))
            if (selected || entry.hover > 0.05f)
                guiGraphics.fill(x1, y1, x2, y2, UiStyle.withAlpha(accent, ((if (selected) 0x20 else 0) + entry.hover * 0x18).toInt()))
            UiStyle.outline(guiGraphics, x1, y1, x2, y2,
                if (selected) UiStyle.withAlpha(accent, 0xCC) else if (entry.hover > 0.05f) UiStyle.withAlpha(accent, 0x88) else UiStyle.BORDER)
            guiGraphics.fill(x1, y1, x1 + 3, y2, accent)

            val s = entry.summary
            guiGraphics.drawString(font, s.levelName, x1 + 14, y1 + 7, if (selected) UiStyle.ACCENT else UiStyle.TEXT, false)
            val last = if (s.lastPlayed > 0) dateFmt.format(Date(s.lastPlayed)) else "—"
            guiGraphics.drawString(font, "${s.levelId}  ·  $last", x1 + 14, y1 + 22, UiStyle.MUTED, false)
        }
        guiGraphics.disableScissor()

        if (maxScroll > 0f) {
            val trackX = (listX + listW + 2).toInt()
            val barH = (listH * (listH / contentH)).coerceAtLeast(20f)
            val barY = listY + (-scrollY / maxScroll) * (listH - barH)
            guiGraphics.fill(trackX, listY.toInt(), trackX + 3, (listY + listH).toInt(), 0x33000000)
            guiGraphics.fill(trackX, barY.toInt(), trackX + 3, (barY + barH).toInt(), UiStyle.withAlpha(UiStyle.ACCENT, 0xAA))
        }

        // bottom bar
        val barY = gh - 34f
        val barH = 24f
        val backW = 64f
        val gap = 6f
        val leftLabels = listOf("Play", "Create", "Delete")
        val availLeft = gw - 48f - backW - gap
        val eachW = ((availLeft - gap * (leftLabels.size - 1)) / leftLabels.size).coerceIn(48f, 120f)
        var bx = 24f
        val hasSel = selectedIndex != -1
        for (label in leftLabels) {
            val enabled = when (label) { "Play", "Delete" -> hasSel; else -> true }
            val rect = Rect(bx, barY, eachW, barH)
            buttons[label] = rect
            drawButton(guiGraphics, font, label, rect, enabled, mouseX, mouseY, primary = label == "Play")
            bx += eachW + gap
        }
        val backRect = Rect(gw - backW - 24f, barY, backW, barH)
        buttons["Back"] = backRect
        drawButton(guiGraphics, font, "Back", backRect, true, mouseX, mouseY)

        if (pendingDelete != -1) renderDeleteConfirm(guiGraphics, font, gw, gh, mouseX, mouseY)

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    private fun renderDeleteConfirm(g: GuiGraphics, font: net.minecraft.client.gui.Font, gw: Float, gh: Float, mouseX: Int, mouseY: Int) {
        g.fill(0, 0, width, height, 0xCC000000.toInt())
        val mw = 260f; val mh = 104f
        val mx = gw / 2f - mw / 2f; val my = gh / 2f - mh / 2f
        UiStyle.card(g, mx.toInt(), my.toInt(), (mx + mw).toInt(), (my + mh).toInt())
        g.drawCenteredString(font, "DELETE WORLD", (gw / 2f).toInt(), (my + 16f).toInt(), 0xFFFF4466.toInt())
        val name = entries.getOrNull(pendingDelete)?.summary?.levelName ?: ""
        g.drawCenteredString(font, "\"$name\" will be lost forever.", (gw / 2f).toInt(), (my + 38f).toInt(), UiStyle.TEXT)
        g.drawCenteredString(font, "This cannot be undone.", (gw / 2f).toInt(), (my + 50f).toInt(), UiStyle.MUTED)
        val subW = 96f; val subH = 20f; val subY = my + mh - subH - 14f
        val delRect = Rect(mx + 20f, subY, subW, subH)
        val cancelRect = Rect(mx + mw - subW - 20f, subY, subW, subH)
        buttons["confirmDelete"] = delRect
        buttons["cancelDelete"] = cancelRect
        drawButton(g, font, "Delete", delRect, true, mouseX, mouseY, primary = true)
        drawButton(g, font, "Cancel", cancelRect, true, mouseX, mouseY)
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
        if (pendingDelete != -1) {
            if (buttons["confirmDelete"]?.has(mouseX, mouseY) == true) { doDelete(); return true }
            if (buttons["cancelDelete"]?.has(mouseX, mouseY) == true) { pendingDelete = -1; return true }
            return true
        }
        rowRects.forEachIndexed { index, rect ->
            if (rect.has(mouseX, mouseY) && mouseY >= listY && mouseY <= listY + listH) {
                if (selectedIndex == index) { playSelected(); return true }
                selectedIndex = index
                return true
            }
        }
        when {
            buttons["Play"]?.has(mouseX, mouseY) == true && selectedIndex != -1 -> { playSelected(); return true }
            buttons["Create"]?.has(mouseX, mouseY) == true -> { minecraft!!.setScreen(CustomCreateWorldScreen(this)); return true }
            buttons["Delete"]?.has(mouseX, mouseY) == true && selectedIndex != -1 -> { pendingDelete = selectedIndex; return true }
            buttons["Back"]?.has(mouseX, mouseY) == true -> { minecraft!!.setScreen(parent ?: MainMenuScreen()); return true }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double): Boolean {
        if (pendingDelete == -1 && maxScroll > 0f) {
            scrollY = (scrollY + deltaY.toFloat() * 18f).coerceIn(-maxScroll, 0f)
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (pendingDelete != -1) { pendingDelete = -1; return true }
            minecraft!!.setScreen(parent ?: MainMenuScreen()); return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun playSelected() {
        val entry = entries.getOrNull(selectedIndex) ?: return
        val s = entry.summary
        if (s.isDisabled) return
        minecraft!!.createWorldOpenFlows().openWorld(s.levelId) {
            minecraft!!.setScreen(this)
        }
    }

    private fun doDelete() {
        val entry = entries.getOrNull(pendingDelete) ?: run { pendingDelete = -1; return }
        val levelId = entry.summary.levelId
        pendingDelete = -1
        selectedIndex = -1
        try {
            val access = minecraft!!.levelSource.createAccess(levelId)
            access.deleteLevel()
            access.close()
        } catch (_: Exception) {}
        reload()
    }

    override fun shouldCloseOnEsc(): Boolean = false
}
