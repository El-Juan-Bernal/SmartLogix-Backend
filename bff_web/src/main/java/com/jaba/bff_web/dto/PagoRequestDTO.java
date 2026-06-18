package com.jaba.bff_web.dto;

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
    private Long pedidoId;
    private String direccionDestino;
    private Boolean requiereExpress;
}

