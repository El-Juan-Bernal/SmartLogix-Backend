package com.jaba.bff_web.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-catalogo", url = "${url.ms.catalogo}")
public interface CatalogoClient {

    // --- RUTAS PARA EL CLIENTE ---

    // 1. Obtener catálogo completo con filtros opcionales
    @GetMapping
    List<Map<String, Object>> obtenerCatalogo(
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "busqueda", required = false) String busqueda);

    // 2. Buscar un solo producto (Para armar el detalle)
    @GetMapping("/{id}")
    Map<String, Object> obtenerProductoPorId(@PathVariable("id") Long id);

    // --- RUTAS PARA EL ADMINISTRADOR ---

    // 3. Crear UN solo producto
    @PostMapping
    Map<String, Object> crearProductoUnico(@RequestBody Map<String, Object> producto);

    // 4. Carga masiva
    @PostMapping("/bulk")
    List<Map<String, Object>> crearProductosMasivamente(@RequestBody List<Map<String, Object>> productos);

    // 5. Actualizar producto completo
    @PutMapping("/{id}")
    Map<String, Object> actualizarProducto(@PathVariable("id") Long id, @RequestBody Map<String, Object> detallesProducto);

    // 6. Ocultar o mostrar un producto
    @PatchMapping("/{id}/estado")
    String cambiarEstadoProducto(@PathVariable("id") Long id, @RequestParam("activo") boolean activo);

    // 7. Eliminar producto físicamente
    @DeleteMapping("/{id}")
    String eliminarProductoFisicamente(@PathVariable("id") Long id);
}

