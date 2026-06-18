package com.jaba.ms_envios.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // --- 1. EXCHANGES (Para poder enlazarnos al ecosistema) ---
    public static final String EXCHANGE_PRINCIPAL = "smartlogix.venta.exchange";
    public static final String DLX_FALLAS = "smartlogix.fallas.dlx";

    // --- 2. COLAS ---
    public static final String COLA_ENVIOS = "smartlogix.envios.queue";
    public static final String DLQ_ENVIOS = "smartlogix.envios.dlq";

    // --- 3. ROUTING KEYS ---
    public static final String KEY_ENVIOS = "venta.envios";
    public static final String KEY_FAIL_ENVIOS = "fail.envios";

    // Declaramos los exchanges por si ms_envios levanta antes que ms_pagos
    @Bean
    public TopicExchange exchangePrincipal() {
        return new TopicExchange(EXCHANGE_PRINCIPAL);
    }

    @Bean
    public DirectExchange dlxFallas() {
        return new DirectExchange(DLX_FALLAS);
    }

    // --- CONFIGURACIÓN DE LA COLA PRINCIPAL DE ENVÍOS ---
    @Bean
    public Queue colaEnvios() {
        Map<String, Object> args = new HashMap<>();
        // Enlazar al DLX de fallas
        args.put("x-dead-letter-exchange", DLX_FALLAS);
        args.put("x-dead-letter-routing-key", KEY_FAIL_ENVIOS);
        
        // Límites estandarizados
        args.put("x-max-length", 500); 
        args.put("x-message-ttl", 86400000); // 24 horas
        
        return new Queue(COLA_ENVIOS, true, false, false, args);
    }

    // --- CONFIGURACIÓN DEL DLQ DE ENVÍOS ---
    @Bean
    public Queue dlqEnvios() {
        return new Queue(DLQ_ENVIOS, true);
    }

    // --- BINDINGS (El puente entre el Exchange y nuestras colas) ---
    @Bean
    public Binding bindingEnvios() {
        return BindingBuilder.bind(colaEnvios()).to(exchangePrincipal()).with(KEY_ENVIOS);
    }

    @Bean
    public Binding bindingDlqEnvios() {
        return BindingBuilder.bind(dlqEnvios()).to(dlxFallas()).with(KEY_FAIL_ENVIOS);
    }

    // --- EL SALVAVIDAS: CONVERTER PARA RECIBIR JSON ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

