package com.jaba.bff_web.dto;

import lombok.Data;

@Data
public class ProductoDetalleDTO {
    // Datos que vienen del ms_catalogo
    private Long id;
    private String nombre;
    private String marca;
    private String descripcion;
    private Integer precio;
    private String imagenPrincipal;
    private String categoria;
    private Integer stockDisponible;
}

