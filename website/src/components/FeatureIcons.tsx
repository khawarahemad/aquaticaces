import './FeatureIcons.css'

type IconProps = { accent?: string; className?: string }

export function FeatureIcon({ tag, accent = '#00c6ff', className = '' }: { tag: string; accent?: string } & IconProps) {
  const props = { accent, className: `feature-svg ${className}` }

  switch (tag) {
    case 'UI':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <rect x="3" y="4" width="18" height="14" rx="2" stroke={accent} strokeWidth="1.5" />
          <path d="M3 8h18" stroke={accent} strokeWidth="1.5" opacity="0.5" />
          <rect x="5" y="10" width="5" height="6" rx="1" fill={accent} fillOpacity="0.2" />
          <rect x="11.5" y="10" width="5" height="6" rx="1" fill={accent} fillOpacity="0.12" />
        </svg>
      )
    case 'Overlay':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <rect x="4" y="6" width="16" height="12" rx="2" stroke={accent} strokeWidth="1.5" />
          <path d="M7 14h6" stroke={accent} strokeWidth="2" strokeLinecap="round" />
          <rect x="7" y="9" width="10" height="2" rx="1" fill={accent} fillOpacity="0.35" />
        </svg>
      )
    case 'Core':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <path d="M12 3l8 4.5v9L12 21l-8-4.5v-9L12 3z" stroke={accent} strokeWidth="1.5" />
          <path d="M12 8v8M8.5 10.5l7 3M15.5 10.5l-7 3" stroke={accent} strokeWidth="1.2" opacity="0.7" />
        </svg>
      )
    case 'Profiles':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <path d="M5 6h14v12H5z" stroke={accent} strokeWidth="1.5" />
          <path d="M8 10h8M8 13h5" stroke={accent} strokeWidth="1.5" strokeLinecap="round" />
          <circle cx="17" cy="7" r="2" fill={accent} fillOpacity="0.5" />
        </svg>
      )
    case 'World':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <path d="M4 18l4-8 4 5 4-9 4 12" stroke={accent} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
          <rect x="3" y="18" width="18" height="2" rx="1" fill={accent} fillOpacity="0.25" />
        </svg>
      )
    case 'Social':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <circle cx="9" cy="10" r="3" stroke={accent} strokeWidth="1.5" />
          <circle cx="16" cy="11" r="2.5" stroke={accent} strokeWidth="1.5" />
          <path d="M4 18c0-2.8 2.2-5 5-5s5 2.2 5 5" stroke={accent} strokeWidth="1.5" />
        </svg>
      )
    case 'Engine':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <path d="M13 3L5 14h6l-1 7 8-11h-6l1-7z" stroke={accent} strokeWidth="1.5" strokeLinejoin="round" />
        </svg>
      )
    case 'Chat':
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <path d="M5 6h14a2 2 0 012 2v6a2 2 0 01-2 2H10l-4 3v-3H5a2 2 0 01-2-2V8a2 2 0 012-2z" stroke={accent} strokeWidth="1.5" />
          <path d="M8 11h8" stroke={accent} strokeWidth="1.5" strokeLinecap="round" />
        </svg>
      )
    default:
      return (
        <svg viewBox="0 0 24 24" fill="none" {...props}>
          <circle cx="12" cy="12" r="8" stroke={accent} strokeWidth="1.5" />
        </svg>
      )
  }
}
