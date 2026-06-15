import { motion } from 'framer-motion'
import type { CSSProperties } from 'react'
import { FEATURES } from '../data/modData'
import { FeatureIcon } from './FeatureIcons'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Features.css'

function FeatureCard({
  feature,
  index,
}: {
  feature: (typeof FEATURES)[number]
  index: number
}) {
  const isHero = feature.layout === 'hero'

  return (
    <motion.article
      className={`feature-tile feature-tile--${feature.layout}`}
      style={{ '--feature-accent': feature.accent } as CSSProperties}
      whileHover={{ y: -6, scale: 1.01 }}
      transition={{ duration: 0.3, ease: [0.22, 1, 0.36, 1] }}
    >
      <div className="feature-tile-glow" aria-hidden />
      <div className="feature-tile-grid" aria-hidden />
      <span className="feature-tile-watermark" aria-hidden>
        {String(index + 1).padStart(2, '0')}
      </span>

      <div className="feature-tile-head">
        <div className="feature-tile-icon">
          <FeatureIcon
            tag={feature.tag}
            accent={feature.accent}
            className={isHero ? 'feature-svg--lg' : ''}
          />
        </div>
        <span className="feature-tile-tag">{feature.tag}</span>
      </div>

      <div className="feature-tile-body">
        <h3>{feature.title}</h3>
        <p>{feature.description}</p>
      </div>

      <div className="feature-tile-highlights">
        {feature.highlights.map((h) => (
          <span key={h} className="feature-highlight">{h}</span>
        ))}
      </div>

      {isHero && (
        <div className="feature-hero-preview" aria-hidden>
          <div className="feature-preview-bar">
            <span /><span /><span />
            <em>Search modules...</em>
          </div>
          <div className="feature-preview-cols">
            {['Combat', 'Render', 'Move'].map((c) => (
              <div key={c} className="feature-preview-col">
                <small>{c}</small>
                <div className="feature-preview-line" />
                <div className="feature-preview-line short" />
              </div>
            ))}
          </div>
        </div>
      )}
    </motion.article>
  )
}

export function Features() {
  return (
    <section id="features" className="page-section page-section--wide features-section">
      <div className="features-ambient features-ambient--left" aria-hidden />
      <div className="features-ambient features-ambient--right" aria-hidden />

      <Reveal>
        <SectionHeader
          label="Features"
          title="Built different. Built complete."
          subtitle="Every system is production-grade — from the NanoVG renderer to the event bus architecture that powers 60+ modules."
        />
      </Reveal>

      <div className="features-bento">
        {FEATURES.map((feature, i) => (
          <Reveal key={feature.title} delay={i * 0.04} className="feature-reveal">
            <FeatureCard feature={feature} index={i} />
          </Reveal>
        ))}
      </div>
    </section>
  )
}
