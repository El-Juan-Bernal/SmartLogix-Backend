package com.jaba.ms_envios.factory;

public class DespachoExpress implements Despacho {
    @Override
    public String obtenerTipo() { 
        return "EXPRES"; 
    }

    @Override
    public int calcularTiempoEntregaHoras() { 
        return 12; // Entrega dentro del mismo día
    }

    @Override
    public double calcularCostoAdicional(double montoPedido) {
        return 4990.0; // Tarifa plana express
    }
}

