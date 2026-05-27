package com.jaba.ms_mensajeria.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_correos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RegistroCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ordenCompra;

    @Column(nullable = false)
    private String clienteEmail;

    @Column(nullable = false)
    private String tipoDocumento; // "BOLETA" o "FACTURA"

    @Column(nullable = false)
    private LocalDateTime fechaEnvio;

    // Usamos LONGTEXT porque el Base64 de un PDF puede ser muy extenso
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String documentoBase64;
}

