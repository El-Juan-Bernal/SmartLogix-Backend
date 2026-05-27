package com.jaba.ms_mensajeria.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record VentaAprobadaEvent(
        Long pagoId,
        String ordenCompra,      
        Long usuarioId,
        String clienteNombre,
        String clienteEmail,
        Integer montoTotal,
        String folioBoleta,      
        String pdfBoletaBase64,  
        String tipoDocumento,    
        String rutEmpresa,       
        String razonSocial,      
        LocalDateTime fechaHora
) implements Serializable {
    private static final long serialVersionUID = 1L;
}

