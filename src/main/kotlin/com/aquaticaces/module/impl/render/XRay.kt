package com.aquaticaces.module.impl.render

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import com.aquaticaces.module.setting.BooleanSetting
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import org.lwjgl.glfw.GLFW

/**
 * XRay — hides non-target blocks and optionally boosts brightness so ores,
 * storage and other targets stand out underground.
 */
class XRay : Module("XRay", "Reveals selected blocks through solid stone.", Category.RENDER, GLFW.GLFW_KEY_X) {

    val overworldOres = BooleanSetting("Overworld Ores", true)
    val deepslateOres = BooleanSetting("Deepslate Ores", true)
    val netherOres = BooleanSetting("Nether Ores", true)
    val rawBlocks = BooleanSetting("Raw Ore Blocks", true)
    val storage = BooleanSetting("Storage", true)
    val spawners = BooleanSetting("Spawners", true)
    val fluids = BooleanSetting("Fluids", true)
    val fullBright = BooleanSetting("FullBright", true)

    init { addSettings(overworldOres, deepslateOres, netherOres, rawBlocks, storage, spawners, fluids, fullBright) }

    companion object {
        private val overworld = setOf(
            Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE,
            Blocks.LAPIS_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.COPPER_ORE
        )
        private val deepslate = setOf(
            Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_COPPER_ORE
        )
        private val nether = setOf(
            Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ANCIENT_DEBRIS
        )
        private val raw = setOf(
            Blocks.RAW_IRON_BLOCK, Blocks.RAW_GOLD_BLOCK, Blocks.RAW_COPPER_BLOCK
        )
        private val storageBlocks = setOf(
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.BARREL,
            Blocks.SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX,
            Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.DISPENSER, Blocks.DROPPER,
            Blocks.HOPPER, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER
        )
        private val fluidBlocks = setOf(Blocks.LAVA, Blocks.WATER)

        @JvmStatic
        fun isXrayEnabled(): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("XRay")
            return module != null && module.isEnabled
        }

        @JvmStatic
        fun useFullBright(): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("XRay") as? XRay
            val xrayFb = module != null && module.isEnabled && module.fullBright.value
            val fb = com.aquaticaces.module.ModuleManager.getModuleByName("Fullbright")?.isEnabled == true
            return xrayFb || fb
        }

        @JvmStatic
        fun shouldRender(block: Block): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("XRay") as? XRay ?: return false
            if (!module.isEnabled) return false
            if (module.overworldOres.value && block in overworld) return true
            if (module.deepslateOres.value && block in deepslate) return true
            if (module.netherOres.value && block in nether) return true
            if (module.rawBlocks.value && block in raw) return true
            if (module.storage.value && block in storageBlocks) return true
            if (module.spawners.value && block == Blocks.SPAWNER) return true
            if (module.fluids.value && block in fluidBlocks) return true
            return false
        }
    }

    override fun onEnable() {
        super.onEnable()
        mc.levelRenderer.allChanged()
    }

    override fun onDisable() {
        super.onDisable()
        mc.levelRenderer.allChanged()
    }
}
