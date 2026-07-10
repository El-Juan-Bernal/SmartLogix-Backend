import { useNavigate } from 'react-router-dom'
import { useCarrito } from '../../context/CarritoContext'
import styles from './ProductoCard.module.css'

function ProductoCard({ producto }) {
  const navigate = useNavigate()
  const { agregarProducto, items } = useCarrito()

  const stockTexto = producto.stock > 100 ? '+100 Unid.' : `${producto.stock} Unid.`
  const tieneOferta = producto.precioOferta && producto.precioOferta < producto.precio
  const agotado = producto.stock === 0

  const itemEnCarrito = items.find(i => i.id === producto.id)
  const cantidadEnCarrito = itemEnCarrito ? itemEnCarrito.cantidad : 0
  const stockAgotadoEnCarrito = cantidadEnCarrito >= producto.stock

  return (
    <div
      data-testid="producto-card"
      className={styles.card}
      onClick={() => navigate(`/producto/${producto.id}`)}
    >
      <div className={styles.imagenWrapper}>
        {producto.imagen
          ? <img src={producto.imagen} alt={producto.nombre} className={styles.imagen} />
          : <div className={styles.imagenPlaceholder}>
              <span className={styles.placeholderIcono}>🖥️</span>
            </div>
        }

        {tieneOferta && (
          <span className={styles.badgeOferta}>
            -{Math.round((1 - producto.precioOferta / producto.precio) * 100)}%
          </span>
        )}

        <span className={`${styles.badgeStock} ${agotado ? styles.badgeStockAgotado : producto.stock <= 5 ? styles.badgeStockBajo : ''}`}>
          {agotado ? 'Agotado' : stockTexto}
        </span>
      </div>

      <div className={styles.cardInfo}>
        <span data-testid="producto-categoria" className={styles.cardCategoria}>{producto.categoria}</span>
        <span data-testid="producto-nombre" className={styles.cardNombre}>{producto.nombre}</span>
        <span data-testid="producto-marca" className={styles.cardMarca}>{producto.marca}</span>

        <div className={styles.precios}>
          {tieneOferta && (
            <span className={styles.precioOriginal}>${Number(producto.precio).toLocaleString('es-CL')}</span>
          )}
          <span data-testid="producto-precio" className={styles.cardPrecio}>
            ${Number(tieneOferta ? producto.precioOferta : producto.precio).toLocaleString('es-CL')}
          </span>
        </div>

        <button
          data-testid="btn-carrito"
          className={`${styles.btnCarrito} ${agotado || stockAgotadoEnCarrito ? styles.btnCarritoAgotado : ''}`}
          onClick={e => {
            e.stopPropagation()
            if (agotado || stockAgotadoEnCarrito) return
            agregarProducto(producto, 1)
          }}
          disabled={agotado || stockAgotadoEnCarrito}
        >
          {agotado
            ? 'Sin stock'
            : stockAgotadoEnCarrito
              ? 'Máximo en carrito'
              : 'Agregar al carrito'
          }
        </button>
      </div>
    </div>
  )
}

export default ProductoCard

