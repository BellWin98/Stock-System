import { fetchAccount } from '@/lib/api'
import { formatKrw } from '@/lib/utils'
import type { Account } from '@/types/order'
import { useCallback, useEffect, useState } from 'react'
import { Card } from './ui/card'

export const Header = () => {
  const [account, setAccount] = useState<Account | null>(null)
  const [error, setError] = useState<string | null>(null)

  const loadAccount = useCallback(async () => {
    try {
      const data = await fetchAccount()
      setAccount(data)
      setError(null)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load account')
    }
  }, [])

  useEffect(() => {
    loadAccount()
    const interval = setInterval(loadAccount, 3000)
    return () => clearInterval(interval)
  }, [loadAccount])

  return (
    <Card className="mb-6">
      <section className="flex flex-wrap items-center justify-between gap-4">
        <header>
          <p className="text-sm text-slate-400">계좌 (ID: 1)</p>
          <h1 className="text-2xl font-bold text-white">실시간 주문/체결 시스템</h1>
        </header>
        <dl className="grid grid-cols-1 gap-2 text-right sm:grid-cols-3 sm:gap-6">
          <div>
            <dt className="text-xs text-slate-400">총 잔고</dt>
            <dd className="text-lg font-semibold">{account ? formatKrw(account.cashBalance) : '-'}</dd>
          </div>
          <div>
            <dt className="text-xs text-slate-400">잠금 증거금</dt>
            <dd className="text-lg font-semibold text-amber-400">
              {account ? formatKrw(account.lockedMargin) : '-'}
            </dd>
          </div>
          <div>
            <dt className="text-xs text-slate-400">주문 가능</dt>
            <dd className="text-lg font-semibold text-emerald-400">
              {account ? formatKrw(account.availableBalance) : '-'}
            </dd>
          </div>
        </dl>
      </section>
      {error && (
        <p className="mt-2 text-sm text-rose-400" role="alert">
          {error}
        </p>
      )}
    </Card>
  )
}
