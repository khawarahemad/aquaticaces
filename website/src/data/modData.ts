export const MOD = {
  name: 'Aquatic Aces',
  version: '1.3.8',
  minecraft: '1.21',
  loader: '0.15.11',
  fabricApi: '0.100.3+1.21',
  fabricKotlin: '1.11.0+kotlin.2.0.0',
  license: 'MIT',
  defaultKeybind: 'Right Shift',
  commandPrefix: '.',
  defaultProfile: 'Ghost',
  repoUrl: 'https://github.com/khawarahemad/aquaticaces',
  discordUrl: 'https://discord.gg/GMDf9vWeuQ',
} as const

export const DOWNLOADS = [
  {
    name: 'Aquatic Aces',
    version: MOD.version,
    description: 'The main mod — drop into your mods folder.',
    file: '/downloads/aquaticaces-1.3.8.jar',
    size: '~500 KB',
    primary: true,
    icon: '⚡',
  },
  {
    name: 'Source Code',
    version: 'GitHub',
    description: 'Full mod source, website, and build instructions on GitHub.',
    file: 'https://github.com/khawarahemad/aquaticaces',
    size: 'Open Source',
    primary: false,
    icon: '💻',
    external: true,
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
    name: 'Fabric Language Kotlin',
    version: MOD.fabricKotlin,
    description: 'Required runtime for Kotlin-based Fabric mods like Aquatic Aces.',
    file: 'https://modrinth.com/mod/fabric-language-kotlin/version/1.11.0+kotlin.2.0.0',
    size: 'External',
    primary: false,
    icon: '🟣',
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
    accent: '#00c6ff',
    tag: 'UI',
    layout: 'hero',
    highlights: ['Live search', 'HSV picker', 'Theme editor'],
  },
  {
    title: 'HUD Suite',
    description:
      'Target, Stats, ArrayList, Notifications, Performance, and Coordinates — toggle individually or drag positions.',
    icon: '📊',
    accent: '#a97bff',
    tag: 'Overlay',
    layout: 'tall',
    highlights: ['6 overlays', 'Drag positions'],
  },
  {
    title: '63+ Modules',
    description:
      'Combat, Movement, Render, World, Exploit, Player, Utility, and Ghost — each with configurable settings.',
    icon: '🧩',
    accent: '#00ff88',
    tag: 'Core',
    layout: 'tall',
    highlights: ['8 categories', 'Keybinds'],
  },
  {
    title: 'Config Profiles',
    description:
      'Save and load full profiles or per-category snapshots as JSON. Hot-swap between PvP, Ghost, and custom setups.',
    icon: '💾',
    accent: '#66aaff',
    tag: 'Profiles',
    layout: 'normal',
    highlights: ['Per-category', 'JSON'],
  },
  {
    title: 'Schematic Tools',
    description:
      'Capture block regions, preview ghost wireframes, and mirror placements with Symmetry Brush.',
    icon: '📐',
    accent: '#ffaa00',
    tag: 'World',
    layout: 'normal',
    highlights: ['Ghost preview', 'Symmetry'],
  },
  {
    title: 'Social Layer',
    description:
      'Friends list, waypoints, alt account manager, and a custom multiplayer screen built in.',
    icon: '👥',
    accent: '#ff88cc',
    tag: 'Social',
    layout: 'normal',
    highlights: ['Friends', 'Alts', 'Waypoints'],
  },
  {
    title: 'Event Bus',
    description:
      'Priority-aware handlers for ticks, packets, rendering, input, and chat — fast and extensible.',
    icon: '⚡',
    accent: '#0072ff',
    tag: 'Engine',
    layout: 'wide',
    highlights: ['Packet hooks', 'Priorities'],
  },
  {
    title: 'Dot Commands',
    description:
      'Full chat command system — toggle modules, bind keys, manage friends, alts, HUD, waypoints, and schematics.',
    icon: '⌨️',
    accent: '#00c6ff',
    tag: 'Chat',
    layout: 'wide',
    highlights: ['10 commands', 'Never sent to server'],
  },
] as const

export const MODULE_CATEGORIES = [
  {
    name: 'Combat',
    count: 19,
    color: '#ff4466',
    modules: [
      'AntiBot', 'Backtrack', 'KillAura', 'TriggerBot', 'AimAssist', 'SilentAim',
      'AutoCrystal', 'AutoAnchor', 'Criticals', 'Velocity', 'KeepSprint',
      'ShieldBreaker', 'WTap', 'AutoPot', 'NoSwing', 'Surround', 'HoleFill',
      'WallHit', 'BedAura',
    ],
  },
  {
    name: 'Movement',
    count: 15,
    color: '#00c6ff',
    modules: [
      'Sprint', 'Flight', 'Speed', 'Step', 'Scaffold', 'AirScaffold', 'Jesus', 'NoSlowdown',
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
    count: 4,
    color: '#888899',
    modules: ['SelfDestruct', 'Hitboxes', 'Reach', 'AimBot'],
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
    title: 'Add Fabric Language Kotlin',
    body: 'Download Fabric Language Kotlin for 1.21 — Aquatic Aces is written in Kotlin and needs this jar in your mods folder.',
  },
  {
    step: 5,
    title: 'Add Aquatic Aces',
    body: 'Download aquaticaces-1.3.8.jar from this page and drop it into .minecraft/mods alongside Fabric API and Fabric Language Kotlin.',
  },
  {
    step: 6,
    title: 'Launch & Play',
    body: 'Select your Fabric 1.21 profile, launch Minecraft, and press Right Shift to open the ClickGUI.',
  },
] as const

export const FAQ = [
  {
    q: 'What Minecraft version does Aquatic Aces support?',
    a: 'Aquatic Aces is built for Minecraft 1.21 with Fabric Loader 0.15.11+, Fabric API, and Fabric Language Kotlin.',
    tag: 'Compatibility',
    accent: '#00c6ff',
  },
  {
    q: 'Where are configs saved?',
    a: 'All data lives in .minecraft/aquaticaces/ — profiles, HUD layout, friends, alts, waypoints, and schematics.',
    tag: 'Storage',
    accent: '#a97bff',
  },
  {
    q: 'How do I change the ClickGUI keybind?',
    a: 'Open ClickGUI, find the ClickGUI module under Render, and rebind it — or use .bind ClickGUI <key> in chat.',
    tag: 'Controls',
    accent: '#00ff88',
  },
  {
    q: 'Can I build from source?',
    a: 'Yes. Clone the repo, run ./gradlew build, and grab the jar from build/libs/. Requires Java 21 and Gradle.',
    tag: 'Developers',
    accent: '#ffaa00',
  },
] as const

export const NAV_LINKS = [
  { label: 'Features', href: '#features' },
  { label: 'Modules', href: '#modules' },
  { label: 'Download', href: '#download' },
  { label: 'Releases', href: '#releases' },
  { label: 'Install', href: '#install' },
  { label: 'Commands', href: '#commands' },
  { label: 'FAQ', href: '#faq' },
] as const
