package com.jaba.bff_web.controller;

import com.jaba.bff_web.client.AuthClient;
import com.jaba.bff_web.dto.RegistroUsuarioDTO;
import com.jaba.bff_web.dto.CambiarPasswordDTO;
import com.jaba.bff_web.dto.RecuperarPasswordDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class BffAuthController {

    @Autowired
    private AuthClient authClient;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroUsuarioDTO dto) {
        try {
            String respuesta = authClient.registrar(dto);
            return ResponseEntity.ok(respuesta);
        } catch (FeignException.BadRequest e) {
            return ResponseEntity.badRequest().body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error de comunicación con el servicio de identidad: " + e.getMessage());
        }
    }

    // NUEVO: Endpoint puente para el inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            // El BFF actúa como puente y devuelve exactamente lo que responde el ms_idp
            Object respuesta = authClient.login(credenciales);
            return ResponseEntity.ok(respuesta);
        } catch (FeignException.Unauthorized e) {
            // Capturamos el error 401 de credenciales incorrectas que envía el ms_idp
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el login: " + e.getMessage());
        }
    }

    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordDTO dto) {
        try {
            String respuesta = authClient.cambiarPassword(dto);
            return ResponseEntity.ok().body(respuesta);
        } catch (FeignException.BadRequest e) {
            return ResponseEntity.badRequest().body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el cambio de contraseña: " + e.getMessage());
        }
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@RequestBody RecuperarPasswordDTO dto) {
        try {
            String respuesta = authClient.recuperarPassword(dto);
            return ResponseEntity.ok().body(respuesta);
        } catch (FeignException.BadRequest e) {
            return ResponseEntity.badRequest().body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la recuperación de contraseña: " + e.getMessage());
        }
    }
}

