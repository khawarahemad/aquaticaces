import './Logo.css'

type LogoProps = {
  size?: number
  showWordmark?: boolean
  className?: string
}

export function Logo({ size = 36, showWordmark = false, className = '' }: LogoProps) {
  return (
    <span className={`brand-logo ${className}`}>
      <svg
        className="brand-logo-mark"
        width={size}
        height={size}
        viewBox="0 0 48 48"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        aria-hidden
      >
        <defs>
          <linearGradient id="logo-bg" x1="4" y1="4" x2="44" y2="44" gradientUnits="userSpaceOnUse">
            <stop stopColor="#0d1118" />
            <stop offset="1" stopColor="#131a28" />
          </linearGradient>
          <linearGradient id="logo-accent" x1="10" y1="8" x2="38" y2="40" gradientUnits="userSpaceOnUse">
            <stop stopColor="#00c6ff" />
            <stop offset="0.55" stopColor="#0072ff" />
            <stop offset="1" stopColor="#5b8dff" />
          </linearGradient>
          <linearGradient id="logo-wave" x1="8" y1="30" x2="40" y2="38" gradientUnits="userSpaceOnUse">
            <stop stopColor="#00c6ff" stopOpacity="0.9" />
            <stop offset="1" stopColor="#0072ff" stopOpacity="0.35" />
          </linearGradient>
        </defs>
        <rect width="48" height="48" rx="12" fill="url(#logo-bg)" />
        <rect x="1" y="1" width="46" height="46" rx="11" stroke="url(#logo-accent)" strokeOpacity="0.35" strokeWidth="1" />
        <path d="M24 10 L33 34 H29.2 L27.1 28 H20.9 L18.8 34 H15 L24 10 Z" fill="url(#logo-accent)" />
        <path d="M21.2 24 H26.8" stroke="#06070b" strokeWidth="2.2" strokeLinecap="round" />
        <path d="M10 36 C16 32 20 34 24 36 C28 38 32 35 38 36" stroke="url(#logo-wave)" strokeWidth="2.2" strokeLinecap="round" />
        <circle cx="36" cy="14" r="2.5" fill="#00c6ff" opacity="0.85" />
      </svg>
      {showWordmark && (
        <span className="brand-logo-text">
          <span className="brand-logo-title">Aquatic</span>
          <span className="brand-logo-sub">Aces</span>
        </span>
      )}
    </span>
  )
}
