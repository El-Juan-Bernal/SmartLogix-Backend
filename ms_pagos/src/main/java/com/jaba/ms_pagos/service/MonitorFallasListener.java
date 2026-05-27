package com.jaba.ms_pagos.service;

import com.jaba.ms_pagos.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@Component
public class MonitorFallasListener {

    @Value("${slack.webhook.url}")
    private String SLACK_WEBHOOK_URL;

    @RabbitListener(queues = {RabbitMQConfig.DLQ_BODEGA, RabbitMQConfig.DLQ_MENSAJERIA, RabbitMQConfig.DLQ_SII})
    public void procesarMensajeMuerto(Message mensaje) {
        
        String contenido = new String(mensaje.getBody(), StandardCharsets.UTF_8);
        String routingKeyFalla = mensaje.getMessageProperties().getReceivedRoutingKey();

        // 1. Texto de alerta para usar con Slack
        String textoAlerta = "<!channel> 🚨 *¡ALERTA EN SMART LOGIX!*\n" +
                             "Un mensaje fue enviado a la DLQ\n" +
                             "*Motivo/Ruta:* `" + routingKeyFalla + "`\n" +
                             "*Datos:* `" + contenido + "`";

        // 2. Usamos un Map en lugar de armar el String JSON a mano
        Map<String, String> payload = new HashMap<>();
        payload.put("text", textoAlerta);

        // 3. Enviamos el mensaje. RestTemplate se encarga de empaquetar el Map como JSON perfecto.
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            restTemplate.postForEntity(SLACK_WEBHOOK_URL, payload, String.class);
            System.out.println("✅ Notificación de fallo enviada exitosamente a Slack.");
        } catch (Exception e) {
            System.err.println("❌ Error al intentar notificar a Slack: " + e.getMessage());
        }
    }
}

