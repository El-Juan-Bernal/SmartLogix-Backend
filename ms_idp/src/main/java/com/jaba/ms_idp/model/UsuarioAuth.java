package com.jaba.ms_idp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios_auth")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UsuarioAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password; // Esta se guardará encriptada con Bcrypt (nunca debe salir en el JSON)

    @Column(unique = true, nullable = false)
    private String email;

    // 'user' por defecto para cualquier registro normal. Solo se cambia
    // a 'admin' manualmente en la base de datos (no hay endpoint público
    // que permita auto-asignarse el rol admin, por seguridad).
    @Column(nullable = false)
    private String rol = "user";
}

