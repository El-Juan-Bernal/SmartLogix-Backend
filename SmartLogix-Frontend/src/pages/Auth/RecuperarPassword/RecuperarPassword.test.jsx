import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../../test/mocks/server'
import { renderWithProviders } from '../../../test/utils/renderWithProviders'
import RecuperarPassword from './index'

describe('HU · Recuperar contraseña', () => {

  it('CA1: muestra únicamente el campo de correo electrónico', () => {
    renderWithProviders(<RecuperarPassword />)
    expect(screen.getByTestId('input-correo')).toBeInTheDocument()
    expect(screen.getByTestId('btn-enviar')).toBeInTheDocument()
  })

  it('CA2: muestra error si el correo tiene formato inválido', () => {
    renderWithProviders(<RecuperarPassword />)
    fireEvent.change(screen.getByTestId('input-correo'), {
      target: { value: 'correo-invalido' }
    })
    fireEvent.click(screen.getByTestId('btn-enviar'))
    expect(screen.getByTestId('error-correo')).toBeInTheDocument()
  })

  it('CA2b: no muestra error si el correo es válido', () => {
    renderWithProviders(<RecuperarPassword />)
    fireEvent.change(screen.getByTestId('input-correo'), {
      target: { value: 'diego@test.cl' }
    })
    fireEvent.click(screen.getByTestId('btn-enviar'))
    expect(screen.queryByTestId('error-correo')).not.toBeInTheDocument()
  })

  it('CA3: el botón se deshabilita mientras se envía la petición', async () => {
    renderWithProviders(<RecuperarPassword />)
    fireEvent.change(screen.getByTestId('input-correo'), {
      target: { value: 'diego@test.cl' }
    })
    fireEvent.click(screen.getByTestId('btn-enviar'))
    expect(screen.getByTestId('btn-enviar')).toBeDisabled()
  })

  it('CA4: muestra confirmación exitosa al responder el servidor con éxito', async () => {
    server.use(
      http.post('/api/auth/recuperar-password', () => {
        return HttpResponse.json({ success: true })
      })
    )
    renderWithProviders(<RecuperarPassword />)
    fireEvent.change(screen.getByTestId('input-correo'), {
      target: { value: 'diego@test.cl' }
    })
    fireEvent.click(screen.getByTestId('btn-enviar'))
    await waitFor(() => {
      expect(screen.getByTestId('confirmacion-exitosa')).toBeInTheDocument()
      expect(screen.getByText('Te hemos enviado las instrucciones a tu correo')).toBeInTheDocument()
    })
  })

  it('CA5: muestra toast de error si hay error de red', async () => {
    server.use(
      http.post('/api/auth/recuperar-password', () => {
        return new HttpResponse(null, { status: 500 })
      })
    )
    renderWithProviders(<RecuperarPassword />)
    fireEvent.change(screen.getByTestId('input-correo'), {
      target: { value: 'diego@test.cl' }
    })
    fireEvent.click(screen.getByTestId('btn-enviar'))
    await waitFor(() => {
      expect(screen.getByTestId('toast-error')).toBeInTheDocument()
    })
  })

})