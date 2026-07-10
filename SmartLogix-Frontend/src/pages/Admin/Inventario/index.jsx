import { useState } from 'react'
import { useProductos } from '../../../context/ProductosContext'
import styles from './Inventario.module.css'

const STOCK_MINIMO = 5

function AdminInventario() {
  const { productos, loading, error, actualizarStock } = useProductos()
  const [actualizando, setActualizando] = useState(null)
  const [errorActualizar, setErrorActualizar] = useState(false)
  // Valor que el usuario está escribiendo ahora mismo, mientras no lo confirme
  // (no se manda a la API en cada tecla, solo al salir del campo o presionar Enter)
  const [valoresInput, setValoresInput] = useState({})

  const handleActualizarStock = async (id, nuevoStock) => {
    if (nuevoStock < 0) return
    const anterior = productos.find(p => p.id === id).stock
    setActualizando(id)
    setErrorActualizar(false)
    actualizarStock(id, nuevoStock)
    try {
      const res = await fetch(`/api/productos/${id}/stock`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ stock: nuevoStock }),
      })
      if (!res.ok) throw new Error('Error')
    } catch {
      actualizarStock(id, anterior)
      setErrorActualizar(true)
    } finally {
      setActualizando(null)
    }
  }

  // Se llama al salir del campo (onBlur) o al presionar Enter: recién ahí
  // se manda el valor final a la API, no en cada tecla que se escribe.
  const confirmarInput = (id) => {
    const valorEscrito = valoresInput[id]
    setValoresInput(prev => {
      const copia = { ...prev }
      delete copia[id]
      return copia
    })
    if (valorEscrito === undefined) return
    const v = parseInt(valorEscrito)
    const actual = productos.find(p => p.id === id)?.stock
    if (!isNaN(v) && v >= 0 && v !== actual) {
      handleActualizarStock(id, v)
    }
  }

  return (
    <main className={styles.main}>
      <h1 className={styles.titulo}>Gestión de Inventario</h1>

      {errorActualizar && <div data-testid="error-actualizar" className={styles.errorActualizar}>Error al actualizar el inventario</div>}
      {loading && <div data-testid="skeleton-loader" className={styles.skeleton}><div data-testid="skeleton-item" /></div>}
      {error && <div data-testid="error-inventario"><p>Error al cargar el inventario</p></div>}

      {!loading && !error && (
        <div className={styles.tablaWrapper}>
          <table data-testid="tabla-inventario" className={styles.tabla}>
            <thead>
              <tr><th>Producto</th><th>Categoría</th><th>Stock actual</th><th>Ajustar stock</th></tr>
            </thead>
            <tbody>
              {productos.map(producto => (
                <tr
                  key={producto.id}
                  data-testid="inventario-fila"
                  data-alerta={producto.stock < STOCK_MINIMO ? 'true' : 'false'}
                  className={producto.stock < STOCK_MINIMO ? styles.filaAlerta : ''}
                >
                  <td data-testid="fila-nombre">{producto.nombre}</td>
                  <td data-testid="fila-categoria">{producto.categoria}</td>
                  <td>
                    <span
                      data-testid={`stock-actual-${producto.id}`}
                      className={producto.stock < STOCK_MINIMO ? styles.stockBajo : ''}
                    >
                      {producto.stock}
                    </span>
                  </td>
                  <td>
                    <div className={styles.selectorStock}>
                      <button
                        data-testid={`btn-decrementar-${producto.id}`}
                        className={styles.btnStock}
                        disabled={actualizando === producto.id}
                        onClick={() => producto.stock > 0 && handleActualizarStock(producto.id, producto.stock - 1)}
                      >-</button>
                      <input
                        data-testid={`input-stock-${producto.id}`}
                        className={styles.inputStock}
                        type="number"
                        value={valoresInput[producto.id] ?? producto.stock}
                        disabled={actualizando === producto.id}
                        onChange={e => {
                          const valor = e.target.value
                          setValoresInput(prev => ({ ...prev, [producto.id]: valor }))
                        }}
                        onBlur={() => confirmarInput(producto.id)}
                        onKeyDown={e => {
                          if (e.key === 'Enter') e.target.blur() // dispara el onBlur de arriba
                        }}
                      />
                      <button
                        data-testid={`btn-incrementar-${producto.id}`}
                        className={styles.btnStock}
                        disabled={actualizando === producto.id}
                        onClick={() => handleActualizarStock(producto.id, producto.stock + 1)}
                      >+</button>
                      {actualizando === producto.id && (
                        <span data-testid={`actualizando-${producto.id}`} className={styles.guardando}>Guardando...</span>
                      )}
                    </div>
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

export default AdminInventario

