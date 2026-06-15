import { INSTALL_STEPS, MOD } from '../data/modData'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './InstallGuide.css'

export function InstallGuide() {
  return (
    <section id="install" className="page-section">
      <Reveal>
        <SectionHeader
          label="Installation"
          title="Five steps to launch"
          subtitle={`Press ${MOD.defaultKeybind} in-game to open the ClickGUI. Configs save to .minecraft/aquaticaces/.`}
        />
      </Reveal>

      <div className="install-timeline">
        {INSTALL_STEPS.map((step, i) => (
          <Reveal key={step.step} delay={i * 0.08} direction={i % 2 === 0 ? 'left' : 'right'}>
            <article className="timeline-step glass-card">
              <div className="timeline-marker">
                <span className="timeline-num">{step.step}</span>
                {i < INSTALL_STEPS.length - 1 && <div className="timeline-line" />}
              </div>
              <div className="timeline-body">
                <h3>{step.title}</h3>
                <p>{step.body}</p>
              </div>
            </article>
          </Reveal>
        ))}
      </div>

      <Reveal delay={0.3}>
        <div className="mods-folder glass-card">
          <div className="mods-folder-header">
            <span className="folder-icon">📁</span>
            <div>
              <h3>Your mods folder</h3>
              <code>.minecraft/mods/</code>
            </div>
          </div>
          <div className="folder-files">
            <div className="folder-file">
              <span className="file-icon">📦</span>
              <span>fabric-api-{MOD.fabricApi}.jar</span>
              <span className="file-tag">Required</span>
            </div>
            <div className="folder-file folder-file--main">
              <span className="file-icon">⚡</span>
              <span>aquaticaces-{MOD.version}.jar</span>
              <span className="file-tag file-tag--accent">Main Mod</span>
            </div>
          </div>
        </div>
      </Reveal>
    </section>
  )
}
