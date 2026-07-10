import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'

// Componente "invisible": no renderiza nada, solo escucha cambios de ruta
// y sube el scroll al tope. Se monta UNA vez en App.jsx, dentro del Router,
// y aplica automáticamente a todas las páginas.
function ScrollToTop() {
  const { pathname } = useLocation()

  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])

  return null
}

export default ScrollToTop

