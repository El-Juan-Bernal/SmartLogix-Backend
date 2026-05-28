package com.jaba.bff_web.client;

import com.jaba.bff_web.dto.PagoRequestDTO; // <-- Importación del DTO
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-pagos", url = "${url.ms.pagos}")
public interface PagosClient {

    @GetMapping("/{id}")
    Map<String, Object> obtenerPagoPorId(@PathVariable("id") Long id);

    @PostMapping
    Map<String, Object> procesarPago(@RequestBody PagoRequestDTO pago);
}

