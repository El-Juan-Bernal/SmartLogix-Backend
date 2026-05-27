package com.jaba.ms_mensajeria.factory;

import com.jaba.ms_mensajeria.event.VentaAprobadaEvent;
import com.jaba.ms_mensajeria.model.RegistroCorreo;
import com.jaba.ms_mensajeria.repository.RegistroCorreoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Base64;

@Component("BOLETA")
public class BoletaNotificacion implements NotificacionEmail {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final RegistroCorreoRepository registroRepository;
    
    @Value("${spring.mail.username}")
    private String remitente;

    public BoletaNotificacion(JavaMailSender mailSender, TemplateEngine templateEngine, RegistroCorreoRepository registroRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.registroRepository = registroRepository;
    }

    @Override
    public void enviar(VentaAprobadaEvent evento) throws MessagingException {
        // 1. Preparar las variables para la plantilla Thymeleaf
        Context context = new Context();
        context.setVariable("clienteNombre", evento.clienteNombre());
        context.setVariable("ordenCompra", evento.ordenCompra());
        context.setVariable("montoTotal", evento.montoTotal());
        
        boolean tienePdf = evento.pdfBoletaBase64() != null && !evento.pdfBoletaBase64().isBlank();
        context.setVariable("mensajeSII", tienePdf ? "Adjuntamos tu boleta electrónica en este correo." : "Tu boleta está siendo procesada por el SII y te la enviaremos a la brevedad.");

        // 2. Procesar la plantilla HTML (correo-boleta.html)
        String htmlBody = templateEngine.process("correo-boleta", context);

        // 3. Configurar el correo electrónico
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(remitente);
        helper.setTo(evento.clienteEmail());
        helper.setSubject("Tu Boleta de Compra #" + evento.ordenCompra() + " - Smart Logix");
        helper.setText(htmlBody, true);

        // 4. Adjuntar el PDF si viene en el evento
        if (tienePdf) {
            byte[] pdfBytes = Base64.getDecoder().decode(evento.pdfBoletaBase64());
            helper.addAttachment("Boleta_" + evento.ordenCompra() + ".pdf", new ByteArrayResource(pdfBytes));
        }

        // 5. Enviar el correo a través del servidor SMTP
        mailSender.send(message);
        System.out.println("✅ Correo de BOLETA enviado exitosamente a: " + evento.clienteEmail());

        // 6. Guardar la evidencia en la Base de Datos
        RegistroCorreo registro = RegistroCorreo.builder()
                .ordenCompra(evento.ordenCompra())
                .clienteEmail(evento.clienteEmail())
                .tipoDocumento("BOLETA")
                .fechaEnvio(LocalDateTime.now())
                .documentoBase64(evento.pdfBoletaBase64())
                .build();
                
        registroRepository.save(registro);
        System.out.println("💾 Registro de envío de BOLETA guardado en la base de datos.");
    }
}

