import { createContext, useContext, useState, useEffect } from 'react'
import { useAuth } from './AuthContext'

const DireccionesContext = createContext(null)

export function DireccionesProvider({ children }) {
  const { usuario, estaAutenticado } = useAuth()
  const [direcciones, setDirecciones] = useState([])

  // Función para obtener las direcciones desde el backend
  const cargarDirecciones = async () => {
    // Asumimos que usuario.id equivale al authId de tu backend
    if (estaAutenticado && usuario?.id) {
      try {
        const res = await fetch(`/api/usuarios/auth/${usuario.id}/direcciones`)
        if (res.ok) {
          const data = await res.json()
          setDirecciones(data)
        }
      } catch (error) {
        console.error("Error al cargar direcciones desde la BD:", error)
      }
    } else {
      setDirecciones([])
    }
  }

  // Cargar al iniciar sesión
  useEffect(() => {
    cargarDirecciones()
  }, [estaAutenticado, usuario])

  // Guardar en la base de datos
  const agregarDireccion = async (direccion) => {
    try {
      const nueva = {
        ...direccion,
        predeterminada: direcciones.length === 0,
      }

      const res = await fetch(`/api/usuarios/auth/${usuario.id}/direcciones`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nueva)
      })

      if (res.ok) {
        // Como el backend devuelve un texto y no el objeto, 
        // volvemos a cargar toda la lista para obtener el ID generado por la BD
        await cargarDirecciones()
      }
    } catch (error) {
      console.error("Error al guardar en la BD:", error)
    }
  }

  // Eliminar de la base de datos
  const eliminarDireccion = async (id) => {
    try {
      const res = await fetch(`/api/usuarios/auth/${usuario.id}/direcciones/${id}`, { 
        method: 'DELETE' 
      })
      if (res.ok) {
        await cargarDirecciones()
      }
    } catch (error) {
      console.error("Error al eliminar de la BD:", error)
    }
  }

  // Actualizar predeterminada en la base de datos
  const marcarPredeterminada = async (id) => {
    try {
      const res = await fetch(`/api/usuarios/auth/${usuario.id}/direcciones/${id}/predeterminada`, { 
        method: 'PUT'
      })
      if (res.ok) {
        await cargarDirecciones()
      }
    } catch (error) {
      console.error("Error al actualizar la BD:", error)
    }
  }

  const direccionPredeterminada = direcciones.find(d => d.predeterminada) || null

  return (
    <DireccionesContext.Provider value={{
      direcciones,
      agregarDireccion,
      eliminarDireccion,
      marcarPredeterminada,
      direccionPredeterminada,
    }}>
      {children}
    </DireccionesContext.Provider>
  )
}

export const useDirecciones = () => useContext(DireccionesContext)

