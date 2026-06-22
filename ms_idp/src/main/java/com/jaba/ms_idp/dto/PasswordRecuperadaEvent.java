package com.jaba.ms_idp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecuperadaEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String email;
    private String username;
    private String claveTemporal;
}

