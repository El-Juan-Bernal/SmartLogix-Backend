package com.jaba.ms_pagos.dto;

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
    private Boolean requiereExpress; 
}

