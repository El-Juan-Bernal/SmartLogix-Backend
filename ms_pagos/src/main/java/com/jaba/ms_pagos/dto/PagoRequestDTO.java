package com.jaba.ms_pagos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {
    private String pasarela;
    private Double monto;
    private String ordenCompra;
    private String usuarioId; // Asegúrate de que el tipo sea String en AMBOS lados
    private String clienteNombre;
    private String clienteEmail;
    private String tipoDocumento;
    private String rutEmpresa;
    private String razonSocial;
}


