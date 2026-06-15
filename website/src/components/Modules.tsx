import { useState, useMemo, type CSSProperties } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { MODULE_CATEGORIES } from '../data/modData'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Modules.css'

export function Modules() {
  const [active, setActive] = useState(0)
  const [search, setSearch] = useState('')
  const category = MODULE_CATEGORIES[active]

  const filtered = useMemo(() => {
    if (!search.trim()) return category.modules
    const q = search.toLowerCase()
    return category.modules.filter((m) => m.toLowerCase().includes(q))
  }, [category, search])

  return (
    <section id="modules" className="page-section">
      <Reveal>
        <SectionHeader
          label="Modules"
          title="60+ modules. Zero compromise."
          subtitle="Combat, movement, render, world, and more — each with settings, keybinds, and automatic conflict resolution."
        />
      </Reveal>

      <Reveal delay={0.1}>
        <div className="modules-shell glass-card">
          <div className="modules-sidebar">
            {MODULE_CATEGORIES.map((cat, i) => (
              <button
                key={cat.name}
                type="button"
                className={`cat-btn ${i === active ? 'active' : ''}`}
                style={{ '--cat': cat.color } as CSSProperties}
                onClick={() => { setActive(i); setSearch('') }}
              >
                <span className="cat-btn-name">{cat.name}</span>
                <span className="cat-btn-count">{cat.count}</span>
                {i === active && (
                  <motion.div className="cat-btn-indicator" layoutId="cat-indicator" />
                )}
              </button>
            ))}
          </div>

          <div className="modules-main">
            <div className="modules-main-header">
              <div>
                <h3 style={{ color: category.color }}>{category.name}</h3>
                <p>{category.count} modules in this category</p>
              </div>
              <input
                type="search"
                className="module-search"
                placeholder="Filter modules..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>

            <AnimatePresence mode="wait">
              <motion.div
                key={`${active}-${search}`}
                className="module-grid"
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -12 }}
                transition={{ duration: 0.25 }}
              >
                {filtered.map((mod, i) => (
                  <motion.div
                    key={mod}
                    className="module-chip"
                    style={{ '--chip-color': category.color } as CSSProperties}
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: i * 0.02 }}
                    whileHover={{ scale: 1.04, y: -2 }}
                  >
                    <span className="module-chip-dot" />
                    {mod}
                  </motion.div>
                ))}
                {filtered.length === 0 && (
                  <p className="module-empty">No modules match "{search}"</p>
                )}
              </motion.div>
            </AnimatePresence>
          </div>
        </div>
      </Reveal>

      <div className="module-marquee" aria-hidden>
        <div className="marquee-track">
          {[...MODULE_CATEGORIES.flatMap((c) => c.modules), ...MODULE_CATEGORIES.flatMap((c) => c.modules)].map((mod, i) => (
            <span key={`${mod}-${i}`} className="marquee-item">{mod}</span>
          ))}
        </div>
      </div>
    </section>
  )
}
