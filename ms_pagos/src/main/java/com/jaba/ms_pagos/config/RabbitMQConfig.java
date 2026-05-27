package com.jaba.ms_pagos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // --- 1. EXCHANGES ---
    public static final String EXCHANGE_PRINCIPAL = "smartlogix.venta.exchange";
    public static final String DLX_FALLAS = "smartlogix.fallas.dlx";

    // --- 2. COLAS PRINCIPALES ---
    public static final String COLA_MENSAJERIA = "smartlogix.mensajeria.queue";
    public static final String COLA_BODEGA = "smartlogix.bodega.queue";

    // --- 3. COLAS DE MENSAJES MUERTOS (DLQ) ---
    public static final String DLQ_MENSAJERIA = "smartlogix.mensajeria.dlq";
    public static final String DLQ_BODEGA = "smartlogix.bodega.dlq";
    
    // --- 4. ROUTING KEYS ---
    public static final String KEY_NOTIFICAR = "venta.notificar";
    public static final String KEY_DESPACHAR = "venta.despachar";
    public static final String KEY_FAIL_NOTIFICAR = "fail.notificar";
    public static final String KEY_FAIL_DESPACHAR = "fail.despachar";


    // --- 5. CONSTANTES PARA SII (CONTINGENCIA) ---
    public static final String COLA_SII_RETRY = "smartlogix.sii.retry.queue";
    public static final String KEY_SII_RETRY = "sii.retry";
    public static final String KEY_FAIL_SII = "fail.sii";
    public static final String DLQ_SII = "smartlogix.sii.dlq";


    // Declaración de los Exchanges
    @Bean
    public TopicExchange exchangePrincipal() {
        return new TopicExchange(EXCHANGE_PRINCIPAL);
    }

    @Bean
    public DirectExchange dlxFallas() {
        return new DirectExchange(DLX_FALLAS);
    }

    // --- CONFIGURACIÓN DE COLA MENSAJERIA + SU DLQ ---
    @Bean
    public Queue colaMensajeria() {
        Map<String, Object> args = new HashMap<>();
        // 1. Vinculación al DLX si falla
        args.put("x-dead-letter-exchange", DLX_FALLAS);
        args.put("x-dead-letter-routing-key", KEY_FAIL_NOTIFICAR);
        
        // 2. NUEVOS LÍMITES DE CAPACIDAD Y TIEMPO
        args.put("x-max-length", 500); // Límite de 500 mensajes encolados
        args.put("x-message-ttl", 86400000); // Caducidad de 24 horas (en milisegundos)
        
        return new Queue(COLA_MENSAJERIA, true, false, false, args);
    }

    @Bean
    public Queue dlqMensajeria() {
        return new Queue(DLQ_MENSAJERIA, true);
    }

    @Bean
    public Binding bindingMensajeria() {
        return BindingBuilder.bind(colaMensajeria()).to(exchangePrincipal()).with(KEY_NOTIFICAR);
    }

    @Bean
    public Binding bindingDlqMensajeria() {
        return BindingBuilder.bind(dlqMensajeria()).to(dlxFallas()).with(KEY_FAIL_NOTIFICAR);
    }

    // --- CONFIGURACIÓN DE COLA BODEGA + SU DLQ ---
    @Bean
    public Queue colaBodega() {
        Map<String, Object> args = new HashMap<>();
        // 1. Vinculación al DLX si falla
        args.put("x-dead-letter-exchange", DLX_FALLAS);
        args.put("x-dead-letter-routing-key", KEY_FAIL_DESPACHAR);
        
        // 2. NUEVOS LÍMITES DE CAPACIDAD Y TIEMPO
        args.put("x-max-length", 500); // Límite de 500 mensajes encolados
        args.put("x-message-ttl", 86400000); // Caducidad de 24 horas (en milisegundos)
        
        return new Queue(COLA_BODEGA, true, false, false, args);
    }

    @Bean
    public Queue dlqBodega() {
        return new Queue(DLQ_BODEGA, true);
    }

    @Bean
    public Binding bindingBodega() {
        return BindingBuilder.bind(colaBodega()).to(exchangePrincipal()).with(KEY_DESPACHAR);
    }

    @Bean
    public Binding bindingDlqBodega() {
        return BindingBuilder.bind(dlqBodega()).to(dlxFallas()).with(KEY_FAIL_DESPACHAR);
    }

    // --- CONFIGURACIÓN DE COLA SII (SALA DE ESPERA) + SU DLQ ---
    @Bean
    public Queue colaSiiRetry() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_FALLAS);
        args.put("x-dead-letter-routing-key", KEY_FAIL_SII); // Usamos su propia llave
        args.put("x-message-ttl", 3600000); // 1 hora de espera
        
        return new Queue(COLA_SII_RETRY, true, false, false, args);
    }

    @Bean
    public Queue dlqSii() {
        return new Queue(DLQ_SII, true);
    }

    @Bean
    public Binding bindingSiiRetry() {
        return BindingBuilder.bind(colaSiiRetry()).to(exchangePrincipal()).with(KEY_SII_RETRY);
    }

    @Bean
    public Binding bindingDlqSii() {
        return BindingBuilder.bind(dlqSii()).to(dlxFallas()).with(KEY_FAIL_SII);
    }

    // --- CONVERTER PARA MANDAR OBJETOS EN FORMATO JSON ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}

