import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import styles from './Register.module.css'
import { useAuth } from '../../../context/AuthContext'
import { registerService } from '../../../services/authService'

function Register() {
  const navigate = useNavigate()
  const { login } = useAuth()
  
  const [form, setForm] = useState({ username: '', correo: '', password: '', confirmPassword: '' })
  const [errores, setErrores] = useState({})
  const [loading, setLoading] = useState(false)
  const [exitoso, setExitoso] = useState(false)
  const [errorServidor, setErrorServidor] = useState(false)

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setErrores({ ...errores, [e.target.name]: '' })
  }

  const validar = () => {
    const nuevosErrores = {}
    if (!form.username.trim()) nuevosErrores.username = 'El nombre de usuario es requerido'
    if (!form.correo.includes('@')) nuevosErrores.correo = 'Correo inválido'
    if (form.password.length < 8) nuevosErrores.password = 'Mínimo 8 caracteres'
    if (form.password !== form.confirmPassword) nuevosErrores.confirmPassword = 'Las contraseñas no coinciden'
    return nuevosErrores
  }

  const handleSubmit = async () => {
    const nuevosErrores = validar()
    if (Object.keys(nuevosErrores).length > 0) { setErrores(nuevosErrores); return }

    setLoading(true)
    try {
      const data = await registerService(form)
      localStorage.setItem('token', data.token)
      login(data.usuario)
      setExitoso(true)
      setTimeout(() => navigate('/'), 2000) 
    } catch (error) {
      setErrorServidor(true)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className={styles.main}>
      <div className={styles.card}>
        <div className={styles.logo}>Smart<span className={styles.logoAccent}>Logix</span></div>
        <h1 className={styles.titulo}>Crear cuenta</h1>

        {exitoso && <div className={styles.exitoso}>Cuenta creada correctamente.</div>}
        {errorServidor && <div className={styles.errorServidor}>Error al crear la cuenta.</div>}

        <div className={styles.form}>
          <div className={styles.campo}>
            <label className={styles.label}>Nombre de usuario</label>
            <input name="username" className={`${styles.input} ${errores.username ? styles.inputError : ''}`} placeholder="Tu usuario" value={form.username} onChange={handleChange} />
            {errores.username && <span className={styles.errorMsg}>{errores.username}</span>}
          </div>

          <div className={styles.campo}>
            <label className={styles.label}>Correo electrónico</label>
            <input name="correo" type="email" className={`${styles.input} ${errores.correo ? styles.inputError : ''}`} placeholder="tu@correo.cl" value={form.correo} onChange={handleChange} />
            {errores.correo && <span className={styles.errorMsg}>{errores.correo}</span>}
          </div>

          <div className={styles.fila}>
            <div className={styles.campo}>
              <label className={styles.label}>Contraseña</label>
              <input name="password" type="password" className={`${styles.input} ${errores.password ? styles.inputError : ''}`} placeholder="••••••••" value={form.password} onChange={handleChange} />
            </div>
            <div className={styles.campo}>
              <label className={styles.label}>Confirmar</label>
              <input name="confirmPassword" type="password" className={`${styles.input} ${errores.confirmPassword ? styles.inputError : ''}`} placeholder="••••••••" value={form.confirmPassword} onChange={handleChange} />
            </div>
          </div>
          {errores.password && <span className={styles.errorMsg}>{errores.password || errores.confirmPassword}</span>}

          <button className={styles.btnSubmit} onClick={handleSubmit} disabled={loading}>
            {loading ? 'Registrando...' : 'Crear cuenta'}
          </button>
        </div>
      </div>
    </main>
  )
}

export default Register

