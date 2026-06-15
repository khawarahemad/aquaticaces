package com.aquaticaces.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.state.BlockState
import java.io.File

@Serializable
data class SchematicBlock(val x: Int, val y: Int, val z: Int, val block: String)

@Serializable
data class SchematicFile(
    val name: String,
    val originX: Int = 0,
    val originY: Int = 0,
    val originZ: Int = 0,
    val blocks: MutableList<SchematicBlock> = mutableListOf()
)

object SchematicManager {
    var active: SchematicFile? = null
        private set

    private val dir: File
        get() = File(Minecraft.getInstance().gameDirectory, "aquaticaces/schematics")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun init() { dir.mkdirs() }

    fun capture(name: String, center: BlockPos, radius: Int): SchematicFile {
        val level = Minecraft.getInstance().level ?: return SchematicFile(name)
        val blocks = mutableListOf<SchematicBlock>()
        for (x in -radius..radius) for (y in -radius..radius) for (z in -radius..radius) {
            val pos = center.offset(x, y, z)
            val state = level.getBlockState(pos)
            if (state.isAir) continue
            val id = BuiltInRegistries.BLOCK.getKey(state.block).toString()
            blocks.add(SchematicBlock(x, y, z, id))
        }
        val schematic = SchematicFile(name, center.x, center.y, center.z, blocks)
        save(schematic)
        active = schematic
        NotificationManager.success("Schematic", "Saved $name (${blocks.size} blocks)")
        return schematic
    }

    fun load(name: String): Boolean {
        val file = File(dir, "$name.json")
        if (!file.exists()) return false
        active = json.decodeFromString<SchematicFile>(file.readText())
        NotificationManager.success("Schematic", "Loaded $name")
        return true
    }

    fun save(schematic: SchematicFile) {
        dir.mkdirs()
        File(dir, "${schematic.name}.json").writeText(json.encodeToString(schematic))
    }

    fun clear() {
        active = null
    }

    fun list(): List<String> = dir.listFiles()?.map { it.nameWithoutExtension }?.sorted() ?: emptyList()

    fun worldPos(entry: SchematicBlock): BlockPos {
        val s = active ?: return BlockPos.ZERO
        return BlockPos(s.originX + entry.x, s.originY + entry.y, s.originZ + entry.z)
    }

    fun previewOffset(entry: SchematicBlock, previewOrigin: BlockPos): BlockPos =
        previewOrigin.offset(entry.x, entry.y, entry.z)

    fun blockState(entry: SchematicBlock): BlockState? {
        val id = net.minecraft.resources.ResourceLocation.tryParse(entry.block) ?: return null
        val block = BuiltInRegistries.BLOCK[id] ?: return null
        return block.defaultBlockState()
    }
}
