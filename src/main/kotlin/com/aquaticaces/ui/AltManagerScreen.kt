package com.aquaticaces.ui

import com.aquaticaces.core.AltManager
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import kotlin.math.roundToInt

class AltManagerScreen(private val parent: Screen?) : Screen(Component.literal("Alt Manager")) {

    private lateinit var nameInput: EditBox
    private var scroll = 0f
    private var fade = 0f

    override fun init() {
        super.init()
        fade = 0f
        nameInput = EditBox(
            minecraft!!.font,
            (width / 2f - 100f).roundToInt(),
            (height - 42f).roundToInt(),
            200,
            18,
            Component.literal("Username")
        )
        nameInput.setMaxLength(16)
        addRenderableWidget(nameInput)
    }

    override fun tick() {
        fade = (fade + 0.08f).coerceAtMost(1f)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val vector = ClickGUI.vectorRenderer
        val font = ClickGUI.fontRenderer
        val guiW = minecraft!!.window.guiScaledWidth.toFloat()
        val guiH = minecraft!!.window.guiScaledHeight.toFloat()
        val scale = minecraft!!.window.guiScale.toFloat()

        vector.begin(guiW, guiH, scale)
        vector.drawRoundedRect(0f, 0f, guiW, guiH, 0f, 0xCC0A0B10.toInt())

        font.drawString("outfit", "Alt Manager", guiW / 2f - 40f, 20f, 16f, 0xFFFFFFFF.toInt())
        font.drawString("outfit", "Current: ${minecraft!!.user.name}", guiW / 2f - 60f, 40f, 10f, 0xFF00C6FF.toInt())

        val listX = guiW / 2f - 140f
        var listY = 70f - scroll
        val rowH = 28f
        val alts = AltManager.all()

        for ((index, alt) in alts.withIndex()) {
            if (listY + rowH < 60f || listY > guiH - 60f) {
                listY += rowH
                continue
            }
            val hovered = mouseX >= listX && mouseX <= listX + 280f && mouseY >= listY && mouseY <= listY + rowH
            val bg = if (hovered) 0xEE1E2330.toInt() else 0xDD14161D.toInt()
            vector.drawRoundedRect(listX, listY, 280f, rowH - 2f, 4f, bg)
            font.drawString("outfit", alt.username, listX + 10f, listY + 6f, 11f, 0xFFFFFFFF.toInt())
            font.drawString("outfit", "[Switch]", listX + 200f, listY + 6f, 9f, 0xFF00C6FF.toInt())
            font.drawString("outfit", "[Del]", listX + 245f, listY + 6f, 9f, 0xFFFF6666.toInt())
            listY += rowH
        }

        if (alts.isEmpty()) {
            font.drawString("outfit", "No alts saved — type a name below and press Enter", guiW / 2f - 150f, 90f, 10f, 0xFF888888.toInt())
        }

        font.drawString("outfit", "Enter username + press Enter to add", guiW / 2f - 95f, guiH - 58f, 9f, 0xFF888888.toInt())
        vector.end()
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        val listX = width / 2f - 140f
        var listY = 70f - scroll
        val rowH = 28f
        for (alt in AltManager.all()) {
            if (mouseY >= listY && mouseY <= listY + rowH) {
                if (mouseX >= listX + 200 && mouseX <= listX + 240) {
                    AltManager.switchTo(alt.username)
                    return true
                }
                if (mouseX >= listX + 245 && mouseX <= listX + 280) {
                    AltManager.remove(alt.username)
                    return true
                }
            }
            listY += rowH
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        scroll = (scroll - scrollY.toFloat() * 12f).coerceAtLeast(0f)
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (nameInput.isFocused && keyCode == GLFW.GLFW_KEY_ENTER) {
            val name = nameInput.value.trim()
            if (name.isNotBlank()) {
                AltManager.add(name)
                nameInput.value = ""
            }
            return true
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft!!.setScreen(parent)
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun isPauseScreen(): Boolean = false
}
