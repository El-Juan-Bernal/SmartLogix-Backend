package com.jaba.ms_envios.factory;

public class DespachoInterregional implements Despacho {
    @Override
    public String obtenerTipo() { 
        return "INTERREGIONAL"; 
    }

    @Override
    public int calcularTiempoEntregaHoras() { 
        return 72; // 3 días para regiones lejanas
    }

    @Override
    public double calcularCostoAdicional(double montoPedido) {
        return 6500.0; // Costo base interregional
    }
}

