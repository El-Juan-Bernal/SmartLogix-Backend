import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../../test/mocks/server'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import AdminInventario from './index'

describe('HU-ADMIN-04 · Gestión de Inventario', () => {

  it('CA1: muestra tabla con todos los productos y su stock actual', async () => {
    renderWithProviders(<AdminInventario />)
    await waitFor(() => {
      expect(screen.getByTestId('tabla-inventario')).toBeInTheDocument()
      const filas = screen.getAllByTestId('inventario-fila')
      expect(filas.length).toBeGreaterThanOrEqual(5)
    })
  })

  it('CA2: botón incrementar aumenta el stock', async () => {
    renderWithProviders(<AdminInventario />)
    await waitFor(() => screen.getAllByTestId('inventario-fila'))
    const stockAntes = parseInt(screen.getByTestId('stock-actual-1').textContent)
    fireEvent.click(screen.getByTestId('btn-incrementar-1'))
    await waitFor(() => {
      const stockDespues = parseInt(screen.getByTestId('stock-actual-1').textContent)
      expect(stockDespues).toBe(stockAntes + 1)
    })
  })

  it('CA2b: botón decrementar reduce el stock', async () => {
    renderWithProviders(<AdminInventario />)
    await waitFor(() => screen.getAllByTestId('inventario-fila'))
    const stockAntes = parseInt(screen.getByTestId('stock-actual-1').textContent)
    fireEvent.click(screen.getByTestId('btn-decrementar-1'))
    await waitFor(() => {
      const stockDespues = parseInt(screen.getByTestId('stock-actual-1').textContent)
      expect(stockDespues).toBe(stockAntes - 1)
    })
  })

  it('CA2c: input numérico permite ajustar stock directamente', async () => {
    renderWithProviders(<AdminInventario />)
    await waitFor(() => screen.getAllByTestId('inventario-fila'))
    fireEvent.change(screen.getByTestId('input-stock-1'), {
      target: { value: '20' }
    })
    await waitFor(() => {
      expect(screen.getByTestId('stock-actual-1')).toHaveTextContent('20')
    })
  })

  it('CA3: fila se resalta cuando stock está por debajo del mínimo', async () => {
    server.use(
      http.get('/api/productos', () => {
        return HttpResponse.json([
          { id: 1, nombre: 'Laptop Gamer', precio: 899990, categoria: 'Computación', marca: 'ASUS', stock: 3 },
        ])
      })
    )
    renderWithProviders(<AdminInventario />)
    await waitFor(() => {
      const fila = screen.getByTestId('inventario-fila')
      expect(fila).toHaveAttribute('data-alerta', 'true')
    })
  })

  it('CA4: controles se deshabilitan mientras se actualiza', async () => {
    renderWithProviders(<AdminInventario />)
    await waitFor(() => screen.getAllByTestId('inventario-fila'))
    fireEvent.click(screen.getByTestId('btn-incrementar-1'))
    expect(screen.getByTestId('btn-incrementar-1')).toBeDisabled()
    expect(screen.getByTestId('btn-decrementar-1')).toBeDisabled()
    expect(screen.getByTestId('input-stock-1')).toBeDisabled()
  })

  it('CA5 (Error): revierte stock y muestra error si API falla', async () => {
    server.use(
      http.put('/api/productos/:id/stock', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )
    renderWithProviders(<AdminInventario />)
    await waitFor(() => screen.getAllByTestId('inventario-fila'))
    const stockAntes = parseInt(screen.getByTestId('stock-actual-1').textContent)
    fireEvent.click(screen.getByTestId('btn-incrementar-1'))
    await waitFor(() => {
      expect(screen.getByTestId('error-actualizar')).toBeInTheDocument()
      expect(screen.getByTestId('stock-actual-1')).toHaveTextContent(String(stockAntes))
    })
  })

  it('CA6 (Loading): muestra skeleton loader mientras carga', () => {
    renderWithProviders(<AdminInventario />)
    expect(screen.getByTestId('skeleton-loader')).toBeInTheDocument()
  })

  it('CA7 (Error): muestra error si la API falla al cargar', async () => {
    server.use(
      http.get('/api/productos', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )
    renderWithProviders(<AdminInventario />)
    await waitFor(() => {
      expect(screen.getByTestId('error-inventario')).toBeInTheDocument()
    })
  })

})