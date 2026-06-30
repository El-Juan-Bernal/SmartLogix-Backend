package com.jaba.ms_usuario.controller;

import com.jaba.ms_usuario.dto.ActualizarPerfilDTO;
import com.jaba.ms_usuario.model.Direccion;
import com.jaba.ms_usuario.model.Usuario;
import com.jaba.ms_usuario.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Endpoint para completar o actualizar el perfil creado por el evento de RabbitMQ
    @PutMapping("/completar/{authId}")
    public ResponseEntity<?> completarPerfil(@PathVariable Long authId, @Valid @RequestBody ActualizarPerfilDTO dto) {
        
        var usuarioOpt = usuarioRepository.findByAuthId(authId);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setTelefono(dto.getTelefono());
            
            if (dto.getImagenPerfil() != null) {
                usuario.setImagenPerfil(dto.getImagenPerfil());
            }
            
            Usuario usuarioActualizado = usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } else {
            return ResponseEntity.badRequest().body("Error: No se encontró un perfil base para el Auth ID proporcionado.");
        }
    }

    @PostMapping
    public ResponseEntity<?> crearPerfil(@Valid @RequestBody Usuario usuario) {
        if (usuario.getDirecciones() != null) {
            for (Direccion dir : usuario.getDirecciones()) {
                dir.setUsuario(usuario);
            }
        }
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @GetMapping
    public List<Usuario> obtenerPerfiles() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/auth/{authId}/direcciones")
    public ResponseEntity<?> agregarNuevaDireccion(@PathVariable Long authId, @Valid @RequestBody Direccion nuevaDireccion) {
        var usuarioOpt = usuarioRepository.findByAuthId(authId);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.agregarDireccion(nuevaDireccion);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Dirección agregada exitosamente a la libreta");
        } else {
            return ResponseEntity.badRequest().body("Error: Usuario no encontrado.");
        }
    }

    // Endpoint para obtener las direcciones de un usuario usando su authId (el ID de su token)
    @GetMapping("/auth/{authId}/direcciones")
    public ResponseEntity<?> obtenerDireccionesPorAuthId(@PathVariable Long authId) {
        var usuarioOpt = usuarioRepository.findByAuthId(authId);
        
        if (usuarioOpt.isPresent()) {
            // Si el usuario existe, devolvemos su lista de direcciones (puede estar vacía, y eso está bien)
            return ResponseEntity.ok(usuarioOpt.get().getDirecciones());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

