package com.jaba.ms_pagos.controller;

import com.jaba.ms_pagos.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(
            @RequestParam String pasarela,
            @RequestParam Double monto,
            @RequestParam String ordenCompra,
            @RequestParam Long usuarioId,
            @RequestParam String clienteNombre,   // <-- Nuevo: Nombre del cliente
            @RequestParam String clienteEmail,    // <-- Nuevo: Correo para el ms_mensajeria
            @RequestParam String tipoDocumento,   // <-- Nuevo: "BOLETA" o "FACTURA"
            @RequestParam(required = false) String rutEmpresa,   // <-- Nuevo: Opcional si es factura
            @RequestParam(required = false) String razonSocial  // <-- Nuevo: Opcional si es factura
    ) { 

        Map<String, Object> resultado;

        // Pasamos los nuevos datos dinámicos al servicio para que se guarden en la base de datos
        switch (pasarela.toLowerCase()) {
            case "webpay":
                resultado = pagoService.procesarPagoWebpay(monto, ordenCompra, usuarioId, clienteNombre, clienteEmail, tipoDocumento, rutEmpresa, razonSocial);
                break;
            case "mercadopago":
                resultado = pagoService.procesarPagoMercadoPago(monto, ordenCompra, usuarioId, clienteNombre, clienteEmail, tipoDocumento, rutEmpresa, razonSocial);
                break;
            case "khipu":
                resultado = pagoService.procesarPagoKhipu(monto, ordenCompra, usuarioId, clienteNombre, clienteEmail, tipoDocumento, rutEmpresa, razonSocial);
                break;
            default:
                return ResponseEntity.badRequest().body(Map.of("error", "Pasarela no soportada."));
        }

        return ResponseEntity.ok(resultado);
    }

    // =================================================================
    // NUEVO ENDPOINT: Webhook para confirmar el pago y generar el PDF
    // =================================================================
    @PostMapping("/webhook/confirmar/{ordenCompra}")
    public ResponseEntity<Map<String, Object>> simularConfirmacionBanco(@PathVariable String ordenCompra) {
        
        // El webhook solo recibe la ordenCompra porque irá a buscar el registro a la base de datos,
        // el cual ya tendrá el nombre, email y tipo de documento guardados gracias al paso anterior.
        Map<String, Object> resultado = pagoService.confirmarPagoYEmitirDocumento(ordenCompra);
        
        return ResponseEntity.ok(resultado);
    }
}

