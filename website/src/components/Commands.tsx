import { COMMANDS, MOD } from '../data/modData'
import { Reveal } from './ui/Reveal'
import { SectionHeader } from './ui/SectionHeader'
import './Commands.css'

export function Commands() {
  return (
    <section id="commands" className="page-section">
      <Reveal>
        <SectionHeader
          label="Commands"
          title="Full dot-prefix system"
          subtitle={`Type ${MOD.commandPrefix} in chat — commands never reach the server.`}
        />
      </Reveal>

      <Reveal delay={0.1}>
        <div className="terminal glass-card">
          <div className="terminal-bar">
            <span className="terminal-dot terminal-dot--red" />
            <span className="terminal-dot terminal-dot--yellow" />
            <span className="terminal-dot terminal-dot--green" />
            <span className="terminal-title">aquaticaces — chat commands</span>
          </div>
          <div className="terminal-body">
            {COMMANDS.map((cmd, i) => (
              <div key={cmd.cmd} className="terminal-line" style={{ animationDelay: `${i * 0.05}s` }}>
                <span className="terminal-prompt">{MOD.commandPrefix}</span>
                <span className="terminal-cmd">{cmd.cmd.replace(/^\./, '')}</span>
                <span className="terminal-comment"># {cmd.desc}</span>
              </div>
            ))}
          </div>
        </div>
      </Reveal>

      <Reveal delay={0.2}>
        <div className="command-chips">
          {[
            '.toggle KillAura',
            '.bind Scaffold R',
            '.config save PvP COMBAT',
            '.schematic save base 12',
            '.hud edit',
          ].map((ex) => (
            <code key={ex} className="command-chip">{ex}</code>
          ))}
        </div>
      </Reveal>
    </section>
  )
}
