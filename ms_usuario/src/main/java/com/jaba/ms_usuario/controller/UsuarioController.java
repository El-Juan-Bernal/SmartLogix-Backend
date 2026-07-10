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

    // Endpoint para obtener el perfil completo de un usuario a partir de su authId
    // (el mismo id que devuelve ms_idp al hacer login). Esto es lo que faltaba:
    // sin esto, el frontend no tiene forma de recuperar nombre/apellido/telefono/imagen
    // después de un login nuevo.
    @GetMapping("/auth/{authId}")
    public ResponseEntity<?> obtenerPerfilPorAuthId(@PathVariable Long authId) {
        var usuarioOpt = usuarioRepository.findByAuthId(authId);

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.notFound().build();
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

    // Endpoint para eliminar una dirección específica de la libreta del usuario
    @DeleteMapping("/auth/{authId}/direcciones/{direccionId}")
    public ResponseEntity<?> eliminarDireccion(@PathVariable Long authId, @PathVariable Long direccionId) {
        // LÍNEA CORREGIDA:
        var usuarioOpt = usuarioRepository.findByAuthId(authId); 
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Remueve la dirección de la lista interna si coincide con el ID solicitado
            boolean removido = usuario.getDirecciones().removeIf(d -> d.getId().equals(direccionId));
            
            if (removido) {
                usuarioRepository.save(usuario);
                return ResponseEntity.ok("Dirección eliminada exitosamente de la libreta");
            } else {
                return ResponseEntity.badRequest().body("Error: Dirección no encontrada en la libreta del usuario.");
            }
        } else {
            return ResponseEntity.badRequest().body("Error: Usuario no encontrado.");
        }
    }

    // Endpoint para marcar una dirección como predeterminada y desmarcar las demás
    @PutMapping("/auth/{authId}/direcciones/{direccionId}/predeterminada")
    public ResponseEntity<?> marcarDireccionPredeterminada(@PathVariable Long authId, @PathVariable Long direccionId) {
        var usuarioOpt = usuarioRepository.findByAuthId(authId);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            boolean encontrada = false;
            
            for (Direccion dir : usuario.getDirecciones()) {
                if (dir.getId().equals(direccionId)) {
                    dir.setPredeterminada(true);
                    encontrada = true;
                } else {
                    dir.setPredeterminada(false);
                }
            }
            
            if (encontrada) {
                usuarioRepository.save(usuario);
                return ResponseEntity.ok("Dirección establecida como predeterminada exitosamente");
            } else {
                return ResponseEntity.badRequest().body("Error: Dirección no encontrada en la libreta del usuario.");
            }
        } else {
            return ResponseEntity.badRequest().body("Error: Usuario no encontrado.");
        }
    }
}

