import { MOD } from '../data/modData'
import { Logo } from './ui/Logo'
import './Footer.css'

export function Footer() {
  return (
    <footer className="footer">
      <div className="footer-glow" aria-hidden />
      <div className="footer-cta glass-card">
        <div className="footer-cta-text">
          <h2>Ready to dominate?</h2>
          <p>Download Aquatic Aces and experience the most polished Fabric client for 1.21.</p>
        </div>
        <a href="#download" className="btn btn-primary">Download v{MOD.version}</a>
      </div>

      <div className="footer-inner">
        <div className="footer-brand">
          <span className="footer-logo">
            <Logo size={36} showWordmark />
          </span>
          <p>Modular Fabric client · Kotlin · NanoVG · MIT License</p>
        </div>
        <div className="footer-links">
          <div className="footer-col">
            <h4>Site</h4>
            <a href="#features">Features</a>
            <a href="#download">Download</a>
            <a href="#releases">Releases</a>
            <a href="#install">Install</a>
            <a href="#faq">FAQ</a>
          </div>
          <div className="footer-col">
            <h4>Dependencies</h4>
            <a href="https://fabricmc.net/use/installer/" target="_blank" rel="noopener noreferrer">Fabric</a>
            <a href="https://modrinth.com/mod/fabric-api" target="_blank" rel="noopener noreferrer">Fabric API</a>
            <a href="https://modrinth.com/mod/fabric-language-kotlin" target="_blank" rel="noopener noreferrer">Fabric Kotlin</a>
            <a href="https://adoptium.net/temurin/releases/?version=21" target="_blank" rel="noopener noreferrer">Java 21</a>
          </div>
          <div className="footer-col">
            <h4>Project</h4>
            <a href={MOD.repoUrl} target="_blank" rel="noopener noreferrer">Source Repo</a>
            <a href="https://www.figma.com/design/X9yZix9KMGPpcUh4HJoean" target="_blank" rel="noopener noreferrer">Figma Design</a>
            <span className="footer-meta">v{MOD.version}</span>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <p>For educational and private/testing use only. Not affiliated with Mojang or Microsoft.</p>
        <p>© {new Date().getFullYear()} Aquatic Aces Team</p>
      </div>
    </footer>
  )
}
