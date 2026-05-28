package com.jaba.bff_web.dto;

import lombok.Data;

@Data
public class MensajeriaRequestDTO {
    private String destinatario;
    private String asunto;
    private String cuerpoMensaje;
}

