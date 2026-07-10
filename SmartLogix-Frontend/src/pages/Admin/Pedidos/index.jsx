import { useState } from 'react'
import { usePedidos } from '../../../context/PedidosContext'
import styles from './Pedidos.module.css'

const ESTADOS = ['Todos', 'Pendiente', 'En Preparación', 'Despachado', 'Entregado', 'Cancelado']

function AdminPedidos() {
  const { pedidos, cambiarEstado } = usePedidos()
  const [filtroEstado, setFiltroEstado] = useState('Todos')
  const [busqueda, setBusqueda] = useState('')
  const [actualizando, setActualizando] = useState(null)
  const [exitoso, setExitoso] = useState(false)

  const handleCambiarEstado = (id, nuevoEstado) => {
    setActualizando(id)
    cambiarEstado(id, nuevoEstado)
    setExitoso(true)
    setTimeout(() => {
      setExitoso(false)
      setActualizando(null)
    }, 1000)
  }

  const pedidosFiltrados = pedidos.filter(p => {
    const coincideEstado = filtroEstado === 'Todos' || p.estado === filtroEstado
    const coincideBusqueda =
      String(p.id).includes(busqueda) ||
      p.cliente?.toLowerCase().includes(busqueda.toLowerCase()) ||
      p.correoCliente?.toLowerCase().includes(busqueda.toLowerCase())
    return coincideEstado && coincideBusqueda
  })

  return (
    <main className={styles.main}>
      <div className={styles.header}>
        <h1 className={styles.titulo}>Gestión de Pedidos</h1>
        <div className={styles.filtros}>
          <select data-testid="filtro-estado" className={styles.select} value={filtroEstado} onChange={e => setFiltroEstado(e.target.value)}>
            {ESTADOS.map(e => <option key={e} value={e}>{e}</option>)}
          </select>
          <input
            data-testid="buscador-pedidos"
            className={styles.buscador}
            placeholder="Buscar por cliente o ID..."
            value={busqueda}
            onChange={e => setBusqueda(e.target.value)}
          />
        </div>
      </div>

      {exitoso && <div data-testid="exitoso" className={styles.exitoso}>✓ Estado actualizado correctamente</div>}

      {pedidos.length === 0 ? (
        <div className={styles.sinPedidos}>
          <span>📋</span>
          <p>No hay pedidos aún. Los pedidos aparecerán aquí cuando los clientes realicen compras.</p>
        </div>
      ) : (
        <div className={styles.tablaWrapper}>
          <table data-testid="tabla-pedidos" className={styles.tabla}>
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Correo</th>
                <th>Items</th>
                <th>Total</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Cambiar estado</th>
              </tr>
            </thead>
            <tbody>
              {pedidosFiltrados.map(pedido => (
                <tr key={pedido.id} data-testid="pedido-fila">
                  <td><span className={styles.idPedido}>#{pedido.id}</span></td>
                  <td data-testid="fila-cliente">{pedido.cliente}</td>
                  <td style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{pedido.correoCliente}</td>
                  <td style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                    {pedido.items?.map(i => `${i.nombre} x${i.cantidad}`).join(', ')}
                  </td>
                  <td><span className={styles.total}>${pedido.total?.toLocaleString('es-CL')}</span></td>
                  <td style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{pedido.fecha}</td>
                  <td data-testid={`estado-${pedido.id}`}>{pedido.estado}</td>
                  <td>
                    <select
                      data-testid={`select-estado-${pedido.id}`}
                      className={styles.selectEstado}
                      value={pedido.estado}
                      disabled={actualizando === pedido.id}
                      onChange={e => handleCambiarEstado(pedido.id, e.target.value)}
                    >
                      {ESTADOS.filter(e => e !== 'Todos').map(estado => (
                        <option key={estado} value={estado}>{estado}</option>
                      ))}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  )
}

export default AdminPedidos