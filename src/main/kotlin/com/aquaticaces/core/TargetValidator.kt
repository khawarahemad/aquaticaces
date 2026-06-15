package com.aquaticaces.core

import com.aquaticaces.module.impl.combat.AntiBot
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object TargetValidator {
    private val mc get() = Minecraft.getInstance()

    fun isValidCombatTarget(entity: LivingEntity, playersOnly: Boolean = true, respectFriends: Boolean = true): Boolean {
        val player = mc.player ?: return false
        if (entity === player || !entity.isAlive) return false
        if (playersOnly && entity !is Player) return false
        if (respectFriends && entity is Player && FriendsManager.isFriend(entity.gameProfile.name)) return false
        if (AntiBot.isBot(entity)) return false
        if (entity is Player && entity.isCreative) return false
        return true
    }
}
