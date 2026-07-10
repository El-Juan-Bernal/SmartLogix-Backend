import { useState, useEffect } from 'react'
import ProductoCard from '../../components/common/ProductoCard'
import styles from './Catalogo.module.css'
import { useProductos } from '../../context/ProductosContext'

function Catalogo() {
  const { productos, loading, error } = useProductos()
  const [productosFiltrados, setProductosFiltrados] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState('Todas')
  const [marcaSeleccionada, setMarcaSeleccionada] = useState('Todas')
  const [ordenPrecio, setOrdenPrecio] = useState('ninguno')
  const [precioMin, setPrecioMin] = useState('')
  const [precioMax, setPrecioMax] = useState('')

  useEffect(() => {
    let resultado = [...productos]

    if (busqueda.trim() !== '') {
      resultado = resultado.filter(p =>
        p.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
        p.marca.toLowerCase().includes(busqueda.toLowerCase())
      )
    }

    if (categoriaSeleccionada !== 'Todas') {
      resultado = resultado.filter(p => p.categoria === categoriaSeleccionada)
    }

    if (marcaSeleccionada !== 'Todas') {
      resultado = resultado.filter(p => p.marca === marcaSeleccionada)
    }

    if (precioMin !== '') {
      resultado = resultado.filter(p => p.precio >= parseInt(precioMin))
    }

    if (precioMax !== '') {
      resultado = resultado.filter(p => p.precio <= parseInt(precioMax))
    }

    if (ordenPrecio === 'asc') resultado.sort((a, b) => a.precio - b.precio)
    else if (ordenPrecio === 'desc') resultado.sort((a, b) => b.precio - a.precio)

    setProductosFiltrados(resultado)
  }, [busqueda, categoriaSeleccionada, marcaSeleccionada, ordenPrecio, precioMin, precioMax, productos])

  const categorias = ['Todas', ...new Set(productos.map(p => p.categoria))]
  const marcas = ['Todas', ...new Set(productos.map(p => p.marca))]

  const handleLimpiarFiltros = () => {
    setBusqueda('')
    setCategoriaSeleccionada('Todas')
    setMarcaSeleccionada('Todas')
    setOrdenPrecio('ninguno')
    setPrecioMin('')
    setPrecioMax('')
  }

  return (
    <main className={styles.main}>
      <h1 className={styles.titulo}>
        Catálogo de <span className={styles.accent}>Productos</span>
      </h1>

      <div className={styles.barraSuperior}>
        <span className={styles.contadorResultados} data-testid="contador-resultados">
          {productosFiltrados.length} productos encontrados
        </span>
        <div className={styles.busquedaWrapper}>
          <span className={styles.busquedaIcono}>🔍</span>
          <input
            data-testid="barra-busqueda"
            className={styles.barraBusqueda}
            type="text"
            placeholder="Buscar productos por nombre o marca..."
            value={busqueda}
            onChange={e => setBusqueda(e.target.value)}
          />
          {busqueda && (
            <button className={styles.btnLimpiarBusqueda} onClick={() => setBusqueda('')}>✕</button>
          )}
        </div>
      </div>

      <div className={styles.layout}>
        {/* Sidebar filtros */}
        <aside className={styles.sidebar}>
          <div className={styles.sidebarHeader}>
            <h3 className={styles.sidebarTitulo}>Filtros</h3>
            <button data-testid="btn-limpiar" className={styles.btnLimpiar} onClick={handleLimpiarFiltros}>
              Limpiar todo
            </button>
          </div>

          <div className={styles.filtroGrupo}>
            <label className={styles.filtroLabel}>Categoría</label>
            <select data-testid="filtro-categoria" className={styles.select} value={categoriaSeleccionada} onChange={e => setCategoriaSeleccionada(e.target.value)}>
              {categorias.map(cat => <option key={cat} value={cat}>{cat}</option>)}
            </select>
          </div>

          <div className={styles.filtroGrupo}>
            <label className={styles.filtroLabel}>Marca</label>
            <select data-testid="filtro-marca" className={styles.select} value={marcaSeleccionada} onChange={e => setMarcaSeleccionada(e.target.value)}>
              {marcas.map(marca => <option key={marca} value={marca}>{marca}</option>)}
            </select>
          </div>

          <div className={styles.filtroGrupo}>
            <label className={styles.filtroLabel}>Ordenar por precio</label>
            <select data-testid="orden-precio" className={styles.select} value={ordenPrecio} onChange={e => setOrdenPrecio(e.target.value)}>
              <option value="ninguno">Sin orden</option>
              <option value="asc">Menor precio primero</option>
              <option value="desc">Mayor precio primero</option>
            </select>
          </div>

          <div className={styles.filtroGrupo}>
            <label className={styles.filtroLabel}>Rango de precio</label>
            <div className={styles.rangoPrecios}>
              <div className={styles.inputPrecioWrapper}>
                <span className={styles.inputPrecioPrefix}>$</span>
                <input data-testid="precio-min" className={styles.inputPrecio} type="number" placeholder="Mínimo" value={precioMin} onChange={e => setPrecioMin(e.target.value)} min="0" />
              </div>
              <span className={styles.rangoDivider}>—</span>
              <div className={styles.inputPrecioWrapper}>
                <span className={styles.inputPrecioPrefix}>$</span>
                <input data-testid="precio-max" className={styles.inputPrecio} type="number" placeholder="Máximo" value={precioMax} onChange={e => setPrecioMax(e.target.value)} min="0" />
              </div>
            </div>
            {precioMin && precioMax && parseInt(precioMin) > parseInt(precioMax) && (
              <span className={styles.errorRango}>El mínimo no puede ser mayor al máximo</span>
            )}
          </div>

          {(categoriaSeleccionada !== 'Todas' || marcaSeleccionada !== 'Todas' || precioMin || precioMax || ordenPrecio !== 'ninguno') && (
            <div className={styles.filtrosActivos}>
              <span className={styles.filtrosActivosLabel}>Filtros activos:</span>
              {categoriaSeleccionada !== 'Todas' && (
                <span className={styles.badge}>{categoriaSeleccionada} <button onClick={() => setCategoriaSeleccionada('Todas')}>✕</button></span>
              )}
              {marcaSeleccionada !== 'Todas' && (
                <span className={styles.badge}>{marcaSeleccionada} <button onClick={() => setMarcaSeleccionada('Todas')}>✕</button></span>
              )}
              {precioMin && (
                <span className={styles.badge}>Min: ${parseInt(precioMin).toLocaleString('es-CL')} <button onClick={() => setPrecioMin('')}>✕</button></span>
              )}
              {precioMax && (
                <span className={styles.badge}>Max: ${parseInt(precioMax).toLocaleString('es-CL')} <button onClick={() => setPrecioMax('')}>✕</button></span>
              )}
              {ordenPrecio !== 'ninguno' && (
                <span className={styles.badge}>{ordenPrecio === 'asc' ? 'Menor precio' : 'Mayor precio'} <button onClick={() => setOrdenPrecio('ninguno')}>✕</button></span>
              )}
            </div>
          )}
        </aside>

        {/* Productos */}
        <div className={styles.contenido}>
          {loading && (
            <div data-testid="skeleton-loader" className={styles.skeletonGrid}>
              {[1,2,3,4,5].map(i => <div key={i} data-testid="skeleton-item" className={styles.skeletonCard} />)}
            </div>
          )}

          {error && (
            <div data-testid="error-catalogo" className={styles.error}>
              <p>Error al cargar el catálogo</p>
              <button className={styles.btnReintentar} onClick={() => window.location.reload()}>Reintentar</button>
            </div>
          )}

          {!loading && !error && (
            <>
              {productosFiltrados.length === 0 ? (
                <div data-testid="sin-resultados" className={styles.sinResultados}>
                  <span className={styles.sinResultadosIcono}>🔍</span>
                  <p>No se encontraron productos</p>
                  <button className={styles.btnLimpiar} onClick={handleLimpiarFiltros}>Limpiar filtros</button>
                </div>
              ) : (
                <div data-testid="lista-productos" className={styles.grid}>
                  {productosFiltrados.map(producto => (
                    <ProductoCard key={producto.id} producto={producto} />
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </main>
  )
}

export default Catalogo