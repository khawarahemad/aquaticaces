package com.aquaticaces.module.impl.render

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventRender3D
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.impl.render.RenderUtil
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class LogoutSpot : Module("LogoutSpot", "Marks where players logged out.", Category.RENDER) {

    private data class Spot(val name: String, val pos: Vec3, val time: Long)

    private val spots = ConcurrentHashMap<String, Spot>()
    private val tracked = ConcurrentHashMap<UUID, Pair<String, Vec3>>()

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val level = mc.level ?: return
        val seen = HashSet<UUID>()
        for (player in level.players()) {
            if (player != mc.player) {
                seen.add(player.uuid)
                tracked[player.uuid] = player.name.string to player.position()
            }
        }
        // Any tracked player that vanished this tick was a logout.
        val gone = tracked.keys.filter { it !in seen }
        for (uuid in gone) {
            tracked.remove(uuid)?.let { (name, pos) -> mark(name, pos) }
        }
    }

    @Subscribe
    fun onRender3D(event: EventRender3D) {
        if (!canRun()) return
        val cam = mc.gameRenderer.mainCamera.position
        val now = System.currentTimeMillis()
        val expired = mutableListOf<String>()
        RenderUtil.begin(2f)
        val fill = RenderUtil.quads()
        val outline = RenderUtil.lines()
        for ((key, spot) in spots) {
            if (now - spot.time > 120_000L) { expired.add(key); continue }
            val box = AABB(spot.pos.x - 0.3, spot.pos.y, spot.pos.z - 0.3,
                spot.pos.x + 0.3, spot.pos.y + 1.8, spot.pos.z + 0.3)
                .move(-cam.x, -cam.y, -cam.z)
            RenderUtil.addFilledBox(fill, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ,
                1f, 0.27f, 0.4f, 0.35f, 0.05f)
            RenderUtil.addBoxOutline(outline, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ,
                1f, 0.27f, 0.4f, 1f)
        }
        RenderUtil.draw(fill)
        RenderUtil.draw(outline)
        RenderUtil.end()
        expired.forEach { spots.remove(it) }
    }

    fun mark(name: String, pos: Vec3) {
        spots[name] = Spot(name, pos, System.currentTimeMillis())
    }
}
