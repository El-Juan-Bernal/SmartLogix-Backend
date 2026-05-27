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

    // Inyección de la fábrica de PDFs y el publicador de RabbitMQ
    @Autowired
    private DocumentoFactory documentoFactory;

    @Autowired
    private VentaPublisher ventaPublisher;

    @Autowired
    private SiiService siiService;

    // --- 1. WEBPAY ---
    @CircuitBreaker(name = "webpayCB", fallbackMethod = "fallbackWebpay")
    public Map<String, Object> procesarPagoWebpay(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial) {
        
        if (pagoRepository.existsByOrdenCompraAndEstado(ordenCompra, "APROBADO")) {
            return Map.of("estado", "RECHAZADO_POR_SISTEMA", "mensaje", "La orden " + ordenCompra + " ya fue pagada anteriormente.");
        }

        System.out.println("Conectando con Transbank Webpay Sandbox...");

/*
        // 👇 TRUCO: Usamos una variable local para simular que se ha caído la pasarela de pago Webpay.
        boolean simularCaida = true;
        if (simularCaida) {
            throw new RuntimeException("Error de conexión 503: Servicio de Transbank no disponible.");
        }
        // Fin del truco
*/

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
                .build();
        pagoRepository.save(registro);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "PENDIENTE");
        respuesta.put("pasarela", "Webpay");
        respuesta.put("codigoAutorizacion", codAutorizacion);
        return respuesta;
    }

    public Map<String, Object> fallbackWebpay(Double monto, String ordenCompra, Long usuarioId, Throwable t) {
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
                .build();
        pagoRepository.save(registroFalla);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "RECHAZADO_POR_SISTEMA");
        respuesta.put("mensaje", "Webpay no responde. Intente con otro medio de pago.");
        return respuesta;
    }

    // --- 2. MERCADOPAGO ---
    @CircuitBreaker(name = "mercadopagoCB", fallbackMethod = "fallbackMercadoPago")
    public Map<String, Object> procesarPagoMercadoPago(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial) {
        
        if (pagoRepository.existsByOrdenCompraAndEstado(ordenCompra, "APROBADO")) {
            return Map.of("estado", "RECHAZADO_POR_SISTEMA", "mensaje", "La orden " + ordenCompra + " ya fue pagada anteriormente.");
        }

        System.out.println("Conectando con MercadoPago Sandbox...");

/*
        // 👇 TRUCO: Usamos una variable local para simular que se ha caído la pasarela de pago MercadoPago.
        boolean simularCaida = true;
        if (simularCaida) {
            throw new RuntimeException("Error de conexión 500: Internal Server Error en MercadoPago.");
        }
        // Fin del truco
*/

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
                .build();
        pagoRepository.save(registro);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "PENDIENTE");
        respuesta.put("pasarela", "MercadoPago");
        respuesta.put("idTransaccion", txId);
        return respuesta;
    }

    public Map<String, Object> fallbackMercadoPago(Double monto, String ordenCompra, Long usuarioId, Throwable t) {
        
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
                .build();
        pagoRepository.save(registroFalla);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "RECHAZADO_POR_SISTEMA");
        respuesta.put("mensaje", "MercadoPago no responde.");
        return respuesta;
    }

    // --- 3. KHIPU ---
    @CircuitBreaker(name = "khipuCB", fallbackMethod = "fallbackKhipu")
    public Map<String, Object> procesarPagoKhipu(Double monto, String ordenCompra, Long usuarioId, String clienteNombre, String clienteEmail, String tipoDocumento, String rutEmpresa, String razonSocial) {
        
        if (pagoRepository.existsByOrdenCompraAndEstado(ordenCompra, "APROBADO")) {
            return Map.of("estado", "RECHAZADO_POR_SISTEMA", "mensaje", "La orden " + ordenCompra + " ya fue pagada anteriormente.");
        }

        System.out.println("Conectando con Khipu Sandbox...");

/*
        // 👇 TRUCO: Usamos una variable local para simular que se ha caído la pasarela de pago Khipu.
        boolean simularCaida = true;
        if (simularCaida) {
            throw new RuntimeException("Error de conexión 504: Gateway Timeout en servidores de Khipu.");
        }
        // Fin del truco
*/

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
                .build();
        pagoRepository.save(registro);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "PENDIENTE");
        respuesta.put("pasarela", "Khipu");
        respuesta.put("urlPago", urlSimulada);
        return respuesta;
    }

    public Map<String, Object> fallbackKhipu(Double monto, String ordenCompra, Long usuarioId, Throwable t) {
        
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
                .build();
        pagoRepository.save(registroFalla);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "RECHAZADO_POR_SISTEMA");
        respuesta.put("mensaje", "Khipu fuera de servicio.");
        return respuesta;
    }

    // =================================================================
    // MÉTODO WEBHOOK: Simula la respuesta asíncrona de aprobación
    // =================================================================
    public Map<String, Object> confirmarPagoYEmitirDocumento(String ordenCompra) {
        
        Pago pago = pagoRepository.findByOrdenCompra(ordenCompra)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada en la base de datos"));

        if (!"PENDIENTE".equals(pago.getEstado())) {
            return Map.of("mensaje", "Esta orden ya fue procesada anteriormente. Estado actual: " + pago.getEstado());
        }

        // 1. Solicitamos el Folio al SII (Aquí entra en acción el Circuit Breaker)
        String folioRespuesta = siiService.solicitarFolioSii(pago.getTipoDocumento(), pago.getMontoTotal());
        
        byte[] pdfBytes = null;
        String mensajeRespuesta;

        // 2. Evaluamos qué nos respondió el SII (¡ADIÓS HARCODEO!)
        if ("PENDIENTE_SII".equals(folioRespuesta)) {
            pago.setEstado("APROBADO_BOLETA_PENDIENTE");
            pago.setFolioSii("PENDIENTE_EMISION");
            mensajeRespuesta = "Pago aprobado. La emisión de tu boleta está en cola por intermitencias del SII.";
            
            // Nota: No llamamos al Factory, el PDF queda en null.
        } else {
            pago.setEstado("APROBADO");
            pago.setFolioSii(folioRespuesta);
            
            // 3. LA MAGIA DEL FACTORY (Solo se ejecuta si hay folio real)
            pdfBytes = documentoFactory.obtenerGenerador(pago.getTipoDocumento()).generarDocumento(pago);
            mensajeRespuesta = "Pago aprobado y boleta emitida exitosamente.";
        }
        
        pagoRepository.save(pago);

        // 1. Primero, convertimos el arreglo de bytes del PDF a un String en Base64 (si es que el SII no falló)
String pdfBase64 = (pdfBytes != null && pdfBytes.length > 0) 
        ? java.util.Base64.getEncoder().encodeToString(pdfBytes) 
        : null;

// 2. Armamos el evento extrayendo todos los datos dinámicamente de la compra
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

        // 4. Publicamos hacia RabbitMQ (Bodega siempre despacha, sin importar el SII)
        ventaPublisher.publicarVentaExitosa(evento);

        if ("PENDIENTE_SII".equals(folioRespuesta)) {
            // Cola de reintentos para SII: Aquí es donde el evento se quedará esperando hasta que el SII vuelva a estar disponible.
            ventaPublisher.publicarReintentoSii(evento);
        }

        return Map.of(
            "estado", pago.getEstado(), 
            "folioSii", pago.getFolioSii(),
            "mensaje", mensajeRespuesta
        );
    }
}


