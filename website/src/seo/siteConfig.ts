import { MOD } from '../data/modData'

const DEFAULT_SITE_URL = 'https://aquaticaces.vercel.app'

export const SITE_URL = (import.meta.env.VITE_SITE_URL as string | undefined)?.replace(/\/$/, '') || DEFAULT_SITE_URL

export const SITE = {
  name: MOD.name,
  title: 'Aquatic Aces — Free Minecraft 1.21 Fabric Client | 60+ Modules',
  shortTitle: 'Aquatic Aces — Minecraft 1.21 Fabric Client',
  description:
    'Download Aquatic Aces — a free, open-source Fabric client for Minecraft 1.21 with 60+ modules, NanoVG ClickGUI, KillAura, ESP, Scaffold, HUD editor, configs, and schematic tools. Requires Fabric API and Fabric Language Kotlin.',
  keywords: [
    'Aquatic Aces',
    'Aquatic Aces Minecraft',
    'Minecraft 1.21 client',
    'Minecraft 1.21 Fabric client',
    'Fabric client 1.21',
    'Minecraft Fabric mod client',
    'Minecraft utility client',
    'Fabric KillAura',
    'Minecraft ESP mod',
    'Minecraft Scaffold',
    'NanoVG ClickGUI',
    'Minecraft HUD mod',
    'Fabric Language Kotlin',
    'Fabric API 1.21',
    'Minecraft ghost client',
    'Minecraft PvP client',
    'free Minecraft client',
    'open source Minecraft client',
    'aquaticaces download',
    'Minecraft 1.21 hack client',
  ],
  author: 'Aquatic Aces Team',
  locale: 'en_US',
  themeColor: '#0a0b0f',
  twitterHandle: '@aquaticaces',
  releaseUrl: `${MOD.repoUrl}/releases/latest`,
  downloadFile: 'aquaticaces-1.4.0.jar',
} as const

export function absoluteUrl(path = '/') {
  const base = import.meta.env.BASE_URL.replace(/\/$/, '') || ''
  if (path.startsWith('#')) {
    return `${SITE_URL}${base}/${path}`
  }
  const normalized = path.startsWith('/') ? path : `/${path}`
  return `${SITE_URL}${base}${normalized === '/' ? '/' : normalized}`
}

export function assetAbsoluteUrl(path: string) {
  const clean = path.replace(/^\//, '')
  const base = import.meta.env.BASE_URL.replace(/\/$/, '') || ''
  return `${SITE_URL}${base}/${clean}`
}
