package com.jaba.ms_envios.factory;

public class DespachoFactory {

    public static Despacho determinarDespacho(String direccion, boolean requiereExpress) {
        if (direccion == null) {
            return new DespachoLocal();
        }

        String dirMinuscula = direccion.toLowerCase();

        // Si es de la región y pide urgencia, se fabrica un envío exprés
        if (requiereExpress && (dirMinuscula.contains("biobio") || dirMinuscula.contains("concepcion"))) {
            return new DespachoExpress();
        }
        
        // Si es de la zona local pero sin urgencia
        if (dirMinuscula.contains("biobio") || dirMinuscula.contains("concepcion")) {
            return new DespachoLocal();
        }

        // Si es despacho al resto de las regiones del país
        return new DespachoInterregional();
    }
}

