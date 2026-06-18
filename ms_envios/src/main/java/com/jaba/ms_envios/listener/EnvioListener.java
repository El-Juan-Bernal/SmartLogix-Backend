package com.jaba.ms_envios.listener;

import com.jaba.ms_envios.dto.PagoConfirmadoDTO;
import com.jaba.ms_envios.factory.Despacho;
import com.jaba.ms_envios.factory.DespachoFactory;
import com.jaba.ms_envios.model.Envio;
import com.jaba.ms_envios.repository.EnvioRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnvioListener {

    @Autowired
    private EnvioRepository envioRepository;

    // Escuchamos la cola donde el ms_pagos enviará la confirmación
    @RabbitListener(queues = "pagos.confirmados.queue")
    public void procesarPagoConfirmado(PagoConfirmadoDTO pagoDTO) {
        
        System.out.println("Recibiendo confirmación de pago para orden: " + pagoDTO.getOrdenCompra());

        // 1. Usar el Factory Method para determinar qué tipo de envío le corresponde
        Despacho despacho = DespachoFactory.determinarDespacho(
                pagoDTO.getDireccionDestino(), 
                pagoDTO.isRequiereExpress()
        );

        // 2. Armar el objeto de la base de datos
        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setPedidoId(pagoDTO.getPedidoId());
        nuevoEnvio.setDireccionDestino(pagoDTO.getDireccionDestino());
        nuevoEnvio.setTipoDespacho(despacho.obtenerTipo()); // "LOCAL", "EXPRES" o "INTERREGIONAL"
        nuevoEnvio.setEstado("PREPARACION");

        // 3. Guardar el envío de forma automática
        envioRepository.save(nuevoEnvio);
        
        System.out.println("Despacho " + despacho.obtenerTipo() + " asignado exitosamente.");
    }
}

