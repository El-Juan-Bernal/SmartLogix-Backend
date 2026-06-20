package com.jaba.ms_idp.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import com.fasterxml.jackson.databind.ObjectMapper;

//import tools.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

    // La magia que transforma nuestro DTO en un JSON puro y legible
    @SuppressWarnings("deprecation")
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Declaramos el Exchange para asegurarnos de que exista en RabbitMQ
    @Bean
    public TopicExchange usuariosExchange() {
        return new TopicExchange("usuarios.exchange");
    }
}

