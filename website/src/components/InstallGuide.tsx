import { INSTALL_STEPS, MOD } from '../data/modData'
import { Logo } from './ui/Logo'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './InstallGuide.css'

const STEP_ICONS = ['☕', '🧵', '📦', '🟣', '⚡', '🎮']

export function InstallGuide() {
  return (
    <section id="install" className="page-section install-section">
      <Reveal>
        <SectionHeader
          label="Installation"
          title="Six steps to launch"
          subtitle={`Press ${MOD.defaultKeybind} in-game to open the ClickGUI. Configs save to .minecraft/aquaticaces/.`}
        />
      </Reveal>

      <Reveal delay={0.08}>
        <div className="install-track glass-card">
          {INSTALL_STEPS.map((step, i) => (
            <div key={step.step} className="install-track-step">
              <div className="install-track-node">
                <span>{step.step}</span>
              </div>
              {i < INSTALL_STEPS.length - 1 && <div className="install-track-line" />}
            </div>
          ))}
        </div>
      </Reveal>

      <div className="install-cards">
        {INSTALL_STEPS.map((step, i) => (
          <Reveal key={step.step} delay={0.1 + i * 0.06}>
            <article className="install-card glass-card">
              <div className="install-card-icon">{STEP_ICONS[i]}</div>
              <div className="install-card-body">
                <span className="install-card-step">Step {step.step}</span>
                <h3>{step.title}</h3>
                <p>{step.body}</p>
              </div>
            </article>
          </Reveal>
        ))}
      </div>

      <Reveal delay={0.35}>
        <div className="finder-window glass-card">
          <div className="finder-titlebar">
            <span className="finder-dot finder-dot--red" />
            <span className="finder-dot finder-dot--yellow" />
            <span className="finder-dot finder-dot--green" />
            <span className="finder-title">mods — Finder</span>
          </div>
          <div className="finder-path">
            <span>~</span>
            <span>›</span>
            <span>.minecraft</span>
            <span>›</span>
            <span className="finder-path-active">mods</span>
          </div>
          <div className="finder-files">
            <div className="finder-file">
              <span className="finder-file-icon">📦</span>
              <div className="finder-file-info">
                <strong>fabric-api-{MOD.fabricApi}.jar</strong>
                <span>Required dependency</span>
              </div>
              <span className="finder-tag">Required</span>
            </div>
            <div className="finder-file">
              <span className="finder-file-icon">🟣</span>
              <div className="finder-file-info">
                <strong>fabric-language-kotlin-{MOD.fabricKotlin}.jar</strong>
                <span>Kotlin runtime for Fabric</span>
              </div>
              <span className="finder-tag">Required</span>
            </div>
            <div className="finder-file finder-file--main">
              <Logo size={32} />
              <div className="finder-file-info">
                <strong>aquaticaces-{MOD.version}.jar</strong>
                <span>Aquatic Aces client mod</span>
              </div>
              <span className="finder-tag finder-tag--accent">Main Mod</span>
            </div>
          </div>
        </div>
      </Reveal>
    </section>
  )
}
