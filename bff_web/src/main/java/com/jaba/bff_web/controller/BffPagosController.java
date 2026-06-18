package com.jaba.bff_web.controller;

import com.jaba.bff_web.client.PagosClient;
import com.jaba.bff_web.dto.PagoRequestDTO; // <-- Importación del DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pagos")
public class BffPagosController {

    @Autowired
    private PagosClient pagosClient;

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPagoCarrito(@RequestBody PagoRequestDTO payloadCarrito) { // <-- Reemplazo aquí
        try {
            Map<String, Object> respuestaPago = pagosClient.procesarPago(payloadCarrito);
            return ResponseEntity.ok(respuestaPago);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el pago en el backend: " + e.getMessage());
        }
    }
}

