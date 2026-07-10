import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import styles from './AdminLayout.module.css'

const menuItems = [
  { label: 'Dashboard', path: '/admin/dashboard', icon: '📊' },
  { label: 'Productos', path: '/admin/productos', icon: '📦' },
  { label: 'Pedidos', path: '/admin/pedidos', icon: '🛍️' },
  { label: 'Inventario', path: '/admin/inventario', icon: '🏭' },
  { label: 'Promociones', path: '/admin/promociones', icon: '🎯' },
]

function AdminLayout() {
  const navigate = useNavigate()
  const location = useLocation()
  const { logout } = useAuth()
  const [sidebarAbierto, setSidebarAbierto] = useState(true)

  const isActive = (path) => location.pathname === path

  const handleCerrarSesion = () => {
    logout()
    navigate('/admin/login')
  }

  return (
    <div className={styles.wrapper}>
      {/* Sidebar */}
      <aside className={`${styles.sidebar} ${sidebarAbierto ? styles.sidebarAbierto : styles.sidebarCerrado}`}>
        <div className={styles.sidebarHeader}>
          <div className={styles.logo} onClick={() => navigate('/admin/dashboard')}>
            <span className={styles.logoText}>Smart</span>
            <span className={styles.logoAccent}>Logix</span>
            {sidebarAbierto && <span className={styles.logoAdmin}>Admin</span>}
          </div>
          <button className={styles.btnToggle} onClick={() => setSidebarAbierto(!sidebarAbierto)}>
            {sidebarAbierto ? '◀' : '▶'}
          </button>
        </div>

        <nav className={styles.nav}>
          {menuItems.map(item => (
            <button
              key={item.path}
              className={`${styles.navItem} ${isActive(item.path) ? styles.navItemActivo : ''}`}
              onClick={() => navigate(item.path)}
            >
              <span className={styles.navIcon}>{item.icon}</span>
              {sidebarAbierto && <span className={styles.navLabel}>{item.label}</span>}
            </button>
          ))}
        </nav>

        <div className={styles.sidebarFooter}>
          <button className={styles.btnCerrarSesion} onClick={handleCerrarSesion}>
            <span>🚪</span>
            {sidebarAbierto && <span>Cerrar sesión</span>}
          </button>
        </div>
      </aside>

      {/* Contenido */}
      <div className={styles.contenido}>
        <header className={styles.topbar}>
          <h2 className={styles.topbarTitulo}>
            {menuItems.find(i => isActive(i.path))?.label || 'Admin'}
          </h2>
          <div className={styles.topbarAcciones}>
            <button className={styles.btnVerTienda} onClick={() => navigate('/')}>
              Ver tienda →
            </button>
          </div>
        </header>
        <main className={styles.main}>
          <Outlet />
        </main>
      </div>
    </div>
  )
}

export default AdminLayout

