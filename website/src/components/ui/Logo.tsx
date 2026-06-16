import { assetUrl } from '../../lib/assetUrl'
import './Logo.css'

const LOGO_SRC = '/AQ.jpg'

type LogoProps = {
  size?: number
  showWordmark?: boolean
  className?: string
}

export function Logo({ size = 36, showWordmark = false, className = '' }: LogoProps) {
  return (
    <span className={`brand-logo ${className}`}>
      <img
        className="brand-logo-mark"
        src={assetUrl(LOGO_SRC)}
        alt=""
        width={size}
        height={size}
        aria-hidden
      />
      {showWordmark && (
        <span className="brand-logo-text">
          <span className="brand-logo-title">Aquatic</span>
          <span className="brand-logo-sub">Aces</span>
        </span>
      )}
    </span>
  )
}
