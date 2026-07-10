import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import styles from './Login.module.css'
import { useAuth } from '../../../context/AuthContext'
import { loginService } from '../../../services/authService'
import { obtenerPerfilPorAuthId } from '../../../services/perfilService'

function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login } = useAuth()
  const [form, setForm] = useState({ correo: '', password: '' })
  const [errores, setErrores] = useState({})
  const [loading, setLoading] = useState(false)
  const [errorServidor, setErrorServidor] = useState(false)
  const [exitoso, setExitoso] = useState(false)

  const rutaDestino = location.state?.from?.pathname || '/'

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setErrores({ ...errores, [e.target.name]: '' })
    setErrorServidor(false)
  }

  const validar = () => {
    const nuevosErrores = {}
    if (!form.correo.includes('@')) nuevosErrores.correo = 'Correo inválido'
    if (form.password.length < 8) nuevosErrores.password = 'Mínimo 8 caracteres'
    return nuevosErrores
  }

  const handleSubmit = async () => {
    const nuevosErrores = validar()
    if (Object.keys(nuevosErrores).length > 0) { setErrores(nuevosErrores); return }

    setLoading(true)
    try {
      const data = await loginService(form)

      localStorage.setItem('token', data.token)

      // data.usuario viene de ms_idp: solo trae id, username, email (auth).
      // Buscamos el perfil real en ms_usuario (nombre, apellido, telefono, imagenPerfil, direcciones).
      let perfil = null
      try {
        perfil = await obtenerPerfilPorAuthId(data.usuario.id)
      } catch (err) {
        console.warn('No se pudo cargar el perfil completo, se usarán solo los datos de auth:', err)
      }

      // Combinamos: el perfil completo tiene prioridad para nombre/apellido/etc,
      // PERO nunca dejamos que perfil.id pise el id real (el authId de ms_idp).
      // perfil.id es el PK propio de la tabla "usuarios" en ms_usuario, un número
      // totalmente distinto del authId, y el resto de la app (direcciones, editar
      // perfil) depende de que usuario.id siga siendo el authId.
      const { id: _idPerfilIgnorado, ...perfilSinId } = perfil || {}

      login({
        ...data.usuario,
        correo: data.usuario.email,
        ...perfilSinId,
      })

      setExitoso(true)
      setTimeout(() => navigate(rutaDestino), 2000)
    } catch (error) {
      setErrorServidor(true)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className={styles.main}>
      <div className={styles.card}>
        <div className={styles.logo}>
          Smart<span className={styles.logoAccent}>Logix</span>
        </div>
        <h1 className={styles.titulo}>Iniciar sesión</h1>

        {exitoso && <div data-testid="login-exitoso" className={styles.exitoso}>Bienvenido de vuelta. Redirigiendo...</div>}
        {errorServidor && <div data-testid="error-credenciales" className={styles.errorCredenciales}>Correo o contraseña incorrectos</div>}

        <form 
          className={styles.form}
          onSubmit={(e) => {
            e.preventDefault();
            handleSubmit();
          }}
        >
          <div className={styles.campo}>
            <label className={styles.label}>Correo electrónico</label>
            <input
              data-testid="input-correo"
              name="correo"
              type="email"
              className={`${styles.input} ${errores.correo ? styles.inputError : ''}`}
              placeholder="tu@correo.cl"
              value={form.correo}
              onChange={handleChange}
            />
            {errores.correo && <span data-testid="error-correo" className={styles.errorMsg}>{errores.correo}</span>}
          </div>

          <div className={styles.campo}>
            <label className={styles.label}>Contraseña</label>
            <input
              data-testid="input-password"
              name="password"
              type="password"
              className={`${styles.input} ${errores.password ? styles.inputError : ''}`}
              placeholder="••••••••"
              value={form.password}
              onChange={handleChange}
            />
            {errores.password && <span data-testid="error-password" className={styles.errorMsg}>{errores.password}</span>}
          </div>

          <button 
            type="submit"
            data-testid="btn-login" 
            className={styles.btnSubmit} 
            disabled={loading}
          >
            {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
          </button>
        </form>

        <div className={styles.links}>
          <button data-testid="link-recuperar" className={styles.link} onClick={() => navigate('/recuperar-password')}>
            ¿Olvidaste tu contraseña?
          </button>
          <button data-testid="link-registro" className={styles.link} onClick={() => navigate('/registro')}>
            ¿No tienes cuenta? Regístrate
          </button>
        </div>
      </div>
    </main>
  )
}

export default Login

