import { Navbar } from './components/Navbar'
import { Hero } from './components/Hero'
import { Features } from './components/Features'
import { Modules } from './components/Modules'
import { Download } from './components/Download'
import { Releases } from './components/Releases'
import { InstallGuide } from './components/InstallGuide'
import { Commands } from './components/Commands'
import { Faq } from './components/Faq'
import { Footer } from './components/Footer'

export default function App() {
  return (
    <>
      <Navbar />
      <main>
        <Hero />
        <div className="glow-line" />
        <Features />
        <div className="glow-line" />
        <Modules />
        <div className="glow-line" />
        <Download />
        <div className="glow-line" />
        <Releases />
        <InstallGuide />
        <Commands />
        <Faq />
      </main>
      <Footer />
    </>
  )
}
