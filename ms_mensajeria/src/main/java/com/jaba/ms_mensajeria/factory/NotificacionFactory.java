package com.jaba.ms_mensajeria.factory;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NotificacionFactory {

    private final Map<String, NotificacionEmail> estrategias;

    // Spring Boot inyecta automáticamente los @Component ("BOLETA" y "FACTURA") en este mapa
    public NotificacionFactory(Map<String, NotificacionEmail> estrategias) {
        this.estrategias = estrategias;
    }

    public NotificacionEmail obtenerEstrategia(String tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.isBlank()) {
            throw new IllegalArgumentException("El tipo de documento no puede ser nulo o vacío");
        }
        
        NotificacionEmail estrategia = estrategias.get(tipoDocumento.toUpperCase());
        
        if (estrategia == null) {
            throw new IllegalArgumentException("Tipo de documento no soportado en mensajería: " + tipoDocumento);
        }
        
        return estrategia;
    }
}

