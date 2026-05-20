import { fetchQuote } from '@/lib/api'
import { formatKrw } from '@/lib/utils'
import type { Quote } from '@/types/order'
import { useCallback, useEffect, useState } from 'react'
import { Card } from './ui/card'

interface QuotePanelProps {
  symbol: string
}

export const QuotePanel = ({ symbol }: QuotePanelProps) => {
  const [quote, setQuote] = useState<Quote | null>(null)
  const [error, setError] = useState<string | null>(null)

  const loadQuote = useCallback(async () => {
    if (!symbol) return
    try {
      const data = await fetchQuote(symbol)
      setQuote(data)
      setError(null)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load quote')
    }
  }, [symbol])

  useEffect(() => {
    loadQuote()
    const interval = setInterval(loadQuote, 3000)
    return () => clearInterval(interval)
  }, [loadQuote])

  return (
    <Card>
      <h2 className="mb-4 text-lg font-semibold">실시간 시세</h2>
      {quote ? (
        <dl className="space-y-3">
          <div>
            <dt className="text-xs text-slate-400">종목</dt>
            <dd className="text-xl font-bold">
              {quote.name} <span className="text-sm text-slate-400">({quote.symbol})</span>
            </dd>
          </div>
          <div>
            <dt className="text-xs text-slate-400">최종 체결가</dt>
            <dd className="text-2xl font-bold text-emerald-400">{formatKrw(quote.lastPrice)}</dd>
          </div>
          <div>
            <dt className="text-xs text-slate-400">체결량</dt>
            <dd className="text-lg">{quote.volume.toLocaleString()}주</dd>
          </div>
          <div>
            <dt className="text-xs text-slate-400">갱신 시각</dt>
            <dd className="text-sm text-slate-300">
              {new Date(quote.updatedAt).toLocaleString('ko-KR')}
            </dd>
          </div>
        </dl>
      ) : (
        <p className="text-slate-400">시세 로딩 중...</p>
      )}
      {error && (
        <p className="mt-2 text-sm text-rose-400" role="alert">
          {error}
        </p>
      )}
    </Card>
  )
}
