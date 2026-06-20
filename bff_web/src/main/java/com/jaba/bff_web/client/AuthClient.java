package com.jaba.bff_web.client;

import com.jaba.bff_web.dto.RegistroUsuarioDTO;
import com.jaba.bff_web.dto.CambiarPasswordDTO;
import com.jaba.bff_web.dto.RecuperarPasswordDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-idp", url = "${url.ms.idp}")
public interface AuthClient {

    @PostMapping("/register")
    String registrar(@RequestBody RegistroUsuarioDTO dto);

    // NUEVO: Ruta para cambiar contraseña
    @PutMapping("/cambiar-password")
    String cambiarPassword(@RequestBody CambiarPasswordDTO dto);

    // NUEVO: Ruta para recuperar contraseña
    @PostMapping("/recuperar-password")
    String recuperarPassword(@RequestBody RecuperarPasswordDTO dto);
}

