package com.aquaticaces.ui

import com.aquaticaces.AquaticAces
import com.aquaticaces.core.ClientTheme
import com.aquaticaces.module.Category
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.module.impl.render.ClickGUIModule
import com.aquaticaces.ui.components.CategoryPanel
import com.aquaticaces.ui.components.ThemePanel
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class ClickGUI : Screen(Component.literal("ClickGUI")) {

    companion object {
        val vectorRenderer = VectorRenderer()
        val fontRenderer = FontRenderer(vectorRenderer)
        val shaderManager = ShaderManager()
        private var initialized = false
        var searchQuery: String = ""

        init {
            Runtime.getRuntime().addShutdownHook(Thread {
                if (initialized) {
                    shaderManager.cleanup()
                    vectorRenderer.cleanup()
                }
            })
        }
    }

    private val panels = mutableListOf<CategoryPanel>()
    private var themePanel: ThemePanel? = null
    private var fadeProgress = 0.0f
    private lateinit var searchBox: EditBox

    init {
        Category.values().forEachIndexed { index, cat ->
            panels.add(CategoryPanel(cat, 35f + index * 130f, 40f, 115f, 22f))
        }
    }

    override fun init() {
        super.init()
        fadeProgress = 0f
        if (!initialized) {
            vectorRenderer.init()
            shaderManager.init()
            try {
                fontRenderer.loadFont("outfit", "/assets/aquaticaces/font/outfit.ttf")
            } catch (e: Exception) {
                AquaticAces.logger.warn("Outfit font file failed to load: ${e.message}")
            }
            initialized = true
        }
        searchBox = EditBox(minecraft!!.font, 10, 10, 150, 18, Component.literal("Search"))
        searchBox.setResponder { searchQuery = it.lowercase() }
        addRenderableWidget(searchBox)

        val clickGui = ModuleManager.getModuleByName("ClickGUI") as? ClickGUIModule
        if (clickGui != null) {
            val guiW = minecraft!!.window.guiScaledWidth.toFloat()
            themePanel = ThemePanel(clickGui, guiW - 200f, 40f, 185f, 22f)
        }
    }

    override fun tick() {
        fadeProgress = (fadeProgress + 0.08f).coerceAtMost(1.0f)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val blurRadius = fadeProgress * ClientTheme.blurStrength
        shaderManager.renderBlur(blurRadius)

        val scale = minecraft!!.window.guiScale.toFloat()
        val guiWidth = minecraft!!.window.guiScaledWidth.toFloat()
        val guiHeight = minecraft!!.window.guiScaledHeight.toFloat()

        vectorRenderer.begin(guiWidth, guiHeight, scale)

        for (panel in panels) {
            panel.render(vectorRenderer, fontRenderer, mouseX, mouseY, partialTick)
        }
        themePanel?.render(vectorRenderer, fontRenderer, mouseX, mouseY, partialTick)

        vectorRenderer.end()
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (themePanel?.mouseClicked(mouseX.toInt(), mouseY.toInt(), button) == true) return true
        for (panel in panels) {
            if (panel.mouseClicked(mouseX.toInt(), mouseY.toInt(), button)) return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        themePanel?.mouseReleased(mouseX.toInt(), mouseY.toInt(), button)
        panels.forEach { it.mouseReleased(mouseX.toInt(), mouseY.toInt(), button) }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (com.aquaticaces.ui.components.KeybindCapture.handle(keyCode)) return true
        for (panel in panels) if (panel.keyTyped(keyCode)) return true
        if (themePanel?.keyTyped(keyCode) == true) return true
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        for (panel in panels) {
            if (mouseX >= panel.x && mouseX <= panel.x + panel.width &&
                mouseY >= panel.y && mouseY <= panel.y + panel.height + 280) {
                panel.scroll(-scrollY.toFloat() * 14f)
                return true
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun shouldCloseOnEsc(): Boolean = true
}
