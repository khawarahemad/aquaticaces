import { motion } from 'framer-motion'
import { FEATURES } from '../data/modData'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Features.css'

const BENTO_SIZES = ['bento--wide', 'bento--tall', '', '', 'bento--wide', '', 'bento--tall', '']

export function Features() {
  return (
    <section id="features" className="page-section page-section--wide">
      <Reveal>
        <SectionHeader
          label="Features"
          title="Built different. Built complete."
          subtitle="Every system is production-grade — from the NanoVG renderer to the event bus architecture that powers 60+ modules."
        />
      </Reveal>

      <div className="bento-grid">
        {FEATURES.map((feature, i) => (
          <Reveal key={feature.title} delay={i * 0.06}>
            <motion.article
              className={`bento-card glass-card ${BENTO_SIZES[i]}`}
              whileHover={{ y: -6, transition: { duration: 0.3 } }}
            >
              <div className="bento-icon-wrap">
                <span className="bento-icon">{feature.icon}</span>
              </div>
              <h3 className="bento-title">{feature.title}</h3>
              <p className="bento-desc">{feature.description}</p>
              <div className="bento-shine" aria-hidden />
            </motion.article>
          </Reveal>
        ))}
      </div>
    </section>
  )
}
