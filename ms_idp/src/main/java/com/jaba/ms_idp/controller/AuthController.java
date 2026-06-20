package com.jaba.ms_idp.controller;

import com.jaba.ms_idp.dto.RegistroUsuarioDTO;
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
}

