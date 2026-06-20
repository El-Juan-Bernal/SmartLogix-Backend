package com.jaba.bff_web.dto;

import lombok.Data;

@Data
public class DireccionDTO {
    private String alias;
    private String region;
    private String comuna;
    private String calle;
    private String numero;
    private String departamento;
}

