package com.jaba.bff_web.client;

import com.jaba.bff_web.dto.MensajeriaRequestDTO; // <-- Importación del DTO
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-mensajeria", url = "${url.ms.mensajeria}")
public interface MensajeriaClient {

    @GetMapping("/{id}")
    Map<String, Object> obtenerMensajePorId(@PathVariable("id") Long id);
    
    @PostMapping
    Map<String, Object> enviarNotificacion(@RequestBody MensajeriaRequestDTO notificacion);
}

