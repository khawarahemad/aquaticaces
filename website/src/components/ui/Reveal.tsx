import { motion, useInView } from 'framer-motion'
import { useRef, type ReactNode } from 'react'
import './Reveal.css'

type RevealProps = {
  children: ReactNode
  delay?: number
  className?: string
  direction?: 'up' | 'left' | 'right'
}

export function Reveal({ children, delay = 0, className = '', direction = 'up' }: RevealProps) {
  const ref = useRef(null)
  const inView = useInView(ref, { once: true, margin: '-80px' })

  const offset = direction === 'up' ? { y: 48 } : direction === 'left' ? { x: -48 } : { x: 48 }

  return (
    <motion.div
      ref={ref}
      className={`reveal ${className}`}
      initial={{ opacity: 0, ...offset }}
      animate={inView ? { opacity: 1, x: 0, y: 0 } : { opacity: 0, ...offset }}
      transition={{ duration: 0.7, delay, ease: [0.22, 1, 0.36, 1] }}
    >
      {children}
    </motion.div>
  )
}
