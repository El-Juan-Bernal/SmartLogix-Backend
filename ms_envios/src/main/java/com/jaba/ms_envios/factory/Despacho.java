package com.jaba.ms_envios.factory;

public interface Despacho {
    String obtenerTipo();
    int calcularTiempoEntregaHoras();
    double calcularCostoAdicional(double montoPedido);
}
