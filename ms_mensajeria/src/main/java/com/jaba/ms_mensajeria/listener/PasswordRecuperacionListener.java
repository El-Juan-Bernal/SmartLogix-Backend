package com.jaba.ms_mensajeria.listener;

import com.jaba.ms_mensajeria.config.RabbitMQConfig;
import com.jaba.ms_mensajeria.dto.PasswordRecuperadaEvent;
import com.jaba.ms_mensajeria.service.EmailService; // <-- ¡Este import es vital!
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordRecuperacionListener { 

    private static final Logger log = LoggerFactory.getLogger(PasswordRecuperacionListener.class);

    // Aquí inyectamos nuestro nuevo servicio para que Java sepa qué es "emailService"
    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PASSWORD)
    public void procesarRecuperacionPassword(PasswordRecuperadaEvent event) {
        log.info("🔔 [x] Evento de recuperación recibido en ms_mensajeria!");
        log.info("Procesando recuperación de contraseña para el usuario: {}", event.getUsername());
        log.info("Email destino: {}", event.getEmail());
        
        emailService.enviarCorreoRecuperacion(event.getEmail(), event.getUsername(), event.getClaveTemporal());
    }
}

