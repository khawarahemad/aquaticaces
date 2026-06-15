import './SectionHeader.css'

type SectionHeaderProps = {
  label: string
  title: string
  subtitle: string
  align?: 'left' | 'center'
}

export function SectionHeader({ label, title, subtitle, align = 'left' }: SectionHeaderProps) {
  return (
    <div className={`section-header section-header--${align}`}>
      <span className="section-label">
        <span className="section-label-dot" />
        {label}
      </span>
      <h2 className="section-title">{title}</h2>
      <p className="section-subtitle">{subtitle}</p>
    </div>
  )
}
