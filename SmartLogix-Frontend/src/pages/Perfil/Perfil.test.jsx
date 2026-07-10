import { describe, it, expect } from 'vitest'
import { screen, fireEvent } from '@testing-library/react'
import { renderWithProviders } from '../../test/utils/renderWithProviders'
import { useAuth } from '../../context/AuthContext'
import { useEffect } from 'react'
import Perfil from './index'

const usuarioMock = {
  nombre: 'Diego',
  apellido: 'Tatin',
  correo: 'diego@test.cl',
  telefono: '+56912345678',
}

function PerfilConSesion() {
  const { login } = useAuth()
  useEffect(() => { login(usuarioMock) }, [])
  return <Perfil />
}

describe('HU-06 · Gestión de perfil', () => {

  it('CA1: muestra los datos del perfil del usuario logueado', async () => {
    renderWithProviders(<PerfilConSesion />)
    expect(await screen.findByTestId('perfil-nombre')).toHaveTextContent('Diego')
    expect(screen.getByTestId('perfil-correo')).toHaveTextContent('diego@test.cl')
  })

  it('CA2: muestra botón editar perfil', async () => {
    renderWithProviders(<PerfilConSesion />)
    expect(await screen.findByTestId('btn-editar')).toBeInTheDocument()
  })

  it('CA3: al hacer clic en editar muestra el formulario de edición', async () => {
    renderWithProviders(<PerfilConSesion />)
    fireEvent.click(await screen.findByTestId('btn-editar'))
    expect(screen.getByTestId('form-edicion')).toBeInTheDocument()
    expect(screen.getByTestId('input-nombre')).toBeInTheDocument()
  })

  it('CA4: muestra botones guardar y cancelar en modo edición', async () => {
    renderWithProviders(<PerfilConSesion />)
    fireEvent.click(await screen.findByTestId('btn-editar'))
    expect(screen.getByTestId('btn-guardar')).toBeInTheDocument()
    expect(screen.getByTestId('btn-cancelar')).toBeInTheDocument()
  })

  it('CA5: cancelar edición vuelve a la vista de perfil', async () => {
    renderWithProviders(<PerfilConSesion />)
    fireEvent.click(await screen.findByTestId('btn-editar'))
    fireEvent.click(screen.getByTestId('btn-cancelar'))
    expect(screen.getByTestId('vista-perfil')).toBeInTheDocument()
  })

  it('CA5b: muestra error si nombre está vacío al guardar', async () => {
    renderWithProviders(<PerfilConSesion />)
    fireEvent.click(await screen.findByTestId('btn-editar'))
    fireEvent.change(screen.getByTestId('input-nombre'), { target: { value: '' } })
    fireEvent.click(screen.getByTestId('btn-guardar'))
    expect(screen.getByTestId('error-nombre')).toBeInTheDocument()
  })

  it('CA6: guarda los cambios correctamente y muestra confirmación', async () => {
    renderWithProviders(<PerfilConSesion />)
    fireEvent.click(await screen.findByTestId('btn-editar'))
    fireEvent.change(screen.getByTestId('input-nombre'), { target: { value: 'Diego Editado' } })
    fireEvent.click(screen.getByTestId('btn-guardar'))
    expect(await screen.findByTestId('guardado-exitoso')).toBeInTheDocument()
  })

})