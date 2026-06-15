export const MOD = {
  name: 'Aquatic Aces',
  version: '1.0.0',
  minecraft: '1.21',
  loader: '0.15.11',
  fabricApi: '0.100.3+1.21',
  license: 'MIT',
  defaultKeybind: 'Right Shift',
  commandPrefix: '.',
  defaultProfile: 'Ghost',
} as const

export const DOWNLOADS = [
  {
    name: 'Aquatic Aces',
    version: MOD.version,
    description: 'The main mod — drop into your mods folder.',
    file: '/downloads/aquaticaces-1.0.0.jar',
    size: '~500 KB',
    primary: true,
    icon: '⚡',
  },
  {
    name: 'Fabric Installer',
    version: 'Latest',
    description: 'Official installer to set up Fabric for Minecraft 1.21.',
    file: 'https://fabricmc.net/use/installer/',
    size: 'External',
    primary: false,
    icon: '🧵',
    external: true,
  },
  {
    name: 'Fabric API',
    version: MOD.fabricApi,
    description: 'Required dependency for all Fabric mods on 1.21.',
    file: 'https://modrinth.com/mod/fabric-api/version/0.100.3+1.21',
    size: 'External',
    primary: false,
    icon: '📦',
    external: true,
  },
  {
    name: 'Java 21',
    version: 'JDK 21',
    description: 'Minecraft 1.21 requires Java 21 to run.',
    file: 'https://adoptium.net/temurin/releases/?version=21',
    size: 'External',
    primary: false,
    icon: '☕',
    external: true,
  },
] as const

export const FEATURES = [
  {
    title: 'NanoVG ClickGUI',
    description:
      'Scrollable category panels, live search, theme editor, HSV color picker, and keybind labels on every module.',
    icon: '🎨',
  },
  {
    title: 'HUD Suite',
    description:
      'Target, Stats, ArrayList, Notifications, Performance, and Coordinates — toggle individually or drag positions.',
    icon: '📊',
  },
  {
    title: '60+ Modules',
    description:
      'Combat, Movement, Render, World, Exploit, Player, Utility, and Ghost — each with configurable settings.',
    icon: '🧩',
  },
  {
    title: 'Config Profiles',
    description:
      'Save and load full profiles or per-category snapshots as JSON. Hot-swap between PvP, Ghost, and custom setups.',
    icon: '💾',
  },
  {
    title: 'Schematic Tools',
    description:
      'Capture block regions, preview ghost wireframes, and mirror placements with Symmetry Brush.',
    icon: '📐',
  },
  {
    title: 'Social Layer',
    description:
      'Friends list, waypoints, alt account manager, and a custom multiplayer screen built in.',
    icon: '👥',
  },
  {
    title: 'Event Bus',
    description:
      'Priority-aware handlers for ticks, packets, rendering, input, and chat — fast and extensible.',
    icon: '⚡',
  },
  {
    title: 'Dot Commands',
    description:
      'Full chat command system — toggle modules, bind keys, manage friends, alts, HUD, waypoints, and schematics.',
    icon: '⌨️',
  },
] as const

export const MODULE_CATEGORIES = [
  {
    name: 'Combat',
    count: 17,
    color: '#ff4466',
    modules: [
      'AntiBot', 'Backtrack', 'KillAura', 'TriggerBot', 'AimAssist', 'SilentAim',
      'AutoCrystal', 'AutoAnchor', 'Criticals', 'Velocity', 'KeepSprint',
      'ShieldBreaker', 'WTap', 'AutoPot', 'NoSwing', 'Surround', 'HoleFill',
    ],
  },
  {
    name: 'Movement',
    count: 14,
    color: '#00c6ff',
    modules: [
      'Sprint', 'Flight', 'Speed', 'Step', 'Scaffold', 'Jesus', 'NoSlowdown',
      'Blink', 'NoFall', 'SafeWalk', 'Parkour', 'Spider', 'LongJump', 'ElytraFly',
    ],
  },
  {
    name: 'Render',
    count: 17,
    color: '#a97bff',
    modules: [
      'ESP', 'Chams', 'XRay', 'Tracers', 'Nametags', 'Freecam', 'ClickGUI',
      'DamageIndicators', 'Fullbright', 'Zoom', 'BlockESP', 'StorageESP',
      'LogoutSpot', 'Breadcrumbs', 'Waypoints', 'ViewModel',
    ],
  },
  {
    name: 'World',
    count: 6,
    color: '#00ff88',
    modules: ['Timer', 'FastPlace', 'GhostHand', 'LiquidInteract', 'SchematicPreview', 'SymmetryBrush'],
  },
  {
    name: 'Exploit',
    count: 2,
    color: '#ffaa00',
    modules: ['Phase', 'TickShift'],
  },
  {
    name: 'Player',
    count: 7,
    color: '#ff88cc',
    modules: [
      'InventoryManager', 'AutoEat', 'Derp', 'AutoTool', 'AutoArmor',
      'FastUse', 'MiddleClickFriend',
    ],
  },
  {
    name: 'Utility',
    count: 5,
    color: '#66aaff',
    modules: ['AutoTotem', 'ChestStealer', 'AutoReconnect', 'PingSpoof', 'HUD Editor'],
  },
  {
    name: 'Ghost',
    count: 3,
    color: '#888899',
    modules: ['SelfDestruct', 'Hitboxes', 'Reach'],
  },
] as const

export const COMMANDS = [
  { cmd: '.help', desc: 'List all available commands' },
  { cmd: '.toggle <module>', desc: 'Enable or disable a module' },
  { cmd: '.bind <module> <key>', desc: 'Set a module keybind' },
  { cmd: '.config load/save [profile] [category]', desc: 'Load or save configuration profiles' },
  { cmd: '.friend add/remove/list [name]', desc: 'Manage your friends list' },
  { cmd: '.alt add/remove/list/switch [name]', desc: 'Manage alt accounts' },
  { cmd: '.hud', desc: 'Open HUD settings (toggle elements)' },
  { cmd: '.hud edit', desc: 'Open drag-and-drop HUD position editor' },
  { cmd: '.wp add/remove/list [name]', desc: 'Manage world waypoints' },
  { cmd: '.schematic save/load/list/clear', desc: 'Capture and preview block schematics' },
] as const

export const INSTALL_STEPS = [
  {
    step: 1,
    title: 'Install Java 21',
    body: 'Download and install JDK 21 from Adoptium. Minecraft 1.21 will not launch without it.',
  },
  {
    step: 2,
    title: 'Install Fabric',
    body: 'Run the Fabric Installer, select Minecraft 1.21, and create a Fabric profile in the launcher.',
  },
  {
    step: 3,
    title: 'Add Fabric API',
    body: 'Download Fabric API for 1.21 and place it in your .minecraft/mods folder.',
  },
  {
    step: 4,
    title: 'Add Aquatic Aces',
    body: 'Download aquaticaces-1.0.0.jar from this page and drop it into .minecraft/mods alongside Fabric API.',
  },
  {
    step: 5,
    title: 'Launch & Play',
    body: 'Select your Fabric 1.21 profile, launch Minecraft, and press Right Shift to open the ClickGUI.',
  },
] as const

export const FAQ = [
  {
    q: 'What Minecraft version does Aquatic Aces support?',
    a: 'Aquatic Aces is built for Minecraft 1.21 with Fabric Loader 0.15.11+ and Fabric API.',
  },
  {
    q: 'Where are configs saved?',
    a: 'All data lives in .minecraft/aquaticaces/ — profiles, HUD layout, friends, alts, waypoints, and schematics.',
  },
  {
    q: 'How do I change the ClickGUI keybind?',
    a: 'Open ClickGUI, find the ClickGUI module under Render, and rebind it — or use .bind ClickGUI <key> in chat.',
  },
  {
    q: 'Can I build from source?',
    a: 'Yes. Clone the repo, run ./gradlew build, and grab the jar from build/libs/. Requires Java 21 and Gradle.',
  },
] as const

export const NAV_LINKS = [
  { label: 'Features', href: '#features' },
  { label: 'Modules', href: '#modules' },
  { label: 'Download', href: '#download' },
  { label: 'Install', href: '#install' },
  { label: 'Commands', href: '#commands' },
  { label: 'FAQ', href: '#faq' },
] as const
