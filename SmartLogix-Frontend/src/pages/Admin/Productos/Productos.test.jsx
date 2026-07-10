import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../../test/mocks/server'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import AdminProductos from './index'

describe('HU-ADMIN-01 · Gestión de Productos', () => {

  it('CA1: muestra botón para agregar nuevo producto', () => {
    renderWithProviders(<AdminProductos />)
    expect(screen.getByTestId('btn-nuevo-producto')).toBeInTheDocument()
  })

  it('CA2: muestra tabla con productos al cargar', async () => {
    renderWithProviders(<AdminProductos />)
    await waitFor(() => {
      expect(screen.getByTestId('tabla-productos')).toBeInTheDocument()
      const filas = screen.getAllByTestId('producto-fila')
      expect(filas.length).toBeGreaterThanOrEqual(5)
    })
  })

  it('CA3: al hacer clic en nuevo producto abre el modal', () => {
    renderWithProviders(<AdminProductos />)
    fireEvent.click(screen.getByTestId('btn-nuevo-producto'))
    expect(screen.getByTestId('modal-producto')).toBeInTheDocument()
  })

  it('CA3b: muestra errores si campos requeridos están vacíos', () => {
    renderWithProviders(<AdminProductos />)
    fireEvent.click(screen.getByTestId('btn-nuevo-producto'))
    fireEvent.click(screen.getByTestId('btn-guardar'))
    expect(screen.getByTestId('error-nombre')).toBeInTheDocument()
    expect(screen.getByTestId('error-precio')).toBeInTheDocument()
  })

  it('CA4: al hacer clic en editar abre modal con datos del producto', async () => {
    renderWithProviders(<AdminProductos />)
    await waitFor(() => screen.getAllByTestId('producto-fila'))
    fireEvent.click(screen.getByTestId('btn-editar-1'))
    expect(screen.getByTestId('modal-producto')).toBeInTheDocument()
    expect(screen.getByTestId('input-nombre').value).toBe('Laptop Gamer')
  })

  it('CA5: buscador filtra productos por nombre', async () => {
    renderWithProviders(<AdminProductos />)
    await waitFor(() => screen.getAllByTestId('producto-fila'))
    fireEvent.change(screen.getByTestId('buscador-productos'), {
      target: { value: 'Laptop' }
    })
    await waitFor(() => {
      const filas = screen.getAllByTestId('producto-fila')
      expect(filas.length).toBe(1)
    })
  })

  it('CA6: cancelar modal lo cierra', () => {
    renderWithProviders(<AdminProductos />)
    fireEvent.click(screen.getByTestId('btn-nuevo-producto'))
    fireEvent.click(screen.getByTestId('btn-cancelar'))
    expect(screen.queryByTestId('modal-producto')).not.toBeInTheDocument()
  })

  it('CA7 (Loading): muestra skeleton loader mientras carga', () => {
    renderWithProviders(<AdminProductos />)
    expect(screen.getByTestId('skeleton-loader')).toBeInTheDocument()
  })

  it('CA8 (Error): muestra error si la API falla', async () => {
    server.use(
      http.get('/api/productos', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )
    renderWithProviders(<AdminProductos />)
    await waitFor(() => {
      expect(screen.getByTestId('error-productos')).toBeInTheDocument()
    })
  })

})