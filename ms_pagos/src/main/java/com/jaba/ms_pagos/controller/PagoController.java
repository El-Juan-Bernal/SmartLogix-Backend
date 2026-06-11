package com.jaba.ms_pagos.controller;

import com.jaba.ms_pagos.dto.PagoRequestDTO;
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
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody PagoRequestDTO request) {
        
        Map<String, Object> resultado;

        // Validamos que la pasarela no sea nula antes de convertir a minúsculas
        if (request.getPasarela() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "La pasarela es obligatoria."));
        }

        // Pasamos el objeto DTO completo al servicio
        switch (request.getPasarela().toLowerCase()) {
            case "webpay":
                resultado = pagoService.procesarPagoWebpay(
                    request.getMonto(), 
                    request.getOrdenCompra(), 
                    Long.valueOf(request.getUsuarioId()), 
                    request.getClienteNombre(), 
                    request.getClienteEmail(), 
                    request.getTipoDocumento(), 
                    request.getRutEmpresa(), 
                    request.getRazonSocial()
                );
                break;
            case "mercadopago":
                resultado = pagoService.procesarPagoMercadoPago(
                    request.getMonto(), 
                    request.getOrdenCompra(), 
                    Long.valueOf(request.getUsuarioId()), 
                    request.getClienteNombre(), 
                    request.getClienteEmail(), 
                    request.getTipoDocumento(), 
                    request.getRutEmpresa(), 
                    request.getRazonSocial()
                );
                break;
            case "khipu":
                resultado = pagoService.procesarPagoKhipu(
                    request.getMonto(), 
                    request.getOrdenCompra(), 
                    Long.valueOf(request.getUsuarioId()), 
                    request.getClienteNombre(), 
                    request.getClienteEmail(), 
                    request.getTipoDocumento(), 
                    request.getRutEmpresa(), 
                    request.getRazonSocial()
                );
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
        
        // El webhook recibe solo la ordenCompra para buscar el registro y proceder con la emisión
        Map<String, Object> resultado = pagoService.confirmarPagoYEmitirDocumento(ordenCompra);
        
        return ResponseEntity.ok(resultado);
    }
}

