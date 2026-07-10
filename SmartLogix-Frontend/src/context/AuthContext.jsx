import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    const guardado = localStorage.getItem('usuario')
    
    // 1. Verificamos que exista y que NO sea la cadena de texto "undefined"
    if (guardado && guardado !== 'undefined') {
      try {
        // 2. Intentamos transformarlo a JSON
        setUsuario(JSON.parse(guardado))
      } catch (error) {
        // 3. Si falla (dato corrupto), lo limpiamos silenciosamente
        console.warn("Dato de usuario corrupto en memoria. Limpiando...")
        localStorage.removeItem('usuario')
      }
    } else if (guardado === 'undefined') {
      // Si por algún motivo se guardó el texto "undefined", lo borramos
      localStorage.removeItem('usuario')
    }
    
    setCargando(false)
  }, [])

  const login = (datosUsuario) => {
    setUsuario(datosUsuario)
    localStorage.setItem('usuario', JSON.stringify(datosUsuario))
  }

  const logout = () => {
    setUsuario(null)
    localStorage.removeItem('usuario')
    localStorage.removeItem('token')
    localStorage.removeItem('admin_token')
  }

  const esAdmin = usuario?.rol === 'admin'

  return (
    <AuthContext.Provider value={{ usuario, login, logout, estaAutenticado: !!usuario, esAdmin, cargando }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)

