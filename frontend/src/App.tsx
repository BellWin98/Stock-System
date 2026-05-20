import { Header } from '@/components/Header'
import { OrderForm } from '@/components/OrderForm'
import { OrderTable } from '@/components/OrderTable'
import { QuotePanel } from '@/components/QuotePanel'
import { useState } from 'react'

const App = () => {
  const [selectedSymbol, setSelectedSymbol] = useState('005930')
  const [refreshKey, setRefreshKey] = useState(0)

  const handleOrderSubmitted = () => {
    setRefreshKey((k) => k + 1)
  }

  return (
    <main className="min-h-screen p-4 md:p-8">
      <div className="mx-auto max-w-6xl">
        <Header />
        <div className="mb-6 grid grid-cols-1 gap-6 lg:grid-cols-2">
          <OrderForm
            selectedSymbol={selectedSymbol}
            onSymbolChange={setSelectedSymbol}
            onOrderSubmitted={handleOrderSubmitted}
          />
          <QuotePanel symbol={selectedSymbol} />
        </div>
        <OrderTable key={refreshKey} />
      </div>
    </main>
  )
}

export default App
