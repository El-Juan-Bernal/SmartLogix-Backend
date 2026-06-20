package com.jaba.bff_web.client;

import com.jaba.bff_web.dto.ActualizarPerfilDTO;
import com.jaba.bff_web.dto.DireccionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-usuario", url = "${url.ms.usuario}")
public interface UsuarioClient {

    @PutMapping("/completar/{authId}")
    Map<String, Object> completarPerfil(@PathVariable("authId") Long authId, @RequestBody ActualizarPerfilDTO dto);

    @PostMapping("/{usuarioId}/direcciones")
    String agregarNuevaDireccion(@PathVariable("usuarioId") Long usuarioId, @RequestBody DireccionDTO nuevaDireccion);
}

