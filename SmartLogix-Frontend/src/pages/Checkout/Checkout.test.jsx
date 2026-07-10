import { describe, it, expect } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/mocks/server'
import { renderWithProviders } from '../../test/utils/renderWithProviders'
import Checkout from './index'

const llenarPaso1 = () => {
  fireEvent.change(screen.getByTestId('input-direccion'), { target: { value: 'Calle 123' } })
  fireEvent.change(screen.getByTestId('input-numero'), { target: { value: '456' } })
  fireEvent.change(screen.getByTestId('input-region'), { target: { value: 'Biobío' } })
  fireEvent.change(screen.getByTestId('input-ciudad'), { target: { value: 'Concepción' } })
}

const llenarPaso2DatosPersonales = () => {
  fireEvent.change(screen.getByTestId('input-nombre-pago'), { target: { value: 'Diego' } })
  fireEvent.change(screen.getByTestId('input-apellido-pago'), { target: { value: 'Tatin' } })
  fireEvent.change(screen.getByTestId('input-email-pago'), { target: { value: 'diego@test.cl' } })
  fireEvent.change(screen.getByTestId('input-confirmar-email-pago'), { target: { value: 'diego@test.cl' } })
}

describe('HU-08 · Proceso de checkout', () => {

  it('CA1: muestra el indicador de pasos', () => {
    renderWithProviders(<Checkout />)
    expect(screen.getByTestId('indicador-pasos')).toBeInTheDocument()
    expect(screen.getByTestId('paso-1-activo')).toBeInTheDocument()
  })

  it('CA2: muestra formulario de envío en el paso 1', () => {
    renderWithProviders(<Checkout />)
    expect(screen.getByTestId('paso-envio')).toBeInTheDocument()
    expect(screen.getByTestId('input-direccion')).toBeInTheDocument()
  })

  it('CA2b: muestra errores si campos de envío están vacíos', () => {
    renderWithProviders(<Checkout />)
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    expect(screen.getByTestId('error-direccion')).toBeInTheDocument()
    expect(screen.getByTestId('error-numero')).toBeInTheDocument()
  })

  it('CA2c: avanza al paso 2 con datos de envío válidos', () => {
    renderWithProviders(<Checkout />)
    llenarPaso1()
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    expect(screen.getByTestId('paso-pago')).toBeInTheDocument()
    expect(screen.getByTestId('paso-2-activo')).toBeInTheDocument()
  })

  it('CA3: muestra los métodos de pago en el paso 2', () => {
    renderWithProviders(<Checkout />)
    llenarPaso1()
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    expect(screen.getByTestId('metodos-pago')).toBeInTheDocument()
    expect(screen.getByTestId('metodo-webpay')).toBeInTheDocument()
  })

  it('CA3b: muestra error si no se selecciona método de pago', () => {
    renderWithProviders(<Checkout />)
    llenarPaso1()
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    llenarPaso2DatosPersonales()
    fireEvent.click(screen.getByTestId('btn-confirmar'))
    expect(screen.getByTestId('error-metodo-pago')).toBeInTheDocument()
  })

  it('CA4: muestra resumen del pedido', () => {
    renderWithProviders(<Checkout />)
    expect(screen.getByTestId('resumen-pedido')).toBeInTheDocument()
  })

  it('CA5: el botón volver regresa al paso 1', () => {
    renderWithProviders(<Checkout />)
    llenarPaso1()
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    fireEvent.click(screen.getByTestId('btn-volver-envio'))
    expect(screen.getByTestId('paso-envio')).toBeInTheDocument()
  })

  it('CA6: muestra pedido exitoso al confirmar correctamente', async () => {
    server.use(
      http.post('/api/checkout', () => HttpResponse.json({ numeroPedido: '12345' }))
    )
    renderWithProviders(<Checkout />)
    llenarPaso1()
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    llenarPaso2DatosPersonales()
    fireEvent.click(screen.getByTestId('metodo-webpay'))
    fireEvent.click(screen.getByTestId('btn-confirmar'))
    await waitFor(() => {
      expect(screen.getByTestId('pedido-exitoso')).toBeInTheDocument()
    })
  })

  it('CA7: muestra error si el pago falla', async () => {
    server.use(
      http.post('/api/checkout', () => new HttpResponse(null, { status: 500 }))
    )
    renderWithProviders(<Checkout />)
    llenarPaso1()
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    llenarPaso2DatosPersonales()
    fireEvent.click(screen.getByTestId('metodo-webpay'))
    fireEvent.click(screen.getByTestId('btn-confirmar'))
    await waitFor(() => {
      expect(screen.getByTestId('error-pago')).toBeInTheDocument()
    })
  })

  it('CA8: el botón confirmar se deshabilita mientras procesa', async () => {
    server.use(
      http.post('/api/checkout', () => HttpResponse.json({ numeroPedido: '12345' }))
    )
    renderWithProviders(<Checkout />)
    llenarPaso1()
    fireEvent.click(screen.getByTestId('btn-siguiente'))
    llenarPaso2DatosPersonales()
    fireEvent.click(screen.getByTestId('metodo-webpay'))
    fireEvent.click(screen.getByTestId('btn-confirmar'))
    expect(screen.getByTestId('btn-confirmar')).toBeDisabled()
  })

})