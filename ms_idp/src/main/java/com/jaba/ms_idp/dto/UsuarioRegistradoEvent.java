package com.jaba.ms_idp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioRegistradoEvent {
    private Long idAuth;
    private String email;
    private String username;
}

