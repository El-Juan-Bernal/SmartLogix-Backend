import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { CarritoProvider } from './context/CarritoContext'
import Layout from './components/layout/Layout'
import AdminLayout from './components/layout/AdminLayout'
import ProtectedAdminRoute from './components/auth/ProtectedAdminRoute'
import Home from './pages/Home'
import Catalogo from './pages/Catalogo'
import Producto from './pages/Producto'
import Login from './pages/Auth/Login'
import Register from './pages/Auth/Register'
import RecuperarPassword from './pages/Auth/RecuperarPassword'
import Perfil from './pages/Perfil'
import Carrito from './pages/Carrito'
import Checkout from './pages/Checkout'
import AdminLogin from './pages/Admin/Login'
import AdminDashboard from './pages/Admin/Dashboard'
import AdminProductos from './pages/Admin/Productos'
import AdminPedidos from './pages/Admin/Pedidos'
import AdminInventario from './pages/Admin/Inventario'
import AdminPromociones from './pages/Admin/Promociones'
import ProtectedRoute from './components/auth/ProtectedRoute'
import { DireccionesProvider } from './context/DireccionesContext'
import { HistorialProvider } from './context/HistorialContext'
import { ProductosProvider } from './context/ProductosContext'
import { PedidosProvider } from './context/PedidosContext'

function App() {
  return (
    <AuthProvider>
      <CarritoProvider>
        <ProductosProvider>
          <PedidosProvider>
            <DireccionesProvider>
              <HistorialProvider>
                <BrowserRouter>
                  <Routes>
                    <Route path="/" element={<Layout />}>
                      <Route index element={<Home />} />
                      <Route path="catalogo" element={<Catalogo />} />
                      <Route path="producto/:id" element={<Producto />} />
                      <Route path="login" element={<Login />} />
                      <Route path="registro" element={<Register />} />
                      <Route path="recuperar-password" element={<RecuperarPassword />} />
                      <Route element={<ProtectedRoute />}>
                        <Route path="perfil" element={<Perfil />} />
                      </Route>
                      <Route path="carrito" element={<Carrito />} />
                      <Route element={<ProtectedRoute />}>
                        <Route path="checkout" element={<Checkout />} />
                      </Route>
                    </Route>

                    <Route path="/admin/login" element={<AdminLogin />} />

                    <Route element={<ProtectedAdminRoute />}>
                      <Route path="/admin" element={<AdminLayout />}>
                        <Route path="dashboard" element={<AdminDashboard />} />
                        <Route path="productos" element={<AdminProductos />} />
                        <Route path="pedidos" element={<AdminPedidos />} />
                        <Route path="inventario" element={<AdminInventario />} />
                        <Route path="promociones" element={<AdminPromociones />} />
                      </Route>
                    </Route>
                  </Routes>
                </BrowserRouter>
              </HistorialProvider>
            </DireccionesProvider>
          </PedidosProvider>
        </ProductosProvider>
      </CarritoProvider>
    </AuthProvider>
  )
}

export default App

