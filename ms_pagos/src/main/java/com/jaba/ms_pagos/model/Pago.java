package com.jaba.ms_pagos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private String ordenCompra;
    
    // --- Campos Tributarios y Financieros ---
    private Double montoNeto;
    private Double iva;
    private Double montoTotal;     // Reemplaza al campo 'monto' único
    
    private String tipoDocumento;  // "BOLETA" o "FACTURA"
    private String folioSii;       // Guardará el número asignado por el SII
    private String rutaPdf;        // Ubicación física del archivo para descargas futuras
    
    private String pasarela;       // "Webpay", "MercadoPago", "Khipu"
    private String estado;         // "PENDIENTE", "RECHAZADO_POR_SISTEMA", etc.
    private String codigoAutorizacion;
    private String mensajeError;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_email")
    private String clienteEmail;

    // Estos pueden ser nulos porque si es BOLETA, no vienen datos de empresa
    @Column(name = "rut_empresa", nullable = true)
    private String rutEmpresa;

    @Column(name = "razon_social", nullable = true)
    private String razonSocial;

    // --- NUEVOS CAMPOS LOGÍSTICOS ---
    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "direccion_destino")
    private String direccionDestino;

    @Column(name = "requiere_express")
    private boolean requiereExpress;

    // Relación Uno a Muchos: cascade = CascadeType.ALL asegura que al guardar el Pago, 
    // automáticamente se guarden todos sus ítems desglosados en la base de datos.
    @Builder.Default
    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PagoItem> items = new ArrayList<>();

    @PrePersist
    public void registrarFechaHora() {
        this.fechaHora = LocalDateTime.now(ZoneId.of("America/Santiago"));
    }
}

