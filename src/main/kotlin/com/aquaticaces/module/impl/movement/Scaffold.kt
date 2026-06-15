package com.aquaticaces.module.impl.movement

import com.aquaticaces.event.Subscribe
import com.aquaticaces.event.impl.EventClientTick
import com.aquaticaces.event.impl.EventPacketSend
import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import com.aquaticaces.util.PacketUtils
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.BlockItem
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Scaffold module.
 * Automatically places blocks under the player's feet when stepping off ledges,
 * utilizing client-side "Silent Rotations" to face placement vectors.
 */
class Scaffold : Module("Scaffold", "Places blocks perfectly underfoot.", Category.MOVEMENT) {

    val rotations = BooleanSetting("Rotations", true)

    private var silentYaw = 0f
    private var silentPitch = 0f
    private var hasRotations = false

    init {
        addSettings(rotations)
    }

    @Subscribe
    fun onTick(event: EventClientTick) {
        if (event.phase != EventClientTick.Phase.PRE) return
        val player = mc.player ?: return
        val level = mc.level ?: return

        // 1. Get position directly under player's feet
        val targetPos = BlockPos.containing(player.x, player.y - 1.0, player.z)
        if (!level.getBlockState(targetPos).isAir) {
            hasRotations = false
            return
        }

        // 2. Find solid block adjacent to target block
        var blockToPlaceAgainst: BlockPos? = null
        var placeDirection: Direction? = null

        for (dir in Direction.values()) {
            val adj = targetPos.relative(dir)
            if (!level.getBlockState(adj).isAir) {
                blockToPlaceAgainst = adj
                placeDirection = dir.opposite
                break
            }
        }

        val solidBlock = blockToPlaceAgainst ?: return
        val face = placeDirection ?: return

        // 3. Compute hit vector
        val hitVec = Vec3(
            solidBlock.x + 0.5 + face.stepX * 0.5,
            solidBlock.y + 0.5 + face.stepY * 0.5,
            solidBlock.z + 0.5 + face.stepZ * 0.5
        )

        // 4. Calculate silent rotations
        if (rotations.value) {
            val eyePos = player.eyePosition
            val diffX = hitVec.x - eyePos.x
            val diffY = hitVec.y - eyePos.y
            val diffZ = hitVec.z - eyePos.z
            val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)

            silentYaw = (Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f)
            silentPitch = (-Math.toDegrees(atan2(diffY, diffXZ))).toFloat().coerceIn(-90f, 90f)
            hasRotations = true
        }

        // 5. Select block in hand
        var blockHand: InteractionHand? = null
        if (player.mainHandItem.item is BlockItem) {
            blockHand = InteractionHand.MAIN_HAND
        } else if (player.offhandItem.item is BlockItem) {
            blockHand = InteractionHand.OFF_HAND
        }

        val hand = blockHand ?: return // Requires a block in either hand

        // 6. Send block placement packet
        val hitResult = BlockHitResult(hitVec, face, solidBlock, false)
        val placePacket = ServerboundUseItemOnPacket(hand, hitResult, 0)
        player.connection.send(placePacket)
        player.swing(hand)
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
