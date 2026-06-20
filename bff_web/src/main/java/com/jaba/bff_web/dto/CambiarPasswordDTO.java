package com.jaba.bff_web.dto;

import lombok.Data;

@Data
public class CambiarPasswordDTO {
    private String email;
    private String passwordActual;
    private String passwordNueva;
}

