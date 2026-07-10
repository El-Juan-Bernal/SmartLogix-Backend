import { createContext, useContext, useState, useEffect } from 'react'
import { useAuth } from './AuthContext'

const CarritoContext = createContext(null)

export function CarritoProvider({ children }) {
  const { usuario } = useAuth()
  const [items, setItems] = useState([])

  // Helper para construir la llave dinámica
  const getKey = (usr) => {
    const u = usr || usuario
    const idUsuario = u?.correo || u?.email || u?.username
    return `carrito_${idUsuario || 'guest'}`
  }

  useEffect(() => {
    if (usuario) {
      // TRANSICIÓN: El usuario acaba de iniciar sesión
      const datosGuest = localStorage.getItem('carrito_guest')
      const datosUsuario = localStorage.getItem(getKey(usuario))

      const itemsGuest = datosGuest ? JSON.parse(datosGuest) : []
      const itemsUsuario = datosUsuario ? JSON.parse(datosUsuario) : []

      if (itemsGuest.length > 0) {
        // FUSIÓN INTELIGENTE: Combinamos ambos carritos sin duplicar IDs
        const mapaUsuario = new Map(itemsUsuario.map(item => [item.id, item]))
        
        itemsGuest.forEach(itemGuest => {
          if (mapaUsuario.has(itemGuest.id)) {
            const existente = mapaUsuario.get(itemGuest.id)
            // Sumamos las cantidades cuidando no superar el stock disponible del producto
            existente.cantidad = Math.min(existente.cantidad + itemGuest.cantidad, itemGuest.stock)
          } else {
            mapaUsuario.set(itemGuest.id, itemGuest)
          }
        })

        const carroFusionado = Array.from(mapaUsuario.values())
        
        // Guardamos el resultado unificado en la cuenta del usuario
        localStorage.setItem(getKey(usuario), JSON.stringify(carroFusionado))
        // Limpiamos la caja temporal de invitado para el próximo ciclo
        localStorage.removeItem('carrito_guest')
        
        setItems(carroFusionado)
      } else {
        // Si no tenía nada como invitado, simplemente cargamos su carrito histórico
        setItems(itemsUsuario)
      }
    } else {
      // MODO INVITADO: Carga el estado de invitado (también actúa al cerrar sesión)
      const guardadoGuest = localStorage.getItem('carrito_guest')
      setItems(guardadoGuest ? JSON.parse(guardadoGuest) : [])
    }
  }, [usuario])

  const guardarEnStorage = (nuevosItems) => {
    localStorage.setItem(getKey(), JSON.stringify(nuevosItems))
    setItems(nuevosItems)
  }

  const agregarProducto = (producto, cantidad = 1) => {
    const prev = items
    const existente = prev.find(item => item.id === producto.id)
    let nuevos
    if (existente) {
      nuevos = prev.map(item =>
        item.id === producto.id
          ? { ...item, cantidad: Math.min(item.cantidad + cantidad, producto.stock), stock: producto.stock }
          : item
      )
    } else {
      nuevos = [...prev, { ...producto, cantidad }]
    }
    guardarEnStorage(nuevos)
  }

  const sincronizarStock = (productosActualizados) => {
    const nuevos = items.map(item => {
      const productoActual = productosActualizados.find(p => p.id === item.id)
      if (productoActual) {
        return {
          ...item,
          stock: productoActual.stock,
          cantidad: Math.min(item.cantidad, productoActual.stock),
        }
      }
      return item
    })
    guardarEnStorage(nuevos)
  }

  const incrementarCantidad = (id) => {
    const nuevos = items.map(item =>
      item.id === id && item.cantidad < item.stock
        ? { ...item, cantidad: item.cantidad + 1 }
        : item
    )
    guardarEnStorage(nuevos)
  }

  const decrementarCantidad = (id) => {
    const nuevos = items.map(item =>
      item.id === id && item.cantidad > 1
        ? { ...item, cantidad: item.cantidad - 1 }
        : item
    )
    guardarEnStorage(nuevos)
  }

  const eliminarProducto = (id) => {
    const nuevos = items.filter(item => item.id !== id)
    guardarEnStorage(nuevos)
  }

  const vaciarCarrito = () => guardarEnStorage([])

  const total = items.reduce((acc, item) => acc + item.precio * item.cantidad, 0)
  const cantidadTotal = items.reduce((acc, item) => acc + item.cantidad, 0)

  return (
    <CarritoContext.Provider value={{
      items,
      agregarProducto,
      incrementarCantidad,
      decrementarCantidad,
      eliminarProducto,
      vaciarCarrito,
      sincronizarStock,
      total,
      cantidadTotal,
    }}>
      {children}
    </CarritoContext.Provider>
  )
}

export const useCarrito = () => useContext(CarritoContext)

