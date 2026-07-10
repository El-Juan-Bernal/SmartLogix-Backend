import { Navigate, Outlet, useLocation } from 'react-router-dom' // <-- Agregamos useLocation
import { useAuth } from '../../context/AuthContext'

function ProtectedRoute() {
  const { estaAutenticado, cargando } = useAuth()
  const location = useLocation() // <-- 1. Capturamos la ruta exacta donde está parado (ej: /checkout)

  if (cargando) return null

  if (!estaAutenticado) {
    // <-- 2. Al redirigir, le adjuntamos en secreto la ruta de origen en el 'state'
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return <Outlet />
}

export default ProtectedRoute

