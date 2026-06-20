package com.jaba.bff_web.dto;

import lombok.Data;

@Data
public class ActualizarPerfilDTO {
    private String nombre;
    private String apellido;
    private String telefono;
    private String imagenPerfil;
}

