package com.jaba.bff_web.dto;

import lombok.Data;
import java.util.List;

@Data
public class PagoRequestDTO {
    private String usuarioId;
    private String metodoPago;
    private Integer montoTotal;
    private List<ItemCarritoDTO> detallesCarrito;

    // Sub-molde para los productos que vienen dentro del carrito
    @Data
    public static class ItemCarritoDTO {
        private Long productoId;
        private Integer cantidad;
        private Integer precioUnitario;
    }
}

