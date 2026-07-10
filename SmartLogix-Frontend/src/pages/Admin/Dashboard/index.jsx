import { useState } from 'react'
import { usePedidos } from '../../../context/PedidosContext'
import { useProductos } from '../../../context/ProductosContext'
import styles from './Dashboard.module.css'

function AdminDashboard() {
  const { pedidos, totalVentas, totalPedidos, pedidosRecientes } = usePedidos()
  const { productos } = useProductos()
  const [periodoSeleccionado, setPeriodoSeleccionado] = useState('mes')

  // Productos más vendidos calculados desde los pedidos reales
  const productosMasVendidos = Object.values(
    pedidos.flatMap(p => p.items || []).reduce((acc, item) => {
      if (!acc[item.id]) {
        acc[item.id] = { id: item.id, nombre: item.nombre, ventas: 0 }
      }
      acc[item.id].ventas += item.cantidad
      return acc
    }, {})
  ).sort((a, b) => b.ventas - a.ventas).slice(0, 5)

  const ticketPromedio = totalPedidos > 0 ? Math.round(totalVentas / totalPedidos) : 0
  const productosConStockBajo = productos.filter(p => p.stock <= 5).length

  return (
    <main className={styles.main}>
      <div className={styles.header}>
        <h1 className={styles.titulo}>
          <span className={styles.accent}>Dashboard</span> Admin
        </h1>
        <div data-testid="selector-periodo" className={styles.periodos}>
          {['dia', 'semana', 'mes', 'año'].map(periodo => (
            <button
              key={periodo}
              data-testid={`periodo-${periodo}`}
              className={`${styles.btnPeriodo} ${periodoSeleccionado === periodo ? styles.btnPeriodoActivo : ''}`}
              onClick={() => setPeriodoSeleccionado(periodo)}
            >
              {periodo}
            </button>
          ))}
        </div>
      </div>

      {/* Tarjetas métricas */}
      <div data-testid="tarjetas-metricas" className={styles.tarjetas}>
        <div data-testid="metrica-ventas" className={styles.tarjeta}>
          <span className={styles.tarjetaLabel}>Ventas totales</span>
          <span data-testid="valor-ventas" className={styles.tarjetaValor}>
            ${totalVentas.toLocaleString('es-CL')}
          </span>
        </div>
        <div data-testid="metrica-pedidos" className={styles.tarjeta}>
          <span className={styles.tarjetaLabel}>Pedidos</span>
          <span data-testid="valor-pedidos" className={styles.tarjetaValor}>{totalPedidos}</span>
        </div>
        <div data-testid="metrica-usuarios" className={styles.tarjeta}>
          <span className={styles.tarjetaLabel}>Ticket promedio</span>
          <span data-testid="valor-usuarios" className={styles.tarjetaValor}>
            ${ticketPromedio.toLocaleString('es-CL')}
          </span>
        </div>
        <div data-testid="metrica-ticket" className={styles.tarjeta}>
          <span className={styles.tarjetaLabel}>Stock bajo</span>
          <span data-testid="valor-ticket" className={`${styles.tarjetaValor} ${productosConStockBajo > 0 ? styles.alerta : ''}`}>
            {productosConStockBajo} productos
          </span>
        </div>
      </div>

      <div className={styles.grid}>
        {/* Productos más vendidos */}
        <div data-testid="productos-mas-vendidos" className={styles.seccion}>
          <h2 className={styles.seccionTitulo}>Productos más vendidos</h2>
          {productosMasVendidos.length === 0 ? (
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Aún no hay ventas registradas.
            </p>
          ) : (
            productosMasVendidos.map(producto => (
              <div key={producto.id} data-testid="producto-ranking" className={styles.rankingItem}>
                <span data-testid="ranking-nombre" className={styles.rankingNombre}>{producto.nombre}</span>
                <span data-testid="ranking-ventas" className={styles.rankingVentas}>{producto.ventas} vendidos</span>
              </div>
            ))
          )}
        </div>

        {/* Pedidos recientes */}
        <div data-testid="pedidos-recientes" className={styles.seccion}>
          <h2 className={styles.seccionTitulo}>Pedidos recientes</h2>
          {pedidosRecientes.length === 0 ? (
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Aún no hay pedidos registrados.
            </p>
          ) : (
            pedidosRecientes.map(pedido => (
              <div key={pedido.id} data-testid="pedido-reciente" className={styles.pedidoItem}>
                <span data-testid="pedido-id" className={styles.pedidoId}>#{pedido.id}</span>
                <span data-testid="pedido-cliente" className={styles.pedidoCliente}>{pedido.cliente}</span>
                <span data-testid="pedido-estado" className={styles.pedidoEstado}>{pedido.estado}</span>
              </div>
            ))
          )}
        </div>
      </div>
    </main>
  )
}

export default AdminDashboard