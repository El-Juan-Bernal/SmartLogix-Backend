package com.jaba.ms_idp.service;

import com.jaba.ms_idp.dto.RegistroUsuarioDTO;
import com.jaba.ms_idp.dto.UsuarioRegistradoEvent;
import com.jaba.ms_idp.model.UsuarioAuth;
import com.jaba.ms_idp.repository.UsuarioAuthRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioAuthRepository usuarioAuthRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioAuth registrarUsuario(RegistroUsuarioDTO dto) {
        if (usuarioAuthRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Error: El correo electrónico ya está registrado.");
        }

        UsuarioAuth nuevoUsuario = new UsuarioAuth();
        nuevoUsuario.setUsername(dto.getUsername());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Encriptación segura

        UsuarioAuth usuarioGuardado = usuarioAuthRepository.save(nuevoUsuario);

        // Disparar evento asíncrono para que ms_usuarios cree el perfil
        UsuarioRegistradoEvent evento = new UsuarioRegistradoEvent(
                usuarioGuardado.getId(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getUsername()
        );
        
        // Asumiendo que usaremos un exchange llamado 'usuarios.exchange'
        rabbitTemplate.convertAndSend("usuarios.exchange", "usuario.creado.routing.key", evento);

        return usuarioGuardado;
    }
}

