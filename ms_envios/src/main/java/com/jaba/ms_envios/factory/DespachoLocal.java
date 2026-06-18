package com.jaba.ms_envios.factory;

public class DespachoLocal implements Despacho {
    @Override
    public String obtenerTipo() { 
        return "LOCAL"; 
    }

    @Override
    public int calcularTiempoEntregaHoras() { 
        return 48; // 2 días estándar
    }

    @Override
    public double calcularCostoAdicional(double montoPedido) {
        return montoPedido > 30000 ? 0.0 : 2500.0; // Gratis sobre $30.000
    }
}

