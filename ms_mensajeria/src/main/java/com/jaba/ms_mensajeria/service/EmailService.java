package com.jaba.ms_mensajeria.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String remitente;

    public void enviarCorreoRecuperacion(String destino, String username, String claveTemporal) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destino);
            helper.setSubject("🔑 Recuperación de tu contraseña - SmartLogix");

            // Pasamos las variables a la plantilla de Thymeleaf
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("claveTemporal", claveTemporal);

            // Procesamos el archivo HTML que acabamos de crear
            String contenidoHtml = templateEngine.process("email-recuperacion", context);

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);
            
            log.info("📧 ¡Correo de recuperación enviado con éxito a: {}!", destino);

        } catch (MessagingException e) {
            log.error("❌ Error al enviar el correo a {}: {}", destino, e.getMessage());
        }
    }
}

