package com.jaba.ms_pagos.service;

import com.jaba.ms_pagos.model.Pago;
import com.jaba.ms_pagos.repository.PagoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jaba.ms_pagos.service.pdf.DocumentoFactory;
import com.jaba.ms_pagos.messaging.VentaPublisher;
import com.jaba.ms_pagos.messaging.VentaAprobadaEvent;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DocumentoFactory documentoFactory;

    @Autowired
    private VentaPublisher ventaPublisher;

    @Autowired
    private SiiService siiService;

    // --- 1. WEBPAY ---
    @CircuitBreaker(name = "webpayCB", fallbackMethod = "fallbackWebpay")
    public Map<String, Object> procesarPagoWebpay(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial, Long pedidoId, String direccionDestino, boolean requiereExpress) {
        
        if (pagoRepository.existsByOrdenCompraAndEstado(ordenCompra, "APROBADO")) {
            return Map.of("estado", "RECHAZADO_POR_SISTEMA", "mensaje", "La orden " + ordenCompra + " ya fue pagada anteriormente.");
        }

        System.out.println("Conectando con Transbank Webpay Sandbox...");

        Double montoNeto = Math.round((monto / 1.19) * 100.0) / 100.0;
        Double iva = Math.round((monto - montoNeto) * 100.0) / 100.0;
        String codAutorizacion = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Pago registro = Pago.builder()
                .usuarioId(usuarioId)
                .ordenCompra(ordenCompra)
                .montoTotal(monto)
                .montoNeto(montoNeto)
                .iva(iva)
                .tipoDocumento(tipoDocumento)
                .pasarela("Webpay")
                .estado("PENDIENTE")
                .codigoAutorizacion(codAutorizacion)
                .clienteNombre(clienteNombre)
                .clienteEmail(clienteEmail)
                .rutEmpresa(rutEmpresa)
                .razonSocial(razonSocial)
                // NUEVOS CAMPOS GUARDADOS EN BD
                .pedidoId(pedidoId)
                .direccionDestino(direccionDestino)
                .requiereExpress(requiereExpress)
                .build();
        pagoRepository.save(registro);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "PENDIENTE");
        respuesta.put("pasarela", "Webpay");
        respuesta.put("codigoAutorizacion", codAutorizacion);
        return respuesta;
    }

    public Map<String, Object> fallbackWebpay(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial, Long pedidoId, String direccionDestino, boolean requiereExpress, Throwable t) {
        System.err.println("¡Alerta! Falló la conexión con Webpay. Guardando traza de error.");
        
        Double montoNeto = Math.round((monto / 1.19) * 100.0) / 100.0;
        Double iva = Math.round((monto - montoNeto) * 100.0) / 100.0;

        Pago registroFalla = Pago.builder()
                .usuarioId(usuarioId)
                .ordenCompra(ordenCompra)
                .montoTotal(monto)
                .montoNeto(montoNeto)
                .iva(iva)
                .tipoDocumento("BOLETA")
                .pasarela("Webpay")
                .estado("RECHAZADO_POR_SISTEMA")
                .mensajeError(t.getMessage())
                .pedidoId(pedidoId)
                .direccionDestino(direccionDestino)
                .requiereExpress(requiereExpress)
                .build();
        pagoRepository.save(registroFalla);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "RECHAZADO_POR_SISTEMA");
        respuesta.put("mensaje", "Webpay no responde. Intente con otro medio de pago.");
        return respuesta;
    }

    // --- 2. MERCADOPAGO ---
    @CircuitBreaker(name = "mercadopagoCB", fallbackMethod = "fallbackMercadoPago")
    public Map<String, Object> procesarPagoMercadoPago(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial, Long pedidoId, String direccionDestino, boolean requiereExpress) {
        
        if (pagoRepository.existsByOrdenCompraAndEstado(ordenCompra, "APROBADO")) {
            return Map.of("estado", "RECHAZADO_POR_SISTEMA", "mensaje", "La orden " + ordenCompra + " ya fue pagada anteriormente.");
        }

        System.out.println("Conectando con MercadoPago Sandbox...");

        Double montoNeto = Math.round((monto / 1.19) * 100.0) / 100.0;
        Double iva = Math.round((monto - montoNeto) * 100.0) / 100.0;
        String txId = "MP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Pago registro = Pago.builder()
                .usuarioId(usuarioId)
                .ordenCompra(ordenCompra)
                .montoTotal(monto)
                .montoNeto(montoNeto)
                .iva(iva)
                .tipoDocumento(tipoDocumento)
                .pasarela("MercadoPago")
                .estado("PENDIENTE")
                .codigoAutorizacion(txId)
                .clienteNombre(clienteNombre)
                .clienteEmail(clienteEmail)
                .rutEmpresa(rutEmpresa)
                .razonSocial(razonSocial)
                .pedidoId(pedidoId)
                .direccionDestino(direccionDestino)
                .requiereExpress(requiereExpress)
                .build();
        pagoRepository.save(registro);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "PENDIENTE");
        respuesta.put("pasarela", "MercadoPago");
        respuesta.put("idTransaccion", txId);
        return respuesta;
    }

    public Map<String, Object> fallbackMercadoPago(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial, Long pedidoId, String direccionDestino, boolean requiereExpress, Throwable t) {
        
        Double montoNeto = Math.round((monto / 1.19) * 100.0) / 100.0;
        Double iva = Math.round((monto - montoNeto) * 100.0) / 100.0;

        Pago registroFalla = Pago.builder()
                .usuarioId(usuarioId)
                .ordenCompra(ordenCompra)
                .montoTotal(monto)
                .montoNeto(montoNeto)
                .iva(iva)
                .tipoDocumento("BOLETA")
                .pasarela("MercadoPago")
                .estado("RECHAZADO_POR_SISTEMA")
                .mensajeError(t.getMessage())
                .pedidoId(pedidoId)
                .direccionDestino(direccionDestino)
                .requiereExpress(requiereExpress)
                .build();
        pagoRepository.save(registroFalla);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "RECHAZADO_POR_SISTEMA");
        respuesta.put("mensaje", "MercadoPago no responde.");
        return respuesta;
    }

    // --- 3. KHIPU ---
    @CircuitBreaker(name = "khipuCB", fallbackMethod = "fallbackKhipu")
    public Map<String, Object> procesarPagoKhipu(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial, Long pedidoId, String direccionDestino, boolean requiereExpress) {
        
        if (pagoRepository.existsByOrdenCompraAndEstado(ordenCompra, "APROBADO")) {
            return Map.of("estado", "RECHAZADO_POR_SISTEMA", "mensaje", "La orden " + ordenCompra + " ya fue pagada anteriormente.");
        }

        System.out.println("Conectando con Khipu Sandbox...");

        Double montoNeto = Math.round((monto / 1.19) * 100.0) / 100.0;
        Double iva = Math.round((monto - montoNeto) * 100.0) / 100.0;
        String urlSimulada = "https://sandbox.khipu.com/pago/" + ordenCompra;
        String khipuId = "KP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Pago registro = Pago.builder()
                .usuarioId(usuarioId)
                .ordenCompra(ordenCompra)
                .montoTotal(monto)
                .montoNeto(montoNeto)
                .iva(iva)
                .tipoDocumento(tipoDocumento)
                .pasarela("Khipu")
                .estado("PENDIENTE")
                .codigoAutorizacion(khipuId)
                .clienteNombre(clienteNombre)
                .clienteEmail(clienteEmail)
                .rutEmpresa(rutEmpresa)
                .razonSocial(razonSocial)
                .pedidoId(pedidoId)
                .direccionDestino(direccionDestino)
                .requiereExpress(requiereExpress)
                .build();
        pagoRepository.save(registro);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "PENDIENTE");
        respuesta.put("pasarela", "Khipu");
        respuesta.put("urlPago", urlSimulada);
        return respuesta;
    }

    public Map<String, Object> fallbackKhipu(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial, Long pedidoId, String direccionDestino, boolean requiereExpress, Throwable t) {
        
        Double montoNeto = Math.round((monto / 1.19) * 100.0) / 100.0;
        Double iva = Math.round((monto - montoNeto) * 100.0) / 100.0;

        Pago registroFalla = Pago.builder()
                .usuarioId(usuarioId)
                .ordenCompra(ordenCompra)
                .montoTotal(monto)
                .montoNeto(montoNeto)
                .iva(iva)
                .tipoDocumento("BOLETA")
                .pasarela("Khipu")
                .estado("RECHAZADO_POR_SISTEMA")
                .mensajeError(t.getMessage())
                .pedidoId(pedidoId)
                .direccionDestino(direccionDestino)
                .requiereExpress(requiereExpress)
                .build();
        pagoRepository.save(registroFalla);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "RECHAZADO_POR_SISTEMA");
        respuesta.put("mensaje", "Khipu fuera de servicio.");
        return respuesta;
    }

    // =================================================================
    // MÉTODO WEBHOOK
    // =================================================================
    public Map<String, Object> confirmarPagoYEmitirDocumento(String ordenCompra) {
        
        Pago pago = pagoRepository.findByOrdenCompra(ordenCompra)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada en la base de datos"));

        if (!"PENDIENTE".equals(pago.getEstado())) {
            return Map.of("mensaje", "Esta orden ya fue procesada anteriormente. Estado actual: " + pago.getEstado(), "exito", false);
        }

        String folioRespuesta = siiService.solicitarFolioSii(pago.getTipoDocumento(), pago.getMontoTotal());
        
        byte[] pdfBytes = null;
        String mensajeRespuesta;

        if ("PENDIENTE_SII".equals(folioRespuesta)) {
            pago.setEstado("APROBADO_BOLETA_PENDIENTE");
            pago.setFolioSii("PENDIENTE_EMISION");
            mensajeRespuesta = "Pago aprobado. La emisión de tu boleta está en cola por intermitencias del SII.";
        } else {
            pago.setEstado("APROBADO");
            pago.setFolioSii(folioRespuesta);
            pdfBytes = documentoFactory.obtenerGenerador(pago.getTipoDocumento()).generarDocumento(pago);
            mensajeRespuesta = "Pago aprobado y boleta emitida exitosamente.";
        }
        
        pagoRepository.save(pago);

        String pdfBase64 = (pdfBytes != null && pdfBytes.length > 0) 
        ? java.util.Base64.getEncoder().encodeToString(pdfBytes) 
        : null;

        VentaAprobadaEvent evento = new VentaAprobadaEvent(
                pago.getId(),
                pago.getOrdenCompra(),
                pago.getUsuarioId(),
                pago.getClienteNombre(), 
                pago.getClienteEmail(), 
                pago.getMontoTotal().intValue(), 
                pago.getFolioSii(),
                pdfBase64, 
                pago.getTipoDocumento(),
                pago.getRutEmpresa(),
                pago.getRazonSocial(),
                pago.getFechaHora()
        );

        ventaPublisher.publicarVentaExitosa(evento);

        if ("PENDIENTE_SII".equals(folioRespuesta)) {
            ventaPublisher.publicarReintentoSii(evento);
        }

        // Retornamos también los datos logísticos para que el Controlador los envíe a ms_envios
        return Map.of(
            "estado", pago.getEstado(), 
            "folioSii", pago.getFolioSii(),
            "mensaje", mensajeRespuesta,
            "exito", true,
            "pedidoId", pago.getPedidoId() != null ? pago.getPedidoId() : 0L,
            "direccionDestino", pago.getDireccionDestino() != null ? pago.getDireccionDestino() : "Desconocida",
            "requiereExpress", pago.isRequiereExpress()
        );
    }
}

