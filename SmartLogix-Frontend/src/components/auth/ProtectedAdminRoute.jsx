import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function ProtectedAdminRoute() {
  const { estaAutenticado, esAdmin, cargando } = useAuth()

  // Mientras se está leyendo el usuario guardado en localStorage, no decidimos nada todavía
  if (cargando) return null

  if (!estaAutenticado || !esAdmin) {
    return <Navigate to="/admin/login" replace />
  }

  return <Outlet />
}

export default ProtectedAdminRoute

