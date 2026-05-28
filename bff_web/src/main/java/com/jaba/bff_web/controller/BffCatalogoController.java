package com.jaba.bff_web.controller;

import com.jaba.bff_web.client.CatalogoClient;
import com.jaba.bff_web.dto.ProductoDetalleDTO;
import feign.FeignException; // <-- Importante para atrapar errores de red
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // <-- Nos permite manejar los códigos HTTP
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/detalle-producto")
public class BffCatalogoController {

    @Autowired
    private CatalogoClient catalogoClient;


    // Cambiamos el retorno a ResponseEntity<?> para poder devolver un DTO o un String
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetalleCompleto(@PathVariable Long id) {
        
        try {
            // 1. Llamamos al Catálogo 
            Map<String, Object> producto = catalogoClient.obtenerProductoPorId(id);
            
            
            // 2. Construimos la "Bandeja" (DTO)
            ProductoDetalleDTO detalle = new ProductoDetalleDTO();
            detalle.setId(id);
            detalle.setNombre((String) producto.get("nombre"));
            detalle.setMarca((String) producto.get("marca"));
            detalle.setDescripcion((String) producto.get("descripcion"));
            detalle.setPrecio(Integer.valueOf(producto.get("precio").toString()));
            detalle.setImagenPrincipal((String) producto.get("imagenPrincipal"));
            detalle.setCategoria((String) producto.get("categoria"));
            detalle.setStockDisponible(Integer.valueOf(producto.get("stock").toString()));
            
            // Retornamos 200 OK con nuestra bandeja
            return ResponseEntity.ok(detalle);

        } catch (FeignException.NotFound e) {
            // EL ESCUDO: Si el ms_catalogo o el ms_inventario no encuentran el ID (Lanzan 404)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error 404: El producto con ID " + id + " no existe o no tiene stock registrado.");
                    
        } catch (Exception e) {
            // ESCUDO DE RESPALDO: Por si se apaga un microservicio o hay otro fallo grave
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error de comunicación en los microservicios: " + e.getMessage());
        }
    }
}


