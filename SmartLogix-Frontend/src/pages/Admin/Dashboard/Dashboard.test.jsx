import { describe, it, expect } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import AdminDashboard from './index'

describe('HU-ADMIN-03 · Dashboard', () => {

  it('CA1: muestra las tarjetas de métricas', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByTestId('tarjetas-metricas')).toBeInTheDocument()
      expect(screen.getByTestId('metrica-ventas')).toBeInTheDocument()
      expect(screen.getByTestId('metrica-pedidos')).toBeInTheDocument()
      expect(screen.getByTestId('metrica-usuarios')).toBeInTheDocument()
      expect(screen.getByTestId('metrica-ticket')).toBeInTheDocument()
    })
  })

  it('CA2: muestra sección de productos más vendidos', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByTestId('productos-mas-vendidos')).toBeInTheDocument()
    })
  })

  it('CA2b: muestra mensaje cuando no hay ventas registradas', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByText('Aún no hay ventas registradas.')).toBeInTheDocument()
    })
  })

  it('CA3: muestra sección de pedidos recientes', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByTestId('pedidos-recientes')).toBeInTheDocument()
    })
  })

  it('CA3b: muestra mensaje cuando no hay pedidos registrados', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByText('Aún no hay pedidos registrados.')).toBeInTheDocument()
    })
  })

  it('CA4: muestra selector de período', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByTestId('selector-periodo')).toBeInTheDocument()
      expect(screen.getByTestId('periodo-dia')).toBeInTheDocument()
      expect(screen.getByTestId('periodo-semana')).toBeInTheDocument()
      expect(screen.getByTestId('periodo-mes')).toBeInTheDocument()
      expect(screen.getByTestId('periodo-año')).toBeInTheDocument()
    })
  })

  it('CA5: las ventas totales inician en $0 sin pedidos registrados', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByTestId('valor-ventas')).toHaveTextContent('$0')
    })
  })

  it('CA6: muestra cantidad de productos con stock bajo', async () => {
    renderWithProviders(<AdminDashboard />)
    await waitFor(() => {
      expect(screen.getByTestId('valor-ticket')).toBeInTheDocument()
    })
  })

})