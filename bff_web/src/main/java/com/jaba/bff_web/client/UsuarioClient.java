package com.jaba.bff_web.client;

import com.jaba.bff_web.dto.ActualizarPerfilDTO;
import com.jaba.bff_web.dto.DireccionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-usuario", url = "${url.ms.usuario}")
public interface UsuarioClient {

    @PutMapping("/completar/{authId}")
    Map<String, Object> completarPerfil(@PathVariable("authId") Long authId, @RequestBody ActualizarPerfilDTO dto);

    // --- CAMBIO: Cambiamos usuarioId por authId ---
    @PostMapping("/auth/{authId}/direcciones")
    String agregarNuevaDireccion(@PathVariable("authId") Long authId, @RequestBody DireccionDTO nuevaDireccion);

    // --- NUEVO: Para traer la lista de direcciones guardadas ---
    @GetMapping("/auth/{authId}/direcciones")
    List<DireccionDTO> obtenerDireccionesPorAuthId(@PathVariable("authId") Long authId);
}

