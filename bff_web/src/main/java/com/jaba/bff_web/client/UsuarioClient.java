package com.jaba.bff_web.client;

import com.jaba.bff_web.dto.ActualizarPerfilDTO;
import com.jaba.bff_web.dto.DireccionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-usuario", url = "${url.ms.usuario}")
public interface UsuarioClient {

    @PutMapping("/completar/{authId}")
    Map<String, Object> completarPerfil(@PathVariable("authId") Long authId, @RequestBody ActualizarPerfilDTO dto);

    @GetMapping("/auth/{authId}")
    Map<String, Object> obtenerPerfilPorAuthId(@PathVariable("authId") Long authId);

    @PostMapping("/auth/{authId}/direcciones")
    String agregarNuevaDireccion(@PathVariable("authId") Long authId, @RequestBody DireccionDTO nuevaDireccion);

    @GetMapping("/auth/{authId}/direcciones")
    List<DireccionDTO> obtenerDireccionesPorAuthId(@PathVariable("authId") Long authId);

    // --- NUEVOS MÉTODOS AÑADIDOS ---
    @DeleteMapping("/auth/{authId}/direcciones/{direccionId}")
    String eliminarDireccion(@PathVariable("authId") Long authId, @PathVariable("direccionId") Long direccionId);

    @PutMapping("/auth/{authId}/direcciones/{direccionId}/predeterminada")
    String marcarDireccionPredeterminada(@PathVariable("authId") Long authId, @PathVariable("direccionId") Long direccionId);
}

