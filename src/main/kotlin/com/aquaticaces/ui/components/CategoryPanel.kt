package com.aquaticaces.ui.components

import com.aquaticaces.core.ClientTheme
import com.aquaticaces.module.Category
import com.aquaticaces.module.ModuleManager
import com.aquaticaces.ui.ClickGUI
import com.aquaticaces.ui.CubicBezier
import com.aquaticaces.ui.VectorRenderer
import com.aquaticaces.ui.FontRenderer

class CategoryPanel(
    val category: Category,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) : UIComponent(x, y, width, height) {

    var isExpanded = true
    var expansionProgress = 1.0f
    var scrollOffset = 0f

    private var isDragging = false
    private var dragX = 0f
    private var dragY = 0f

    val children = mutableListOf<ModuleButton>()
    private val maxBodyHeight = 280f

    private fun visibleModules() = ModuleManager.getModulesByCategory(category)
        .filter { ClickGUI.searchQuery.isBlank() || it.name.lowercase().contains(ClickGUI.searchQuery) }

    private fun syncChildren() {
        val visible = visibleModules()
        if (children.size != visible.size || children.map { it.module } != visible) {
            children.clear()
            for (module in visible) children.add(ModuleButton(module, 0f, 0f, width, 20f))
        }
    }

    private fun totalBodyHeight(): Float = children.sumOf { it.height.toDouble() }.toFloat()

    private fun layoutChildren() {
        var currentY = y + height - scrollOffset
        for (child in children) {
            child.x = x
            child.y = currentY
            currentY += child.height
        }
    }

    fun scroll(delta: Float) {
        val maxScroll = (totalBodyHeight() - maxBodyHeight).coerceAtLeast(0f)
        scrollOffset = (scrollOffset + delta).coerceIn(0f, maxScroll)
    }

    override fun render(vectorRenderer: VectorRenderer, fontRenderer: FontRenderer, mouseX: Int, mouseY: Int, partialTicks: Float) {
        syncChildren()
        if (isDragging) {
            x = (mouseX - dragX).coerceAtLeast(0f)
            y = (mouseY - dragY).coerceAtLeast(0f)
        }

        val target = if (isExpanded) 1.0f else 0.0f
        expansionProgress += (target - expansionProgress) * 0.15f
        val eased = CubicBezier.easeInOutBezier(expansionProgress)

        layoutChildren()
        val bodyContentH = totalBodyHeight()
        val bodyHeight = (bodyContentH.coerceAtMost(maxBodyHeight)) * eased
        val totalPanelHeight = height + bodyHeight

        vectorRenderer.drawDropShadow(x, y, width, totalPanelHeight, 6f, 10f, 0x90000000.toInt())
        vectorRenderer.drawRoundedRect(x, y, width, height, 5f, ClientTheme.panelBg)
        vectorRenderer.drawMultiPassOutline(x, y, width, totalPanelHeight, 5f, 1.2f, 0xFF3A3D4D.toInt(), ClientTheme.accentLeft and 0x33FFFFFF)

        val count = visibleModules().size
        fontRenderer.drawString("outfit", "${category.name} ($count)", x + 10f, y + 5f, 12f, 0xFFFFFFFF.toInt())

        if (eased > 0.01f && bodyHeight > 0f) {
            val bodyY = y + height
            vectorRenderer.drawRoundedRect(x, bodyY, width, bodyHeight, 0f, 0xCC111215.toInt())
            for (child in children) {
                val cy = child.y
                if (cy + child.height < bodyY || cy > bodyY + bodyHeight) continue
                child.render(vectorRenderer, fontRenderer, mouseX, mouseY, partialTicks)
            }
            if (bodyContentH > maxBodyHeight) {
                val scrollPct = if (bodyContentH - maxBodyHeight > 0f) scrollOffset / (bodyContentH - maxBodyHeight) else 0f
                val barH = (maxBodyHeight * (maxBodyHeight / bodyContentH)).coerceAtLeast(20f)
                val barY = bodyY + scrollPct * (bodyHeight - barH)
                vectorRenderer.drawRoundedRect(x + width - 3f, barY, 2f, barH, 1f, 0x6600C6FF)
            }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if (button == 0) {
                isDragging = true
                dragX = mouseX - x
                dragY = mouseY - y
                return true
            } else if (button == 1) {
                isExpanded = !isExpanded
                return true
            }
        }

        if (isExpanded && expansionProgress > 0.9f) {
            layoutChildren()
            for (child in children) {
                if (child.mouseClicked(mouseX, mouseY, button)) return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0) isDragging = false
        children.forEach { it.mouseReleased(mouseX, mouseY, button) }
        return false
    }

    override fun keyTyped(keyCode: Int): Boolean {
        for (child in children) if (child.keyTyped(keyCode)) return true
        return false
    }
}
