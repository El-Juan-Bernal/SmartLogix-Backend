package com.jaba.ms_catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, message = "El nombre debe ser descriptivo")
    private String nombre;

    @NotBlank (message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(columnDefinition = "TEXT") // TEXT para descripciones largas
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Integer precio;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "La URL de la imagen principal es obligatoria")
    private String imagenPrincipal;

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser un número positivo")
    private Integer stock;

    // Control para el administrador: Si es false, no se muestra en la tienda pública
    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true; 

    @PrePersist
    public void asignarEstadoPorDefecto() {
        if (this.activo == null) {
            this.activo = true; // Si el Frontend no lo envía, lo hacemos true por defecto
        }
    }
}

