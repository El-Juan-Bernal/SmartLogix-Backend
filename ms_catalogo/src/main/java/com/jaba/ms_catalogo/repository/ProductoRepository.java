package com.jaba.ms_catalogo.repository;

import com.jaba.ms_catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // 1. Traer todos los productos que están activos (Vitrina principal)
    List<Producto> findByActivoTrue();

    // 2. Filtrar por categoría (ignorando mayúsculas) y que estén activos
    List<Producto> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);

    // 3. Buscador: Coincidencia parcial del nombre (ignorando mayúsculas) y que estén activos
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}

