package com.jaba.ms_mensajeria.messaging;

import com.jaba.ms_mensajeria.event.VentaAprobadaEvent;
import com.jaba.ms_mensajeria.factory.NotificacionEmail;
import com.jaba.ms_mensajeria.factory.NotificacionFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MensajeriaListener {

    private final NotificacionFactory factory;

    // Inyectamos nuestro "Jefe de distribución"
    public MensajeriaListener(NotificacionFactory factory) {
        this.factory = factory;
    }

    @RabbitListener(queues = "smartlogix.mensajeria.queue")
    public void recibirMensajeVenta(VentaAprobadaEvent evento) {
        System.out.println("📥 ¡Mensaje recibido desde RabbitMQ para la orden: #" + evento.ordenCompra() + "!");
        
        try {
            // 1. Le preguntamos a la fábrica qué estrategia usar basándonos en el tipo de documento
            NotificacionEmail estrategia = factory.obtenerEstrategia(evento.tipoDocumento());
            
            // 2. Ejecutamos el envío (la interfaz obliga a que exista este método)
            estrategia.enviar(evento);
            
            System.out.println("✅ Proceso de mensajería completado con éxito para: " + evento.clienteEmail());
            System.out.println("--------------------------------------------------");
            
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Error de formato: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error crítico al enviar el correo a " + evento.clienteEmail() + ": " + e.getMessage());
            // En el futuro, si el correo falla (ej. Gmail se cae), podríamos mandar este mensaje a una DLQ (Dead Letter Queue)
        }
    }
}

