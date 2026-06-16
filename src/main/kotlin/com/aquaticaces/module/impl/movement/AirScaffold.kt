package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.impl.utility.BlockPlacer
import com.aquaticaces.module.setting.BooleanSetting
import com.aquaticaces.module.setting.ModeSetting
import com.aquaticaces.module.setting.NumberSetting
import com.aquaticaces.util.PacketUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

/**
 * Places blocks underfoot and ahead while moving — bridges even when airborne.
 */
class AirScaffold : Module("AirScaffold", "Auto-places blocks while walking in the air.", Category.MOVEMENT) {

    val mode = ModeSetting("Mode", "Extend", listOf("Underfoot", "Extend", "Both"))
    val onlyInAir = BooleanSetting("OnlyInAir", true)
    val extendBlocks = NumberSetting("Extend", 1.0, 1.0, 2.0, 1.0)
    val rotations = BooleanSetting("Rotations", true)

    private var silentYaw = 0f
    private var silentPitch = 0f
    private var hasRotations = false

    init {
        addSettings(mode, onlyInAir, extendBlocks, rotations)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE || !canRun()) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        if (onlyInAir.value && player.onGround()) {
            hasRotations = false
            return
        }

        if (BlockPlacer.findBlockHand(player) == null) {
            hasRotations = false
            return
        }

        val positions = mutableListOf<BlockPos>()
        val underfoot = BlockPos.containing(player.x, player.y - 1.0, player.z)
        if (mode.value == "Underfoot" || mode.value == "Both") {
            positions.add(underfoot)
        }
        if (mode.value == "Extend" || mode.value == "Both") {
            val steps = extendBlocks.value.toInt().coerceIn(1, 2)
            for (i in 1..steps) {
                positions.add(predictAhead(player, i.toDouble()))
            }
        }

        for (pos in positions.distinct()) {
            if (!level.getBlockState(pos).isAir) continue
            if (prepareRotations(pos)) {
                if (BlockPlacer.placeAt(level, player, pos)) break
            }
        }
    }

    private fun predictAhead(player: net.minecraft.world.entity.player.Player, steps: Double): BlockPos {
        val yawRad = Math.toRadians(player.yRot.toDouble())
        val forwardX = -sin(yawRad) * steps
        val forwardZ = cos(yawRad) * steps
        val motionX = player.deltaMovement.x
        val motionZ = player.deltaMovement.z
        val useMotion = kotlin.math.abs(motionX) > 0.03 || kotlin.math.abs(motionZ) > 0.03
        val x = if (useMotion) player.x + motionX * steps else player.x + forwardX
        val z = if (useMotion) player.z + motionZ * steps else player.z + forwardZ
        return BlockPos.containing(x, player.y - 1.0, z)
    }

    private fun prepareRotations(targetPos: BlockPos): Boolean {
        val player = mc.player ?: return false
        val level = mc.level ?: return false
        if (!rotations.value) return true

        var against: BlockPos? = null
        var face: Direction? = null
        for (dir in Direction.entries) {
            val adj = targetPos.relative(dir)
            if (!level.getBlockState(adj).isAir) {
                against = adj
                face = dir.opposite
                break
            }
        }
        val solid = against ?: return false
        val direction = face ?: return false

        val hitVec = Vec3(
            solid.x + 0.5 + direction.stepX * 0.5,
            solid.y + 0.5 + direction.stepY * 0.5,
            solid.z + 0.5 + direction.stepZ * 0.5
        )
        val eyePos = player.eyePosition
        val diff = hitVec.subtract(eyePos)
        val diffXZ = kotlin.math.sqrt(diff.x * diff.x + diff.z * diff.z)
        silentYaw = (Math.toDegrees(kotlin.math.atan2(diff.z, diff.x)).toFloat() - 90f)
        silentPitch = (-Math.toDegrees(kotlin.math.atan2(diff.y, diffXZ))).toFloat().coerceIn(-90f, 90f)
        hasRotations = true
        return true
    }

    @Subscribe
    fun onPacketSend(event: EventPacketSend) {
        val packet = event.packet
        if (packet is ServerboundMovePlayerPacket && rotations.value && hasRotations) {
            PacketUtils.setRotations(packet, silentYaw, silentPitch)
        }
    }

    override fun onDisable() {
        super.onDisable()
        hasRotations = false
    }
}
