import { fetchInstruments, submitOrder } from '@/lib/api'
import { formatKrw } from '@/lib/utils'
import type { Instrument, OrderSide } from '@/types/order'
import { useCallback, useEffect, useState } from 'react'
import { Button } from './ui/button'
import { Card } from './ui/card'
import { Input } from './ui/input'
import { Label } from './ui/label'

interface OrderFormProps {
  selectedSymbol: string
  onSymbolChange: (symbol: string) => void
  onOrderSubmitted: () => void
}

export const OrderForm = ({ selectedSymbol, onSymbolChange, onOrderSubmitted }: OrderFormProps) => {
  const [instruments, setInstruments] = useState<Instrument[]>([])
  const [side, setSide] = useState<OrderSide>('BUY')
  const [quantity, setQuantity] = useState('10')
  const [price, setPrice] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  const loadInstruments = useCallback(async () => {
    try {
      const data = await fetchInstruments()
      setInstruments(data)
      if (!price && data.length > 0) {
        const selected = data.find((i) => i.symbol === selectedSymbol) ?? data[0]
        setPrice(String(selected.lastPrice))
        if (!selectedSymbol) {
          onSymbolChange(selected.symbol)
        }
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load instruments')
    }
  }, [onSymbolChange, price, selectedSymbol])

  useEffect(() => {
    loadInstruments()
  }, [loadInstruments])

  useEffect(() => {
    const instrument = instruments.find((i) => i.symbol === selectedSymbol)
    if (instrument) {
      setPrice(String(instrument.lastPrice))
    }
  }, [selectedSymbol, instruments])

  const handleSymbolChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onSymbolChange(e.target.value)
  }

  const handleSideChange = (newSide: OrderSide) => {
    setSide(newSide)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setMessage(null)
    setError(null)

    try {
      const order = await submitOrder({
        symbol: selectedSymbol,
        side,
        quantity: Number(quantity),
        price: Number(price),
      })
      setMessage(`주문 접수: ${order.id.slice(0, 8)}... (${order.status})`)
      onOrderSubmitted()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Order failed')
    } finally {
      setLoading(false)
    }
  }

  const estimatedAmount = Number(quantity) * Number(price) || 0

  return (
    <Card>
      <h2 className="mb-4 text-lg font-semibold">주문</h2>
      <form onSubmit={handleSubmit} className="space-y-4" aria-label="주문 입력 폼">
        <div>
          <Label htmlFor="symbol">종목</Label>
          <select
            id="symbol"
            value={selectedSymbol}
            onChange={handleSymbolChange}
            aria-label="종목 선택"
            className="mt-1 flex h-10 w-full rounded-md border border-slate-600 bg-slate-800 px-3 text-sm text-slate-100"
          >
            {instruments.map((i) => (
              <option key={i.symbol} value={i.symbol}>
                {i.name} ({i.symbol})
              </option>
            ))}
          </select>
        </div>

        <div className="flex gap-2">
          <Button
            type="button"
            variant={side === 'BUY' ? 'buy' : 'outline'}
            className="flex-1"
            onClick={() => handleSideChange('BUY')}
            aria-pressed={side === 'BUY'}
          >
            매수
          </Button>
          <Button
            type="button"
            variant={side === 'SELL' ? 'sell' : 'outline'}
            className="flex-1"
            onClick={() => handleSideChange('SELL')}
            aria-pressed={side === 'SELL'}
          >
            매도
          </Button>
        </div>

        <div>
          <Label htmlFor="quantity">수량</Label>
          <Input
            id="quantity"
            type="number"
            min={1}
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            aria-label="주문 수량"
            required
          />
        </div>

        <div>
          <Label htmlFor="price">지정가</Label>
          <Input
            id="price"
            type="number"
            min={1}
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            aria-label="지정가"
            required
          />
        </div>

        <p className="text-sm text-slate-400">
          예상 주문금액: <span className="font-medium text-white">{formatKrw(estimatedAmount)}</span>
        </p>

        <Button type="submit" variant={side === 'BUY' ? 'buy' : 'sell'} className="w-full" disabled={loading}>
          {loading ? '접수 중...' : `${side === 'BUY' ? '매수' : '매도'} 주문`}
        </Button>

        {message && (
          <p className="text-sm text-emerald-400" role="status">
            {message}
          </p>
        )}
        {error && (
          <p className="text-sm text-rose-400" role="alert">
            {error}
          </p>
        )}
      </form>
    </Card>
  )
}
