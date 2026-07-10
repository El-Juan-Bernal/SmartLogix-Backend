import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import { render } from '@testing-library/react'
import { AuthProvider } from '../../context/AuthContext'
import { CarritoProvider } from '../../context/CarritoContext'
import { ProductosProvider } from '../../context/ProductosContext'
import { PedidosProvider } from '../../context/PedidosContext'
import { DireccionesProvider } from '../../context/DireccionesContext'
import { HistorialProvider } from '../../context/HistorialContext'
import Producto from './index'

function renderProducto(id = '1') {
  return render(
    <MemoryRouter initialEntries={[`/producto/${id}`]}>
      <AuthProvider>
        <CarritoProvider>
          <ProductosProvider>
            <PedidosProvider>
              <DireccionesProvider>
                <HistorialProvider>
                  <Routes>
                    <Route path="/producto/:id" element={<Producto />} />
                  </Routes>
                </HistorialProvider>
              </DireccionesProvider>
            </PedidosProvider>
          </ProductosProvider>
        </CarritoProvider>
      </AuthProvider>
    </MemoryRouter>
  )
}

describe('HU-03 · Ver detalle de producto', () => {

  it('CA1: muestra la imagen del producto', async () => {
    renderProducto('1')
    await waitFor(() => {
      expect(screen.getByTestId('producto-imagen')).toBeInTheDocument()
    })
  })

  it('CA2: muestra nombre, marca, categoría y descripción', async () => {
    renderProducto('1')
    await waitFor(() => {
      expect(screen.getByTestId('producto-nombre')).toBeInTheDocument()
      expect(screen.getByTestId('producto-marca')).toBeInTheDocument()
      expect(screen.getByTestId('producto-categoria')).toBeInTheDocument()
      expect(screen.getByTestId('producto-descripcion')).toBeInTheDocument()
    })
  })

  it('CA3: muestra el precio final del producto', async () => {
    renderProducto('1')
    await waitFor(() => {
      expect(screen.getByTestId('precio-final')).toBeInTheDocument()
    })
  })

  it('CA4: muestra stock disponible del producto', async () => {
    renderProducto('1')
    await waitFor(() => {
      expect(screen.getByTestId('stock-disponible')).toBeInTheDocument()
    })
  })

  it('CA5: el selector de cantidad incrementa y decrementa correctamente', async () => {
    renderProducto('1')
    await waitFor(() => screen.getByTestId('selector-cantidad'))

    const btnIncrementar = screen.getByTestId('btn-incrementar')
    const btnDecrementar = screen.getByTestId('btn-decrementar')
    const cantidad = screen.getByTestId('cantidad-seleccionada')

    expect(cantidad).toHaveTextContent('1')
    fireEvent.click(btnIncrementar)
    expect(cantidad).toHaveTextContent('2')
    fireEvent.click(btnDecrementar)
    expect(cantidad).toHaveTextContent('1')
  })

  it('CA5b: la cantidad no baja de 1', async () => {
    renderProducto('1')
    await waitFor(() => screen.getByTestId('btn-decrementar'))
    fireEvent.click(screen.getByTestId('btn-decrementar'))
    expect(screen.getByTestId('cantidad-seleccionada')).toHaveTextContent('1')
  })

  it('CA6: el botón agregar al carrito está presente y habilitado', async () => {
    renderProducto('1')
    await waitFor(() => {
      const btn = screen.getByTestId('btn-agregar-carrito')
      expect(btn).toBeInTheDocument()
      expect(btn).not.toBeDisabled()
    })
  })

  it('CA7: el botón volver al catálogo está presente', async () => {
    renderProducto('1')
    await waitFor(() => {
      expect(screen.getByTestId('btn-volver')).toBeInTheDocument()
    })
  })

  it('CA8 (Loading): muestra skeleton loader mientras carga', () => {
    renderProducto('1')
    expect(screen.getByTestId('skeleton-loader')).toBeInTheDocument()
  })

  it('CA9 (Error): muestra error si el producto no existe', async () => {
    renderProducto('999')
    await waitFor(() => {
      expect(screen.getByTestId('error-producto')).toBeInTheDocument()
    })
  })

  it('CA10: muestra confirmación visual al agregar al carrito', async () => {
    renderProducto('1')
    await waitFor(() => screen.getByTestId('btn-agregar-carrito'))
    fireEvent.click(screen.getByTestId('btn-agregar-carrito'))
    expect(screen.getByTestId('confirmacion-agregado')).toBeInTheDocument()
  })

})