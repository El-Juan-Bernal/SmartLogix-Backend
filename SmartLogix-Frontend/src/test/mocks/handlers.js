import { http, HttpResponse } from 'msw'


const getUsuariosRegistrados = () => {
  if (typeof localStorage === 'undefined') return []
  const data = localStorage.getItem('usuarios_registrados')
  return data ? JSON.parse(data) : []
}

const guardarUsuarios = (usuarios) => {
  if (typeof localStorage === 'undefined') return
  localStorage.setItem('usuarios_registrados', JSON.stringify(usuarios))
}

// Admin precargado
const ADMIN = {
  nombre: 'Admin',
  apellido: 'SmartLogix',
  correo: 'admin@smartlogix.cl',
  password: 'admin12345',
  rol: 'admin',
}

export const handlers = [
  // HU-01, 02, 03 — Productos
  http.get('/api/productos', () => {
  return HttpResponse.json([
  { id: 1, nombre: 'Laptop Gamer', precio: 899990, precioOferta: 809990, categoria: 'Computación', marca: 'ASUS', stock: 10, destacado: true, imagen: null },
  { id: 2, nombre: 'Mouse Inalámbrico', precio: 24990, precioOferta: null, categoria: 'Periféricos', marca: 'Logitech', stock: 125, destacado: true, imagen: null },
  { id: 3, nombre: 'Monitor 27"', precio: 349990, precioOferta: 299990, categoria: 'Monitores', marca: 'Samsung', stock: 5, destacado: true, imagen: null },
  { id: 4, nombre: 'Teclado Mecánico', precio: 79990, precioOferta: null, categoria: 'Periféricos', marca: 'Redragon', stock: 15, destacado: false, imagen: null },
  { id: 5, nombre: 'SSD 1TB', precio: 89990, precioOferta: null, categoria: 'Almacenamiento', marca: 'Kingston', stock: 30, destacado: false, imagen: null },
  { id: 6, nombre: 'Audífonos Gamer', precio: 59990, precioOferta: 49990, categoria: 'Audio', marca: 'HyperX', stock: 20, destacado: true, imagen: null },
  { id: 7, nombre: 'Webcam HD', precio: 44990, precioOferta: null, categoria: 'Periféricos', marca: 'Logitech', stock: 8, destacado: true, imagen: null },
    ])
  }),

  http.get('/api/productos/:id', ({ params }) => {
    return HttpResponse.json({
      id: params.id,
      nombre: 'Laptop Gamer',
      precio: 899990,
      precioOferta: 809990,
      categoria: 'Computación',
      marca: 'ASUS',
      stock: 10,
      descripcion: 'Laptop de alto rendimiento para gaming profesional.',
      descuento: 10,
      garantia: '12 meses',
      especificaciones: 'Intel Core i7, 16GB RAM, RTX 4060, SSD 512GB',
      sobre: 'Diseñada para gamers que exigen el máximo rendimiento.',
      imagen: null,
      })
  }),

  // HU-04, 05 — Auth

  

  http.post('/api/auth/register', async ({ request }) => {
    const body = await request.json()
    const usuarios = getUsuariosRegistrados()
    if (body.correo === ADMIN.correo) {
      return new HttpResponse(JSON.stringify({ message: 'El correo ya está registrado' }), { status: 409 })
    }
    const yaExiste = usuarios.find(u => u.correo === body.correo)
    if (yaExiste) {
      return new HttpResponse(JSON.stringify({ message: 'El correo ya está registrado' }), { status: 409 })
    }
    usuarios.push(body)
    guardarUsuarios(usuarios)
    return HttpResponse.json({
      token: 'fake-token',
      usuario: { nombre: body.nombre, apellido: body.apellido, correo: body.correo, telefono: body.telefono, rol: 'usuario' },
    })
  }),

  http.post('/api/auth/login', async ({ request }) => {
    const body = await request.json()
    // Verificar si es admin
    if (body.correo === ADMIN.correo && body.password === ADMIN.password) {
      return HttpResponse.json({
        token: 'fake-admin-token',
        usuario: { nombre: ADMIN.nombre, apellido: ADMIN.apellido, correo: ADMIN.correo, rol: 'admin' },
      })
    }
    // Verificar usuarios registrados
    const usuarios = getUsuariosRegistrados()
    const usuario = usuarios.find(u => u.correo === body.correo && u.password === body.password)
    if (!usuario) {
      return new HttpResponse(null, { status: 401 })
    }
    return HttpResponse.json({
      token: 'fake-token',
      usuario: { nombre: usuario.nombre, apellido: usuario.apellido, correo: usuario.correo, telefono: usuario.telefono, rol: 'usuario' },
    })
  }),

  // HU-06 — Perfil
  http.get('/api/perfil', () => {
    return HttpResponse.json({ nombre: 'Diego', apellido: 'Tatin', correo: 'diego@test.cl', telefono: '+56912345678' })
  }),

  http.put('/api/perfil', () => {
    return HttpResponse.json({ success: true })
  }),
  
  http.post('/api/checkout', () => {
  return HttpResponse.json({ numeroPedido: '12345' })
  }),
  
  http.post('/api/productos', async ({ request }) => {
    const body = await request.json()
    return HttpResponse.json({
      id: Date.now(),
      nombre: body.nombre,
      precio: Number(body.precio),
      categoria: body.categoria,
      marca: body.marca,
      stock: Number(body.stock) || 0,
      descripcion: body.descripcion || '',
      imagen: body.imagen || null,
      garantia: body.garantia || '',
      especificaciones: body.especificaciones || '',
      sobre: body.sobre || '',
      precioOferta: body.precioOferta ? Number(body.precioOferta) : null,
      destacado: false,
      enOferta: false,
    })
  }),

  http.put('/api/productos/:id', async ({ request, params }) => {
    const body = await request.json()
    return HttpResponse.json({
      id: Number(params.id),
      nombre: body.nombre,
      precio: Number(body.precio),
      categoria: body.categoria,
      marca: body.marca,
      stock: Number(body.stock) || 0,
      descripcion: body.descripcion || '',
      imagen: body.imagen || null,
      garantia: body.garantia || '',
      especificaciones: body.especificaciones || '',
      sobre: body.sobre || '',
      precioOferta: body.precioOferta ? Number(body.precioOferta) : null,
      destacado: body.destacado || false,
      enOferta: body.enOferta || false,
    })
  }),

  http.delete('/api/productos/:id', () => {
    return HttpResponse.json({ success: true })
  }),

  http.get('/api/pedidos', () => {
  return HttpResponse.json([
    { id: 1, cliente: 'Diego Tatin', total: 924980, estado: 'Pendiente' },
    { id: 2, cliente: 'María López', total: 349990, estado: 'En Preparación' },
    { id: 3, cliente: 'Carlos Pérez', total: 89990, estado: 'Entregado' },
  ])
  }),

  http.put('/api/pedidos/:id', () => {
    return HttpResponse.json({ success: true })
  }),
  
  http.get('/api/admin/dashboard', () => {
  return HttpResponse.json({
    ventasTotales: 15234990,
    totalPedidos: 142,
    usuariosNuevos: 38,
    ticketPromedio: 107288,
    productosMasVendidos: [
      { id: 1, nombre: 'Laptop Gamer', ventas: 45 },
      { id: 2, nombre: 'Mouse Inalámbrico', ventas: 38 },
      { id: 3, nombre: 'Monitor 27"', ventas: 27 },
    ],
    pedidosRecientes: [
      { id: 101, cliente: 'Diego Tatin', estado: 'Pendiente' },
      { id: 102, cliente: 'María López', estado: 'En Preparación' },
      { id: 103, cliente: 'Carlos Pérez', estado: 'Entregado' },
    ],
    })
   }),

  http.put('/api/productos/:id/stock', () => {
  return HttpResponse.json({ success: true })
   }),
   
   http.post('/api/auth/recuperar-password', () => {
  return HttpResponse.json({ success: true })
  }),

   http.put('/api/productos/:id/destacado', async ({ request, params }) => {
    const body = await request.json()
    return HttpResponse.json({ id: Number(params.id), destacado: body.destacado })
  }),

http.put('/api/productos/:id/oferta', () => {
  return HttpResponse.json({ success: true })
  }),

  http.post('/api/auth/admin/login', async ({ request }) => {
    const body = await request.json()
    if (body.correo === ADMIN.correo && body.password === ADMIN.password) {
      return HttpResponse.json({ token: 'fake-admin-token', rol: 'admin' })
    }
    return new HttpResponse(null, { status: 401 })
  }),
]