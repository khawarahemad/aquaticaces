import { defineConfig, type Plugin } from 'vite'
import react from '@vitejs/plugin-react'
import { readFileSync, writeFileSync } from 'node:fs'
import { resolve } from 'node:path'

const SITE = {
  title: 'Aquatic Aces — Free Minecraft 1.21 Fabric Client | 60+ Modules',
  description:
    'Download Aquatic Aces — a free, open-source Fabric client for Minecraft 1.21 with 60+ modules, NanoVG ClickGUI, KillAura, ESP, Scaffold, HUD editor, configs, and schematic tools. Requires Fabric API and Fabric Language Kotlin.',
  keywords:
    'Aquatic Aces, Minecraft 1.21 client, Fabric client 1.21, Minecraft Fabric mod, KillAura, ESP, Scaffold, NanoVG ClickGUI, Minecraft HUD mod, Fabric Language Kotlin, free Minecraft client, aquaticaces download',
}

function getSiteUrl() {
  const base = (process.env.VITE_BASE || '/').replace(/\/$/, '')
  const origin = (process.env.VITE_SITE_URL || 'https://aquaticaces.vercel.app').replace(/\/$/, '')
  return `${origin}${base}`
}

function injectSeoPlaceholders(html: string) {
  const siteUrl = getSiteUrl()
  return html
    .replaceAll('__SITE_URL__', siteUrl)
    .replaceAll('__SITE_TITLE__', SITE.title)
    .replaceAll('__SITE_DESCRIPTION__', SITE.description)
    .replaceAll('__SITE_KEYWORDS__', SITE.keywords)
}

function seoStaticAssetsPlugin(): Plugin {
  return {
    name: 'seo-static-assets',
    closeBundle() {
      const outDir = resolve(__dirname, 'dist')

      for (const file of ['robots.txt', 'sitemap.xml']) {
        const source = readFileSync(resolve(__dirname, 'public', file), 'utf8')
        writeFileSync(resolve(outDir, file), injectSeoPlaceholders(source))
      }
    },
    transformIndexHtml(html) {
      return injectSeoPlaceholders(html)
    },
  }
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), seoStaticAssetsPlugin()],
  base: process.env.VITE_BASE || '/',
})
