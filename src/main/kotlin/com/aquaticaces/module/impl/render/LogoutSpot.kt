package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.phys.Vec3
import java.util.concurrent.ConcurrentHashMap

class LogoutSpot : Module("LogoutSpot", "Marks where players logged out.", Category.RENDER) {
    private val spots = ConcurrentHashMap<String, Vec3>()

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun()) return
        val cam = mc.gameRenderer.mainCamera.position
        for ((name, pos) in spots) {
            event.poseStack.pushPose()
            event.poseStack.translate(pos.x - cam.x, pos.y - cam.y + 1.5, pos.z - cam.z)
            mc.font.drawInBatch("§cLogout: $name", 0f, 0f, 0xFFFF5555.toInt(), false,
                event.poseStack.last().pose(), mc.renderBuffers().bufferSource(),
                net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 15728880)
            event.poseStack.popPose()
        }
    }

    fun mark(name: String, pos: Vec3) { spots[name] = pos }
}
