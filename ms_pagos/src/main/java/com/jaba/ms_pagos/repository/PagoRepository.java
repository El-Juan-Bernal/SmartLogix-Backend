package com.jaba.ms_pagos.repository;

import com.jaba.ms_pagos.model.Pago;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PagoRepository extends CrudRepository<Pago, Long> {
    
    // Spring Boot es tan inteligente que crea la consulta SQL solo leyendo este nombre
    boolean existsByOrdenCompraAndEstado(String ordenCompra, String estado);

    // 👇 Agregamos este buscador para el Webhook 👇
    Optional<Pago> findByOrdenCompra(String ordenCompra);
}

