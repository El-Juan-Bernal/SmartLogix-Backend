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
    private String usuarioId;
    private String clienteNombre;
    private String clienteEmail;
    private String tipoDocumento;
    private String rutEmpresa;
    private String razonSocial;
    
    // --- NUEVOS CAMPOS PARA EL ENVÍO ---
    private Long pedidoId;           // Para saber qué pedido es en la base de datos
    private String direccionDestino; // Hacia dónde va el multiconector o producto
    private boolean requiereExpress; // Para que el Factory sepa si es urgente
}

