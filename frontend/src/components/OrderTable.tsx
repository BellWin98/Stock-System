import { fetchOrders } from '@/lib/api'
import { formatKrw } from '@/lib/utils'
import type { Order } from '@/types/order'
import { useCallback, useEffect, useState } from 'react'
import { Badge } from './ui/badge'
import { Card } from './ui/card'

export const OrderTable = () => {
  const [orders, setOrders] = useState<Order[]>([])
  const [error, setError] = useState<string | null>(null)

  const loadOrders = useCallback(async () => {
    try {
      const data = await fetchOrders()
      setOrders(data)
      setError(null)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load orders')
    }
  }, [])

  useEffect(() => {
    loadOrders()
    const interval = setInterval(loadOrders, 2000)
    return () => clearInterval(interval)
  }, [loadOrders])

  return (
    <Card>
      <h2 className="mb-4 text-lg font-semibold">주문 내역</h2>
      {error && (
        <p className="mb-2 text-sm text-rose-400" role="alert">
          {error}
        </p>
      )}
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm" aria-label="주문 내역 테이블">
          <thead>
            <tr className="border-b border-slate-700 text-slate-400">
              <th className="pb-2 pr-4">종목</th>
              <th className="pb-2 pr-4">구분</th>
              <th className="pb-2 pr-4">수량</th>
              <th className="pb-2 pr-4">가격</th>
              <th className="pb-2 pr-4">상태</th>
              <th className="pb-2">시각</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0 ? (
              <tr>
                <td colSpan={6} className="py-4 text-center text-slate-400">
                  주문 내역이 없습니다
                </td>
              </tr>
            ) : (
              orders.map((order) => (
                <tr key={order.id} className="border-b border-slate-800">
                  <td className="py-3 pr-4">
                    <span className="font-medium">{order.instrumentName}</span>
                    <span className="ml-1 text-xs text-slate-400">({order.symbol})</span>
                  </td>
                  <td className={`py-3 pr-4 font-medium ${order.side === 'BUY' ? 'text-emerald-400' : 'text-rose-400'}`}>
                    {order.side === 'BUY' ? '매수' : '매도'}
                  </td>
                  <td className="py-3 pr-4">{order.quantity}</td>
                  <td className="py-3 pr-4">{formatKrw(order.price)}</td>
                  <td className="py-3 pr-4">
                    <Badge status={order.status} />
                  </td>
                  <td className="py-3 text-slate-400">
                    {new Date(order.createdAt).toLocaleString('ko-KR')}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </Card>
  )
}
