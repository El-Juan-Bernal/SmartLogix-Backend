package com.jaba.bff_web.controller;

import com.jaba.bff_web.client.UsuarioClient;
import com.jaba.bff_web.dto.ActualizarPerfilDTO;
import com.jaba.bff_web.dto.DireccionDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// ¡AQUÍ ESTÁ LA MAGIA! Cambiamos /perfil por /usuarios
@RequestMapping("/api/v1/usuarios") 
public class BffUsuarioController {

    @Autowired
    private UsuarioClient usuarioClient;

    @PutMapping("/completar/{authId}")
    public ResponseEntity<?> completarPerfil(@PathVariable Long authId, @RequestBody ActualizarPerfilDTO dto) {
        try {
            return ResponseEntity.ok(usuarioClient.completarPerfil(authId, dto));
        } catch (FeignException.BadRequest e) {
            return ResponseEntity.badRequest().body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al comunicar con el servicio de perfiles: " + e.getMessage());
        }
    }

    // Este es el que el frontend debe llamar justo despues del login,
    // para traer nombre/apellido/telefono/imagenPerfil (datos que el login de ms_idp no tiene).
    @GetMapping("/auth/{authId}")
    public ResponseEntity<?> obtenerPerfilPorAuthId(@PathVariable Long authId) {
        try {
            return ResponseEntity.ok(usuarioClient.obtenerPerfilPorAuthId(authId));
        } catch (FeignException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el perfil: " + e.getMessage());
        }
    }

    @PostMapping("/auth/{authId}/direcciones")
    public ResponseEntity<?> agregarDireccion(@PathVariable Long authId, @RequestBody DireccionDTO dto) {
        try {
            return ResponseEntity.ok(usuarioClient.agregarNuevaDireccion(authId, dto));
        } catch (FeignException.BadRequest e) {
            return ResponseEntity.badRequest().body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la dirección: " + e.getMessage());
        }
    }

    @GetMapping("/auth/{authId}/direcciones")
    public ResponseEntity<?> obtenerDirecciones(@PathVariable Long authId) {
        try {
            List<DireccionDTO> direcciones = usuarioClient.obtenerDireccionesPorAuthId(authId);
            return ResponseEntity.ok(direcciones);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las direcciones: " + e.getMessage());
        }
    }

    // --- NUEVO: Endpoint para eliminar ---
    @DeleteMapping("/auth/{authId}/direcciones/{direccionId}")
    public ResponseEntity<?> eliminarDireccion(@PathVariable Long authId, @PathVariable Long direccionId) {
        try {
            return ResponseEntity.ok(usuarioClient.eliminarDireccion(authId, direccionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la dirección: " + e.getMessage());
        }
    }

    // --- NUEVO: Endpoint para marcar como predeterminada ---
    @PutMapping("/auth/{authId}/direcciones/{direccionId}/predeterminada")
    public ResponseEntity<?> marcarPredeterminada(@PathVariable Long authId, @PathVariable Long direccionId) {
        try {
            return ResponseEntity.ok(usuarioClient.marcarDireccionPredeterminada(authId, direccionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la dirección: " + e.getMessage());
        }
    }
}

