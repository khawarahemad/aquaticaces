import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { MOD } from '../data/modData'
import { useGitHubReleases } from '../hooks/useGitHubReleases'
import {
  extractReleaseHighlights,
  formatFileSize,
  formatReleaseDate,
  getJarAsset,
  getReleasePreview,
  getReleaseVersion,
  type GitHubRelease,
} from '../lib/githubReleases'
import { MarkdownContent } from './ui/MarkdownContent'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Releases.css'

function ReleaseIcon({ accent }: { accent: string }) {
  return (
    <svg viewBox="0 0 24 24" className="release-icon-svg" aria-hidden>
      <path
        d="M12 3l7 4v10l-7 4-7-4V7l7-4z"
        stroke={accent}
        strokeWidth="1.5"
        fill="none"
      />
      <path d="M12 11v6M9 9.5l6 3.5" stroke={accent} strokeWidth="1.5" strokeLinecap="round" />
    </svg>
  )
}

function ReleaseHero({ release }: { release: GitHubRelease }) {
  const [notesOpen, setNotesOpen] = useState(true)
  const jar = getJarAsset(release)
  const version = getReleaseVersion(release)
  const highlights = extractReleaseHighlights(release.body)

  return (
    <motion.article
      className="releases-hero glass-card"
      whileHover={{ y: -3 }}
      transition={{ duration: 0.3 }}
    >
      <div className="releases-hero-mesh" aria-hidden />
      <div className="releases-hero-glow" aria-hidden />

      <div className="releases-hero-top">
        <div className="releases-hero-left">
          <div className="releases-hero-icon">
            <ReleaseIcon accent="var(--accent)" />
          </div>
          <div className="releases-hero-meta">
            <div className="releases-hero-pills">
              <span className="releases-pill releases-pill--live">v{version}</span>
              <span className="releases-pill releases-pill--latest">Latest</span>
              {release.prerelease && <span className="releases-pill releases-pill--pre">Pre-release</span>}
              <span className="releases-pill">{formatReleaseDate(release.published_at)}</span>
            </div>
            <h3>{release.name || release.tag_name}</h3>
            {highlights.length > 0 ? (
              <ul className="releases-hero-highlights">
                {highlights.map((item) => (
                  <li key={item}>{item}</li>
                ))}
              </ul>
            ) : (
              <p className="releases-hero-preview">{getReleasePreview(release.body)}</p>
            )}
          </div>
        </div>

        <div className="releases-hero-actions">
          {jar && (
            <a href={jar.browser_download_url} className="btn btn-primary" download>
              Download v{version}
            </a>
          )}
          <a
            href={release.html_url}
            className="btn btn-secondary"
            target="_blank"
            rel="noopener noreferrer"
          >
            View on GitHub ↗
          </a>
          {jar && (
            <div className="releases-hero-file">
              <span>{jar.name}</span>
              <span>·</span>
              <span>{formatFileSize(jar.size)}</span>
            </div>
          )}
        </div>
      </div>

      <div className="releases-notes-shell">
        <button
          type="button"
          className="releases-notes-toggle"
          onClick={() => setNotesOpen((value) => !value)}
          aria-expanded={notesOpen}
        >
          <span className="releases-notes-toggle-label">Full changelog</span>
          <span className="releases-notes-toggle-hint">
            {notesOpen ? 'Hide details' : 'Show install steps & notes'}
          </span>
          <motion.span
            className="releases-notes-chevron"
            animate={{ rotate: notesOpen ? 180 : 0 }}
            transition={{ duration: 0.25 }}
            aria-hidden
          >
            ⌄
          </motion.span>
        </button>

        <AnimatePresence initial={false}>
          {notesOpen && (
            <motion.div
              className="releases-notes-panel"
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              transition={{ duration: 0.3, ease: [0.22, 1, 0.36, 1] }}
            >
              <div className="releases-notes-inner">
                {release.body ? (
                  <MarkdownContent content={release.body} />
                ) : (
                  <p className="release-empty-notes">No release notes provided on GitHub.</p>
                )}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </motion.article>
  )
}

function ReleaseTimelineItem({
  release,
  isLast,
  defaultOpen,
}: {
  release: GitHubRelease
  isLast: boolean
  defaultOpen: boolean
}) {
  const [open, setOpen] = useState(defaultOpen)
  const jar = getJarAsset(release)
  const preview = getReleasePreview(release.body)

  return (
    <div className={`release-timeline-item ${open ? 'release-timeline-item--open' : ''}`}>
      <div className="release-timeline-rail" aria-hidden>
        <span className="release-timeline-dot" />
        {!isLast && <span className="release-timeline-line" />}
      </div>

      <article className="release-timeline-card glass-card">
        <button
          type="button"
          className="release-timeline-head"
          onClick={() => setOpen((value) => !value)}
          aria-expanded={open}
        >
          <div className="release-timeline-title">
            <div className="release-card-badges">
              <span className="release-tag">{release.tag_name}</span>
              {release.prerelease && <span className="release-badge release-badge--pre">Pre-release</span>}
            </div>
            <h3>{release.name || release.tag_name}</h3>
            <p className="release-timeline-preview">{preview}</p>
            <div className="release-timeline-meta">
              <span>{formatReleaseDate(release.published_at)}</span>
              {jar && (
                <>
                  <span>·</span>
                  <span>{formatFileSize(jar.size)}</span>
                </>
              )}
            </div>
          </div>

          <div className="release-timeline-actions">
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
              animate={{ rotate: open ? 180 : 0 }}
              transition={{ duration: 0.25 }}
              aria-hidden
            >
              ⌄
            </motion.span>
          </div>
        </button>

        <AnimatePresence initial={false}>
          {open && (
            <motion.div
              className="release-timeline-body"
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              transition={{ duration: 0.3, ease: [0.22, 1, 0.36, 1] }}
            >
              <div className="release-timeline-inner">
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
      </article>
    </div>
  )
}

export function Releases() {
  const { releases, latest, loading, error } = useGitHubReleases()
  const olderReleases = latest ? releases.filter((release) => release.id !== latest.id) : releases

  return (
    <section id="releases" className="page-section releases-section">
      <div className="releases-ambient releases-ambient--left" aria-hidden />
      <div className="releases-ambient releases-ambient--right" aria-hidden />

      <Reveal>
        <SectionHeader
          label="Releases"
          title="Version history"
          subtitle="Every release is published on GitHub with full notes, install steps, and downloadable jars."
          align="center"
        />
      </Reveal>

      {!loading && !error && releases.length > 0 && (
        <Reveal delay={0.04}>
          <div className="releases-stats glass-card">
            <div className="releases-stat">
              <span className="releases-stat-value">{releases.length}</span>
              <span className="releases-stat-label">Published releases</span>
            </div>
            <div className="releases-stat-divider" aria-hidden />
            <div className="releases-stat">
              <span className="releases-stat-value">v{latest ? getReleaseVersion(latest) : MOD.version}</span>
              <span className="releases-stat-label">Current version</span>
            </div>
            <div className="releases-stat-divider" aria-hidden />
            <div className="releases-stat">
              <span className="releases-stat-value">
                {latest ? formatReleaseDate(latest.published_at) : '—'}
              </span>
              <span className="releases-stat-label">Last updated</span>
            </div>
          </div>
        </Reveal>
      )}

      <div className="releases-layout">
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

        {!loading && !error && latest && (
          <Reveal delay={0.08}>
            <ReleaseHero release={latest} />
          </Reveal>
        )}

        {!loading && !error && olderReleases.length > 0 && (
          <Reveal delay={0.12}>
            <div className="releases-timeline-panel glass-card">
              <div className="releases-timeline-head">
                <span className="releases-timeline-label">Previous versions</span>
                <span className="releases-timeline-count">{olderReleases.length} older</span>
              </div>
              <div className="releases-timeline">
                {olderReleases.map((release, index) => (
                  <ReleaseTimelineItem
                    key={release.id}
                    release={release}
                    isLast={index === olderReleases.length - 1}
                    defaultOpen={false}
                  />
                ))}
              </div>
            </div>
          </Reveal>
        )}
      </div>
    </section>
  )
}
