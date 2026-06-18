package com.jaba.ms_envios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al pedido que se está enviando (Desacoplamiento)
    private Long pedidoId;

    private String direccionDestino;
    
    // Aquí más adelante actuará tu Factory Method (Ej: "LOCAL", "INTERREGIONAL")
    private String tipoDespacho; 
    
    private String estado; // Ej: "PREPARACION", "EN_RUTA", "ENTREGADO"

    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "PREPARACION";
        }
    }
}

