import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/mocks/server'
import { renderWithProviders } from '../../test/utils/renderWithProviders'
import Catalogo from './index'

describe('HU-02 · Ver catálogo de productos', () => {

  it('CA1: muestra barra de búsqueda', () => {
    renderWithProviders(<Catalogo />)
    expect(screen.getByTestId('barra-busqueda')).toBeInTheDocument()
  })

  it('CA2: muestra filtro por categoría', async () => {
    renderWithProviders(<Catalogo />)
    await waitFor(() => {
      expect(screen.getByTestId('filtro-categoria')).toBeInTheDocument()
    })
  })

  it('CA3: muestra filtro por marca', async () => {
    renderWithProviders(<Catalogo />)
    await waitFor(() => {
      expect(screen.getByTestId('filtro-marca')).toBeInTheDocument()
    })
  })

  it('CA4: muestra selector de orden por precio', () => {
    renderWithProviders(<Catalogo />)
    expect(screen.getByTestId('orden-precio')).toBeInTheDocument()
  })

  it('CA5: muestra todos los productos al cargar', async () => {
    renderWithProviders(<Catalogo />)
    await waitFor(() => {
      const cards = screen.getAllByTestId('producto-card')
      expect(cards.length).toBeGreaterThanOrEqual(5)
    })
  })

  it('CA6: filtra productos por búsqueda de texto', async () => {
    renderWithProviders(<Catalogo />)
    await waitFor(() => screen.getAllByTestId('producto-card'))

    fireEvent.change(screen.getByTestId('barra-busqueda'), {
      target: { value: 'Laptop' }
    })

    await waitFor(() => {
      const cards = screen.getAllByTestId('producto-card')
      expect(cards.length).toBe(1)
      expect(screen.getByText('Laptop Gamer')).toBeInTheDocument()
    })
  })

  it('CA7: filtra productos por categoría', async () => {
    renderWithProviders(<Catalogo />)
    await waitFor(() => screen.getAllByTestId('producto-card'))

    fireEvent.change(screen.getByTestId('filtro-categoria'), {
      target: { value: 'Periféricos' }
    })

    await waitFor(() => {
      const cards = screen.getAllByTestId('producto-card')
      expect(cards.length).toBe(3)
    })
  })

  it('CA8 (Loading): muestra skeleton loaders mientras carga', () => {
    renderWithProviders(<Catalogo />)
    expect(screen.getByTestId('skeleton-loader')).toBeInTheDocument()
  })

  it('CA9: el botón limpiar filtros resetea todos los filtros', async () => {
    renderWithProviders(<Catalogo />)
    await waitFor(() => screen.getAllByTestId('producto-card'))

    fireEvent.change(screen.getByTestId('barra-busqueda'), {
      target: { value: 'Laptop' }
    })

    fireEvent.click(screen.getByTestId('btn-limpiar'))

    await waitFor(() => {
      expect(screen.getByTestId('barra-busqueda').value).toBe('')
      const cards = screen.getAllByTestId('producto-card')
      expect(cards.length).toBeGreaterThanOrEqual(5)
    })
  })

  it('CA10 (Error): muestra mensaje de error si la API falla', async () => {
    server.use(
      http.get('/api/productos', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )

    renderWithProviders(<Catalogo />)

    await waitFor(() => {
      expect(screen.getByTestId('error-catalogo')).toBeInTheDocument()
      expect(screen.getByText('Error al cargar el catálogo')).toBeInTheDocument()
    })
  })

})