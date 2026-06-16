package com.aquaticaces.module

import com.aquaticaces.module.impl.combat.*
import com.aquaticaces.module.impl.movement.*
import com.aquaticaces.module.impl.render.*
import com.aquaticaces.module.impl.world.*
import com.aquaticaces.module.impl.exploit.*
import com.aquaticaces.module.impl.player.*
import com.aquaticaces.module.impl.utility.*
import com.aquaticaces.module.impl.ghost.*
import java.util.concurrent.CopyOnWriteArrayList

object ModuleManager {
    val modules = CopyOnWriteArrayList<Module>()

    init {
        // COMBAT
        modules.add(AntiBot())
        modules.add(Backtrack())
        modules.add(KillAura())
        modules.add(WallHit())
        modules.add(TriggerBot())
        modules.add(AimAssist())
        modules.add(SilentAim())
        modules.add(AutoCrystal())
        modules.add(AutoAnchor())
        modules.add(BedAura())
        modules.add(Criticals())
        modules.add(Velocity())
        modules.add(KeepSprint())
        modules.add(ShieldBreaker())
        modules.add(WTap())
        modules.add(AutoPot())
        modules.add(NoSwing())
        modules.add(Surround())
        modules.add(HoleFill())

        // MOVEMENT
        modules.add(Sprint())
        modules.add(Flight())
        modules.add(Speed())
        modules.add(Step())
        modules.add(Scaffold())
        modules.add(AirScaffold())
        modules.add(Jesus())
        modules.add(NoSlowdown())
        modules.add(Blink())
        modules.add(NoFall())
        modules.add(SafeWalk())
        modules.add(Parkour())
        modules.add(Spider())
        modules.add(LongJump())
        modules.add(ElytraFly())

        // RENDER
        modules.add(ESP())
        modules.add(Chams())
        modules.add(XRay())
        modules.add(Tracers())
        modules.add(Nametags())
        modules.add(Freecam())
        modules.add(ClickGUIModule())
        modules.add(DamageIndicators())
        modules.add(Fullbright())
        modules.add(Zoom())
        modules.add(BlockESP())
        modules.add(StorageESP())
        modules.add(LogoutSpot())
        modules.add(Breadcrumbs())
        modules.add(Waypoints())
        modules.add(ViewModel())

        // WORLD
        modules.add(Timer())
        modules.add(FastPlace())
        modules.add(GhostHand())
        modules.add(LiquidInteract())
        modules.add(SchematicPreview())
        modules.add(SymmetryBrush())

        // EXPLOIT
        modules.add(Phase())
        modules.add(TickShift())

        // PLAYER
        modules.add(InventoryManager())
        modules.add(AutoEat())
        modules.add(Derp())
        modules.add(AutoTool())
        modules.add(AutoArmor())
        modules.add(FastUse())
        modules.add(MiddleClickFriend())

        // UTILITY
        modules.add(AutoTotem())
        modules.add(ChestStealer())
        modules.add(AutoReconnect())
        modules.add(PingSpoof())
        modules.add(HudEditorModule())

        // GHOST
        modules.add(SelfDestruct())
        modules.add(Hitboxes())
        modules.add(Reach())
        modules.add(AimBot())
    }

    fun getModuleByName(name: String): Module? =
        modules.firstOrNull { it.name.equals(name, ignoreCase = true) }

    fun getModulesByCategory(category: Category): List<Module> =
        modules.filter { it.category == category }

    inline fun forEachEnabled(action: (Module) -> Unit) {
        for (module in modules) if (module.isEnabled) action(module)
    }
}
