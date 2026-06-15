package com.aquaticaces.module.impl.render

import com.aquaticaces.module.Category
import com.aquaticaces.module.Module
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import org.lwjgl.glfw.GLFW

/**
 * XRay module.
 * Alters block occlusion/face rendering algorithms to highlight valuable mining targets.
 */
class XRay : Module("XRay", "Reveals ores through solid blocks.", Category.RENDER, GLFW.GLFW_KEY_X) {

    companion object {
        private val ores = setOf(
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_GOLD_ORE,
            Blocks.ANCIENT_DEBRIS, Blocks.LAVA, Blocks.WATER
        )

        @JvmStatic
        fun isXrayEnabled(): Boolean {
            val module = com.aquaticaces.module.ModuleManager.getModuleByName("XRay")
            return module != null && module.isEnabled
        }

        @JvmStatic
        fun shouldRender(block: Block): Boolean {
            return ores.contains(block)
        }
    }

    override fun onEnable() {
        super.onEnable()
        // Reload all chunks to re-evaluate block faces with XRay enabled
        mc.levelRenderer.allChanged()
    }

    override fun onDisable() {
        super.onDisable()
        // Reload all chunks to restore normal rendering
        mc.levelRenderer.allChanged()
    }
}
