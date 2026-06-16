export type ModuleInfo = {
  description: string
  benefit: string
}

export const MODULE_INFO: Record<string, ModuleInfo> = {
  AntiBot: {
    description: 'Filters fake player entities.',
    benefit: 'Keeps combat modules from targeting NPCs and bot entities — you only fight real players.',
  },
  Backtrack: {
    description: 'Targets past enemy positions.',
    benefit: 'Hits players where they were a moment ago, helping land attacks on fast or laggy targets.',
  },
  KillAura: {
    description: 'Automatically attacks nearby entities.',
    benefit: 'Auto-attacks enemies in range so you don’t have to click every swing in close fights.',
  },
  MaceAura: {
    description: 'Auto mace smash attacks with fall-distance bonus damage.',
    benefit: 'Switches to your mace, jumps for smash hits, and auto-attacks nearby targets — built for 1.21 mace PvP.',
  },
  TriggerBot: {
    description: 'Attacks entities in crosshair.',
    benefit: 'Only swings when your crosshair is on a target — precise and controlled auto-attacks.',
  },
  AimAssist: {
    description: 'Smoothly pulls target onto crosshair.',
    benefit: 'Gently helps your aim track opponents without snapping unnaturally.',
  },
  SilentAim: {
    description: 'Aims silently without turning camera.',
    benefit: 'Redirects attacks to targets while your screen stays still — looks legit from your view.',
  },
  AutoCrystal: {
    description: 'Automatically places and detonates crystals.',
    benefit: 'Handles crystal PvP placement and breaking for high burst damage in crystal fights.',
  },
  AutoAnchor: {
    description: 'Places and explodes Respawn Anchors.',
    benefit: 'Automates anchor placement and detonation for Nether PvP burst combos.',
  },
  Criticals: {
    description: 'Forces critical hits on attacks.',
    benefit: 'Makes every hit deal critical damage even when you wouldn’t normally get one.',
  },
  Velocity: {
    description: 'Reduces player knockback velocity.',
    benefit: 'You take less knockback in fights, so you stay in range and don’t get combo’d away.',
  },
  KeepSprint: {
    description: 'Maintains sprint after attacking.',
    benefit: 'Stay sprinting through hits so movement and knockback combos stay smooth.',
  },
  ShieldBreaker: {
    description: 'Breaks enemy shields with axe.',
    benefit: 'Automatically switches to axe and breaks blocking players so you can keep attacking.',
  },
  WTap: {
    description: 'Resets sprint on hit for extra knockback.',
    benefit: 'Adds extra knockback to your hits by resetting sprint at the right moment.',
  },
  AutoPot: {
    description: 'Throws health potions when low.',
    benefit: 'Throws instant health pots when HP drops — keeps you alive without fumbling in inventory.',
  },
  NoSwing: {
    description: 'Hides attack swing animation.',
    benefit: 'Hides your arm swing so attacks look cleaner or less obvious on screen.',
  },
  Surround: {
    description: 'Places blocks around your feet.',
    benefit: 'Instantly walls yourself in with obsidian or blocks when you’re in danger.',
  },
  HoleFill: {
    description: 'Fills the hole you are standing in.',
    benefit: 'Closes the hole you’re standing in so enemies can’t easily crystal or trap you.',
  },
  WallHit: {
    description: 'Attacks enemies through walls.',
    benefit: 'Hits the closest target without line-of-sight — damage through cover when they think they’re safe.',
  },
  BedAura: {
    description: 'Places and explodes beds on targets.',
    benefit: 'Automates Nether/End bed bombing — place and detonate beds near enemies for massive burst damage.',
  },
  Sprint: {
    description: 'Always sprints when moving.',
    benefit: 'Never walk accidentally — you’re always at full movement speed.',
  },
  Flight: {
    description: 'Enables player flight capabilities.',
    benefit: 'Fly freely in the world for fast travel, scouting, or building from above.',
  },
  Speed: {
    description: 'Increases movement speed.',
    benefit: 'Move faster on the ground to chase, escape, or cross maps quickly.',
  },
  Step: {
    description: 'Allows the player to step up full blocks instantly.',
    benefit: 'Walk up full blocks without jumping — smoother movement on uneven terrain.',
  },
  Scaffold: {
    description: 'Places blocks perfectly underfoot.',
    benefit: 'Automatically places blocks below you while bridging — essential for sky walks and towers.',
  },
  AirScaffold: {
    description: 'Auto-places blocks while airborne.',
    benefit: 'Bridges underfoot and ahead even in mid-air — keep building paths while jumping or falling.',
  },
  Jesus: {
    description: 'Allows walking on liquids.',
    benefit: 'Walk on water and lava without sinking — cross oceans or escape lava traps.',
  },
  NoSlowdown: {
    description: 'Prevents slowdown when using items.',
    benefit: 'Eat, drink, and use items at full speed without the vanilla slowdown.',
  },
  Blink: {
    description: 'Delays outgoing movement updates.',
    benefit: 'Freezes your server position briefly then releases — useful for baiting and repositioning.',
  },
  NoFall: {
    description: 'Prevents fall damage.',
    benefit: 'Drop from any height without taking fall damage.',
  },
  SafeWalk: {
    description: 'Prevents walking off block edges.',
    benefit: 'Stops you from accidentally walking off ledges while bridging or fighting.',
  },
  Parkour: {
    description: 'Auto jumps at block edges.',
    benefit: 'Automatically jumps at edges so you keep momentum on parkour and bridges.',
  },
  Spider: {
    description: 'Climbs walls like a spider.',
    benefit: 'Climb vertical walls without ladders or scaffolding.',
  },
  LongJump: {
    description: 'Boosts horizontal jump distance.',
    benefit: 'Jump much farther horizontally — great for gaps and escape routes.',
  },
  ElytraFly: {
    description: 'Enhanced elytra flight control.',
    benefit: 'Better control and speed while flying with elytra — smoother aerial movement.',
  },
  ESP: {
    description: 'Renders visuals through blocks.',
    benefit: 'See players, mobs, and items through walls so you always know what’s nearby.',
  },
  Chams: {
    description: 'Overrides entity rendering models through walls.',
    benefit: 'Highlights entities with solid colors through walls for clear visibility.',
  },
  XRay: {
    description: 'Reveals ores through solid blocks.',
    benefit: 'Spot diamonds, ancient debris, and ores through stone while mining.',
  },
  Tracers: {
    description: 'Draws lines to other players.',
    benefit: 'Lines point to players around you — easy to track positions at a glance.',
  },
  Nametags: {
    description: 'Renders detailed name tags above players.',
    benefit: 'See names, health, and distance above players from farther away.',
  },
  Freecam: {
    description: 'Enables free camera movement.',
    benefit: 'Detach your camera to scout areas while your body stays in place.',
  },
  ClickGUI: {
    description: 'Opens the interactive ClickGUI menu.',
    benefit: 'Your main control panel — toggle modules, change settings, and bind keys.',
  },
  DamageIndicators: {
    description: 'Displays floating damage indicators in 3D.',
    benefit: 'See exact damage numbers pop up in the world when you or others take hits.',
  },
  Fullbright: {
    description: 'Maximum brightness without XRay.',
    benefit: 'See clearly in caves and at night without placing torches.',
  },
  Zoom: {
    description: 'Optifine-style camera zoom.',
    benefit: 'Zoom in like a scope for scouting distant players or terrain.',
  },
  BlockESP: {
    description: 'Highlights valuable blocks.',
    benefit: 'Highlights spawners, chests, and other valuable blocks through walls.',
  },
  StorageESP: {
    description: 'Highlights storage containers.',
    benefit: 'Find chests, barrels, and shulkers through walls in bases and caves.',
  },
  LogoutSpot: {
    description: 'Marks where players logged out.',
    benefit: 'Shows where players disconnected so you know who combat-logged and where.',
  },
  Breadcrumbs: {
    description: 'Draws a trail of recent positions.',
    benefit: 'Leaves a visible trail of where you walked — useful for retracing paths.',
  },
  Waypoints: {
    description: 'Shows saved world waypoints.',
    benefit: 'Mark and navigate to saved locations in the world with in-game markers.',
  },
  ViewModel: {
    description: 'Adjusts hand view position.',
    benefit: 'Move and scale your hand/item view for a cleaner or custom look.',
  },
  Timer: {
    description: 'Speeds up or slows down the client.',
    benefit: 'Change game tick speed — faster actions or slow-motion for fine control.',
  },
  FastPlace: {
    description: 'Removes block placement delay.',
    benefit: 'Place blocks as fast as you click — faster bridging and building.',
  },
  GhostHand: {
    description: 'Interact with blocks through walls.',
    benefit: 'Open chests and interact with blocks through solid walls.',
  },
  LiquidInteract: {
    description: 'Allows block placements on liquids.',
    benefit: 'Place blocks on water and lava surfaces for bridging and trapping.',
  },
  SchematicPreview: {
    description: 'Renders loaded schematic ghosts.',
    benefit: 'Preview saved builds as ghost wireframes before placing blocks.',
  },
  SymmetryBrush: {
    description: 'Mirrors block placements across an axis.',
    benefit: 'Build symmetrically — place once and mirror to the other side automatically.',
  },
  Phase: {
    description: 'Allows walking through blocks.',
    benefit: 'Walk through walls and solid blocks to escape traps or enter sealed areas.',
  },
  TickShift: {
    description: 'Buffers ticks for sudden speed bursts.',
    benefit: 'Store game ticks and release them for sudden burst movement.',
  },
  InventoryManager: {
    description: 'Manages armor and drops garbage.',
    benefit: 'Auto-equips best armor and drops junk items to keep inventory clean.',
  },
  AutoEat: {
    description: 'Automatically eats food from hotbar.',
    benefit: 'Eats food when hungry so you don’t die while busy fighting or building.',
  },
  Derp: {
    description: 'Spins player rotations head-spinningly.',
    benefit: 'Spins your head/body for fun or to look chaotic to other players.',
  },
  AutoTool: {
    description: 'Selects best tool for block.',
    benefit: 'Automatically switches to the fastest tool for whatever you’re breaking.',
  },
  AutoArmor: {
    description: 'Equips best armor automatically.',
    benefit: 'Swaps to the best armor in your inventory when you pick up upgrades.',
  },
  FastUse: {
    description: 'Uses items faster.',
    benefit: 'Use pearls, pots, and food faster with reduced item use cooldown.',
  },
  'MiddleClickFriend': {
    description: 'Middle click player to friend.',
    benefit: 'Middle-click a player to add them to your friends list instantly.',
  },
  AutoTotem: {
    description: 'Swaps totems to the offhand automatically.',
    benefit: 'Keeps a totem in your offhand when you take lethal damage — saves you from death.',
  },
  ChestStealer: {
    description: 'Automatically steals items from containers.',
    benefit: 'Quickly loots chests and containers without clicking every slot.',
  },
  AutoReconnect: {
    description: 'Reconnects after disconnect.',
    benefit: 'Automatically rejoins the server if you get kicked or disconnected.',
  },
  PingSpoof: {
    description: 'Displays spoofed ping value.',
    benefit: 'Show a custom ping value on the tab list instead of your real latency.',
  },
  'HUD Editor': {
    description: 'HUD toggle and position editor.',
    benefit: 'Turn HUD elements on/off and drag them anywhere on screen.',
  },
  SelfDestruct: {
    description: 'Wipes client traces from the JVM.',
    benefit: 'Emergency wipe — disables everything and clears traces if you need to hide fast.',
  },
  Hitboxes: {
    description: 'Expands targeted enemy bounding boxes.',
    benefit: 'Makes hitboxes larger so attacks connect more easily on nearby players.',
  },
  Reach: {
    description: 'Extends attack and interaction range.',
    benefit: 'Hit and interact from farther away than vanilla allows.',
  },
  AimBot: {
    description: 'Ghost head assist while attacking crosshair targets.',
    benefit: 'While you hold left-click on an enemy in range, it subtly pulls your aim to their head so hits land cleaner — built for ghost play.',
  },
}

export function getModuleInfo(name: string): ModuleInfo {
  return (
    MODULE_INFO[name] ?? {
      description: 'A configurable Aquatic Aces module.',
      benefit: 'Toggle and customize this module from the ClickGUI to fit your playstyle.',
    }
  )
}
