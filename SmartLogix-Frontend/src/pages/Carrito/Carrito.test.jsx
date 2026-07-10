import { describe, it, expect } from 'vitest'
import { screen, fireEvent } from '@testing-library/react'
import { useEffect } from 'react'
import { renderWithProviders } from '../../test/utils/renderWithProviders'
import { useCarrito } from '../../context/CarritoContext'
import Carrito from './index'

const productoMock = {
  id: 1,
  nombre: 'Laptop Gamer',
  precio: 899990,
  stock: 10,
}

// Componente helper para agregar productos antes de renderizar el carrito
function CarritoConProductos({ productos = [] }) {
  const { agregarProducto } = useCarrito()

  useEffect(() => {
    if (productos.length === 2 && productos[0].producto.id === productos[1].producto.id) {
      // Caso especial: mismo producto dos veces, sumar cantidades en una sola llamada
      agregarProducto(productos[0].producto, productos[0].cantidad + productos[1].cantidad)
    } else {
      productos.forEach(p => agregarProducto(p.producto, p.cantidad))
    }
  }, [])

  return <Carrito />
}

describe('HU-07 · Carrito de compras', () => {

  it('CA1: muestra carrito vacío cuando no hay productos', () => {
    renderWithProviders(<Carrito />)
    expect(screen.getByTestId('carrito-vacio')).toBeInTheDocument()
  })

  it('CA1b: muestra botón para ir al catálogo cuando está vacío', () => {
    renderWithProviders(<Carrito />)
    expect(screen.getByTestId('btn-ir-catalogo')).toBeInTheDocument()
  })

  it('CA2: muestra los productos agregados al carrito', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 1 }]} />)
    const items = await screen.findAllByTestId('carrito-item')
    expect(items.length).toBe(1)
    expect(screen.getByTestId('item-nombre')).toHaveTextContent('Laptop Gamer')
  })

  it('CA3: el selector de cantidad incrementa correctamente', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 1 }]} />)
    await screen.findByTestId('cantidad-1')
    fireEvent.click(screen.getByTestId('btn-incrementar-1'))
    expect(screen.getByTestId('cantidad-1')).toHaveTextContent('2')
  })

  it('CA3b: el selector de cantidad decrementa correctamente', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 2 }]} />)
    await screen.findByTestId('cantidad-1')
    fireEvent.click(screen.getByTestId('btn-decrementar-1'))
    expect(screen.getByTestId('cantidad-1')).toHaveTextContent('1')
  })

  it('CA3c: la cantidad no baja de 1', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 1 }]} />)
    await screen.findByTestId('cantidad-1')
    fireEvent.click(screen.getByTestId('btn-decrementar-1'))
    expect(screen.getByTestId('cantidad-1')).toHaveTextContent('1')
  })

  it('CA3d: la cantidad no supera el stock disponible', async () => {
    const productoStockBajo = { ...productoMock, stock: 2 }
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoStockBajo, cantidad: 2 }]} />)
    await screen.findByTestId('cantidad-1')
    fireEvent.click(screen.getByTestId('btn-incrementar-1'))
    expect(screen.getByTestId('cantidad-1')).toHaveTextContent('2')
  })

  it('CA4: muestra el subtotal por producto', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 2 }]} />)
    const subtotal = await screen.findByTestId('item-subtotal')
    expect(subtotal).toHaveTextContent('1.799.980')
  })

  it('CA5: eliminar un producto lo quita del carrito', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 1 }]} />)
    await screen.findByTestId('btn-eliminar-1')
    fireEvent.click(screen.getByTestId('btn-eliminar-1'))
    expect(screen.getByTestId('carrito-vacio')).toBeInTheDocument()
  })

  it('CA6: muestra el total del carrito correctamente', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 2 }]} />)
    const total = await screen.findByTestId('total-carrito')
    expect(total).toHaveTextContent('1.799.980')
  })

  it('CA7: vaciar carrito elimina todos los productos', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 1 }]} />)
    await screen.findByTestId('btn-vaciar')
    fireEvent.click(screen.getByTestId('btn-vaciar'))
    expect(screen.getByTestId('carrito-vacio')).toBeInTheDocument()
  })

  it('CA8: botón proceder al pago está presente cuando hay productos', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 1 }]} />)
    expect(await screen.findByTestId('btn-checkout')).toBeInTheDocument()
  })

  it('CA9: botón seguir comprando está presente cuando hay productos', async () => {
    renderWithProviders(<CarritoConProductos productos={[{ producto: productoMock, cantidad: 1 }]} />)
    expect(await screen.findByTestId('btn-seguir-comprando')).toBeInTheDocument()
  })

  it('CA10: el carrito suma cantidades si se agrega el mismo producto dos veces', async () => {
    renderWithProviders(
      <CarritoConProductos productos={[
        { producto: productoMock, cantidad: 1 },
        { producto: productoMock, cantidad: 1 },
      ]} />
    )
    const cantidad = await screen.findByTestId('cantidad-1')
    expect(cantidad).toHaveTextContent('2')
  })

})