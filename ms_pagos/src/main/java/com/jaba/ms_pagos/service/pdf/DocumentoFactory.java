package com.jaba.ms_pagos.service.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DocumentoFactory {

    // Spring Boot inyecta automáticamente aquí todas las clases que implementen GeneradorPdf.
    // La llave (String) será el nombre que le demos a cada clase.
    @Autowired
    private Map<String, GeneradorPdf> generadores;

    public GeneradorPdf obtenerGenerador(String tipoDocumento) {
        GeneradorPdf generador = generadores.get(tipoDocumento.toUpperCase());
        if (generador == null) {
            throw new IllegalArgumentException("No existe un generador para el tipo: " + tipoDocumento);
        }
        return generador;
    }
}

