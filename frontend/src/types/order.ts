export type OrderSide = 'BUY' | 'SELL'

export type OrderStatus = 'PENDING' | 'ACCEPTED' | 'FILLED' | 'REJECTED'

export interface Account {
  id: number
  cashBalance: number
  lockedMargin: number
  availableBalance: number
}

export interface Instrument {
  symbol: string
  name: string
  lastPrice: number
}

export interface Quote {
  symbol: string
  name: string
  lastPrice: number
  volume: number
  updatedAt: string
}

export interface Order {
  id: string
  accountId: number
  symbol: string
  instrumentName: string
  side: OrderSide
  quantity: number
  price: number
  status: OrderStatus
  createdAt: string
}

export interface CreateOrderRequest {
  symbol: string
  side: OrderSide
  quantity: number
  price: number
}
