import { createContext, useContext, useState, useEffect } from 'react'

const ProductosContext = createContext(null)

export function ProductosProvider({ children }) {
  const [productos, setProductos] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)
  const [categorias, setCategorias] = useState([])
  const [marcas, setMarcas] = useState([])

  const cargarProductos = () => {
    setLoading(true)
    setError(false)
    fetch('/api/productos')
      .then(res => {
        if (!res.ok) throw new Error('Error')
        return res.json()
      })
      .then(data => {
        // El backend devuelve "imagenPrincipal", pero el resto del frontend
        // (ProductoCard, catálogo público, admin) usa "imagen". Se normaliza acá,
        // en un solo lugar, para no tener que tocar cada componente.
        const normalizados = data.map(p => ({ ...p, imagen: p.imagenPrincipal }))
        setProductos(normalizados)
        // Extraer categorías y marcas únicas
        setCategorias([...new Set(normalizados.map(p => p.categoria).filter(Boolean))])
        setMarcas([...new Set(normalizados.map(p => p.marca).filter(Boolean))])
        setLoading(false)
      })
      .catch(() => {
        setError(true)
        setLoading(false)
      })
  }

  useEffect(() => {
    cargarProductos()
  }, [])

  const agregarProducto = (producto) => {
    setProductos(prev => {
      const nuevos = [...prev, producto]
      // Actualizar categorías y marcas
      setCategorias([...new Set(nuevos.map(p => p.categoria).filter(Boolean))])
      setMarcas([...new Set(nuevos.map(p => p.marca).filter(Boolean))])
      return nuevos
    })
  }

  const actualizarProducto = (productoActualizado) => {
    setProductos(prev => {
      const nuevos = prev.map(p => p.id === productoActualizado.id ? productoActualizado : p)
      setCategorias([...new Set(nuevos.map(p => p.categoria).filter(Boolean))])
      setMarcas([...new Set(nuevos.map(p => p.marca).filter(Boolean))])
      return nuevos
    })
  }

  const eliminarProducto = (id) => {
    setProductos(prev => {
      const nuevos = prev.filter(p => p.id !== id)
      setCategorias([...new Set(nuevos.map(p => p.categoria).filter(Boolean))])
      setMarcas([...new Set(nuevos.map(p => p.marca).filter(Boolean))])
      return nuevos
    })
  }

  const toggleDestacado = (id, valor) => {
    setProductos(prev =>
      prev.map(p => p.id === id ? { ...p, destacado: valor } : p)
    )
  }

  const actualizarOferta = (id, precioOferta) => {
    setProductos(prev =>
      prev.map(p => p.id === id ? { ...p, precioOferta, enOferta: true } : p)
    )
  }

  const actualizarStock = (id, nuevoStock) => {
    setProductos(prev =>
      prev.map(p => p.id === id ? { ...p, stock: nuevoStock } : p)
    )
  }

  const agregarCategoria = (categoria) => {
    if (!categorias.includes(categoria)) {
      setCategorias(prev => [...prev, categoria])
    }
  }

  const agregarMarca = (marca) => {
    if (!marcas.includes(marca)) {
      setMarcas(prev => [...prev, marca])
    }
  }

  return (
    <ProductosContext.Provider value={{
      productos,
      loading,
      error,
      categorias,
      marcas,
      cargarProductos,
      agregarProducto,
      actualizarProducto,
      eliminarProducto,
      toggleDestacado,
      actualizarOferta,
      actualizarStock,
      agregarCategoria,
      agregarMarca,
    }}>
      {children}
    </ProductosContext.Provider>
  )
}

export const useProductos = () => useContext(ProductosContext)

