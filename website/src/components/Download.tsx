import { motion } from 'framer-motion'
import { DOWNLOADS, MOD } from '../data/modData'
import { useGitHubReleases } from '../hooks/useGitHubReleases'
import {
  formatFileSize,
  formatReleaseDate,
  getJarAsset,
  getReleaseVersion,
} from '../lib/githubReleases'
import { assetUrl } from '../lib/assetUrl'
import { Logo } from './ui/Logo'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Download.css'

const DEPS = DOWNLOADS.filter((d) => !d.primary && d.name !== 'Source Code')
const SOURCE = DOWNLOADS.find((d) => d.name === 'Source Code')!
const FALLBACK = DOWNLOADS.find((d) => d.primary)!

export function Download() {
  const { latest, loading, error } = useGitHubReleases()
  const jar = latest ? getJarAsset(latest) : null
  const downloadUrl = jar?.browser_download_url ?? assetUrl(FALLBACK!.file)
  const version = latest ? getReleaseVersion(latest) : MOD.version
  const fileSize = jar ? formatFileSize(jar.size) : FALLBACK!.size
  const publishedAt = latest ? formatReleaseDate(latest.published_at) : null

  return (
    <section id="download" className="page-section download-section">
      <div className="download-ambient" aria-hidden />

      <Reveal>
        <SectionHeader
          label="Download"
          title="Get Aquatic Aces"
          subtitle={`Everything you need for Minecraft ${MOD.minecraft} — mod jar, Fabric dependencies, and Java 21.`}
          align="center"
        />
      </Reveal>

      <Reveal delay={0.08}>
        <motion.div className="download-spotlight glass-card" whileHover={{ y: -3 }}>
          <div className="download-spotlight-mesh" aria-hidden />
          <div className="download-spotlight-left">
            <Logo size={72} />
            <div className="download-spotlight-meta">
              <div className="download-pills">
                <span className="download-pill download-pill--live">v{version}</span>
                <span className="download-pill">MC {MOD.minecraft}</span>
                <span className="download-pill">Fabric</span>
                {latest?.prerelease && <span className="download-pill">Pre-release</span>}
              </div>
              <h3>Aquatic Aces Client</h3>
              <p>Drop the jar into <code>.minecraft/mods/</code> alongside Fabric API and Fabric Language Kotlin.</p>
              <div className="download-file-meta">
                <span>{fileSize}</span>
                <span>·</span>
                <span>MIT License</span>
                {publishedAt && (
                  <>
                    <span>·</span>
                    <span>{publishedAt}</span>
                  </>
                )}
              </div>
              {error && (
                <p className="download-fallback-note">
                  GitHub releases unavailable — using bundled fallback jar.
                </p>
              )}
            </div>
          </div>
          <div className="download-spotlight-actions">
            <a
              href={downloadUrl}
              className="btn btn-primary"
              download={jar ? jar.name : undefined}
              target={jar ? '_blank' : undefined}
              rel={jar ? 'noopener noreferrer' : undefined}
            >
              {loading ? 'Loading...' : `Download v${version}`}
            </a>
            <a
              href={latest?.html_url ?? `${MOD.repoUrl}/releases/latest`}
              className="btn btn-secondary"
              target="_blank"
              rel="noopener noreferrer"
            >
              Latest Release ↗
            </a>
            <a
              href={MOD.repoUrl}
              className="btn btn-secondary"
              target="_blank"
              rel="noopener noreferrer"
            >
              Source Repo ↗
            </a>
          </div>
        </motion.div>
      </Reveal>

      <div className="download-panels">
        <Reveal delay={0.12}>
          <div className="download-panel glass-card">
            <div className="download-panel-head">
              <h4>Dependencies</h4>
              <p>Required to run the mod</p>
            </div>
            <div className="download-panel-list">
              {DEPS.map((item) => (
                <a
                  key={item.name}
                  href={assetUrl(item.file)}
                  className="download-list-item"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <span className="download-list-icon">{item.icon}</span>
                  <div className="download-list-body">
                    <strong>{item.name}</strong>
                    <span>{item.version}</span>
                  </div>
                  <span className="download-list-arrow">↗</span>
                </a>
              ))}
            </div>
          </div>
        </Reveal>

        <Reveal delay={0.16}>
          <a
            href={assetUrl(SOURCE.file)}
            className="download-panel glass-card download-panel--source"
            target="_blank"
            rel="noopener noreferrer"
          >
            <div className="download-panel-head">
              <h4>Source Repository</h4>
              <p>Mod + website + build scripts</p>
            </div>
            <div className="download-source-body">
              <span className="download-source-icon">{SOURCE.icon}</span>
              <div>
                <strong>github.com/khawarahemad/aquaticaces</strong>
                <span>Clone, build with Gradle, or open issues.</span>
              </div>
            </div>
            <span className="download-source-cta">View on GitHub ↗</span>
          </a>
        </Reveal>
      </div>

      <Reveal delay={0.2}>
        <div className="requirements glass-card">
          <h3>System Requirements</h3>
          <div className="req-row">
            {[
              ['Minecraft', '1.21'],
              ['Java', 'JDK 21'],
              ['Loader', `${MOD.loader}+`],
              ['Fabric API', MOD.fabricApi],
              ['Fabric Kotlin', MOD.fabricKotlin],
              ['RAM', '6 GB rec.'],
            ].map(([k, v]) => (
              <div key={k} className="req-chip">
                <span className="req-chip-key">{k}</span>
                <span className="req-chip-val">{v}</span>
              </div>
            ))}
          </div>
        </div>
      </Reveal>
    </section>
  )
}
