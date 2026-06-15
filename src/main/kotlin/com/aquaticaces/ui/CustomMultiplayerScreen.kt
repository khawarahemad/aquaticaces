package com.aquaticaces.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.ConnectScreen
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.ServerList
import net.minecraft.client.multiplayer.resolver.ServerAddress
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import kotlin.math.roundToInt

/**
 * CustomMultiplayerScreen.
 * Premium replacement for standard Multiplayer screen. Draws saved servers
 * as glowing vector cards and hosts an overlay dialog for Direct Connection IP typing.
 */
class CustomMultiplayerScreen(private val parent: Screen?) : Screen(Component.literal("Multiplayer")) {

    private class ServerCard(val data: ServerData) {
        var hoverProgress = 0f
    }

    private var serverList: ServerList? = null
    private val cards = mutableListOf<ServerCard>()
    private var selectedIndex = -1
    private var isDirectConnectOpen = false

    // Input text widget wrapped inside vector popup bounds
    private lateinit var ipInput: EditBox

    private var fadeProgress = 0.0f
    private var scrollY = 0f

    override fun init() {
        super.init()
        fadeProgress = 0f
        
        // 1. Load saved servers
        serverList = ServerList(minecraft!!)
        serverList?.load()
        cards.clear()
        
        val list = serverList
        if (list != null) {
            for (i in 0 until list.size()) {
                cards.add(ServerCard(list.get(i)))
            }
        }

        // 2. Initialize Direct Connect input box
        ipInput = EditBox(
            minecraft!!.font,
            (width / 2f - 90f).roundToInt(),
            (height / 2f - 10f).roundToInt(),
            180,
            20,
            Component.literal("Server IP")
        )
        ipInput.visible = false
        addRenderableWidget(ipInput)
    }

    override fun tick() {
        fadeProgress = (fadeProgress + 0.08f).coerceAtMost(1.0f)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val vectorRenderer = ClickGUI.vectorRenderer
        val fontRenderer = ClickGUI.fontRenderer

        val scale = minecraft!!.window.guiScale.toFloat()
        val guiWidth = minecraft!!.window.guiScaledWidth.toFloat()
        val guiHeight = minecraft!!.window.guiScaledHeight.toFloat()

        // Begin NanoVG Frame
        vectorRenderer.begin(guiWidth, guiHeight, scale)

        // 1. Background gradient
        val leftColor = 0xFF080A14.toInt()
        val rightColor = 0xFF140D26.toInt()
        vectorRenderer.drawLinearGradientRect(
            0f, 0f, guiWidth, guiHeight, 0f,
            0f, 0f, guiWidth, guiHeight,
            leftColor, rightColor
        )

        // 2. Header Title
        val headerText = "MULTIPLAYER BROWSER"
        fontRenderer.drawString(
            "outfit",
            headerText,
            20f,
            20f,
            16f,
            0xFFFFFFFF.toInt()
        )

        // 3. Render Server List
        val listX = 20f
        val listY = 50f
        val listW = guiWidth - 40f
        val listH = guiHeight - 110f
        
        val cardH = 34f
        val cardGap = 8f

        cards.forEachIndexed { index, card ->
            val cardY = listY + index * (cardH + cardGap) + scrollY
            
            // Skip rendering if off-screen
            if (cardY + cardH < listY || cardY > listY + listH) return@forEachIndexed

            val isSelected = index == selectedIndex
            val isHovered = mouseX >= listX && mouseX <= listX + listW && mouseY >= cardY && mouseY <= cardY + cardH && !isDirectConnectOpen
            
            // Update animations
            val targetHover = if (isHovered) 1.0f else 0.0f
            card.hoverProgress += (targetHover - card.hoverProgress) * 0.15f

            val hoverFactor = card.hoverProgress
            val bgAlpha = (0x22 + (0x1F * hoverFactor)).toInt()
            val cardBg = (bgAlpha shl 24) or (if (isSelected) 0x0072FF else 0x131521)
            val cardBorder = (if (isSelected) 0xFF00C6FF.toInt() else ((0x3C + (0x7F * hoverFactor)).toInt() shl 24) or 0x2A2E3D)

            // Draw Server Card Container
            vectorRenderer.drawRoundedRect(listX, cardY, listW, cardH, 4f, cardBg)
            vectorRenderer.drawMultiPassOutline(listX, cardY, listW, cardH, 4f, 1.0f, cardBorder, (0x10 shl 24) or 0x00C6FF)

            // Server Icon visual box placeholder
            val iconSize = 22f
            val ix = listX + 6f
            val iy = cardY + cardH / 2f - iconSize / 2f
            vectorRenderer.drawRoundedRect(ix, iy, iconSize, iconSize, 3f, 0xFF2A2E3D.toInt())
            fontRenderer.drawString("outfit", "S", ix + 8f, iy + 4f, 11f, 0xFFFFFFFF.toInt())

            // Server details text
            fontRenderer.drawString(
                "outfit",
                card.data.name,
                ix + iconSize + 10f,
                cardY + 5f,
                12f,
                0xFFFFFFFF.toInt()
            )
            fontRenderer.drawString(
                "outfit",
                card.data.ip,
                ix + iconSize + 10f,
                cardY + 18f,
                9f,
                0xFF9898A6.toInt()
            )

            // MOTD
            val motd = card.data.motd?.string ?: "No connection details resolved."
            val motdText = if (motd.length > 50) motd.substring(0, 47) + "..." else motd
            fontRenderer.drawString(
                "outfit",
                motdText,
                ix + iconSize + 150f,
                cardY + 11f,
                9.5f,
                0xFFB6B6C2.toInt()
            )

            // Player counts
            val players = "${card.data.players?.online ?: 0} / ${card.data.players?.max ?: 0}"
            val countW = fontRenderer.getStringWidth("outfit", players, 11f)
            fontRenderer.drawString(
                "outfit",
                players,
                listX + listW - countW - 12f,
                cardY + 11f,
                11f,
                0xFF00FF55.toInt()
            )
        }

        // 4. Render Bottom Control Buttons
        val btnW = 85f
        val btnH = 22f
        val btnY = guiHeight - 35f
        val startBtnX = 20f

        // Draw Join Button
        val hasSelection = selectedIndex != -1
        drawMenuButton(vectorRenderer, fontRenderer, "Join", startBtnX, btnY, btnW, btnH, hasSelection && !isDirectConnectOpen, mouseX, mouseY) {
            joinSelectedServer()
        }

        // Draw Direct Connect Button
        drawMenuButton(vectorRenderer, fontRenderer, "Direct Join", startBtnX + btnW + 10f, btnY, btnW, btnH, !isDirectConnectOpen, mouseX, mouseY) {
            isDirectConnectOpen = true
            ipInput.visible = true
            ipInput.isFocused = true
        }

        // Draw Alts Button
        drawMenuButton(vectorRenderer, fontRenderer, "Alts", startBtnX + (btnW + 10f) * 2, btnY, btnW, btnH, !isDirectConnectOpen, mouseX, mouseY) {
            minecraft!!.setScreen(com.aquaticaces.ui.AltManagerScreen(this))
        }

        // Draw Back Button
        drawMenuButton(vectorRenderer, fontRenderer, "Back", guiWidth - btnW - 20f, btnY, btnW, btnH, true, mouseX, mouseY) {
            minecraft!!.setScreen(MainMenuScreen())
        }

        // 5. Draw Direct Connect Overlay Popup Modal
        if (isDirectConnectOpen) {
            // Dark transparent background shield
            vectorRenderer.drawRoundedRect(0f, 0f, guiWidth, guiHeight, 0f, 0x88000000.toInt())

            val modalW = 220f
            val modalH = 100f
            val mx = guiWidth / 2f - modalW / 2f
            val my = guiHeight / 2f - modalH / 2f

            // Modal container card
            vectorRenderer.drawDropShadow(mx, my, modalW, modalH, 6f, 12f, 0x80000000.toInt())
            vectorRenderer.drawRoundedRect(mx, my, modalW, modalH, 6f, 0xFF131521.toInt())
            vectorRenderer.drawMultiPassOutline(mx, my, modalW, modalH, 6f, 1.0f, 0xFF2A2E3D.toInt(), 0x1A00C6FF.toInt())

            // Modal Header text
            val modalTitle = "DIRECT JOIN SERVER"
            val mtW = fontRenderer.getStringWidth("outfit", modalTitle, 12f)
            fontRenderer.drawString(
                "outfit",
                modalTitle,
                guiWidth / 2f - mtW / 2f,
                my + 10f,
                12f,
                0xFFFFFFFF.toInt()
            )

            // Styled background outline around the vanilla EditBox widget
            vectorRenderer.drawRoundedRect(mx + 18f, my + 38f, 184f, 24f, 3f, 0xFF0F1015.toInt())
            vectorRenderer.drawMultiPassOutline(mx + 18f, my + 38f, 184f, 24f, 3f, 1.0f, 0xFF2A2E3D.toInt(), 0x00000000)

            // Dialog action buttons
            val subBtnW = 80f
            val subBtnH = 18f
            val subBtnY = my + 72f

            // Connect button inside modal
            val ipVal = ipInput.value
            val canConnect = ipVal.isNotBlank()
            drawMenuButton(vectorRenderer, fontRenderer, "Connect", mx + 20f, subBtnY, subBtnW, subBtnH, canConnect, mouseX, mouseY) {
                connectDirect(ipVal)
            }

            // Cancel button inside modal
            drawMenuButton(vectorRenderer, fontRenderer, "Cancel", mx + modalW - subBtnW - 20f, subBtnY, subBtnW, subBtnH, true, mouseX, mouseY) {
                isDirectConnectOpen = false
                ipInput.visible = false
                ipInput.value = ""
            }
        }

        vectorRenderer.end()
    }

    private fun drawMenuButton(
        vr: VectorRenderer,
        fr: FontRenderer,
        label: String,
        bx: Float,
        by: Float,
        bw: Float,
        bh: Float,
        enabled: Boolean,
        mx: Int,
        my: Int,
        onClick: () -> Unit
    ) {
        val isHovered = enabled && mx >= bx && mx <= bx + bw && my >= by && my <= by + bh
        val bgAlpha = if (enabled) (if (isHovered) 0x66 else 0x36) else 0x15
        val buttonBg = (bgAlpha shl 24) or (if (isHovered) 0x0072FF else 0x131521)
        val buttonBorder = ((if (enabled) (if (isHovered) 0xAA else 0x4B) else 0x24) shl 24) or 0x2A2E3D

        vr.drawRoundedRect(bx, by, bw, bh, 3f, buttonBg)
        vr.drawMultiPassOutline(bx, by, bw, bh, 3f, 1.0f, buttonBorder, 0)

        val textW = fr.getStringWidth("outfit", label, 10f)
        val textColor = if (enabled) (if (isHovered) 0xFF00C6FF.toInt() else 0xFFFFFFFF.toInt()) else 0xFF585864.toInt()
        fr.drawString(
            "outfit",
            label,
            bx + bw / 2f - textW / 2f,
            by + bh / 2f - 5f,
            10f,
            textColor
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val guiWidth = minecraft!!.window.guiScaledWidth.toFloat()
        val guiHeight = minecraft!!.window.guiScaledHeight.toFloat()

        if (isDirectConnectOpen) {
            val modalW = 220f
            val modalH = 100f
            val mx = guiWidth / 2f - modalW / 2f
            val my = guiHeight / 2f - modalH / 2f
            val subBtnW = 80f
            val subBtnH = 18f
            val subBtnY = my + 72f

            // Check Connect click
            if (mouseX >= mx + 20f && mouseX <= mx + 20f + subBtnW && mouseY >= subBtnY && mouseY <= subBtnY + subBtnH) {
                if (ipInput.value.isNotBlank()) {
                    connectDirect(ipInput.value)
                }
                return true
            }

            // Check Cancel click
            if (mouseX >= mx + modalW - subBtnW - 20f && mouseX <= mx + modalW - 20f && mouseY >= subBtnY && mouseY <= subBtnY + subBtnH) {
                isDirectConnectOpen = false
                ipInput.visible = false
                ipInput.value = ""
                return true
            }

            return super.mouseClicked(mouseX, mouseY, button)
        }

        // Standard multiplayer coordinates
        val listX = 20f
        val listY = 50f
        val listW = guiWidth - 40f
        val cardH = 34f
        val cardGap = 8f

        // Check list selections
        cards.forEachIndexed { index, card ->
            val cardY = listY + index * (cardH + cardGap) + scrollY
            if (mouseX >= listX && mouseX <= listX + listW && mouseY >= cardY && mouseY <= cardY + cardH) {
                selectedIndex = index
                return true
            }
        }

        // Bottom control clicks
        val btnW = 85f
        val btnH = 22f
        val btnY = guiHeight - 35f
        val startBtnX = 20f

        // Check Join click
        if (selectedIndex != -1 && mouseX >= startBtnX && mouseX <= startBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
            joinSelectedServer()
            return true
        }

        // Check Direct Join click
        if (mouseX >= startBtnX + btnW + 10f && mouseX <= startBtnX + btnW + 10f + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
            isDirectConnectOpen = true
            ipInput.visible = true
            ipInput.isFocused = true
            return true
        }

        // Check Alts click
        if (mouseX >= startBtnX + (btnW + 10f) * 2 && mouseX <= startBtnX + (btnW + 10f) * 2 + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
            minecraft!!.setScreen(com.aquaticaces.ui.AltManagerScreen(this))
            return true
        }

        // Check Back click
        if (mouseX >= guiWidth - btnW - 20f && mouseX <= guiWidth - 20f && mouseY >= btnY && mouseY <= btnY + btnH) {
            minecraft!!.setScreen(MainMenuScreen())
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isDirectConnectOpen) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                if (ipInput.value.isNotBlank()) {
                    connectDirect(ipInput.value)
                }
                return true
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                isDirectConnectOpen = false
                ipInput.visible = false
                ipInput.value = ""
                return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun joinSelectedServer() {
        val index = selectedIndex
        val list = serverList
        if (index != -1 && list != null && index < list.size()) {
            val server = list.get(index)
            ConnectScreen.startConnecting(
                this, minecraft!!, ServerAddress.parseString(server.ip), server, false, null
            )
        }
    }

    private fun connectDirect(ip: String) {
        val serverData = ServerData("Direct Connect", ip, ServerData.Type.OTHER)
        ConnectScreen.startConnecting(
            this, minecraft!!, ServerAddress.parseString(ip), serverData, false, null
        )
    }

    override fun shouldCloseOnEsc(): Boolean = !isDirectConnectOpen
}
