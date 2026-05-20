import type {
  Account,
  CreateOrderRequest,
  Instrument,
  Order,
  Quote,
} from '@/types/order'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'
const ACCOUNT_ID = '1'

const headers = (): HeadersInit => ({
  'Content-Type': 'application/json',
  'X-Account-Id': ACCOUNT_ID,
})

const handleResponse = async <T>(response: Response): Promise<T> => {
  if (!response.ok) {
    const body = await response.json().catch(() => ({ message: response.statusText }))
    throw new Error(body.message ?? `HTTP ${response.status}`)
  }
  return response.json() as Promise<T>
}

export const fetchAccount = async (): Promise<Account> => {
  const response = await fetch(`${API_URL}/api/accounts/${ACCOUNT_ID}`)
  return handleResponse<Account>(response)
}

export const fetchInstruments = async (): Promise<Instrument[]> => {
  const response = await fetch(`${API_URL}/api/instruments`)
  return handleResponse<Instrument[]>(response)
}

export const fetchQuote = async (symbol: string): Promise<Quote> => {
  const response = await fetch(`${API_URL}/api/quotes/${symbol}`)
  return handleResponse<Quote>(response)
}

export const fetchOrders = async (): Promise<Order[]> => {
  const response = await fetch(`${API_URL}/api/orders?accountId=${ACCOUNT_ID}`)
  return handleResponse<Order[]>(response)
}

export const submitOrder = async (request: CreateOrderRequest): Promise<Order> => {
  const response = await fetch(`${API_URL}/api/orders`, {
    method: 'POST',
    headers: headers(),
    body: JSON.stringify(request),
  })
  return handleResponse<Order>(response)
}
