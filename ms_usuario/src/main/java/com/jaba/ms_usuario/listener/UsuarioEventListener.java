package com.jaba.ms_usuario.listener;

import com.jaba.ms_usuario.config.RabbitMQConfig;
import com.jaba.ms_usuario.dto.UsuarioRegistradoEvent;
import com.jaba.ms_usuario.model.Usuario;
import com.jaba.ms_usuario.repository.UsuarioRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEventListener {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_USUARIO)
public void procesarUsuarioCreado(UsuarioRegistradoEvent event) {
        System.out.println("⚡ Evento recibido de RabbitMQ: Creando perfil para " + event.getEmail());

        Usuario nuevoPerfil = new Usuario();
        nuevoPerfil.setAuthId(event.getIdAuth());
        nuevoPerfil.setEmailUsuario(event.getEmail());
        nuevoPerfil.setUsername(event.getUsername());
        
        // Guardamos el cascarón en la base de datos
        usuarioRepository.save(nuevoPerfil);
        
        System.out.println("✅ Perfil vacío creado exitosamente para el Auth ID: " + event.getIdAuth());
    }
}

