import { useState } from 'react'
import type { CSSProperties } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { FAQ, MOD } from '../data/modData'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Faq.css'

const QUICK_LINKS = [
  { label: 'Download', href: '#download', desc: 'Get the latest jar' },
  { label: 'Setup Guide', href: '#install', desc: 'Step-by-step install' },
  { label: 'Discord', href: MOD.discordUrl, desc: 'Join the community', external: true },
  { label: 'Source Repo', href: MOD.repoUrl, desc: 'Build from source', external: true },
] as const

function FaqIcon({ tag, accent }: { tag: string; accent: string }) {
  const props = { stroke: accent, strokeWidth: 1.5, fill: 'none' }

  switch (tag) {
    case 'Compatibility':
      return (
        <svg viewBox="0 0 24 24" className="faq-icon-svg" aria-hidden>
          <rect x="4" y="4" width="16" height="16" rx="3" {...props} />
          <path d="M8 12h8M12 8v8" {...props} />
        </svg>
      )
    case 'Storage':
      return (
        <svg viewBox="0 0 24 24" className="faq-icon-svg" aria-hidden>
          <path d="M5 7h14v12H5z" {...props} />
          <path d="M9 7V5h6v2" {...props} />
          <path d="M8 12h8" {...props} strokeLinecap="round" />
        </svg>
      )
    case 'Controls':
      return (
        <svg viewBox="0 0 24 24" className="faq-icon-svg" aria-hidden>
          <rect x="3" y="7" width="18" height="10" rx="2" {...props} />
          <rect x="6" y="10" width="3" height="2" rx="0.5" fill={accent} fillOpacity="0.35" stroke="none" />
          <rect x="11" y="10" width="7" height="2" rx="0.5" fill={accent} fillOpacity="0.2" stroke="none" />
        </svg>
      )
    case 'Developers':
      return (
        <svg viewBox="0 0 24 24" className="faq-icon-svg" aria-hidden>
          <path d="M8 6l-4 6 4 6" {...props} strokeLinecap="round" strokeLinejoin="round" />
          <path d="M16 6l4 6-4 6" {...props} strokeLinecap="round" strokeLinejoin="round" />
          <path d="M13 5l-2 14" {...props} strokeLinecap="round" />
        </svg>
      )
    default:
      return (
        <svg viewBox="0 0 24 24" className="faq-icon-svg" aria-hidden>
          <circle cx="12" cy="12" r="8" {...props} />
        </svg>
      )
  }
}

export function Faq() {
  const [open, setOpen] = useState<number | null>(0)

  return (
    <section id="faq" className="page-section faq-section">
      <div className="faq-ambient faq-ambient--left" aria-hidden />
      <div className="faq-ambient faq-ambient--right" aria-hidden />

      <Reveal>
        <SectionHeader
          label="FAQ"
          title="Questions answered"
          subtitle="Everything you need to know before installing Aquatic Aces."
          align="center"
        />
      </Reveal>

      <div className="faq-layout">
        <Reveal delay={0.06}>
          <div className="faq-panel glass-card">
            <div className="faq-panel-head">
              <span className="faq-panel-label">Common questions</span>
              <span className="faq-panel-count">{FAQ.length} topics</span>
            </div>

            <div className="faq-rows">
              {FAQ.map((item, i) => {
                const isOpen = open === i

                return (
                  <article
                    key={item.q}
                    className={`faq-row ${isOpen ? 'open' : ''}`}
                    style={{ '--faq-accent': item.accent } as CSSProperties}
                  >
                    <button
                      type="button"
                      className="faq-trigger"
                      onClick={() => setOpen(isOpen ? null : i)}
                      aria-expanded={isOpen}
                    >
                      <span className="faq-index">{String(i + 1).padStart(2, '0')}</span>

                      <span className="faq-icon-wrap" aria-hidden>
                        <FaqIcon tag={item.tag} accent={item.accent} />
                      </span>

                      <span className="faq-trigger-copy">
                        <span className="faq-tag">{item.tag}</span>
                        <span className="faq-question">{item.q}</span>
                      </span>

                      <motion.span
                        className="faq-toggle"
                        animate={{ rotate: isOpen ? 45 : 0 }}
                        transition={{ duration: 0.25 }}
                        aria-hidden
                      >
                        +
                      </motion.span>
                    </button>

                    <AnimatePresence initial={false}>
                      {isOpen && (
                        <motion.div
                          className="faq-answer-wrap"
                          initial={{ height: 0, opacity: 0 }}
                          animate={{ height: 'auto', opacity: 1 }}
                          exit={{ height: 0, opacity: 0 }}
                          transition={{ duration: 0.32, ease: [0.22, 1, 0.36, 1] }}
                        >
                          <p className="faq-answer">{item.a}</p>
                        </motion.div>
                      )}
                    </AnimatePresence>
                  </article>
                )
              })}
            </div>
          </div>
        </Reveal>

        <Reveal delay={0.12}>
          <aside className="faq-aside">
            <div className="faq-help glass-card">
              <div className="faq-help-glow" aria-hidden />
              <span className="faq-help-eyebrow">Need a hand?</span>
              <h3>Still stuck after install?</h3>
              <p>
                Jump to the setup guide, grab the latest build, or open the source repo if you
                want to compile it yourself.
              </p>

              <div className="faq-help-links">
                {QUICK_LINKS.map((link) => (
                  <a
                    key={link.label}
                    href={link.href}
                    className="faq-help-link"
                    {...('external' in link && link.external
                      ? { target: '_blank', rel: 'noopener noreferrer' }
                      : {})}
                  >
                    <span className="faq-help-link-label">{link.label}</span>
                    <span className="faq-help-link-desc">{link.desc}</span>
                    <span className="faq-help-link-arrow" aria-hidden>→</span>
                  </a>
                ))}
              </div>
            </div>

            <div className="faq-meta glass-card">
              <div className="faq-meta-item">
                <span>Version</span>
                <strong>v{MOD.version}</strong>
              </div>
              <div className="faq-meta-item">
                <span>Minecraft</span>
                <strong>{MOD.minecraft}</strong>
              </div>
              <div className="faq-meta-item">
                <span>Default key</span>
                <strong>{MOD.defaultKeybind}</strong>
              </div>
            </div>
          </aside>
        </Reveal>
      </div>
    </section>
  )
}
