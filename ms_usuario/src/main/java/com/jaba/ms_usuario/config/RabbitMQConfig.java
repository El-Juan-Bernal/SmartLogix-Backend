package com.jaba.ms_usuario.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres estandarizados
    public static final String EXCHANGE_NAME = "smartlogix.exchange";
    public static final String QUEUE_USUARIO = "smartlogix.usuario.queue";
    public static final String QUEUE_USUARIO_DLQ = "smartlogix.usuario.dlq";
    public static final String ROUTING_KEY_USUARIO = "usuario.creado";
    public static final String ROUTING_KEY_USUARIO_DLQ = "usuario.dlq";

    // 1. El agrupador central (Exchange)
    @Bean
    public DirectExchange smartlogixExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // 2. La cola de fallos (DLQ)
    @Bean
    public Queue usuarioDlq() {
        return QueueBuilder.durable(QUEUE_USUARIO_DLQ).build();
    }

    // 3. La cola principal (Con instrucciones de rebote hacia la DLQ)
    @Bean
    public Queue usuarioQueue() {
        return QueueBuilder.durable(QUEUE_USUARIO)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_USUARIO_DLQ)
                .build();
    }

    // 4. Enlace: Conectar la DLQ al Exchange
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(usuarioDlq()).to(smartlogixExchange()).with(ROUTING_KEY_USUARIO_DLQ);
    }

    // 5. Enlace: Conectar la Cola Principal al Exchange
    @Bean
    public Binding usuarioBinding() {
        return BindingBuilder.bind(usuarioQueue()).to(smartlogixExchange()).with(ROUTING_KEY_USUARIO);
    }
}

