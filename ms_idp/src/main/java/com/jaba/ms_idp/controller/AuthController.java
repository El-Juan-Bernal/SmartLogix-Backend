package com.jaba.ms_idp.controller;

import com.jaba.ms_idp.dto.RegistroUsuarioDTO;
import com.jaba.ms_idp.model.UsuarioAuth;
import com.jaba.ms_idp.dto.CambiarPasswordDTO;
import com.jaba.ms_idp.dto.RecuperarPasswordDTO;
import com.jaba.ms_idp.service.AuthService;
import com.jaba.ms_idp.repository.UsuarioAuthRepository;
import com.jaba.ms_idp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioAuthRepository usuarioAuthRepository;

    @Autowired
    private JwtUtil jwtUtil; 

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<UsuarioAuth> registrarUsuario(@RequestBody RegistroUsuarioDTO dto) {
        UsuarioAuth nuevoUsuario = authService.registrarUsuario(dto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        Optional<UsuarioAuth> usuarioOpt = usuarioAuthRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            UsuarioAuth usuario = usuarioOpt.get();
            
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                
                // NUEVO: Generamos el token firmado digitalmente
                String token = jwtUtil.generateToken(usuario.getEmail());
                
                response.put("token", token);
                response.put("usuario", usuario); 

                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(401).body("Error: Credenciales incorrectas");
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
            return ResponseEntity.ok().body("Clave temporal generada: " + claveTemporal + " (En producción, esto se envía por email)");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

