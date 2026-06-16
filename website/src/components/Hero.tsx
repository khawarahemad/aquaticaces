import { motion } from 'framer-motion'
import { MOD } from '../data/modData'
import './Hero.css'

const PANELS = [
  { name: 'Combat', color: '#ff4466', modules: ['KillAura', 'Backtrack', 'Surround'] },
  { name: 'Movement', color: '#00c6ff', modules: ['Scaffold', 'Flight', 'Speed'] },
  { name: 'Render', color: '#a97bff', modules: ['ESP', 'Tracers', 'ClickGUI'] },
]

export function Hero() {
  return (
    <header className="hero">
      <div className="hero-orb hero-orb--1" aria-hidden />
      <div className="hero-orb hero-orb--2" aria-hidden />
      <div className="hero-orb hero-orb--3" aria-hidden />
      <div className="hero-grid" aria-hidden />

      <div className="hero-layout">
        <motion.div
          className="hero-content"
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, ease: [0.22, 1, 0.36, 1] }}
        >
          <div className="hero-badges">
            <span className="badge badge-live">
              <span className="badge-pulse" />
              Live v{MOD.version}
            </span>
            <span className="badge">MC {MOD.minecraft}</span>
            <span className="badge">Fabric</span>
            <span className="badge">Kotlin</span>
          </div>

          <h1 className="hero-title">
            <span className="hero-brand">Aquatic Aces</span>
            <span className="hero-title-line">The most <span className="gradient-text">advanced</span> Fabric client for 1.21</span>
          </h1>

          <p className="hero-tagline">
            Free open-source Minecraft {MOD.minecraft} Fabric client — NanoVG ClickGUI, 60+ modules
            including KillAura, ESP, and Scaffold, plus drag-and-drop HUD editor, schematic preview,
            and a full command system.
          </p>

          <div className="hero-actions">
            <a href="#download" className="btn btn-primary">
              <span>↓</span> Download v{MOD.version}
            </a>
            <a href="#install" className="btn btn-secondary">
              Setup Guide
            </a>
            <a
              href={MOD.repoUrl}
              className="btn btn-secondary"
              target="_blank"
              rel="noopener noreferrer"
            >
              Source Repo ↗
            </a>
            <a
              href={MOD.discordUrl}
              className="btn btn-secondary btn-discord"
              target="_blank"
              rel="noopener noreferrer"
            >
              Join Discord ↗
            </a>
          </div>

          <div className="hero-stats">
            {[
              { value: '60+', label: 'Modules' },
              { value: '8', label: 'Categories' },
              { value: '6', label: 'HUDs' },
              { value: '10', label: 'Commands' },
            ].map((stat, i) => (
              <motion.div
                key={stat.label}
                className="stat"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.4 + i * 0.08, duration: 0.5 }}
              >
                <span className="stat-value">{stat.value}</span>
                <span className="stat-label">{stat.label}</span>
              </motion.div>
            ))}
          </div>
        </motion.div>

        <motion.div
          className="hero-preview"
          initial={{ opacity: 0, x: 60, rotateY: -8 }}
          animate={{ opacity: 1, x: 0, rotateY: 0 }}
          transition={{ duration: 1, delay: 0.2, ease: [0.22, 1, 0.36, 1] }}
        >
          <div className="preview-glow" aria-hidden />
          <div className="clickgui-mock glass-card">
            <div className="mock-toolbar">
              <span className="mock-dot mock-dot--red" />
              <span className="mock-dot mock-dot--yellow" />
              <span className="mock-dot mock-dot--green" />
              <span className="mock-search">Search modules...</span>
              <div className="mock-keybind" aria-label="Press Right Shift to open ClickGUI">
                <kbd>⇧</kbd>
                <span>Right Shift</span>
              </div>
            </div>
            <div className="mock-panels">
              {PANELS.map((panel, i) => (
                <motion.div
                  key={panel.name}
                  className="mock-panel"
                  initial={{ opacity: 0, y: 16 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.6 + i * 0.12 }}
                >
                  <div className="mock-panel-header" style={{ borderColor: `${panel.color}44` }}>
                    <span style={{ color: panel.color }}>{panel.name}</span>
                  </div>
                  {panel.modules.map((mod) => (
                    <div key={mod} className="mock-module">
                      <span className="mock-module-dot" style={{ background: panel.color }} />
                      {mod}
                    </div>
                  ))}
                </motion.div>
              ))}
            </div>
            <div className="mock-hud">
              <div className="mock-hud-card">
                <span className="mock-hud-label">Target</span>
                <div className="mock-hud-bar"><div className="mock-hud-fill" /></div>
              </div>
              <div className="mock-hud-card mock-hud-card--small">
                <span>142 FPS</span>
              </div>
            </div>
          </div>
        </motion.div>
      </div>
    </header>
  )
}
