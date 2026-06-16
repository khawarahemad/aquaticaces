package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.ConnectScreen
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.ServerList
import net.minecraft.client.multiplayer.ServerStatusPinger
import net.minecraft.client.multiplayer.resolver.ServerAddress
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW

class CustomMultiplayerScreen(private val parent: Screen?) : Screen(Component.literal("Multiplayer")) {

    private data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun has(mx: Double, my: Double) = mx >= x && mx <= x + w && my >= y && my <= y + h
    }

    private enum class Modal { NONE, DIRECT, ADD, EDIT }

    private class ServerCard(val data: ServerData) { var hoverProgress = 0f }

    private var serverList: ServerList? = null
    private val cards = mutableListOf<ServerCard>()
    private var selectedIndex = -1
    private var modal = Modal.NONE
    private var lastClickIndex = -1
    private var lastClickTime = 0L

    private lateinit var nameInput: EditBox
    private lateinit var ipInput: EditBox
    private val pinger = ServerStatusPinger()

    private var menuTick = 0f
    private var scrollY = 0f
    private var maxScroll = 0f

    private val buttons = LinkedHashMap<String, Rect>()
    private val cardRects = mutableListOf<Rect>()

    private val cardH = 54f
    private val cardGap = 8f
    private val listX get() = 20f
    private val listY get() = 56f
    private val listW get() = width - 40f
    private val listH get() = height - 116f

    override fun init() {
        super.init()
        serverList = ServerList(minecraft!!).also { it.load() }
        rebuildCards()
        pingAll()

        nameInput = EditBox(minecraft!!.font, 0, 0, 196, 16, Component.literal("Name")).apply {
            setBordered(false); setHint(Component.literal("My Server")); visible = false; setMaxLength(64)
        }
        ipInput = EditBox(minecraft!!.font, 0, 0, 196, 16, Component.literal("Server IP")).apply {
            setBordered(false); setHint(Component.literal("play.example.net")); visible = false; setMaxLength(128)
        }
        addRenderableWidget(nameInput)
        addRenderableWidget(ipInput)
    }

    private fun rebuildCards() {
        cards.clear()
        val list = serverList ?: return
        for (i in 0 until list.size()) cards.add(ServerCard(list.get(i)))
        if (selectedIndex >= cards.size) selectedIndex = -1
    }

    private fun pingAll() {
        for (card in cards) {
            try {
                pinger.pingServer(card.data, {}, {})
            } catch (_: Exception) {}
        }
    }

    override fun tick() {
        menuTick += 1f
        pinger.tick()
    }

    override fun onClose() {
        pinger.removeAll()
        ServerIconHelper.clear()
        super.onClose()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = minecraft!!.font
        val gw = width.toFloat()
        val gh = height.toFloat()
        buttons.clear(); cardRects.clear()

        UiStyle.backdrop(guiGraphics, width, height, menuTick + partialTick)

        UiStyle.logoMark(guiGraphics, 32, 14, 26)
        guiGraphics.drawString(font, "MULTIPLAYER", 52, 16, UiStyle.ACCENT, true)
        guiGraphics.drawString(font, "${cards.size} servers  |  double-click to join", 52, 28, UiStyle.MUTED, true)
        guiGraphics.fill(20, 46, width - 20, 47, UiStyle.BORDER)

        val contentH = cards.size * (cardH + cardGap)
        maxScroll = (contentH - listH).coerceAtLeast(0f)
        scrollY = scrollY.coerceIn(-maxScroll, 0f)

        if (cards.isEmpty()) {
            val cx = width / 2
            guiGraphics.drawCenteredString(font, "No saved servers yet", cx, (listY + listH / 2 - 12).toInt(), UiStyle.TEXT)
            guiGraphics.drawCenteredString(font, "Use Add Server or Direct Join below.", cx, (listY + listH / 2 + 2).toInt(), UiStyle.MUTED)
        }

        guiGraphics.enableScissor(listX.toInt(), listY.toInt(), (listX + listW).toInt(), (listY + listH).toInt())
        cards.forEachIndexed { index, card ->
            val cardY = listY + index * (cardH + cardGap) + scrollY
            val rect = Rect(listX, cardY, listW, cardH)
            cardRects.add(rect)
            if (cardY + cardH < listY || cardY > listY + listH) return@forEachIndexed

            val isSelected = index == selectedIndex
            val isHovered = modal == Modal.NONE && rect.has(mouseX.toDouble(), mouseY.toDouble())
            card.hoverProgress += ((if (isHovered) 1f else 0f) - card.hoverProgress) * 0.18f
            val hover = card.hoverProgress
            val accent = if (isSelected) UiStyle.ACCENT else UiStyle.PURPLE

            val x1 = listX.toInt(); val y1 = cardY.toInt()
            val x2 = (listX + listW).toInt(); val y2 = (cardY + cardH).toInt()
            guiGraphics.fill(x1, y1, x2, y2, UiStyle.withAlpha(0x10131A, (0xCC + hover * 0x22).toInt().coerceAtMost(0xFF)))
            if (isSelected || hover > 0.05f)
                guiGraphics.fill(x1, y1, x2, y2, UiStyle.withAlpha(accent, ((if (isSelected) 0x20 else 0) + hover * 0x18).toInt()))
            UiStyle.outline(guiGraphics, x1, y1, x2, y2,
                if (isSelected) UiStyle.withAlpha(accent, 0xCC) else if (hover > 0.05f) UiStyle.withAlpha(accent, 0x88) else UiStyle.BORDER)
            guiGraphics.fill(x1, y1, x1 + 3, y2, accent)

            val iconSize = 40
            val ix = (listX + 10f).toInt()
            val iy = (cardY + cardH / 2f - iconSize / 2f).toInt()
            ServerIconHelper.draw(guiGraphics, card.data, ix, iy, iconSize, card.data.name, font)

            val textX = ix + iconSize + 12
            guiGraphics.drawString(font, card.data.name, textX, (cardY + 10f).toInt(),
                if (isSelected) UiStyle.ACCENT else UiStyle.TEXT, false)
            guiGraphics.drawString(font, card.data.ip, textX, (cardY + 24f).toInt(), UiStyle.MUTED, false)

            val motd = card.data.motd?.string?.take(42) ?: ""
            if (motd.isNotBlank()) {
                guiGraphics.drawString(font, motd, textX, (cardY + 36f).toInt(), UiStyle.DIM, false)
            }

            // status + ping (right side)
            val status = statusLabel(card.data)
            val statusCol = statusColor(card.data)
            val statusW = font.width(status) + 12
            val sx = x2 - statusW - 10
            val sy = (cardY + 10f).toInt()
            guiGraphics.fill(sx, sy, sx + statusW, sy + 13, UiStyle.withAlpha(statusCol, 0x33))
            UiStyle.outline(guiGraphics, sx, sy, sx + statusW, sy + 13, UiStyle.withAlpha(statusCol, 0x88))
            guiGraphics.drawString(font, status, sx + 6, sy + 3, statusCol, false)

            val pingText = pingLabel(card.data)
            val pingCol = pingColor(card.data)
            guiGraphics.drawString(font, pingText, x2 - font.width(pingText) - 10, (cardY + 28f).toInt(), pingCol, false)

            val players = playerLabel(card.data)
            if (players.isNotBlank()) {
                guiGraphics.drawString(font, players, x2 - font.width(players) - 10, (cardY + 40f).toInt(), UiStyle.MUTED, false)
            }
        }
        guiGraphics.disableScissor()

        if (maxScroll > 0f) {
            val trackX = (listX + listW + 2).toInt()
            val barH = (listH * (listH / contentH)).coerceAtLeast(20f)
            val barY = listY + (-scrollY / maxScroll) * (listH - barH)
            guiGraphics.fill(trackX, listY.toInt(), trackX + 3, (listY + listH).toInt(), 0x33000000)
            guiGraphics.fill(trackX, barY.toInt(), trackX + 3, (barY + barH).toInt(), UiStyle.withAlpha(UiStyle.ACCENT, 0xAA))
        }

        val barY = gh - 34f
        val barH = 24f
        val backW = 64f
        val gap = 6f
        val leftLabels = listOf("Join", "Direct", "Add", "Edit", "Delete", "Refresh", "Alts")
        val availLeft = gw - 40f - backW - gap
        val eachW = ((availLeft - gap * (leftLabels.size - 1)) / leftLabels.size).coerceIn(34f, 82f)
        var bx = 20f
        val hasSel = selectedIndex != -1
        for (label in leftLabels) {
            val enabled = when (label) {
                "Join", "Edit", "Delete" -> hasSel && modal == Modal.NONE
                else -> modal == Modal.NONE
            }
            val rect = Rect(bx, barY, eachW, barH)
            buttons[label] = rect
            drawButton(guiGraphics, font, label, rect, enabled, mouseX, mouseY, primary = label == "Join")
            bx += eachW + gap
        }
        val backRect = Rect(gw - backW - 20f, barY, backW, barH)
        buttons["Back"] = backRect
        drawButton(guiGraphics, font, "Back", backRect, true, mouseX, mouseY)

        if (modal != Modal.NONE) renderModal(guiGraphics, font, gw, gh, mouseX, mouseY)
        else { nameInput.visible = false; ipInput.visible = false }

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    private fun statusLabel(data: ServerData): String = when (data.state()) {
        ServerData.State.SUCCESSFUL -> "ONLINE"
        ServerData.State.PINGING -> "PINGING"
        ServerData.State.UNREACHABLE -> "OFFLINE"
        ServerData.State.INCOMPATIBLE -> "INCOMPATIBLE"
        ServerData.State.INITIAL -> "..."
        else -> "UNKNOWN"
    }

    private fun statusColor(data: ServerData): Int = when (data.state()) {
        ServerData.State.SUCCESSFUL -> UiStyle.SUCCESS
        ServerData.State.PINGING -> UiStyle.ACCENT
        ServerData.State.UNREACHABLE -> 0xFFFF4466.toInt()
        ServerData.State.INCOMPATIBLE -> 0xFFFFAA00.toInt()
        else -> UiStyle.MUTED
    }

    private fun pingLabel(data: ServerData): String =
        if (data.state() == ServerData.State.SUCCESSFUL && data.ping > 0) "${data.ping} ms"
        else if (data.state() == ServerData.State.PINGING) "ping..."
        else "-- ms"

    private fun pingColor(data: ServerData): Int {
        if (data.state() != ServerData.State.SUCCESSFUL) return UiStyle.MUTED
        return when {
            data.ping < 80 -> UiStyle.SUCCESS
            data.ping < 200 -> UiStyle.ACCENT
            else -> 0xFFFF4466.toInt()
        }
    }

    private fun playerLabel(data: ServerData): String {
        val players = data.players ?: return ""
        return "${players.online()}/${players.max()} online"
    }

    private fun renderModal(g: GuiGraphics, font: net.minecraft.client.gui.Font, gw: Float, gh: Float, mouseX: Int, mouseY: Int) {
        g.fill(0, 0, width, height, 0xCC000000.toInt())
        val twoFields = modal == Modal.ADD || modal == Modal.EDIT
        val modalW = 260f
        val modalH = if (twoFields) 150f else 110f
        val mx = gw / 2f - modalW / 2f
        val my = gh / 2f - modalH / 2f
        UiStyle.card(g, mx.toInt(), my.toInt(), (mx + modalW).toInt(), (my + modalH).toInt())
        val titleText = when (modal) {
            Modal.DIRECT -> "DIRECT JOIN"
            Modal.ADD -> "ADD SERVER"
            Modal.EDIT -> "EDIT SERVER"
            else -> ""
        }
        g.drawCenteredString(font, titleText, (gw / 2f).toInt(), (my + 14f).toInt(), UiStyle.ACCENT)
        var fieldY = my + 34f
        if (twoFields) {
            g.drawString(font, "Name", (mx + 18f).toInt(), fieldY.toInt(), UiStyle.MUTED, false)
            field(g, nameInput, mx + 18f, fieldY + 10f, modalW - 36f)
            fieldY += 38f
            g.drawString(font, "Address", (mx + 18f).toInt(), fieldY.toInt(), UiStyle.MUTED, false)
            field(g, ipInput, mx + 18f, fieldY + 10f, modalW - 36f)
        } else {
            g.drawString(font, "Address", (mx + 18f).toInt(), fieldY.toInt(), UiStyle.MUTED, false)
            field(g, ipInput, mx + 18f, fieldY + 10f, modalW - 36f)
            nameInput.visible = false
        }
        val subW = 96f; val subH = 20f; val subY = my + modalH - subH - 14f
        buttons["modalOk"] = Rect(mx + 20f, subY, subW, subH)
        buttons["modalCancel"] = Rect(mx + modalW - subW - 20f, subY, subW, subH)
        drawButton(g, font, if (modal == Modal.DIRECT) "Connect" else "Save", buttons["modalOk"]!!,
            ipInput.value.isNotBlank() && (!twoFields || nameInput.value.isNotBlank()), mouseX, mouseY, primary = true)
        drawButton(g, font, "Cancel", buttons["modalCancel"]!!, true, mouseX, mouseY)
    }

    private fun field(g: GuiGraphics, box: EditBox, x: Float, y: Float, w: Float) {
        g.fill(x.toInt(), y.toInt(), (x + w).toInt(), (y + 18f).toInt(), 0x66000000)
        UiStyle.outline(g, x.toInt(), y.toInt(), (x + w).toInt(), (y + 18f).toInt(), UiStyle.BORDER)
        box.setX((x + 5f).toInt()); box.setY((y + 5f).toInt())
        box.width = (w - 10f).toInt(); box.visible = true
    }

    private fun drawButton(g: GuiGraphics, font: net.minecraft.client.gui.Font, label: String, r: Rect, enabled: Boolean, mx: Int, my: Int, primary: Boolean = false) {
        val x1 = r.x.toInt(); val y1 = r.y.toInt(); val x2 = (r.x + r.w).toInt(); val y2 = (r.y + r.h).toInt()
        val cx = (r.x + r.w / 2f).toInt(); val cy = (r.y + r.h / 2f - 4f).toInt()
        val hovered = enabled && r.has(mx.toDouble(), my.toDouble())
        if (!enabled) {
            g.fill(x1, y1, x2, y2, 0x55131521); UiStyle.outline(g, x1, y1, x2, y2, 0x552A2E3D)
            g.drawCenteredString(font, label, cx, cy, 0xFF585864.toInt()); return
        }
        if (primary) {
            if (hovered) g.fill(x1 - 2, y1 - 2, x2 + 2, y2 + 2, UiStyle.withAlpha(UiStyle.ACCENT, 0x55))
            g.fillGradient(x1, y1, x2, y2, UiStyle.ACCENT, UiStyle.ACCENT_2)
            g.drawCenteredString(font, label, cx, cy, 0xFFFFFFFF.toInt())
        } else {
            g.fill(x1, y1, x2, y2, if (hovered) UiStyle.withAlpha(0x1A2230, 0xCC) else 0x99131521.toInt())
            UiStyle.outline(g, x1, y1, x2, y2, if (hovered) UiStyle.withAlpha(UiStyle.ACCENT, 0x88) else UiStyle.BORDER)
            g.drawCenteredString(font, label, cx, cy, if (hovered) UiStyle.ACCENT else UiStyle.TEXT)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (modal != Modal.NONE) {
            if (buttons["modalOk"]?.has(mouseX, mouseY) == true) { confirmModal(); return true }
            if (buttons["modalCancel"]?.has(mouseX, mouseY) == true) { closeModal(); return true }
            
            // Custom field focus click handling
            val twoFields = modal == Modal.ADD || modal == Modal.EDIT
            val modalW = 260f
            val modalH = if (twoFields) 150f else 110f
            val mx = width / 2f - modalW / 2f
            val my = height / 2f - modalH / 2f
            var fieldY = my + 34f
            
            if (twoFields) {
                if (mouseX >= mx + 18 && mouseX <= mx + modalW - 18 && mouseY >= fieldY + 10 && mouseY <= fieldY + 28) {
                    nameInput.isFocused = true
                    ipInput.isFocused = false
                    setFocused(nameInput)
                    return true
                }
                fieldY += 38f
                if (mouseX >= mx + 18 && mouseX <= mx + modalW - 18 && mouseY >= fieldY + 10 && mouseY <= fieldY + 28) {
                    nameInput.isFocused = false
                    ipInput.isFocused = true
                    setFocused(ipInput)
                    return true
                }
            } else {
                if (mouseX >= mx + 18 && mouseX <= mx + modalW - 18 && mouseY >= fieldY + 10 && mouseY <= fieldY + 28) {
                    ipInput.isFocused = true
                    setFocused(ipInput)
                    return true
                }
            }
            return super.mouseClicked(mouseX, mouseY, button)
        }
        cardRects.forEachIndexed { index, rect ->
            if (rect.has(mouseX, mouseY) && mouseY >= listY && mouseY <= listY + listH) {
                val now = System.currentTimeMillis()
                if (button == 0) {
                    if (selectedIndex == index && now - lastClickTime < 350) {
                        joinSelectedServer(); return true
                    }
                    selectedIndex = index
                    lastClickIndex = index
                    lastClickTime = now
                }
                return true
            }
        }
        when {
            buttons["Join"]?.has(mouseX, mouseY) == true && selectedIndex != -1 -> { joinSelectedServer(); return true }
            buttons["Direct"]?.has(mouseX, mouseY) == true -> { openModal(Modal.DIRECT); return true }
            buttons["Add"]?.has(mouseX, mouseY) == true -> { openModal(Modal.ADD); return true }
            buttons["Edit"]?.has(mouseX, mouseY) == true && selectedIndex != -1 -> { openModal(Modal.EDIT); return true }
            buttons["Delete"]?.has(mouseX, mouseY) == true && selectedIndex != -1 -> { deleteSelected(); return true }
            buttons["Refresh"]?.has(mouseX, mouseY) == true -> { pingAll(); return true }
            buttons["Alts"]?.has(mouseX, mouseY) == true -> { minecraft!!.setScreen(AltManagerScreen(this)); return true }
            buttons["Back"]?.has(mouseX, mouseY) == true -> { minecraft!!.setScreen(parent ?: MainMenuScreen()); return true }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double): Boolean {
        if (modal == Modal.NONE && maxScroll > 0f) {
            scrollY = (scrollY + deltaY.toFloat() * 18f).coerceIn(-maxScroll, 0f)
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY)
    }

    private fun openModal(m: Modal) {
        modal = m; nameInput.value = ""; ipInput.value = ""
        if (m == Modal.EDIT && selectedIndex != -1) {
            val d = cards[selectedIndex].data
            nameInput.value = d.name; ipInput.value = d.ip
        }
        if (m == Modal.ADD || m == Modal.EDIT) {
            nameInput.isFocused = true
            ipInput.isFocused = false
            setFocused(nameInput)
        } else if (m == Modal.DIRECT) {
            nameInput.isFocused = false
            ipInput.isFocused = true
            setFocused(ipInput)
        }
    }

    private fun closeModal() {
        modal = Modal.NONE; nameInput.visible = false; ipInput.visible = false
        nameInput.value = ""; ipInput.value = ""
        setFocused(null)
    }

    private fun confirmModal() {
        when (modal) {
            Modal.DIRECT -> if (ipInput.value.isNotBlank()) { connectDirect(ipInput.value); return }
            Modal.ADD -> if (ipInput.value.isNotBlank() && nameInput.value.isNotBlank()) {
                serverList?.add(ServerData(nameInput.value, ipInput.value, ServerData.Type.OTHER), false)
                serverList?.save(); rebuildCards(); pingAll()
            }
            Modal.EDIT -> if (selectedIndex != -1 && ipInput.value.isNotBlank() && nameInput.value.isNotBlank()) {
                val list = serverList
                if (list != null && selectedIndex < list.size()) {
                    val d = list.get(selectedIndex)
                    d.name = nameInput.value; d.ip = ipInput.value
                    list.save(); rebuildCards(); pingAll()
                }
            }
            Modal.NONE -> {}
        }
        if (modal != Modal.DIRECT) closeModal()
    }

    private fun deleteSelected() {
        val list = serverList ?: return
        if (selectedIndex < 0 || selectedIndex >= list.size()) return
        list.remove(list.get(selectedIndex)); list.save()
        selectedIndex = -1; rebuildCards()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (modal != Modal.NONE) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { confirmModal(); return true }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { closeModal(); return true }
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                if (modal == Modal.ADD || modal == Modal.EDIT) {
                    if (nameInput.isFocused) {
                        nameInput.isFocused = false
                        ipInput.isFocused = true
                        setFocused(ipInput)
                    } else {
                        nameInput.isFocused = true
                        ipInput.isFocused = false
                        setFocused(nameInput)
                    }
                    return true
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers)
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { minecraft!!.setScreen(parent ?: MainMenuScreen()); return true }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun joinSelectedServer() {
        val list = serverList ?: return
        if (selectedIndex in 0 until list.size()) {
            ConnectScreen.startConnecting(this, minecraft!!, ServerAddress.parseString(list.get(selectedIndex).ip), list.get(selectedIndex), false, null)
        }
    }

    private fun connectDirect(ip: String) {
        ConnectScreen.startConnecting(this, minecraft!!, ServerAddress.parseString(ip), ServerData("Direct Connect", ip, ServerData.Type.OTHER), false, null)
    }

    override fun shouldCloseOnEsc(): Boolean = false
}
