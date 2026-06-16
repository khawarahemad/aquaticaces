import { MOD } from '../data/modData'
import { MODULE_CATEGORIES } from '../data/modData'
import { SITE } from '../seo/siteConfig'
import './SeoContent.css'

export function SeoContent() {
  const modules = MODULE_CATEGORIES.map((c) => `${c.name}: ${c.modules.join(', ')}`).join('. ')

  return (
    <section className="seo-content" aria-label="About Aquatic Aces">
      <div className="seo-content-inner">
        <h2>Aquatic Aces — Minecraft {MOD.minecraft} Fabric Client Download</h2>
        <p>
          {SITE.description} Aquatic Aces is an open-source Minecraft client mod for Fabric {MOD.minecraft},
          built with Kotlin and NanoVG. Download the latest release from GitHub and install alongside
          Fabric API and Fabric Language Kotlin.
        </p>
        <p>
          <strong>Popular modules:</strong> {modules}
        </p>
        <p>
          <strong>Keywords:</strong> {SITE.keywords.join(' · ')}
        </p>
        <nav aria-label="Site sections">
          <a href="#download">Download Aquatic Aces</a>
          <a href="#releases">Release history</a>
          <a href="#install">Install guide</a>
          <a href="#modules">Module list</a>
          <a href="#features">Features</a>
          <a href="#faq">FAQ</a>
          <a href={MOD.repoUrl}>GitHub source</a>
          <a href={MOD.discordUrl}>Discord community</a>
          <a href={SITE.releaseUrl}>Latest GitHub release</a>
        </nav>
      </div>
    </section>
  )
}
