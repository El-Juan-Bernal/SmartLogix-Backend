package com.jaba.ms_usuario.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El alias es obligatorio (ej: Casa, Trabajo, Vacaciones)")
    private String alias;

    @NotBlank(message = "La región es obligatoria")
    private String region;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    private String numero;

    // OPCIONAL
    private String departamento; 

    // Indica si es la dirección por defecto del usuario
    private boolean esPrincipal; 

    // Relación Muchos a Uno (Muchas direcciones pertenecen a un Usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference // Evita un bucle infinito al generar el JSON
    private Usuario usuario;
}

