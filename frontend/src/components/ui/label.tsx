import { cn } from '@/lib/utils'
import type { LabelHTMLAttributes } from 'react'

export const Label = ({ className, ...props }: LabelHTMLAttributes<HTMLLabelElement>) => (
  <label className={cn('text-sm font-medium text-slate-300', className)} {...props} />
)
