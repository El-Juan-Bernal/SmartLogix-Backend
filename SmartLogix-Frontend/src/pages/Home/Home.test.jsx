import { screen, waitFor, fireEvent, within } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/mocks/server'
import { renderWithProviders } from '../../test/utils/renderWithProviders'
import Home from './index'

describe('HU-01 · Ver página principal', () => {

  it('CA1: muestra el hero con título y botones de navegación', () => {
    renderWithProviders(<Home />)
    expect(screen.getByTestId('hero')).toBeInTheDocument()
    expect(screen.getByTestId('hero')).toHaveTextContent('Bienvenido a SmartLogix')
    expect(screen.getByText('Ver ofertas')).toBeInTheDocument()
  })

  it('CA2: el carrusel muestra solo productos con estado destacado', async () => {
    renderWithProviders(<Home />)
    await waitFor(() => {
      // 1. Capturamos el contenedor específico del carrusel
      const carrusel = screen.getByTestId('carrusel-destacados')
      // 2. Buscamos las tarjetas SOLO dentro de ese contenedor
      const cards = within(carrusel).getAllByTestId('producto-card')
      
      expect(cards.length).toBeGreaterThanOrEqual(1)
      expect(cards.length).toBeLessThanOrEqual(10)
    })
  })

  it('CA2b: el carrusel no muestra productos sin estado destacado', async () => {
    renderWithProviders(<Home />)
    await waitFor(() => {
      // Aplicamos la misma lógica aquí
      const carrusel = screen.getByTestId('carrusel-destacados')
      const cards = within(carrusel).getAllByTestId('producto-card')
      
      // Ahora sí contará solo los 5 del mock de destacados
      expect(cards.length).toBe(5)
    })
  })

  it('CA2c: el carrusel tiene botones de navegación anterior y siguiente', async () => {
    renderWithProviders(<Home />)
    await waitFor(() => screen.getByTestId('carrusel-destacados'))
    expect(screen.getByTestId('btn-anterior')).toBeInTheDocument()
    expect(screen.getByTestId('btn-siguiente')).toBeInTheDocument()
  })

  it('CA3: cada producto muestra nombre, precio, categoría y botón de carrito', async () => {
    renderWithProviders(<Home />)
    await waitFor(() => {
      expect(screen.getAllByTestId('producto-nombre').length).toBeGreaterThan(0)
      expect(screen.getAllByTestId('producto-precio').length).toBeGreaterThan(0)
      expect(screen.getAllByTestId('producto-categoria').length).toBeGreaterThan(0)
      expect(screen.getAllByTestId('btn-carrito').length).toBeGreaterThan(0)
    })
  })

  it('CA4: el botón "Ver ofertas" redirige a /catalogo', () => {
    renderWithProviders(<Home />)
    fireEvent.click(screen.getByText('Ver ofertas'))
    expect(window.location.pathname).toBe('/catalogo')
  })

  it('CA5: los contadores del hero están presentes al cargar', () => {
    renderWithProviders(<Home />)
    expect(screen.getByTestId('hero-contadores')).toBeInTheDocument()
    expect(screen.getByTestId('contador-productos')).toBeInTheDocument()
    expect(screen.getByTestId('contador-clientes')).toBeInTheDocument()
  })

  it('CA6: al hacer clic en un producto redirige a su detalle', async () => {
    renderWithProviders(<Home />)
    await waitFor(() => {
      const cards = screen.getAllByTestId('producto-card')
      fireEvent.click(cards[0])
      expect(window.location.pathname).toContain('/producto/')
    })
  })

  it('CA7 (Loading): muestra Skeleton Loaders mientras se obtiene la información', () => {
    renderWithProviders(<Home />)
    expect(screen.getByTestId('skeleton-loader')).toBeInTheDocument()
    expect(screen.getAllByTestId('skeleton-item').length).toBe(5)
  })

  it('CA8 (Error): muestra mensaje de error y botón reintentar si la API falla', async () => {
    server.use(
      http.get('/api/productos', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )
    renderWithProviders(<Home />)
    await waitFor(() => {
      expect(screen.getByTestId('error-productos')).toBeInTheDocument()
      expect(screen.getByText('Error al cargar las ofertas')).toBeInTheDocument()
      expect(screen.getByText('Reintentar')).toBeInTheDocument()
    })
  })

})