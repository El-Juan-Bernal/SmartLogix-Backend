package com.jaba.bff_web.controller;

import com.jaba.bff_web.client.CatalogoClient;
import com.jaba.bff_web.dto.ProductoDetalleDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
public class BffCatalogoController {

    @Autowired
    private CatalogoClient catalogoClient;

    // ==========================================
    // RUTAS PÚBLICAS (CLIENTE FRONTEND)
    // ==========================================

    @GetMapping
    public ResponseEntity<?> obtenerCatalogo(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busqueda) {
        try {
            List<Map<String, Object>> catalogo = catalogoClient.obtenerCatalogo(categoria, busqueda);
            return ResponseEntity.ok(catalogo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el catálogo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetalleCompleto(@PathVariable Long id) {
        try {
            Map<String, Object> producto = catalogoClient.obtenerProductoPorId(id);
            
            ProductoDetalleDTO detalle = new ProductoDetalleDTO();
            detalle.setId(id);
            detalle.setNombre((String) producto.get("nombre"));
            detalle.setMarca((String) producto.get("marca"));
            detalle.setDescripcion((String) producto.get("descripcion"));
            detalle.setPrecio(Integer.valueOf(producto.get("precio").toString()));
            detalle.setImagenPrincipal((String) producto.get("imagenPrincipal"));
            detalle.setCategoria((String) producto.get("categoria"));
            detalle.setStockDisponible(Integer.valueOf(producto.get("stock").toString()));
            
            return ResponseEntity.ok(detalle);

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error 404: El producto con ID " + id + " no existe o no tiene stock registrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error de comunicación en los microservicios: " + e.getMessage());
        }
    }

    // ==========================================
    // RUTAS PRIVADAS (ADMINISTRADOR)
    // ==========================================

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Map<String, Object> producto) {
        try {
            Map<String, Object> respuesta = catalogoClient.crearProductoUnico(producto);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el producto: " + e.getMessage());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> crearProductosMasivamente(@RequestBody List<Map<String, Object>> productos) {
        try {
            List<Map<String, Object>> respuesta = catalogoClient.crearProductosMasivamente(productos);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar productos masivamente: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, Object> detallesProducto) {
        try {
            Map<String, Object> respuesta = catalogoClient.actualizarProducto(id, detallesProducto);
            return ResponseEntity.ok(respuesta);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado para actualizar.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoProducto(@PathVariable Long id, @RequestParam boolean activo) {
        try {
            String respuesta = catalogoClient.cambiarEstadoProducto(id, activo);
            return ResponseEntity.ok(respuesta);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cambiar estado: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/destacado")
    public ResponseEntity<?> actualizarDestacado(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(catalogoClient.actualizarDestacado(id, body));
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar destacado: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/oferta")
    public ResponseEntity<?> actualizarOferta(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(catalogoClient.actualizarOferta(id, body));
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar oferta: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(catalogoClient.actualizarStock(id, body));
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar stock: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProductoFisicamente(@PathVariable Long id) {
        try {
            String respuesta = catalogoClient.eliminarProductoFisicamente(id);
            return ResponseEntity.ok(respuesta);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado para eliminar.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar: " + e.getMessage());
        }
    }
}

