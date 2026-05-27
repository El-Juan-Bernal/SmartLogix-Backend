package com.jaba.ms_mensajeria.repository;

import com.jaba.ms_mensajeria.model.RegistroCorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroCorreoRepository extends JpaRepository<RegistroCorreo, Long> {
    // Spring Data JPA ya nos regala el método save() por defecto
}

