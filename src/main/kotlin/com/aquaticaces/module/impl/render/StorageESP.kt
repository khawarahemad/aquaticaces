package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import com.aquaticaces.module.setting.NumberSetting
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BarrelBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.EnderChestBlockEntity
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity

/**
 * StorageESP — highlights storage containers with a filled gradient box and a
 * glowing outline. Different container types are tinted for quick reading.
 */
class StorageESP : Module("StorageESP", "Highlights storage containers.", Category.RENDER) {
    val chestColor = ColorSetting("Chest", 0xFFFFB000.toInt())
    val shulkerColor = ColorSetting("Shulker", 0xFFA97BFF.toInt())
    val otherColor = ColorSetting("Other", 0xFF00C6FF.toInt())
    val range = NumberSetting("Range", 32.0, 8.0, 64.0, 4.0)
    init { addSettings(chestColor, shulkerColor, otherColor, range) }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        val cam = mc.gameRenderer.mainCamera.position
        val rng = range.value.toInt()
        val origin = BlockPos.containing(player.position())

        data class Hit(val pos: BlockPos, val argb: Int)
        val hits = ArrayList<Hit>()
        for (x in -rng..rng) for (y in -rng..rng) for (z in -rng..rng) {
            val pos = origin.offset(x, y, z)
            val be: BlockEntity = level.getBlockEntity(pos) ?: continue
            val argb = colorFor(be) ?: continue
            hits.add(Hit(pos, argb))
        }
        if (hits.isEmpty()) return

        RenderUtil.begin(2f)

        val fill = RenderUtil.quads()
        for (h in hits) {
            val r = RenderUtil.red(h.argb); val g = RenderUtil.green(h.argb); val b = RenderUtil.blue(h.argb)
            val x1 = h.pos.x - cam.x; val y1 = h.pos.y - cam.y; val z1 = h.pos.z - cam.z
            RenderUtil.addFilledBox(fill, x1 + 0.02, y1, z1 + 0.02, x1 + 0.98, y1 + 0.92, z1 + 0.98, r, g, b, 0.26f, 0.05f)
        }
        RenderUtil.draw(fill)

        val outline = RenderUtil.lines()
        for (h in hits) {
            val r = RenderUtil.red(h.argb); val g = RenderUtil.green(h.argb); val b = RenderUtil.blue(h.argb)
            val x1 = h.pos.x - cam.x; val y1 = h.pos.y - cam.y; val z1 = h.pos.z - cam.z
            RenderUtil.addBoxOutline(outline, x1 + 0.02, y1, z1 + 0.02, x1 + 0.98, y1 + 0.92, z1 + 0.98, r, g, b, 0.95f)
        }
        RenderUtil.draw(outline)

        RenderUtil.end()
    }

    private fun colorFor(be: BlockEntity): Int? = when (be) {
        is ChestBlockEntity, is BarrelBlockEntity -> chestColor.value
        is ShulkerBoxBlockEntity -> shulkerColor.value
        is EnderChestBlockEntity, is HopperBlockEntity -> otherColor.value
        else -> null
    }
}
