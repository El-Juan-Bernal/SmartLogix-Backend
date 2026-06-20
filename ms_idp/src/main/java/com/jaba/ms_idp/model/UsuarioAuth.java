package com.jaba.ms_idp.model;

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
    private String password; // Esta se guardará encriptada con Bcrypt

    @Column(unique = true, nullable = false)
    private String email;
}

