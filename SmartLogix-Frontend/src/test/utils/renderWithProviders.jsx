import { render } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from '../../context/AuthContext'
import { CarritoProvider } from '../../context/CarritoContext'
import { ProductosProvider } from '../../context/ProductosContext'
import { PedidosProvider } from '../../context/PedidosContext'
import { DireccionesProvider } from '../../context/DireccionesContext'
import { HistorialProvider } from '../../context/HistorialContext'

export function renderWithProviders(ui) {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <CarritoProvider>
          <ProductosProvider>
            <PedidosProvider>
              <DireccionesProvider>
                <HistorialProvider>
                  {ui}
                </HistorialProvider>
              </DireccionesProvider>
            </PedidosProvider>
          </ProductosProvider>
        </CarritoProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}