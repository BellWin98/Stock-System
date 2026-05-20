import { cn } from '@/lib/utils'
import type { InputHTMLAttributes } from 'react'

export const Input = ({ className, ...props }: InputHTMLAttributes<HTMLInputElement>) => (
  <input
    className={cn(
      'flex h-10 w-full rounded-md border border-slate-600 bg-slate-800 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500',
      className,
    )}
    {...props}
  />
)
