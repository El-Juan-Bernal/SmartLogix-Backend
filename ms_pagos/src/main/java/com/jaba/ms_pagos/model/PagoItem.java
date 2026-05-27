package com.jaba.ms_pagos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pago_items")
public class PagoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productoId;       // ID del artículo (teclado, mouse, pendrive, etc.)
    private Integer cantidad;      // Unidades compradas
    private Double precioUnitario; // Precio histórico al momento exacto de la compra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id")
    @JsonIgnore                    // Evita bucles infinitos al serializar el objeto a JSON
    private Pago pago;
}

