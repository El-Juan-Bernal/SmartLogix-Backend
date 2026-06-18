package com.jaba.ms_envios.controller;

import com.jaba.ms_envios.model.Envio;
import com.jaba.ms_envios.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioRepository envioRepository;

    @PostMapping
    public Envio crearEnvio(@RequestBody Envio envio) {
        return envioRepository.save(envio);
    }

    @GetMapping
    public List<Envio> obtenerEnvios() {
        return envioRepository.findAll();
    }
}


