package com.jaba.ms_idp.service;

import com.jaba.ms_idp.dto.CambiarPasswordDTO;
import com.jaba.ms_idp.dto.PasswordRecuperadaEvent;
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
        
        // ¡ACTUALIZADO! Disparamos al agrupador central con la etiqueta correcta
        rabbitTemplate.convertAndSend("smartlogix.exchange", "usuario.creado", evento);

        return usuarioGuardado;
    }

    public void cambiarPassword(CambiarPasswordDTO dto) {
        UsuarioAuth usuario = usuarioAuthRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Error: Usuario no encontrado."));

        // Verificamos que la contraseña actual ingresada coincida con el hash de la BD
        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("Error: La contraseña actual es incorrecta.");
        }

        // Encriptamos y guardamos la nueva
        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioAuthRepository.save(usuario);
    }

    // NUEVO MÉTODO PARA RECUPERAR CONTRASEÑA
    public String recuperarPassword(String email) {
        UsuarioAuth usuario = usuarioAuthRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Error: No existe una cuenta con ese correo electrónico."));

        // Generamos una clave temporal aleatoria de 8 caracteres
        String claveTemporal = java.util.UUID.randomUUID().toString().substring(0, 8);
        
        // Encriptamos y guardamos la clave temporal en la base de datos
        usuario.setPassword(passwordEncoder.encode(claveTemporal));
        usuarioAuthRepository.save(usuario);

        // ¡NUEVO! Construimos el evento asíncrono con los datos del usuario
        PasswordRecuperadaEvent evento = new PasswordRecuperadaEvent(
                usuario.getEmail(),
                usuario.getUsername(),
                claveTemporal
        );
        
        // ¡NUEVO! Disparamos el evento al agrupador central con la nueva etiqueta 'usuario.recuperar'
        rabbitTemplate.convertAndSend("smartlogix.exchange", "usuario.recuperar", evento);

        // Por seguridad, nunca retornamos la clave temporal al cliente/frontend
        return "Si el correo existe, se ha enviado una clave temporal para la recuperación de su cuenta.";
    }

}

