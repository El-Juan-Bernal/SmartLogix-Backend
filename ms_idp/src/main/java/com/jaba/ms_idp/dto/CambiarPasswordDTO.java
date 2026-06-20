package com.jaba.ms_idp.dto;

import lombok.Data;

@Data
public class CambiarPasswordDTO {
    private String email;
    private String passwordActual;
    private String passwordNueva;
}

