import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../../test/mocks/server'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import Register from './index'

const llenarFormulario = (overrides = {}) => {
  const datos = {
    nombre: 'Diego',
    apellido: 'Tatin',
    correo: 'diego@test.cl',
    password: 'password123',
    confirmPassword: 'password123',
    ...overrides,
  }
  fireEvent.change(screen.getByTestId('input-nombre'), { target: { value: datos.nombre } })
  fireEvent.change(screen.getByTestId('input-apellido'), { target: { value: datos.apellido } })
  fireEvent.change(screen.getByTestId('input-correo'), { target: { value: datos.correo } })
  fireEvent.change(screen.getByTestId('input-password'), { target: { value: datos.password } })
  fireEvent.change(screen.getByTestId('input-confirm-password'), { target: { value: datos.confirmPassword } })
}

describe('HU-04 · Registro de usuario', () => {

  it('CA1: muestra todos los campos del formulario', () => {
    renderWithProviders(<Register />)
    expect(screen.getByTestId('input-nombre')).toBeInTheDocument()
    expect(screen.getByTestId('input-apellido')).toBeInTheDocument()
    expect(screen.getByTestId('input-correo')).toBeInTheDocument()
    expect(screen.getByTestId('input-password')).toBeInTheDocument()
    expect(screen.getByTestId('input-confirm-password')).toBeInTheDocument()
    expect(screen.getByTestId('input-telefono')).toBeInTheDocument()
  })

  it('CA2: muestra error si nombre está vacío al enviar', () => {
    renderWithProviders(<Register />)
    fireEvent.click(screen.getByTestId('btn-registrar'))
    expect(screen.getByTestId('error-nombre')).toBeInTheDocument()
  })

  it('CA3: muestra error si el correo es inválido', () => {
    renderWithProviders(<Register />)
    fireEvent.change(screen.getByTestId('input-nombre'), { target: { value: 'Diego' } })
    fireEvent.change(screen.getByTestId('input-apellido'), { target: { value: 'Tatin' } })
    fireEvent.change(screen.getByTestId('input-correo'), { target: { value: 'correo-invalido' } })
    fireEvent.change(screen.getByTestId('input-password'), { target: { value: 'password123' } })
    fireEvent.change(screen.getByTestId('input-confirm-password'), { target: { value: 'password123' } })
    fireEvent.click(screen.getByTestId('btn-registrar'))
    expect(screen.getByTestId('error-correo')).toBeInTheDocument()
  })

  it('CA4: muestra error si la contraseña tiene menos de 8 caracteres', () => {
    renderWithProviders(<Register />)
    llenarFormulario({ password: '123', confirmPassword: '123' })
    fireEvent.click(screen.getByTestId('btn-registrar'))
    expect(screen.getByTestId('error-password')).toBeInTheDocument()
  })

  it('CA5: muestra error si las contraseñas no coinciden', () => {
    renderWithProviders(<Register />)
    llenarFormulario({ confirmPassword: 'diferente123' })
    fireEvent.click(screen.getByTestId('btn-registrar'))
    expect(screen.getByTestId('error-confirm-password')).toBeInTheDocument()
  })

  it('CA6: el botón registrar está presente', () => {
    renderWithProviders(<Register />)
    expect(screen.getByTestId('btn-registrar')).toBeInTheDocument()
  })

  it('CA7: muestra link para ir al login', () => {
    renderWithProviders(<Register />)
    expect(screen.getByTestId('link-login')).toBeInTheDocument()
  })

  it('CA8: muestra mensaje de éxito al registrarse correctamente', async () => {
    renderWithProviders(<Register />)
    llenarFormulario({ correo: `nuevo${Date.now()}@test.cl` })
    fireEvent.click(screen.getByTestId('btn-registrar'))
    await waitFor(() => {
      expect(screen.getByTestId('registro-exitoso')).toBeInTheDocument()
    })
  })

  it('CA9: muestra error si el servidor falla al registrar', async () => {
    server.use(
      http.post('/api/auth/register', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )
    renderWithProviders(<Register />)
    llenarFormulario()
    fireEvent.click(screen.getByTestId('btn-registrar'))
    await waitFor(() => {
      expect(screen.getByTestId('error-servidor')).toBeInTheDocument()
    })
  })

  it('CA10: el botón se deshabilita mientras se está registrando', async () => {
    renderWithProviders(<Register />)
    llenarFormulario()
    fireEvent.click(screen.getByTestId('btn-registrar'))
    expect(screen.getByTestId('btn-registrar')).toBeDisabled()
  })

})