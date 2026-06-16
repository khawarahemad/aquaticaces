package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.ModeSetting
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

/**
 * ESP — draws filled, glowing 3D boxes around entities with a vertical fade
 * and a bright outline, matching the Aquatic Aces accent styling.
 */
class ESP : Module("ESP", "Renders glowing boxes around entities through walls.", Category.RENDER) {

    val mode = ModeSetting("Mode", "Filled", listOf("Filled", "Outline"))
    val targets = ModeSetting("Targets", "Players", listOf("Players", "Living", "All"))
    val color = ColorSetting("Color", 0xFF00C6FF.toInt())
    val expand = BooleanSetting("Expand", true)

    init {
        addSettings(mode, targets, color, expand)
    }

    private fun shouldRender(entity: net.minecraft.world.entity.Entity, player: Player): Boolean {
        if (entity === player || !entity.isAlive) return false
        return when (targets.value) {
            "Players" -> entity is Player
            "Living" -> entity is LivingEntity
            else -> true
        }
    }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        val player = mc.player ?: return
        val level = mc.level ?: return
        val cam = mc.gameRenderer.mainCamera.position

        val r = RenderUtil.red(color.value)
        val g = RenderUtil.green(color.value)
        val b = RenderUtil.blue(color.value)

        val pad = if (expand.value) 0.08 else 0.0

        RenderUtil.begin(2.5f)

        if (mode.value == "Filled") {
            val fill = RenderUtil.quads()
            for (entity in level.entitiesForRendering()) {
                if (!shouldRender(entity, player)) continue
                val box = entity.boundingBox.move(-cam.x, -cam.y, -cam.z).inflate(pad)
                RenderUtil.addFilledBox(fill, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, 0.32f, 0.04f)
            }
            RenderUtil.draw(fill)
        }

        val outline = RenderUtil.lines()
        for (entity in level.entitiesForRendering()) {
            if (!shouldRender(entity, player)) continue
            val box = entity.boundingBox.move(-cam.x, -cam.y, -cam.z).inflate(pad)
            RenderUtil.addBoxOutline(outline, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, 1f)
        }
        RenderUtil.draw(outline)

        RenderUtil.end()
    }
}
