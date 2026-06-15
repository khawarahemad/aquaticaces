import { motion } from 'framer-motion'
import { DOWNLOADS, MOD } from '../data/modData'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Download.css'

export function Download() {
  const primary = DOWNLOADS.find((d) => d.primary)!

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

      <Reveal delay={0.1}>
        <motion.div
          className="download-hero glass-card"
          whileHover={{ scale: 1.01 }}
          transition={{ duration: 0.3 }}
        >
          <div className="download-hero-glow" aria-hidden />
          <div className="download-hero-content">
            <span className="download-hero-icon">⚡</span>
            <div>
              <h3>Aquatic Aces v{MOD.version}</h3>
              <p>The complete Fabric client — ready to drop in your mods folder.</p>
            </div>
          </div>
          <a href={primary.file} className="btn btn-primary download-hero-btn" download>
            Download JAR — {primary.size}
          </a>
        </motion.div>
      </Reveal>

      <div className="download-grid">
        {DOWNLOADS.filter((d) => !d.primary).map((item, i) => (
          <Reveal key={item.name} delay={0.15 + i * 0.08}>
            <motion.a
              href={item.file}
              className="download-card glass-card"
              {...('external' in item && item.external
                ? { target: '_blank', rel: 'noopener noreferrer' }
                : { download: true })}
              whileHover={{ y: -4 }}
            >
              <span className="download-card-icon">{item.icon}</span>
              <div className="download-card-body">
                <h4>{item.name}</h4>
                <span className="download-card-ver">v{item.version}</span>
                <p>{item.description}</p>
              </div>
              <span className="download-card-arrow">↗</span>
            </motion.a>
          </Reveal>
        ))}
      </div>

      <Reveal delay={0.2}>
        <div className="requirements glass-card">
          <h3>System Requirements</h3>
          <div className="req-grid">
            {[
              ['Minecraft', '1.21 Java Edition'],
              ['Java', 'JDK 21 (required)'],
              ['Fabric Loader', `${MOD.loader}+`],
              ['Fabric API', MOD.fabricApi],
              ['RAM', '4 GB min · 6+ GB recommended'],
            ].map(([k, v]) => (
              <div key={k} className="req-item">
                <span className="req-key">{k}</span>
                <span className="req-val">{v}</span>
              </div>
            ))}
          </div>
        </div>
      </Reveal>
    </section>
  )
}
