import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../../test/mocks/server'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import AdminPromociones from './index'

describe('HU-ADMIN-06 · Gestionar Promociones y Productos Destacados', () => {

  it('CA1: muestra toggle de destacado por cada producto', async () => {
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => {
      expect(screen.getByTestId('toggle-destacado-1')).toBeInTheDocument()
    })
  })

  it('CA1b: al activar toggle cambia estado destacado del producto', async () => {
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => screen.getByTestId('toggle-destacado-4'))
    fireEvent.click(screen.getByTestId('toggle-destacado-4'))
    await waitFor(() => {
      expect(screen.getByTestId('toggle-destacado-4').checked).toBe(true)
    })
  })

  it('CA2: al abrir modal de oferta muestra precio base y campo precio oferta', async () => {
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => screen.getByTestId('btn-editar-oferta-1'))
    fireEvent.click(screen.getByTestId('btn-editar-oferta-1'))
    expect(screen.getByTestId('modal-oferta')).toBeInTheDocument()
    expect(screen.getByTestId('precio-base-modal')).toBeInTheDocument()
    expect(screen.getByTestId('input-precio-oferta')).toBeInTheDocument()
  })

  it('CA3: guardar oferta válida actualiza el precio oferta del producto', async () => {
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => screen.getByTestId('btn-editar-oferta-1'))
    fireEvent.click(screen.getByTestId('btn-editar-oferta-1'))
    fireEvent.change(screen.getByTestId('input-precio-oferta'), {
      target: { value: '799990' }
    })
    fireEvent.click(screen.getByTestId('btn-guardar-oferta'))
    await waitFor(() => {
      expect(screen.queryByTestId('modal-oferta')).not.toBeInTheDocument()
      expect(screen.getByTestId('precio-oferta-1')).toHaveTextContent('799.990')
    })
  })

  it('CA4: filtro destacados muestra solo productos destacados', async () => {
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => screen.getAllByTestId('promocion-fila'))
    fireEvent.click(screen.getByTestId('filtro-destacados'))
    await waitFor(() => {
      const filas = screen.getAllByTestId('promocion-fila')
      expect(filas.length).toBe(5)
    })
  })

  it('CA5: muestra error si precio oferta es mayor o igual al precio base', async () => {
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => screen.getByTestId('btn-editar-oferta-1'))
    fireEvent.click(screen.getByTestId('btn-editar-oferta-1'))
    fireEvent.change(screen.getByTestId('input-precio-oferta'), {
      target: { value: '999990' }
    })
    fireEvent.click(screen.getByTestId('btn-guardar-oferta'))
    expect(screen.getByTestId('error-precio-oferta')).toBeInTheDocument()
    expect(screen.getByTestId('error-precio-oferta')).toHaveTextContent(
      'El precio oferta debe ser menor al precio base'
    )
  })

  it('CA5b: muestra error si precio oferta es igual al precio base', async () => {
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => screen.getByTestId('btn-editar-oferta-1'))
    fireEvent.click(screen.getByTestId('btn-editar-oferta-1'))
    fireEvent.change(screen.getByTestId('input-precio-oferta'), {
      target: { value: '899990' }
    })
    fireEvent.click(screen.getByTestId('btn-guardar-oferta'))
    expect(screen.getByTestId('error-precio-oferta')).toBeInTheDocument()
  })

  it('CA6 (Loading): muestra skeleton loader mientras carga', () => {
    renderWithProviders(<AdminPromociones />)
    expect(screen.getByTestId('skeleton-loader')).toBeInTheDocument()
  })

  it('CA7 (Error): muestra error si la API falla al cargar', async () => {
    server.use(
      http.get('/api/productos', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )
    renderWithProviders(<AdminPromociones />)
    await waitFor(() => {
      expect(screen.getByTestId('error-productos')).toBeInTheDocument()
    })
  })

})