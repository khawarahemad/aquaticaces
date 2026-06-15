package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.ColorSetting
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity
import net.minecraft.world.level.block.entity.BarrelBlockEntity

class StorageESP : Module("StorageESP", "Highlights storage containers.", Category.RENDER) {
    val color = ColorSetting("Color", 0xFFAA00FF.toInt())
    val range = com.aquaticaces.module.setting.NumberSetting("Range", 32.0, 8.0, 64.0, 4.0)
    init { addSettings(color, range) }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return
        val cam = mc.gameRenderer.mainCamera.position
        val r = range.value.toInt()
        val origin = BlockPos.containing(player.position())
        for (x in -r..r) for (y in -r..r) for (z in -r..r) {
            val pos = origin.offset(x, y, z)
            val be: BlockEntity = level.getBlockEntity(pos) ?: continue
            if (!isStorage(be)) continue
            val sx = (pos.x + 0.5 - cam.x).toFloat()
            val sy = (pos.y + 1.2 - cam.y).toFloat()
            val sz = (pos.z + 0.5 - cam.z).toFloat()
            event.poseStack.pushPose()
            event.poseStack.translate(sx.toDouble(), sy.toDouble(), sz.toDouble())
            mc.font.drawInBatch("§d[Storage]", -12f, 0f, color.value, false,
                event.poseStack.last().pose(), mc.renderBuffers().bufferSource(),
                net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 15728880)
            event.poseStack.popPose()
        }
    }

    private fun isStorage(be: BlockEntity): Boolean =
        be is ChestBlockEntity || be is ShulkerBoxBlockEntity || be is BarrelBlockEntity
}
