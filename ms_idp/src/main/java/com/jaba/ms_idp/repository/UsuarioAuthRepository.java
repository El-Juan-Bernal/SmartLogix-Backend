package com.jaba.ms_idp.repository;

import com.jaba.ms_idp.model.UsuarioAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioAuthRepository extends JpaRepository<UsuarioAuth, Long> {
    Optional<UsuarioAuth> findByUsername(String username);
    
    // Verifica si el correo ya está registrado
    boolean existsByEmail(String email);

    // Para buscar el registro completo por correo
    Optional<UsuarioAuth> findByEmail(String email);
}

