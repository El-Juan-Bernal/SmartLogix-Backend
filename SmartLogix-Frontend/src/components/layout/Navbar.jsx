import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useCarrito } from '../../context/CarritoContext'
import { useAuth } from '../../context/AuthContext'
import styles from './Navbar.module.css'

function Navbar() {
  const navigate = useNavigate()
  const location = useLocation()
  const [menuAbierto, setMenuAbierto] = useState(false)
  
  // MODIFICACIÓN 1: Extraemos la función vaciarCarrito del contexto
  const { cantidadTotal, vaciarCarrito } = useCarrito() 
  const { usuario, logout, estaAutenticado, esAdmin } = useAuth()

  const links = [
    { label: 'Inicio', path: '/' },
    { label: 'Catálogo', path: '/catalogo' },
  ]

  const isActive = (path) => location.pathname === path

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav className={styles.navbar}>
      <div className={styles.container}>

        <div className={styles.logo} onClick={() => navigate('/')}>
          <span className={styles.logoText}>Smart</span>
          <span className={styles.logoAccent}>Logix</span>
        </div>

        <ul className={styles.links}>
          {links.map(link => (
            <li key={link.path}>
              <button
                className={`${styles.link} ${isActive(link.path) ? styles.active : ''}`}
                onClick={() => navigate(link.path)}
              >
                {link.label}
              </button>
            </li>
          ))}
        </ul>

        <div className={styles.acciones}>
          <button className={styles.btnCarrito} onClick={() => navigate('/carrito')}>
            🛒
            {cantidadTotal > 0 && (
              <span data-testid="carrito-contador" className={styles.carritoContador}>
                {cantidadTotal}
              </span>
            )}
          </button>

          {estaAutenticado ? (
            <>
              {esAdmin && (
                <button
                  className={styles.btnAdmin}
                  onClick={() => navigate('/admin/dashboard')}
                >
                  ⚙️ Admin
                </button>
              )}
              <button className={styles.linkPerfil} onClick={() => navigate('/perfil')}>
                {usuario.nombre}
              </button>
              <button className={styles.btnLogin} onClick={handleLogout}>
                Cerrar sesión
              </button>
            </>
          ) : (
            <>
              <button className={styles.btnLogin} onClick={() => navigate('/login')}>
                Iniciar sesión
              </button>
              <button className={styles.btnRegistro} onClick={() => navigate('/registro')}>
                Registrarse
              </button>
            </>
          )}
        </div>

        <button className={styles.hamburguesa} onClick={() => setMenuAbierto(!menuAbierto)}>
          <span /><span /><span />
        </button>
      </div>

      {menuAbierto && (
        <div className={styles.menuMobile}>
          {links.map(link => (
            <button
              key={link.path}
              className={`${styles.linkMobile} ${isActive(link.path) ? styles.active : ''}`}
              onClick={() => { navigate(link.path); setMenuAbierto(false) }}
            >
              {link.label}
            </button>
          ))}
          <button className={styles.linkMobile} onClick={() => { navigate('/carrito'); setMenuAbierto(false) }}>
            🛒 Carrito {cantidadTotal > 0 && `(${cantidadTotal})`}
          </button>
          {estaAutenticado ? (
            <>
              {esAdmin && (
                <button className={styles.linkMobile} onClick={() => { navigate('/admin/dashboard'); setMenuAbierto(false) }}>
                  ⚙️ Panel Admin
                </button>
              )}
              <button className={styles.linkMobile} onClick={() => { navigate('/perfil'); setMenuAbierto(false) }}>
                {usuario.nombre}
              </button>
              <button className={styles.btnLogin} onClick={handleLogout}>
                Cerrar sesión
              </button>
            </>
          ) : (
            <>
              <button className={styles.btnLogin} onClick={() => navigate('/login')}>
                Iniciar sesión
              </button>
              <button className={styles.btnRegistro} onClick={() => navigate('/registro')}>
                Registrarse
              </button>
            </>
          )}
        </div>
      )}
    </nav>
  )
}

export default Navbar

