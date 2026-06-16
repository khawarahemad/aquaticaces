package com.aquaticaces.ui

import com.aquaticaces.AquaticAces
import com.aquaticaces.core.ClientTheme
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL
import kotlin.math.sin

/**
 * Shared NanoVG UI engine — LiquidBounce-style blur, panels, and menu chrome.
 */
object UiEngine {
    val vectorRenderer = VectorRenderer()
    val fontRenderer = FontRenderer(vectorRenderer)
    val shaderManager = ShaderManager()

    private var initialized = false
    private var disabledReason: String? = null
    var fontName: String = "sans"
        private set

    var searchQuery: String = ""

    @JvmStatic
    fun ensureInitialized(): Boolean {
        if (initialized) return true
        disabledReason?.let { return false }
        if (!isGlReady()) return false

        try {
            vectorRenderer.init()
            shaderManager.init()
            fontName = try {
                fontRenderer.loadFont("outfit", "/assets/aquaticaces/font/outfit.ttf")
                "outfit"
            } catch (e: Exception) {
                AquaticAces.logger.warn("Custom font unavailable, using NanoVG sans: ${e.message}")
                "sans"
            }
            initialized = true
            return true
        } catch (t: Throwable) {
            disabledReason = t.message ?: t.javaClass.simpleName
            AquaticAces.logger.error("NanoVG UI disabled; falling back to Minecraft UI renderer.", t)
            return false
        }
    }

    fun isAvailable(): Boolean = initialized

    private fun isGlReady(): Boolean {
        return try {
            GL.getCapabilities() != null
        } catch (_: Throwable) {
            false
        }
    }

    fun drawAnimatedBackground(vr: VectorRenderer, width: Float, height: Float, tick: Float) {
        vr.drawLinearGradientRect(0f, 0f, width, height, 0f, 0f, 0f, 0f, height, UiStyle.BG_TOP, UiStyle.BG_BOT)

        val pulse = (sin(tick * 0.04f) * 0.5f + 0.5f)
        val glowAlpha = (0x18 + pulse * 0x20).toInt()
        vr.drawRoundedRect(width * 0.15f, height * 0.08f, width * 0.7f, height * 0.84f, 18f, (glowAlpha shl 24) or 0x0072FF)

        val lineY = height * 0.18f + sin(tick * 0.06f) * 6f
        vr.drawLinearGradientRect(
            width * 0.2f, lineY, width * 0.6f, 2f, 0f,
            0f, 0f, width * 0.6f, 0f,
            0xFF00C6FF.toInt(), ClientTheme.accentRight
        )
    }

    fun drawTopBar(fr: FontRenderer, width: Float, subtitle: String) {
        fr.drawString(fontName, "AQUATIC ACES", 14f, 10f, 14f, 0xFF00C6FF.toInt())
        fr.drawString(fontName, subtitle, 14f, 26f, 9f, 0xFF9898A6.toInt())
        val hint = if (searchQuery.isBlank()) "Search modules..." else "Search: $searchQuery"
        fr.drawString(fontName, hint, width - 170f, 16f, 9f, 0xFF666677.toInt())
    }

    fun beginFrame(mc: Minecraft) {
        val scale = mc.window.guiScale.toFloat()
        vectorRenderer.begin(mc.window.guiScaledWidth.toFloat(), mc.window.guiScaledHeight.toFloat(), scale)
    }

    fun endFrame() = vectorRenderer.end()

    fun renderBlur(strength: Float) {
        if (initialized && strength > 0.01f) shaderManager.renderBlur(strength)
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            if (initialized) {
                shaderManager.cleanup()
                vectorRenderer.cleanup()
            }
        })
    }
}
