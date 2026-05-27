package com.jaba.ms_pagos.service.pdf;

import com.jaba.ms_pagos.model.Pago;

public interface GeneradorPdf {
    // Recibe el pago completo y devuelve el archivo PDF en crudo (bytes)
    byte[] generarDocumento(Pago pago);
}

