import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../../test/mocks/server'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import Login from './index'
import { cleanup } from '@testing-library/react'
import { afterEach } from 'vitest'

afterEach(() => cleanup())

const llenarFormulario = (overrides = {}) => {
  const datos = {
    correo: 'diego@test.cl',
    password: 'password123',
    ...overrides,
  }
  fireEvent.change(screen.getByTestId('input-correo'), { target: { value: datos.correo } })
  fireEvent.change(screen.getByTestId('input-password'), { target: { value: datos.password } })
}

describe('HU-05 · Inicio de sesión', () => {

  beforeEach(() => {
  server.use(
    http.post('/api/auth/login', () => {
      return HttpResponse.json({
        token: 'fake-token',
        usuario: { nombre: 'Diego', correo: 'diego@test.cl' }
      })
    })
  )
})

  it('CA1: muestra los campos de correo y contraseña', () => {
    renderWithProviders(<Login />)
    expect(screen.getByTestId('input-correo')).toBeInTheDocument()
    expect(screen.getByTestId('input-password')).toBeInTheDocument()
  })

  it('CA2: muestra el botón de iniciar sesión', () => {
    renderWithProviders(<Login />)
    expect(screen.getByTestId('btn-login')).toBeInTheDocument()
  })

  it('CA3: muestra error si el correo es inválido', () => {
    renderWithProviders(<Login />)
    llenarFormulario({ correo: 'correo-invalido' })
    fireEvent.click(screen.getByTestId('btn-login'))
    expect(screen.getByTestId('error-correo')).toBeInTheDocument()
  })

  it('CA3b: muestra error si la contraseña tiene menos de 8 caracteres', () => {
    renderWithProviders(<Login />)
    llenarFormulario({ password: '123' })
    fireEvent.click(screen.getByTestId('btn-login'))
    expect(screen.getByTestId('error-password')).toBeInTheDocument()
  })

  it('CA4: muestra link para ir al registro', () => {
    renderWithProviders(<Login />)
    expect(screen.getByTestId('link-registro')).toBeInTheDocument()
  })

  it('CA5: muestra link para recuperar contraseña', () => {
    renderWithProviders(<Login />)
    expect(screen.getByTestId('link-recuperar')).toBeInTheDocument()
  })

  it('CA6: muestra error si las credenciales son incorrectas', async () => {
    server.use(
      http.post('/api/auth/login', () => {
        return new HttpResponse(null, { status: 401 })
      })
    )
    renderWithProviders(<Login />)
    llenarFormulario()
    fireEvent.click(screen.getByTestId('btn-login'))
    await waitFor(() => {
      expect(screen.getByTestId('error-credenciales')).toBeInTheDocument()
    })
  })

  it('CA7: muestra mensaje de éxito al iniciar sesión correctamente', async () => {
    renderWithProviders(<Login />)
    llenarFormulario()
    fireEvent.click(screen.getByTestId('btn-login'))
    await waitFor(() => {
      expect(screen.getByTestId('login-exitoso')).toBeInTheDocument()
    })
  })

  it('CA8: el botón se deshabilita mientras se está autenticando', async () => {
  const { getByTestId } = renderWithProviders(<Login />)
  fireEvent.change(getByTestId('input-correo'), { target: { value: 'diego@test.cl' } })
  fireEvent.change(getByTestId('input-password'), { target: { value: 'password123' } })
  fireEvent.click(getByTestId('btn-login'))
  expect(getByTestId('btn-login')).toBeDisabled()
  })

})