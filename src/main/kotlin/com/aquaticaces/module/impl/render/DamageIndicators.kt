package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList

/**
 * DamageIndicators module.
 * Tracks health changes of nearby living entities and displays floating 3D text in world space.
 */
class DamageIndicators : Module("DamageIndicators", "Displays floating damage indicators in 3D.", Category.RENDER) {

    private class Indicator(var pos: Vec3, val text: String, var age: Int)

    private val indicators = CopyOnWriteArrayList<Indicator>()
    private val lastHealthMap = mutableMapOf<Int, Float>()

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        // 1. Scan and detect health changes
        val currentEntities = mutableSetOf<Int>()
        for (entity in level.entitiesForRendering()) {
            if (entity === player || entity !is LivingEntity || !entity.isAlive) continue
            currentEntities.add(entity.id)

            val currentHealth = entity.health + entity.absorptionAmount
            val lastHealth = lastHealthMap[entity.id]

            if (lastHealth != null) {
                if (currentHealth < lastHealth) {
                    val diff = lastHealth - currentHealth
                    val text = String.format("-%.1f", diff)
                    val spawnPos = Vec3(
                        entity.x,
                        entity.boundingBox.maxY + 0.2,
                        entity.z
                    )
                    indicators.add(Indicator(spawnPos, text, 0))
                }
            }
            lastHealthMap[entity.id] = currentHealth
        }

        // Keep map key elements synchronized
        lastHealthMap.keys.retainAll(currentEntities)

        // 2. Move and update active indicator lifetimes
        val iterator = indicators.iterator()
        while (iterator.hasNext()) {
            val ind = iterator.next()
            ind.age++
            ind.pos = ind.pos.add(0.0, 0.03, 0.0) // Rise slowly upwards
            if (ind.age > 30) {
                indicators.remove(ind)
            }
        }
    }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        val camera = mc.gameRenderer.mainCamera
        val cameraPos = camera.position
        val font = mc.font
        val bufferSource = mc.renderBuffers().bufferSource()
        val poseStack = event.poseStack

        for (ind in indicators) {
            poseStack.pushPose()

            // Translate position relative to camera origin coordinates
            val tx = ind.pos.x - cameraPos.x
            val ty = ind.pos.y - cameraPos.y
            val tz = ind.pos.z - cameraPos.z
            poseStack.translate(tx, ty, tz)

            // Billboard rotation so text always faces viewer
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-camera.yRot))
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.xRot))

            // Scale down text size to fit block space
            poseStack.scale(-0.025f, -0.025f, 0.025f)

            // Render damage number in bright red
            font.drawInBatch(
                ind.text,
                -font.width(ind.text) / 2f,
                0f,
                0xFFFF3333.toInt(),
                true,
                poseStack.last().pose(),
                bufferSource,
                net.minecraft.client.gui.Font.DisplayMode.NORMAL,
                0,
                15728880
            )

            poseStack.popPose()
        }
    }

    override fun onDisable() {
        super.onDisable()
        indicators.clear()
        lastHealthMap.clear()
    }
}
