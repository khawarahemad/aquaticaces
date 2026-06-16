package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

/**
 * BlockESP — highlights valuable blocks with a filled gradient box and a
 * glowing outline, matching the entity ESP styling.
 */
class BlockESP : Module("BlockESP", "Highlights valuable blocks.", Category.RENDER) {
    val color = ColorSetting("Color", 0xFFFFD700.toInt())
    val range = NumberSetting("Range", 32.0, 8.0, 64.0, 4.0)
    init { addSettings(color, range) }

    private val targets = setOf(
        Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
        Blocks.ANCIENT_DEBRIS, Blocks.CHEST, Blocks.TRAPPED_CHEST,
        Blocks.SPAWNER, Blocks.END_PORTAL_FRAME, Blocks.EMERALD_ORE,
        Blocks.DEEPSLATE_EMERALD_ORE
    )

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        val cam = mc.gameRenderer.mainCamera.position
        val radius = range.value.toInt()
        val origin = BlockPos.containing(player.position())

        val r = RenderUtil.red(color.value)
        val g = RenderUtil.green(color.value)
        val b = RenderUtil.blue(color.value)

        val found = ArrayList<BlockPos>()
        for (x in -radius..radius) for (y in -radius..radius) for (z in -radius..radius) {
            val pos = origin.offset(x, y, z)
            if (targets.contains(level.getBlockState(pos).block)) found.add(pos)
        }
        if (found.isEmpty()) return

        RenderUtil.begin(2f)

        val fill = RenderUtil.quads()
        for (pos in found) {
            val x1 = pos.x - cam.x; val y1 = pos.y - cam.y; val z1 = pos.z - cam.z
            RenderUtil.addFilledBox(fill, x1, y1, z1, x1 + 1, y1 + 1, z1 + 1, r, g, b, 0.28f, 0.05f)
        }
        RenderUtil.draw(fill)

        val outline = RenderUtil.lines()
        for (pos in found) {
            val x1 = pos.x - cam.x; val y1 = pos.y - cam.y; val z1 = pos.z - cam.z
            RenderUtil.addBoxOutline(outline, x1, y1, z1, x1 + 1, y1 + 1, z1 + 1, r, g, b, 0.95f)
        }
        RenderUtil.draw(outline)

        RenderUtil.end()
    }
}
