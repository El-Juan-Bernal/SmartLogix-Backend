package com.jaba.ms_mensajeria.factory;

import com.jaba.ms_mensajeria.event.VentaAprobadaEvent;
import jakarta.mail.MessagingException;

public interface NotificacionEmail {
    
    // Este método será el encargado de armar y enviar el correo
    void enviar(VentaAprobadaEvent evento) throws MessagingException;
    
}

