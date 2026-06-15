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

function ReleaseSidebarItem({ release }: { release: GitHubRelease }) {
  const [open, setOpen] = useState(false)
  const jar = getJarAsset(release)
  const preview = getReleasePreview(release.body)

  return (
    <article className={`release-sidebar-item ${open ? 'release-sidebar-item--open' : ''}`}>
      <button
        type="button"
        className="release-sidebar-head"
        onClick={() => setOpen((value) => !value)}
        aria-expanded={open}
      >
        <div className="release-sidebar-title">
          <div className="release-sidebar-top">
            <span className="release-tag">{release.tag_name}</span>
            {release.prerelease && <span className="release-badge release-badge--pre">Pre</span>}
            <span className="release-sidebar-date">{formatReleaseDate(release.published_at)}</span>
          </div>
          <p className="release-sidebar-preview">{preview}</p>
          {jar && <span className="release-sidebar-size">{formatFileSize(jar.size)}</span>}
        </div>
        <motion.span
          className="release-sidebar-chevron"
          animate={{ rotate: open ? 180 : 0 }}
          transition={{ duration: 0.25 }}
          aria-hidden
        >
          ⌄
        </motion.span>
      </button>

      <div className="release-sidebar-actions">
        {jar && (
          <a href={jar.browser_download_url} className="release-sidebar-download" download>
            ↓ Jar
          </a>
        )}
        <a
          href={release.html_url}
          className="release-sidebar-link"
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub ↗
        </a>
      </div>

      <AnimatePresence initial={false}>
        {open && (
          <motion.div
            className="release-sidebar-body"
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.3, ease: [0.22, 1, 0.36, 1] }}
          >
            <div className="release-sidebar-inner">
              {release.body ? (
                <MarkdownContent content={release.body} />
              ) : (
                <p className="release-empty-notes">No release notes provided on GitHub.</p>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </article>
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
            <div className={`releases-grid ${olderReleases.length === 0 ? 'releases-grid--solo' : ''}`}>
              <div className="releases-main">
                <ReleaseHero release={latest} />
              </div>

              {olderReleases.length > 0 && (
                <aside className="releases-sidebar glass-card">
                  <div className="releases-sidebar-head">
                    <span className="releases-sidebar-label">Older versions</span>
                    <span className="releases-sidebar-count">{olderReleases.length}</span>
                  </div>
                  <div className="releases-sidebar-list">
                    {olderReleases.map((release) => (
                      <ReleaseSidebarItem key={release.id} release={release} />
                    ))}
                  </div>
                  <a
                    href={`${MOD.repoUrl}/releases`}
                    className="releases-sidebar-all"
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    All releases on GitHub ↗
                  </a>
                </aside>
              )}
            </div>
          </Reveal>
        )}
      </div>
    </section>
  )
}
