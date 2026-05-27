package com.jaba.ms_pagos.service.pdf;

import com.jaba.ms_pagos.model.Pago;
import com.jaba.ms_pagos.model.PagoItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component("BOLETA")
public class GeneradorBoleta implements GeneradorPdf {

    @Override
    public byte[] generarDocumento(Pago pago) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        // Formato Ticket de impresora térmica (aprox 80mm de ancho)
        Rectangle formatoTicket = new Rectangle(226, 800); 
        Document document = new Document(formatoTicket, 10, 10, 10, 10);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Tipografías
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 8);

            // Cabecera Minimalista
            Paragraph cabecera = new Paragraph("Smart Logix\nConcepción, Bio Bío\n\n", fontTitulo);
            cabecera.setAlignment(Element.ALIGN_CENTER);
            document.add(cabecera);

            document.add(new Paragraph("BOLETA ELECTRÓNICA N° " + pago.getFolioSii(), fontTitulo));
            document.add(new Paragraph("Orden: " + pago.getOrdenCompra(), fontNormal));
            document.add(new Paragraph("Fecha: " + pago.getFechaHora().toLocalDate().toString() + "\n\n", fontNormal));

            // Detalle (Si tuviéramos items guardados, iteraríamos aquí)
            document.add(new Paragraph("----------------------------------", fontNormal));
            document.add(new Paragraph("Detalle de la compra:", fontNormal));
            if(pago.getItems() != null) {
                for (PagoItem item : pago.getItems()) {
                    document.add(new Paragraph(item.getCantidad() + "x Art. " + item.getProductoId() + "  $" + item.getPrecioUnitario(), fontNormal));
                }
            }
            document.add(new Paragraph("----------------------------------\n", fontNormal));

            // Totales
            Paragraph totales = new Paragraph(
                    "Monto Neto: $" + pago.getMontoNeto() + "\n" +
                    "IVA (19%): $" + pago.getIva() + "\n" +
                    "TOTAL: $" + pago.getMontoTotal(), fontTitulo);
            totales.setAlignment(Element.ALIGN_RIGHT);
            document.add(totales);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}

