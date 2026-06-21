package com.jaba.ms_usuario.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartlogix.exchange";
    public static final String QUEUE_USUARIO = "smartlogix.usuario.queue";
    public static final String QUEUE_USUARIO_DLQ = "smartlogix.usuario.dlq";
    public static final String ROUTING_KEY_USUARIO = "usuario.creado";
    public static final String ROUTING_KEY_USUARIO_DLQ = "usuario.dlq";

    // --- EL TRADUCTOR JSON (¡Lo que faltaba!) ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange smartlogixExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue usuarioDlq() {
        return QueueBuilder.durable(QUEUE_USUARIO_DLQ).build();
    }

    @Bean
    public Queue usuarioQueue() {
        return QueueBuilder.durable(QUEUE_USUARIO)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_USUARIO_DLQ)
                .build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(usuarioDlq()).to(smartlogixExchange()).with(ROUTING_KEY_USUARIO_DLQ);
    }

    @Bean
    public Binding usuarioBinding() {
        return BindingBuilder.bind(usuarioQueue()).to(smartlogixExchange()).with(ROUTING_KEY_USUARIO);
    }
}

