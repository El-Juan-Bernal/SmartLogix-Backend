import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../../context/AuthContext'
import { obtenerPerfilPorAuthId } from '../../../services/perfilService'
import styles from './AdminLogin.module.css'

function AdminLogin() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [form, setForm] = useState({ correo: '', password: '' })
  const [error, setError] = useState(false)
  const [loading, setLoading] = useState(false)

  const handleLogin = async () => {
    if (!form.correo.includes('@') || form.password.length < 6) {
      setError(true)
      return
    }
    setLoading(true)
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: form.correo, password: form.password }),
      })
      if (!res.ok) throw new Error('Credenciales incorrectas')

      const data = await res.json()

      if (data.usuario?.rol !== 'admin') {
        setError(true)
        return
      }

      localStorage.setItem('token', data.token)

      let perfil = null
      try {
        perfil = await obtenerPerfilPorAuthId(data.usuario.id)
      } catch {
        // Si no tiene perfil completo en ms_usuario, igual lo dejamos pasar como admin
      }

      // Igual que en el login normal: perfil.id NO debe pisar el authId.
      const { id: _idPerfilIgnorado, ...perfilSinId } = perfil || {}

      login({
        ...data.usuario,
        correo: data.usuario.email,
        ...perfilSinId,
      })

      navigate('/admin/dashboard')
    } catch {
      setError(true)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className={styles.main}>
      <div className={styles.card}>
        <div className={styles.logo}>
          Smart<span className={styles.logoAccent}>Logix</span>
          <span className={styles.badge}>Admin</span>
        </div>
        <h1 className={styles.titulo}>Acceso administrativo</h1>

        {error && (
          <div className={styles.error}>
            Credenciales incorrectas o sin permisos de administrador
          </div>
        )}

        <div className={styles.form}>
          <div className={styles.campo}>
            <label className={styles.label}>Correo</label>
            <input
              className={styles.input}
              type="email"
              placeholder="admin@smartlogix.cl"
              value={form.correo}
              onChange={e => { setForm({ ...form, correo: e.target.value }); setError(false) }}
            />
          </div>
          <div className={styles.campo}>
            <label className={styles.label}>Contraseña</label>
            <input
              className={styles.input}
              type="password"
              placeholder="••••••••"
              value={form.password}
              onChange={e => { setForm({ ...form, password: e.target.value }); setError(false) }}
            />
          </div>
          <button className={styles.btnLogin} onClick={handleLogin} disabled={loading}>
            {loading ? 'Verificando...' : 'Ingresar al panel'}
          </button>
        </div>
      </div>
    </main>
  )
}

export default AdminLogin

