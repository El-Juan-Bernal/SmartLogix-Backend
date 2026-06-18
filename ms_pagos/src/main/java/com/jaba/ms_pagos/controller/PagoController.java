package com.jaba.ms_pagos.controller;

import com.jaba.ms_pagos.dto.PagoRequestDTO;
import com.jaba.ms_pagos.config.RabbitMQConfig;
import com.jaba.ms_pagos.dto.PagoConfirmadoDTO; // NUEVO: Importamos el mensajero
import com.jaba.ms_pagos.service.PagoService;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // NUEVO: Herramienta de RabbitMQ
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    // NUEVO: Inyectamos la plantilla para enviar mensajes a las colas
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody PagoRequestDTO request) {
        
        Map<String, Object> resultado;

        if (request.getPasarela() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "La pasarela es obligatoria."));
        }

        /* * IMPORTANTE A FUTURO: 
         * Recuerda que le agregamos 3 campos nuevos al DTO (pedidoId, direccionDestino, requiereExpress).
         * Deberás actualizar los métodos procesarPagoWebpay, procesarPagoMercadoPago, etc., 
         * en tu PagoService para que también reciban y guarden estos 3 datos en la base de datos de pagos.
         */
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
                    request.getRazonSocial(),
                    request.getPedidoId(),
                    request.getDireccionDestino(),
                    Boolean.TRUE.equals(request.getRequiereExpress())
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
                    request.getRazonSocial(),
                    request.getPedidoId(),
                    request.getDireccionDestino(),
                    Boolean.TRUE.equals(request.getRequiereExpress())
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
                    request.getRazonSocial(),
                    request.getPedidoId(),
                    request.getDireccionDestino(),
                    Boolean.TRUE.equals(request.getRequiereExpress())
                );
                break;
            default:
                return ResponseEntity.badRequest().body(Map.of("error", "Pasarela no soportada."));
        }

        return ResponseEntity.ok(resultado);
    }

    // =================================================================
    // Webhook para confirmar el pago, generar el PDF y AVISAR A ENVÍOS
    // =================================================================
    @PostMapping("/webhook/confirmar/{ordenCompra}")
    public ResponseEntity<Map<String, Object>> simularConfirmacionBanco(@PathVariable String ordenCompra) {
        
        // 1. Confirmamos el pago y emitimos el documento (tu flujo original)
        Map<String, Object> resultado = pagoService.confirmarPagoYEmitirDocumento(ordenCompra);
        
        // 2. Verificamos que el pago realmente fue exitoso antes de despachar
        // Asumiendo que tu servicio devuelve un "status" o algo similar en el map
        if (resultado.containsKey("exito") && (boolean) resultado.get("exito")) {
            
            // 3. Rescatamos los datos del envío que tu PagoService debería devolver en el Map
            // (Ya que el webhook solo recibe la ordenCompra, el servicio debe buscar los demás datos en la BD)
            Long pedidoId = Long.valueOf(resultado.getOrDefault("pedidoId", "0").toString());
            String direccion = resultado.getOrDefault("direccionDestino", "Desconocida").toString();
            boolean requiereExpress = Boolean.parseBoolean(resultado.getOrDefault("requiereExpress", "false").toString());

            // 4. Construimos el mensaje
            PagoConfirmadoDTO mensajeEnvio = new PagoConfirmadoDTO(
                ordenCompra,
                pedidoId,
                direccion,
                requiereExpress
            );

            // 5. ¡MAGIA ASÍNCRONA! Disparamos el mensaje a la cola de RabbitMQ
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PRINCIPAL, "venta.envios", mensajeEnvio);
            System.out.println("Mensaje enviado a RabbitMQ para la orden: " + ordenCompra);
        }
        
        return ResponseEntity.ok(resultado);
    }
}

