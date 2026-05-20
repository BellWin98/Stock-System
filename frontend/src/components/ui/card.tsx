import { cn } from '@/lib/utils'
import type { HTMLAttributes } from 'react'

export const Card = ({ className, ...props }: HTMLAttributes<HTMLDivElement>) => (
  <div className={cn('rounded-xl border border-slate-700 bg-slate-900/80 p-6 shadow-lg', className)} {...props} />
)
