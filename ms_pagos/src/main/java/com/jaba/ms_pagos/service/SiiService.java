package com.jaba.ms_pagos.service;

import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class SiiService {

    // 1. Primero actúa el Retry. Solo si se acaban los intentos, llama al fallbackSii
    @Retry(name = "siiRetry", fallbackMethod = "fallbackSii")
    @CircuitBreaker(name = "siiCB") 
    public String solicitarFolioSii(String tipoDocumento, Double montoTotal) {
        System.out.println("⏳ Conectando con los servidores del SII para emitir " + tipoDocumento + "...");

        // Simulador de caídas aleatorias
        if (Math.random() > 0.5) {
            System.err.println("⚠️ Micro-corte detectado en el SII. Preparando reintento...");
            throw new RuntimeException("Error 504: Gateway Timeout en portal del SII.");
        }

        // Si sobrevive, retorna un folio válido
        System.out.println("✅ Conexión exitosa. Folio obtenido.");
        return "F-" + ((int)(Math.random() * 90000) + 10000);
    }

    // 2. EL SALVAVIDAS FINAL: Se ejecuta solo cuando todos los reintentos fallaron
    public String fallbackSii(String tipoDocumento, Double montoTotal, Throwable t) {
        System.err.println("🚨 ¡Alerta! El SII no respondió después de 15 segundos. Se despachará a la cola de espera. Motivo: " + t.getMessage());
        return "PENDIENTE_SII"; 
    }
}

