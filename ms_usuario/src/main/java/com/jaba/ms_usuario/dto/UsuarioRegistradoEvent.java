package com.jaba.ms_usuario.dto;

import lombok.Data;

@Data
public class UsuarioRegistradoEvent {
    private Long idAuth;
    private String email;
    private String username;
}

