import { useNavigate } from 'react-router-dom'
import styles from './Footer.module.css'

function Footer() {
  const navigate = useNavigate()

  return (
    <footer className={styles.footer}>
      <div className={styles.container}>

        {/* Logo y descripción */}
        <div className={styles.columna}>
          <div className={styles.logo}>
            <span className={styles.logoText}>Smart</span>
            <span className={styles.logoAccent}>Logix</span>
          </div>
          <p className={styles.descripcion}>
            Tu tienda de tecnología con los mejores productos y precios del mercado.
          </p>
        </div>

        {/* Links navegación */}
        <div className={styles.columna}>
          <h4 className={styles.tituloColumna}>Navegación</h4>
          <ul className={styles.listaLinks}>
            <li><button className={styles.link} onClick={() => navigate('/')}>Inicio</button></li>
            <li><button className={styles.link} onClick={() => navigate('/catalogo')}>Catálogo</button></li>
            <li><button className={styles.link} onClick={() => navigate('/carrito')}>Carrito</button></li>
          </ul>
        </div>

        {/* Links cuenta */}
        <div className={styles.columna}>
          <h4 className={styles.tituloColumna}>Mi cuenta</h4>
          <ul className={styles.listaLinks}>
            <li><button className={styles.link} onClick={() => navigate('/login')}>Iniciar sesión</button></li>
            <li><button className={styles.link} onClick={() => navigate('/registro')}>Registrarse</button></li>
            <li><button className={styles.link} onClick={() => navigate('/perfil')}>Mi perfil</button></li>
          </ul>
        </div>

      </div>

      <div className={styles.bottom}>
        <p className={styles.copyright}>© 2025 SmartLogix. Todos los derechos reservados.</p>
      </div>
    </footer>
  )
}

export default Footer