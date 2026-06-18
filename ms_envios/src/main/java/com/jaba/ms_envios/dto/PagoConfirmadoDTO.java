package com.jaba.ms_envios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoConfirmadoDTO {
    private String ordenCompra;
    private Long pedidoId;
    private String direccionDestino; 
    private boolean requiereExpress; 
}

