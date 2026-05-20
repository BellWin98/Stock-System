import { cn } from '@/lib/utils'
import type { OrderStatus } from '@/types/order'
import type { HTMLAttributes } from 'react'

const statusStyles: Record<OrderStatus, string> = {
  PENDING: 'bg-slate-600 text-slate-100',
  ACCEPTED: 'bg-blue-600 text-white',
  FILLED: 'bg-emerald-600 text-white',
  REJECTED: 'bg-rose-600 text-white',
}

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  status: OrderStatus
}

export const Badge = ({ status, className, ...props }: BadgeProps) => (
  <span
    className={cn('inline-flex rounded-full px-2.5 py-0.5 text-xs font-semibold', statusStyles[status], className)}
    {...props}
  >
    {status}
  </span>
)
