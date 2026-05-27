package com.jaba.ms_catalogo.controller;

import com.jaba.ms_catalogo.model.Producto;
import com.jaba.ms_catalogo.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // --- RUTAS PARA EL CLIENTE (Frontend) ---

    // Obtener catálogo (Soporta búsqueda y filtro por categoría)
    @GetMapping
    public List<Producto> obtenerCatalogo(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busqueda) {
        
        // Si el frontend envía "?categoria=poleras", filtramos por categoría
        if (categoria != null && !categoria.trim().isEmpty()) {
            return productoRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria);
        } 
        // Si el frontend envía "?busqueda=gamer", buscamos coincidencias en el nombre
        else if (busqueda != null && !busqueda.trim().isEmpty()) {
            return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(busqueda);
        }
        
        // Si no envía nada, devolvemos toda la vitrina activa
        return productoRepository.findByActivoTrue();
    }

    // Ver detalle de un producto específico
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- RUTAS PARA EL ADMINISTRADOR ---

    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        Producto nuevoProducto = productoRepository.save(producto);
        return ResponseEntity.ok(nuevoProducto);
    }

    // Carga masiva de productos (Crear varios a la vez)
    @PostMapping("/bulk")
    public ResponseEntity<List<Producto>> crearProductosMasivamente(@Valid @RequestBody List<Producto> productos) {
        // Usamos saveAll para persistir toda la lista de una sola vez
        List<Producto> nuevosProductos = productoRepository.saveAll(productos);
        return ResponseEntity.ok(nuevosProductos);
    }

    // Para actualizar un producto completo (Editar)
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto detallesProducto) {
        return productoRepository.findById(id).map(producto -> {
            // Reemplazamos los datos antiguos con los nuevos que vienen en el JSON
            producto.setNombre(detallesProducto.getNombre());
            producto.setMarca(detallesProducto.getMarca());
            producto.setDescripcion(detallesProducto.getDescripcion());
            producto.setPrecio(detallesProducto.getPrecio());
            producto.setCategoria(detallesProducto.getCategoria());
            producto.setImagenPrincipal(detallesProducto.getImagenPrincipal());
            producto.setStock(detallesProducto.getStock());
            
            // Mantenemos el estado 'activo' tal como estaba
            // Guardamos y devolvemos el producto actualizado
            Producto productoActualizado = productoRepository.save(producto);
            return ResponseEntity.ok(productoActualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Ocultar o mostrar un producto (Activar/Desactivar)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoProducto(@PathVariable Long id, @RequestParam boolean activo) {
        return productoRepository.findById(id).map(producto -> {
            producto.setActivo(activo);
            productoRepository.save(producto);
            return ResponseEntity.ok("Estado actualizado a: " + (activo ? "Activo" : "Oculto"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar un producto físicamente de la base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProductoFisicamente(@PathVariable Long id) {
        return productoRepository.findById(id).map(producto -> {
            productoRepository.delete(producto);
            return ResponseEntity.ok("Producto con ID " + id + " eliminado de la base de datos.");
        }).orElse(ResponseEntity.notFound().build());
    }
}

