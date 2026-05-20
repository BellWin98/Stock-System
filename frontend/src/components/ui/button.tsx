import { cn } from '@/lib/utils'
import type { ButtonHTMLAttributes } from 'react'

type ButtonVariant = 'default' | 'outline' | 'buy' | 'sell'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant
}

const variantClasses: Record<ButtonVariant, string> = {
  default: 'bg-slate-100 text-slate-900 hover:bg-white',
  outline: 'border border-slate-600 bg-transparent hover:bg-slate-800',
  buy: 'bg-emerald-600 text-white hover:bg-emerald-500',
  sell: 'bg-rose-600 text-white hover:bg-rose-500',
}

export const Button = ({
  className,
  variant = 'default',
  type = 'button',
  ...props
}: ButtonProps) => (
  <button
    type={type}
    className={cn(
      'inline-flex items-center justify-center rounded-md px-4 py-2 text-sm font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50',
      variantClasses[variant],
      className,
    )}
    {...props}
  />
)
