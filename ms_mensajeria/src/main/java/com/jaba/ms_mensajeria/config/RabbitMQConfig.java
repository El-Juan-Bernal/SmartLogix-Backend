package com.jaba.ms_mensajeria.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartlogix.exchange";
    
    // Nombres específicos para la mensajería
    public static final String QUEUE_PASSWORD = "smartlogix.mensajeria.password.queue";
    public static final String QUEUE_PASSWORD_DLQ = "smartlogix.mensajeria.password.dlq";
    
    // La etiqueta que dispara el ms_idp
    public static final String ROUTING_KEY_RECUPERAR = "usuario.recuperar";
    public static final String ROUTING_KEY_RECUPERAR_DLQ = "usuario.recuperar.dlq";

    // ¡El traductor fundamental!
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange smartlogixExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue passwordDlq() {
        return QueueBuilder.durable(QUEUE_PASSWORD_DLQ).build();
    }

    @Bean
    public Queue passwordQueue() {
        return QueueBuilder.durable(QUEUE_PASSWORD)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_RECUPERAR_DLQ)
                .build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(passwordDlq()).to(smartlogixExchange()).with(ROUTING_KEY_RECUPERAR_DLQ);
    }

    @Bean
    public Binding passwordBinding() {
        return BindingBuilder.bind(passwordQueue()).to(smartlogixExchange()).with(ROUTING_KEY_RECUPERAR);
    }
}

