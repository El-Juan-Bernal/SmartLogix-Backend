package com.jaba.bff_web.controller;

import com.jaba.bff_web.client.MensajeriaClient;
import com.jaba.bff_web.dto.MensajeriaRequestDTO; // <-- Importación del DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/mensajeria")
public class BffMensajeriaController {

    @Autowired
    private MensajeriaClient mensajeriaClient;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarNotificacionCliente(@RequestBody MensajeriaRequestDTO payloadMensaje) { // <-- Reemplazo aquí
        try {
            Map<String, Object> respuestaMensajeria = mensajeriaClient.enviarNotificacion(payloadMensaje);
            return ResponseEntity.ok(respuestaMensajeria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el envío del mensaje en el backend: " + e.getMessage());
        }
    }
}