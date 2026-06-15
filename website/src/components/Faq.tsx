import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { FAQ } from '../data/modData'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Faq.css'

export function Faq() {
  const [open, setOpen] = useState<number | null>(0)

  return (
    <section id="faq" className="page-section">
      <Reveal>
        <SectionHeader
          label="FAQ"
          title="Questions answered"
          subtitle="Everything you need to know before installing Aquatic Aces."
          align="center"
        />
      </Reveal>

      <div className="faq-list">
        {FAQ.map((item, i) => (
          <Reveal key={item.q} delay={i * 0.06}>
            <article className={`faq-item glass-card ${open === i ? 'open' : ''}`}>
              <button
                type="button"
                className="faq-question"
                onClick={() => setOpen(open === i ? null : i)}
                aria-expanded={open === i}
              >
                <span>{item.q}</span>
                <motion.span
                  className="faq-chevron"
                  animate={{ rotate: open === i ? 180 : 0 }}
                  transition={{ duration: 0.25 }}
                >
                  ⌄
                </motion.span>
              </button>
              <AnimatePresence>
                {open === i && (
                  <motion.div
                    className="faq-answer-wrap"
                    initial={{ height: 0, opacity: 0 }}
                    animate={{ height: 'auto', opacity: 1 }}
                    exit={{ height: 0, opacity: 0 }}
                    transition={{ duration: 0.3, ease: [0.22, 1, 0.36, 1] }}
                  >
                    <p className="faq-answer">{item.a}</p>
                  </motion.div>
                )}
              </AnimatePresence>
            </article>
          </Reveal>
        ))}
      </div>
    </section>
  )
}
