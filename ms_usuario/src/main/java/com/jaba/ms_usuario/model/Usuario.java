package com.jaba.ms_usuario.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios") 
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ESTE ES EL ESLABÓN que lo une con ms_idp
    @Column(unique = true, nullable = false)
    private Long authId; 

    @Column(unique = true, nullable = false)
    private String emailUsuario; 

    private String username;

    // Estos nacen nulos y se llenan después
    private String nombre;
    private String apellido;
    private String telefono;
    private String imagenPerfil; 

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Direccion> direcciones = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void formatearDatos() {
        if (this.telefono != null && this.telefono.length() == 9 && this.telefono.startsWith("9")) {
            this.telefono = "+56" + this.telefono;
        }
    }

    public void agregarDireccion(Direccion direccion) {
        direcciones.add(direccion);
        direccion.setUsuario(this);
    }
}

