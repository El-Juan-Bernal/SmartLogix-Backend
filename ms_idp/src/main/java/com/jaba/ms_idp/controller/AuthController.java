package com.jaba.ms_idp.controller;

import com.jaba.ms_idp.dto.RegistroUsuarioDTO;
import com.jaba.ms_idp.dto.CambiarPasswordDTO;
import com.jaba.ms_idp.dto.RecuperarPasswordDTO;
import com.jaba.ms_idp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegistroUsuarioDTO dto) {
        try {
            authService.registrarUsuario(dto);
            return ResponseEntity.ok().body("Usuario registrado exitosamente en el IdP y evento emitido.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordDTO dto) {
        try {
            authService.cambiarPassword(dto);
            return ResponseEntity.ok().body("Contraseña actualizada exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@RequestBody RecuperarPasswordDTO dto) {
        try {
            String claveTemporal = authService.recuperarPassword(dto.getEmail());
            // Se muestra la clave temporal en el body de la respuesta para facilitar las pruebas en Postman
            return ResponseEntity.ok().body("Clave temporal generada: " + claveTemporal + " (En producción, esto se envía por email)");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

