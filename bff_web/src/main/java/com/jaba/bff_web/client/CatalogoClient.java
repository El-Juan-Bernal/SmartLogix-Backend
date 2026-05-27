package com.jaba.bff_web.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

// Cambiamos el atributo 'url' para que lea desde el application.properties
@FeignClient(name = "ms-catalogo", url = "${url.ms.catalogo}")
public interface CatalogoClient {

    @GetMapping("/{id}")
    Map<String, Object> obtenerProductoPorId(@PathVariable("id") Long id);
}

