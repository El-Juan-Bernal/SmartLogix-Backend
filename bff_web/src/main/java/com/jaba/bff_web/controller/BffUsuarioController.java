package com.jaba.bff_web.controller;

import com.jaba.bff_web.client.UsuarioClient;
import com.jaba.bff_web.dto.ActualizarPerfilDTO;
import com.jaba.bff_web.dto.DireccionDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/perfil")
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

    @PostMapping("/{usuarioId}/direcciones")
    public ResponseEntity<?> agregarDireccion(@PathVariable Long usuarioId, @RequestBody DireccionDTO dto) {
        try {
            return ResponseEntity.ok(usuarioClient.agregarNuevaDireccion(usuarioId, dto));
        } catch (FeignException.BadRequest e) {
            return ResponseEntity.badRequest().body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la dirección: " + e.getMessage());
        }
    }
}

