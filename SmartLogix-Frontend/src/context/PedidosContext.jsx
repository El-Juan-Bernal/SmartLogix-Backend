import { createContext, useContext, useState } from 'react'

const PedidosContext = createContext(null)

const actualizarHistorialUsuario = (correoCliente, idPedido, nuevoEstado) => {
  if (!correoCliente) return
  const key = `historial_${correoCliente}`
  const guardado = localStorage.getItem(key)
  if (!guardado) return
  const historial = JSON.parse(guardado)
  const actualizado = historial.map(p =>
    String(p.id) === String(idPedido) ? { ...p, estado: nuevoEstado } : p
  )
  localStorage.setItem(key, JSON.stringify(actualizado))
}

export function PedidosProvider({ children }) {
  const [pedidos, setPedidos] = useState([])

  const agregarPedido = (pedido) => {
    const nuevo = {
      id: pedido.id || Date.now(),
      cliente: pedido.cliente || 'Cliente',
      correoCliente: pedido.correoCliente || '',
      items: pedido.items || [],
      total: pedido.total || 0,
      metodoPago: pedido.metodoPago || '',
      direccion: pedido.direccion || {},
      estado: 'Pendiente',
      fecha: new Date().toLocaleDateString('es-CL'),
      numeroPedido: pedido.id || `SL-${Date.now()}`,
    }
    setPedidos(prev => [nuevo, ...prev])
    return nuevo
  }

  const cambiarEstado = (id, nuevoEstado) => {
    setPedidos(prev =>
      prev.map(p => {
        if (p.id === id) {
          actualizarHistorialUsuario(p.correoCliente, p.id, nuevoEstado)
          return { ...p, estado: nuevoEstado }
        }
        return p
      })
    )
  }

  const totalVentas = pedidos.reduce((acc, p) => acc + p.total, 0)
  const totalPedidos = pedidos.length
  const pedidosRecientes = pedidos.slice(0, 5)

  return (
    <PedidosContext.Provider value={{
      pedidos,
      agregarPedido,
      cambiarEstado,
      totalVentas,
      totalPedidos,
      pedidosRecientes,
    }}>
      {children}
    </PedidosContext.Provider>
  )
}

export const usePedidos = () => useContext(PedidosContext)