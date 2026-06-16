import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { NAV_LINKS, MOD } from '../data/modData'
import { Logo } from './ui/Logo'
import './Navbar.css'

function DiscordIcon() {
  return (
    <svg viewBox="0 0 24 24" className="navbar-icon" aria-hidden>
      <path
        fill="currentColor"
        d="M20.317 4.37a19.79 19.79 0 0 0-4.885-1.515.074.074 0 0 0-.079.037 12.3 12.3 0 0 0-.608 1.25 18.27 18.27 0 0 0-5.487 0 11.64 11.64 0 0 0-.617-1.25.077.077 0 0 0-.079-.037A19.74 19.74 0 0 0 3.677 4.37a.07.07 0 0 0-.032.027C1.566 7.76.48 11.1.734 14.39a.082.082 0 0 0 .031.057 19.9 19.9 0 0 0 5.993 3.03.078.078 0 0 0 .084-.028 13.83 13.83 0 0 0 1.226-1.994.076.076 0 0 0-.041-.106 13.2 13.2 0 0 1-1.872-.892.077.077 0 0 1-.008-.128 10.2 10.2 0 0 0 .372-.292.074.074 0 0 1 .077-.01c3.928 1.793 8.18 1.793 12.062 0a.074.074 0 0 1 .078.01c.12.098.246.198.373.292a.077.077 0 0 1-.006.127 12.3 12.3 0 0 1-1.873.892.077.077 0 0 0-.041.107 15.4 15.4 0 0 0 1.225 1.993.076.076 0 0 0 .084.028 19.84 19.84 0 0 0 6.002-3.03.077.077 0 0 0 .032-.054c.334-3.79-.559-7.106-2.911-9.993a.061.061 0 0 0-.031-.03zM8.02 12.34c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.956-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.956 2.418-2.157 2.418zm7.975 0c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.955-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.946 2.418-2.157 2.418z"
      />
    </svg>
  )
}

function GitHubIcon() {
  return (
    <svg viewBox="0 0 24 24" className="navbar-icon" aria-hidden>
      <path
        fill="currentColor"
        d="M12 2C6.48 2 2 6.58 2 12.25c0 4.52 2.87 8.35 6.84 9.7.5.1.68-.22.68-.48 0-.24-.01-.87-.01-1.7-2.78.62-3.37-1.36-3.37-1.36-.45-1.17-1.12-1.48-1.12-1.48-.92-.64.07-.63.07-.63 1.02.07 1.55 1.07 1.55 1.07.9 1.57 2.36 1.12 2.94.86.09-.67.35-1.12.63-1.38-2.22-.26-4.55-1.14-4.55-5.07 0-1.12.39-2.03 1.03-2.75-.1-.26-.45-1.32.1-2.74 0 0 .84-.27 2.75 1.05A9.2 9.2 0 0112 6.84c.85 0 1.7.12 2.5.34 1.9-1.32 2.74-1.05 2.74-1.05.55 1.42.2 2.48.1 2.74.64.72 1.03 1.63 1.03 2.75 0 3.94-2.34 4.81-4.57 5.06.36.32.68.94.68 1.9 0 1.37-.01 2.47-.01 2.8 0 .27.18.59.69.49A10.03 10.03 0 0022 12.25C22 6.58 17.52 2 12 2z"
      />
    </svg>
  )
}

export function Navbar() {
  const [scrolled, setScrolled] = useState(false)
  const [menuOpen, setMenuOpen] = useState(false)
  const [scrollProgress, setScrollProgress] = useState(0)
  const [activeSection, setActiveSection] = useState(NAV_LINKS[0].href.slice(1))

  useEffect(() => {
    const onScroll = () => {
      setScrolled(window.scrollY > 24)
      const max = document.body.scrollHeight - window.innerHeight
      setScrollProgress(max > 0 ? window.scrollY / max : 0)
    }
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  useEffect(() => {
    const sections = NAV_LINKS
      .map((link) => document.querySelector<HTMLElement>(link.href))
      .filter((el): el is HTMLElement => el !== null)

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setActiveSection(entry.target.id)
          }
        })
      },
      { rootMargin: '-35% 0px -55% 0px', threshold: 0 },
    )

    sections.forEach((section) => observer.observe(section))
    return () => observer.disconnect()
  }, [])

  useEffect(() => {
    document.body.style.overflow = menuOpen ? 'hidden' : ''
    return () => {
      document.body.style.overflow = ''
    }
  }, [menuOpen])

  const showProgress = scrollProgress > 0.005 && !menuOpen

  return (
    <>
      <motion.header
        className={`navbar ${scrolled ? 'navbar--scrolled' : ''} ${menuOpen ? 'navbar--menu-open' : ''}`}
        initial={{ y: -80, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
      >
        <div className="navbar-shell">
          <nav className="navbar-inner" aria-label="Main navigation">
            <a href="#" className="navbar-logo" onClick={() => setMenuOpen(false)}>
              <Logo size={36} showWordmark />
              <span className="navbar-version">v{MOD.version}</span>
            </a>

            <div className="navbar-links">
              {NAV_LINKS.map((link) => {
                const id = link.href.slice(1)
                const isActive = activeSection === id

                return (
                  <a
                    key={link.href}
                    href={link.href}
                    className={`nav-link ${isActive ? 'nav-link--active' : ''}`}
                    aria-current={isActive ? 'page' : undefined}
                  >
                    <span className="nav-link-text">{link.label}</span>
                    {isActive && <span className="nav-link-indicator" aria-hidden />}
                  </a>
                )
              })}
            </div>

            <div className="navbar-actions">
              <a
                href={MOD.discordUrl}
                className="navbar-discord"
                target="_blank"
                rel="noopener noreferrer"
              >
                <DiscordIcon />
                <span>Discord</span>
              </a>
              <a
                href={MOD.repoUrl}
                className="navbar-repo"
                target="_blank"
                rel="noopener noreferrer"
              >
                <GitHubIcon />
                <span>GitHub</span>
              </a>
              <a href="#download" className="navbar-cta">
                <span className="navbar-cta-text">Download</span>
                <span className="navbar-cta-arrow" aria-hidden>↓</span>
                <span className="navbar-cta-icon" aria-hidden>↓</span>
              </a>
              <button
                type="button"
                className={`menu-toggle ${menuOpen ? 'open' : ''}`}
                onClick={() => setMenuOpen(!menuOpen)}
                aria-label="Toggle menu"
                aria-expanded={menuOpen}
              >
                <span /><span /><span />
              </button>
            </div>
          </nav>

          <div className="navbar-progress-track" aria-hidden>
            <div
              className="navbar-progress"
              style={{ width: showProgress ? `${scrollProgress * 100}%` : '0%' }}
            />
          </div>
        </div>
      </motion.header>

      <AnimatePresence>
        {menuOpen && (
          <motion.div
            className="mobile-menu"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.25 }}
            onClick={() => setMenuOpen(false)}
          >
            <motion.aside
              className="mobile-menu-panel"
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ duration: 0.35, ease: [0.22, 1, 0.36, 1] }}
              onClick={(e) => e.stopPropagation()}
            >
              <div className="mobile-menu-head">
                <Logo size={32} showWordmark />
                <button
                  type="button"
                  className="mobile-menu-close"
                  onClick={() => setMenuOpen(false)}
                  aria-label="Close menu"
                >
                  ×
                </button>
              </div>

              <div className="mobile-menu-meta">
                <span>MC {MOD.minecraft}</span>
                <span>Fabric</span>
                <span>v{MOD.version}</span>
              </div>

              <div className="mobile-menu-links">
                {NAV_LINKS.map((link, i) => {
                  const id = link.href.slice(1)
                  const isActive = activeSection === id

                  return (
                    <motion.a
                      key={link.href}
                      href={link.href}
                      className={`mobile-link ${isActive ? 'mobile-link--active' : ''}`}
                      onClick={() => setMenuOpen(false)}
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ delay: 0.05 + i * 0.04 }}
                    >
                      <span className="mobile-link-index">{String(i + 1).padStart(2, '0')}</span>
                      <span>{link.label}</span>
                      <span className="mobile-link-arrow" aria-hidden>→</span>
                    </motion.a>
                  )
                })}
              </div>

              <div className="mobile-menu-footer">
                <a
                  href={MOD.discordUrl}
                  className="mobile-discord"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <DiscordIcon />
                  Join Discord
                </a>
                <a
                  href={MOD.repoUrl}
                  className="mobile-repo"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <GitHubIcon />
                  Source Repo
                </a>
                <a href="#download" className="mobile-cta" onClick={() => setMenuOpen(false)}>
                  Download v{MOD.version}
                </a>
              </div>
            </motion.aside>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  )
}
