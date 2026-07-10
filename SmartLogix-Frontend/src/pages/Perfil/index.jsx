import { useState, useEffect } from 'react'
import { useAuth } from '../../context/AuthContext'
import { useDirecciones } from '../../context/DireccionesContext'
import { useHistorial } from '../../context/HistorialContext'
import styles from './Perfil.module.css'

// 1. Importamos el diccionario de ubicaciones
import { regionesChile } from '../../utils/ubicaciones' 

// 2. Alineamos el estado inicial con el modelo de Java
const direccionInicial = { 
  alias: '', 
  calle: '', 
  numero: '', 
  region: '', 
  comuna: '', 
  departamento: '', 
  esPrincipal: false 
}

function Perfil() {
  const { usuario, login } = useAuth()
  const { direcciones, agregarDireccion, eliminarDireccion, marcarPredeterminada } = useDirecciones()
  const { historial, recargarHistorial } = useHistorial()
  const [form, setForm] = useState(usuario || {})
  const [editando, setEditando] = useState(false)
  const [guardando, setGuardando] = useState(false)
  const [guardadoExitoso, setGuardadoExitoso] = useState(false)
  const [errorGuardar, setErrorGuardar] = useState(false)
  const [errores, setErrores] = useState({})
  const [seccionActiva, setSeccionActiva] = useState('perfil')
  const [formDireccion, setFormDireccion] = useState(direccionInicial)
  const [erroresDireccion, setErroresDireccion] = useState({})
  const [agregandoDireccion, setAgregandoDireccion] = useState(false)

  useEffect(() => {
    if (usuario) setForm(usuario)
  }, [usuario])

  const validar = () => {
    const e = {}
    if (!form.nombre?.trim()) e.nombre = 'El nombre es requerido'
    return e
  }

  // 3. Validamos los nuevos campos obligatorios (alias y comuna)
  const validarDireccion = () => {
    const e = {}
    if (!formDireccion.region.trim()) e.region = 'La región es requerida'
    if (!formDireccion.comuna.trim()) e.comuna = 'La comuna es requerida'
    if (!formDireccion.calle.trim()) e.calle = 'La calle es requerida'
    if (!formDireccion.numero.trim()) e.numero = 'El número es requerido'
    if (!formDireccion.alias.trim()) e.alias = 'El nombre o alias es requerido'
    return e
  }

  const handleGuardar = async () => {
    const e = validar()
    if (Object.keys(e).length > 0) { setErrores(e); return }
    setGuardando(true)
    
    // Filtramos y construimos el objeto exactamente como el DTO de Java lo espera
    const payload = {
      nombre: form.nombre,
      apellido: form.apellido,
      telefono: form.telefono,
      imagenPerfil: form.imagenPerfil || null 
    }

    try {
      const res = await fetch(`/api/usuarios/completar/${usuario.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload), // <--- ENVIAMOS SOLO EL PAYLOAD LIMPIO
      })
      
      if (!res.ok) {
        const errorMsg = await res.text();
        throw new Error(errorMsg || 'Error al guardar en el servidor');
      }
      
      // Actualizamos el contexto con los nuevos datos
      login({ ...usuario, ...payload }); 
      setEditando(false)
      setGuardadoExitoso(true)
      setTimeout(() => setGuardadoExitoso(false), 2000)
    } catch (error) {
      console.error("Error al guardar:", error);
      setErrorGuardar(true)
    } finally {
      setGuardando(false)
    }
  }

  const handleAgregarDireccion = () => {
    const e = validarDireccion()
    if (Object.keys(e).length > 0) { setErroresDireccion(e); return }
    
    // El Context de direcciones se encargará de enviarlo al backend
    agregarDireccion(formDireccion)
    
    setFormDireccion(direccionInicial)
    setAgregandoDireccion(false)
    setErroresDireccion({})
  }

  const iniciales = usuario ? `${usuario.nombre?.[0] || ''}${usuario.apellido?.[0] || ''}`.toUpperCase() : ''

  if (!usuario) return null

  return (
    <main className={styles.main}>
      <div className={styles.perfilHeader}>
        <div className={styles.avatar}>{iniciales}</div>
        <div className={styles.perfilInfo}>
          <h1 className={styles.perfilNombre}>{usuario.nombre} {usuario.apellido}</h1>
          <span className={styles.perfilCorreo}>{usuario.correo}</span>
        </div>
      </div>

      <div className={styles.tabs}>
        <button
          data-testid="tab-perfil"
          className={`${styles.tab} ${seccionActiva === 'perfil' ? styles.tabActivo : ''}`}
          onClick={() => setSeccionActiva('perfil')}
        >
          👤 Mi perfil
        </button>
        <button
          data-testid="tab-direcciones"
          className={`${styles.tab} ${seccionActiva === 'direcciones' ? styles.tabActivo : ''}`}
          onClick={() => setSeccionActiva('direcciones')}
        >
          📍 Direcciones
        </button>
        <button
          data-testid="tab-historial"
          className={`${styles.tab} ${seccionActiva === 'historial' ? styles.tabActivo : ''}`}
          onClick={() => {
            setSeccionActiva('historial')
            recargarHistorial()
          }}
        >
          📦 Historial
        </button>
      </div>

      {/* Tab Perfil */}
      {seccionActiva === 'perfil' && (
        <div className={styles.card}>
          {guardadoExitoso && <div data-testid="guardado-exitoso" className={styles.exitoso}>✓ Perfil actualizado correctamente</div>}
          {errorGuardar && <div data-testid="error-guardar" className={styles.errorGuardar}>Error al guardar los cambios</div>}

          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitulo}>Información personal</h2>
            {!editando && (
              <button data-testid="btn-editar" className={styles.btnEditar} onClick={() => setEditando(true)}>
                Editar perfil
              </button>
            )}
          </div>

          {!editando ? (
            <div data-testid="vista-perfil" className={styles.grid}>
              <div className={styles.campo}>
                <span className={styles.label}>Nombre</span>
                <span data-testid="perfil-nombre" className={styles.valor}>{usuario.nombre}</span>
              </div>
              <div className={styles.campo}>
                <span className={styles.label}>Apellido</span>
                <span data-testid="perfil-apellido" className={styles.valor}>{usuario.apellido}</span>
              </div>
              <div className={styles.campo}>
                <span className={styles.label}>Correo</span>
                <span data-testid="perfil-correo" className={styles.valor}>{usuario.correo}</span>
              </div>
              <div className={styles.campo}>
                <span className={styles.label}>Teléfono</span>
                <span data-testid="perfil-telefono" className={styles.valor}>{usuario.telefono || '-'}</span>
              </div>
            </div>
          ) : (
            <div data-testid="form-edicion" className={styles.grid}>
              <div className={styles.campo}>
                <span className={styles.label}>Nombre</span>
                <input data-testid="input-nombre" className={`${styles.input} ${errores.nombre ? styles.inputError : ''}`} value={form.nombre || ''} onChange={e => setForm({ ...form, nombre: e.target.value })} />
                {errores.nombre && <span data-testid="error-nombre" className={styles.errorMsg}>{errores.nombre}</span>}
              </div>
              <div className={styles.campo}>
                <span className={styles.label}>Apellido</span>
                <input data-testid="input-apellido" className={styles.input} value={form.apellido || ''} onChange={e => setForm({ ...form, apellido: e.target.value })} />
              </div>
              <div className={styles.campo}>
                <span className={styles.label}>Correo</span>
                <input
                  data-testid="input-correo"
                  className={styles.input}
                  value={usuario.correo || ''}
                  disabled
                  readOnly
                  title="El correo no se puede modificar"
                />
                <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '-4px' }}>
                  El correo no se puede modificar
                </span>
              </div>
              <div className={styles.campo}>
                <span className={styles.label}>Teléfono</span>
                <input data-testid="input-telefono" className={styles.input} value={form.telefono || ''} onChange={e => setForm({ ...form, telefono: e.target.value })} />
              </div>
              <div className={styles.acciones}>
                <button data-testid="btn-guardar" className={styles.btnGuardar} onClick={handleGuardar} disabled={guardando}>
                  {guardando ? 'Guardando...' : 'Guardar cambios'}
                </button>
                <button data-testid="btn-cancelar" className={styles.btnCancelar} onClick={() => { setForm(usuario); setEditando(false); setErrores({}) }}>
                  Cancelar
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Tab Direcciones */}
      {seccionActiva === 'direcciones' && (
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitulo}>Mis direcciones</h2>
            <button
              data-testid="btn-agregar-direccion"
              className={styles.btnEditar}
              onClick={() => setAgregandoDireccion(true)}
            >
              + Agregar dirección
            </button>
          </div>

          {direcciones.length === 0 && !agregandoDireccion ? (
            <div data-testid="sin-direcciones" className={styles.sinDatos}>
              <span>📍</span>
              <p>No tienes direcciones guardadas</p>
            </div>
          ) : (
            <div data-testid="lista-direcciones" className={styles.listaDirecciones}>
              {direcciones.map(dir => (
                <div key={dir.id} data-testid="direccion-item" className={`${styles.direccionCard} ${dir.predeterminada ? styles.direccionPredeterminada : ''}`}>
                  <div className={styles.direccionInfo}>
                    {dir.predeterminada && <span className={styles.badgePredeterminada}>⭐ Predeterminada</span>}
                    <p className={styles.direccionTexto}><strong>{dir.alias}</strong> - {dir.calle} {dir.numero}</p>
                    <p className={styles.direccionSubtexto}>{dir.comuna}, {dir.region}</p>
                    {dir.departamento && <p className={styles.direccionReferencia}>{dir.departamento}</p>}
                  </div>
                  <div className={styles.direccionAcciones}>
                    {!dir.predeterminada && (
                      <button
                        data-testid={`btn-predeterminada-${dir.id}`}
                        className={styles.btnSecundario}
                        onClick={() => marcarPredeterminada(dir.id)}
                      >
                        Marcar como predeterminada
                      </button>
                    )}
                    <button
                      data-testid={`btn-eliminar-direccion-${dir.id}`}
                      className={styles.btnEliminarDir}
                      onClick={() => eliminarDireccion(dir.id)}
                    >
                      Eliminar
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {agregandoDireccion && (
            <div data-testid="form-direccion" className={styles.formDireccion}>
              <h3 className={styles.formDireccionTitulo}>Nueva dirección</h3>
              
              {/* FILA 1: Región y Comuna (Menús desplegables) */}
              <div className={styles.grid}>
                <div className={styles.campo}>
                  <span className={styles.label}>Región*</span>
                  <select
                    className={`${styles.input} ${erroresDireccion.region ? styles.inputError : ''}`}
                    value={formDireccion.region}
                    onChange={e => setFormDireccion({ ...formDireccion, region: e.target.value, comuna: '' })}
                  >
                    <option value="">Selecciona tu región</option>
                    {regionesChile.map(r => (
                      <option key={r.region} value={r.region}>{r.region}</option>
                    ))}
                  </select>
                  {erroresDireccion.region && <span className={styles.errorMsg}>{erroresDireccion.region}</span>}
                </div>
                <div className={styles.campo}>
                  <span className={styles.label}>Comuna*</span>
                  <select
                    className={`${styles.input} ${erroresDireccion.comuna ? styles.inputError : ''}`}
                    value={formDireccion.comuna}
                    onChange={e => setFormDireccion({ ...formDireccion, comuna: e.target.value })}
                    disabled={!formDireccion.region}
                  >
                    <option value="">Selecciona tu comuna</option>
                    {regionesChile.find(r => r.region === formDireccion.region)?.comunas.map(c => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                  {erroresDireccion.comuna && <span className={styles.errorMsg}>{erroresDireccion.comuna}</span>}
                </div>
              </div>

              {/* FILA 2: Calle y Número */}
              <div className={styles.grid}>
                <div className={styles.campo}>
                  <span className={styles.label}>Calle*</span>
                  <input data-testid="input-calle" className={`${styles.input} ${erroresDireccion.calle ? styles.inputError : ''}`} placeholder="Ej: Av. Paicaví" value={formDireccion.calle} onChange={e => setFormDireccion({ ...formDireccion, calle: e.target.value })} />
                  {erroresDireccion.calle && <span data-testid="error-calle" className={styles.errorMsg}>{erroresDireccion.calle}</span>}
                </div>
                <div className={styles.campo}>
                  <span className={styles.label}>Número*</span>
                  <input data-testid="input-numero" className={`${styles.input} ${erroresDireccion.numero ? styles.inputError : ''}`} placeholder="Ej: 1234" value={formDireccion.numero} onChange={e => setFormDireccion({ ...formDireccion, numero: e.target.value })} />
                  {erroresDireccion.numero && <span data-testid="error-numero" className={styles.errorMsg}>{erroresDireccion.numero}</span>}
                </div>
              </div>

              {/* FILA 3: Depto y Alias */}
              <div className={styles.grid}>
                <div className={styles.campo}>
                  <span className={styles.label}>Depto / Oficina / Nº Casa (Opcional)</span>
                  <input data-testid="input-departamento" className={styles.input} placeholder="Ej: Oficina 301" value={formDireccion.departamento} onChange={e => setFormDireccion({ ...formDireccion, departamento: e.target.value })} />
                </div>
                <div className={styles.campo}>
                  <span className={styles.label}>Nombre de la dirección (Alias)*</span>
                  <input data-testid="input-alias" className={`${styles.input} ${erroresDireccion.alias ? styles.inputError : ''}`} placeholder="Ej: Casa, Trabajo, Local" value={formDireccion.alias} onChange={e => setFormDireccion({ ...formDireccion, alias: e.target.value })} />
                  {erroresDireccion.alias && <span data-testid="error-alias" className={styles.errorMsg}>{erroresDireccion.alias}</span>}
                </div>
              </div>

              <div className={styles.acciones}>
                <button data-testid="btn-guardar-direccion" className={styles.btnGuardar} onClick={handleAgregarDireccion}>
                  Guardar dirección
                </button>
                <button className={styles.btnCancelar} onClick={() => { setAgregandoDireccion(false); setFormDireccion(direccionInicial); setErroresDireccion({}) }}>
                  Cancelar
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Tab Historial */}
      {seccionActiva === 'historial' && (
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitulo}>Historial de compras</h2>
          </div>

          {historial.length === 0 ? (
            <div data-testid="sin-historial" className={styles.sinDatos}>
              <span>📦</span>
              <p>Aún no tienes compras</p>
            </div>
          ) : (
            <div data-testid="lista-historial" className={styles.listaHistorial}>
              {historial.map(pedido => (
                <div key={pedido.id} data-testid="pedido-historial" className={styles.pedidoCard}>
                  <div className={styles.pedidoHeader}>
                    <span data-testid="pedido-numero" className={styles.pedidoNumero}>{pedido.id}</span>
                    <span data-testid="pedido-estado" className={styles.pedidoEstado}>{pedido.estado}</span>
                  </div>
                  <div className={styles.pedidoDetalle}>
                    <span data-testid="pedido-fecha" className={styles.pedidoFecha}>📅 {pedido.fecha}</span>
                    <span data-testid="pedido-total" className={styles.pedidoTotal}>${pedido.total?.toLocaleString('es-CL')}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </main>
  )
}

export default Perfil

