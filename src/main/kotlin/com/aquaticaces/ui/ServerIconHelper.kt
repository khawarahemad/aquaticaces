package com.aquaticaces.ui

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation

/** Caches and draws multiplayer server favicons from ping responses. */
object ServerIconHelper {
    private val mc get() = Minecraft.getInstance()
    private val cache = mutableMapOf<String, ResourceLocation>()

    fun draw(g: GuiGraphics, data: ServerData, x: Int, y: Int, size: Int, fallbackLetter: String, font: net.minecraft.client.gui.Font) {
        val key = data.ip
        val bytes = data.iconBytes
        if (bytes != null && bytes.isNotEmpty()) {
            val tex = cache.getOrPut(key) {
                try {
                    val img = NativeImage.read(bytes)
                    val id = ResourceLocation.fromNamespaceAndPath("aquaticaces", "server_icon/${key.hashCode()}")
                    mc.textureManager.register(id, DynamicTexture(img))
                    id
                } catch (_: Exception) {
                    cache.remove(key)
                    return drawFallback(g, x, y, size, fallbackLetter, font)
                }
            }
            try {
                g.setColor(1f, 1f, 1f, 1f)
                g.blit(tex, x, y, 0f, 0f, size, size, size, size)
                UiStyle.outline(g, x, y, x + size, y + size, UiStyle.BORDER)
                return
            } catch (_: Exception) {
                cache.remove(key)
            }
        }
        drawFallback(g, x, y, size, fallbackLetter, font)
    }

    private fun drawFallback(g: GuiGraphics, x: Int, y: Int, size: Int, letter: String, font: net.minecraft.client.gui.Font) {
        g.fillGradient(x, y, x + size, y + size, UiStyle.ACCENT, UiStyle.ACCENT_2)
        val ch = letter.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        g.drawCenteredString(font, ch, x + size / 2, y + size / 2 - 4, 0xFF02030A.toInt())
        UiStyle.outline(g, x, y, x + size, y + size, UiStyle.BORDER)
    }

    fun clear() { cache.clear() }
}
