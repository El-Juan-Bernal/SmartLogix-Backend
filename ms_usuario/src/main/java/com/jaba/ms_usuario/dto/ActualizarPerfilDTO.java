package com.jaba.ms_usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarPerfilDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 letras")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 3, message = "El apellido debe tener al menos 3 letras")
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^(\\+569\\d{8}|9\\d{8})$", message = "El teléfono debe ser 9XXXXXXXX o +569XXXXXXXX")
    private String telefono;

    private String imagenPerfil; // Opcional
}

