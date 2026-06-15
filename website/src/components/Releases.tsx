import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { MOD } from '../data/modData'
import { useGitHubReleases } from '../hooks/useGitHubReleases'
import {
  formatFileSize,
  formatReleaseDate,
  getJarAsset,
  getReleaseVersion,
  type GitHubRelease,
} from '../lib/githubReleases'
import { MarkdownContent } from './ui/MarkdownContent'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Releases.css'

function ReleaseCard({
  release,
  isLatest,
  defaultOpen,
}: {
  release: GitHubRelease
  isLatest: boolean
  defaultOpen: boolean
}) {
  const [open, setOpen] = useState(defaultOpen)
  const jar = getJarAsset(release)
  const version = getReleaseVersion(release)

  return (
    <article className={`release-card glass-card ${isLatest ? 'release-card--latest' : ''}`}>
      <button
        type="button"
        className="release-card-head"
        onClick={() => setOpen((value) => !value)}
        aria-expanded={open}
      >
        <div className="release-card-title">
          <div className="release-card-badges">
            <span className="release-tag">{release.tag_name}</span>
            {isLatest && <span className="release-badge release-badge--latest">Latest</span>}
            {release.prerelease && <span className="release-badge release-badge--pre">Pre-release</span>}
          </div>
          <h3>{release.name || release.tag_name}</h3>
          <p>{formatReleaseDate(release.published_at)}</p>
        </div>

        <div className="release-card-actions">
          {jar && (
            <a
              href={jar.browser_download_url}
              className="release-download-btn"
              download
              onClick={(e) => e.stopPropagation()}
            >
              ↓ {jar.name}
            </a>
          )}
          <motion.span
            className="release-toggle"
            animate={{ rotate: open ? 45 : 0 }}
            transition={{ duration: 0.25 }}
            aria-hidden
          >
            +
          </motion.span>
        </div>
      </button>

      <AnimatePresence initial={false}>
        {open && (
          <motion.div
            className="release-card-body"
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.3, ease: [0.22, 1, 0.36, 1] }}
          >
            <div className="release-card-inner">
              {release.body ? (
                <MarkdownContent content={release.body} />
              ) : (
                <p className="release-empty-notes">No release notes provided on GitHub.</p>
              )}

              <div className="release-card-footer">
                <div className="release-assets">
                  {release.assets.length > 0 ? (
                    release.assets.map((asset) => (
                      <a
                        key={asset.name}
                        href={asset.browser_download_url}
                        className="release-asset"
                        download
                      >
                        <span>{asset.name}</span>
                        <span>{formatFileSize(asset.size)}</span>
                      </a>
                    ))
                  ) : (
                    <span className="release-empty-notes">No downloadable assets attached.</span>
                  )}
                </div>
                <a
                  href={release.html_url}
                  className="release-github-link"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  View on GitHub ↗
                </a>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {!open && jar && (
        <div className="release-card-meta">
          <span>v{version}</span>
          <span>·</span>
          <span>{formatFileSize(jar.size)}</span>
        </div>
      )}
    </article>
  )
}

export function Releases() {
  const { releases, latest, loading, error } = useGitHubReleases()

  return (
    <section id="releases" className="page-section releases-section">
      <Reveal>
        <SectionHeader
          label="Releases"
          title="Version history"
          subtitle="Every release is published on GitHub with full notes, install steps, and downloadable jars."
          align="center"
        />
      </Reveal>

      <div className="releases-list">
        {loading && (
          <Reveal>
            <div className="releases-state glass-card">
              <span className="releases-spinner" aria-hidden />
              <p>Loading releases from GitHub...</p>
            </div>
          </Reveal>
        )}

        {!loading && error && (
          <Reveal>
            <div className="releases-state glass-card releases-state--error">
              <p>Could not load releases from GitHub.</p>
              <span>{error}</span>
              <a
                href={`${MOD.repoUrl}/releases`}
                target="_blank"
                rel="noopener noreferrer"
                className="btn btn-secondary"
              >
                Open GitHub Releases ↗
              </a>
            </div>
          </Reveal>
        )}

        {!loading && !error && releases.length === 0 && (
          <Reveal>
            <div className="releases-state glass-card">
              <p>No releases found yet.</p>
              <a
                href={`${MOD.repoUrl}/releases`}
                target="_blank"
                rel="noopener noreferrer"
                className="btn btn-secondary"
              >
                Check GitHub ↗
              </a>
            </div>
          </Reveal>
        )}

        {!loading &&
          !error &&
          releases.map((release, index) => (
            <Reveal key={release.id} delay={index * 0.05}>
              <ReleaseCard
                release={release}
                isLatest={latest?.id === release.id}
                defaultOpen={index === 0}
              />
            </Reveal>
          ))}
      </div>
    </section>
  )
}
