package com.jaba.ms_pagos.messaging;

import com.jaba.ms_pagos.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VentaPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publicarVentaExitosa(VentaAprobadaEvent evento) {
        
        System.out.println("📢 Publicando evento de venta aprobada para la orden: " + evento.ordenCompra());

        // 1. Avisamos a Mensajería para que envíe el correo con el PDF adjunto
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_PRINCIPAL,
                RabbitMQConfig.KEY_NOTIFICAR,
                evento
        );
        System.out.println("✅ Mensaje enviado a la cola de Mensajería.");

        // 2. Avisamos a Bodega para que empiecen a preparar los artículos electrónicos
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_PRINCIPAL,
                RabbitMQConfig.KEY_DESPACHAR,
                evento
        );
        System.out.println("✅ Mensaje enviado a la cola de Bodega.");
    }

    public void publicarReintentoSii(VentaAprobadaEvent evento) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_PRINCIPAL, 
            RabbitMQConfig.KEY_SII_RETRY, 
            evento
        );
        System.out.println("⏳ Contingencia: Venta " + evento.ordenCompra() + " enviada a la sala de espera del SII.");
    }
}

