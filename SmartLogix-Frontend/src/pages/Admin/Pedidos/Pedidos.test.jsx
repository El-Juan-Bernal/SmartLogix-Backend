import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { useEffect } from 'react'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import { usePedidos } from '../../../context/PedidosContext'
import AdminPedidos from './index'

const pedidosMock = [
  { cliente: 'Diego Tatin', correoCliente: 'diego@test.cl', total: 924980, items: [] },
  { cliente: 'María López', correoCliente: 'maria@test.cl', total: 349990, items: [] },
  { cliente: 'Carlos Pérez', correoCliente: 'carlos@test.cl', total: 89990, items: [] },
]

function AdminPedidosConDatos({ pedidos = pedidosMock }) {
  const { agregarPedido } = usePedidos()
  useEffect(() => {
    pedidos.forEach(p => agregarPedido(p))
  }, [])
  return <AdminPedidos />
}

describe('HU-ADMIN-02 · Gestión de Pedidos', () => {

  it('CA1: muestra tabla con pedidos al cargar', async () => {
    renderWithProviders(<AdminPedidosConDatos />)
    await waitFor(() => {
      const filas = screen.getAllByTestId('pedido-fila')
      expect(filas.length).toBe(3)
    })
  })

  it('CA2: muestra filtro por estado', () => {
    renderWithProviders(<AdminPedidosConDatos />)
    expect(screen.getByTestId('filtro-estado')).toBeInTheDocument()
  })

  it('CA2b: filtra pedidos por estado', async () => {
    renderWithProviders(<AdminPedidosConDatos />)
    await waitFor(() => screen.getAllByTestId('pedido-fila'))
    fireEvent.change(screen.getByTestId('filtro-estado'), { target: { value: 'Pendiente' } })
    await waitFor(() => {
      const filas = screen.getAllByTestId('pedido-fila')
      expect(filas.length).toBe(3)
    })
  })

  it('CA3: buscador filtra por cliente', async () => {
    renderWithProviders(<AdminPedidosConDatos />)
    await waitFor(() => screen.getAllByTestId('pedido-fila'))
    fireEvent.change(screen.getByTestId('buscador-pedidos'), { target: { value: 'Diego' } })
    await waitFor(() => {
      const filas = screen.getAllByTestId('pedido-fila')
      expect(filas.length).toBe(1)
    })
  })

  it('CA4: muestra selector de estado por pedido', async () => {
    renderWithProviders(<AdminPedidosConDatos />)
    await waitFor(() => {
      const selects = screen.getAllByTestId(/select-estado-/)
      expect(selects.length).toBe(3)
    })
  })

  it('CA4b: cambia el estado de un pedido correctamente', async () => {
    renderWithProviders(<AdminPedidosConDatos />)
    await waitFor(() => screen.getAllByTestId('pedido-fila'))
    const select = screen.getAllByTestId(/select-estado-/)[0]
    fireEvent.change(select, { target: { value: 'En Preparación' } })
    await waitFor(() => {
      expect(screen.getByTestId('exitoso')).toBeInTheDocument()
    })
  })

  it('CA5: muestra mensaje cuando no hay pedidos', () => {
    renderWithProviders(<AdminPedidosConDatos pedidos={[]} />)
    expect(screen.getByText(/No hay pedidos aún/)).toBeInTheDocument()
  })

})