package com.jaba.ms_usuario.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        
        // Configuramos un mapeador de clases para resolver el cruce de paquetes
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        // Mapea la firma de ms_idp directamente a nuestro DTO local de ms_usuario
        idClassMapping.put("com.jaba.ms_idp.dto.UsuarioRegistradoEvent", com.jaba.ms_usuario.dto.UsuarioRegistradoEvent.class);
        
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        
        return converter;
    }

    @Bean
    public TopicExchange usuariosExchange() {
        return new TopicExchange("usuarios.exchange");
    }

    @Bean
    public Queue usuarioCreadoQueue() {
        return new Queue("usuario.creado.queue", true);
    }

    @Bean
    public Binding bindingUsuarioCreado(Queue usuarioCreadoQueue, TopicExchange usuariosExchange) {
        return BindingBuilder.bind(usuarioCreadoQueue).to(usuariosExchange).with("usuario.creado.routing.key");
    }
}

