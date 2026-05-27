package com.jaba.ms_pagos.service.pdf;

import com.jaba.ms_pagos.model.Pago;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Component("FACTURA")
public class GeneradorFactura implements GeneradorPdf {

    @Override
    public byte[] generarDocumento(Pago pago) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Formato formal Carta
        Document document = new Document(PageSize.LETTER, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontRoja = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.RED);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Estructura de tabla principal (2 columnas: Izquierda logo/empresa, Derecha recuadro rojo SII)
            PdfPTable tablaPrincipal = new PdfPTable(2);
            tablaPrincipal.setWidthPercentage(100);
            tablaPrincipal.setWidths(new float[]{60f, 40f});

            // Lado Izquierdo
            PdfPCell celdaIzquierda = new PdfPCell(new Phrase("Smart Logix\nGiro: Venta de Insumos Informáticos\nConcepción, Bio Bío", fontNormal));
            celdaIzquierda.setBorder(Rectangle.NO_BORDER);
            tablaPrincipal.addCell(celdaIzquierda);

            // Lado Derecho: EL RECUADRO ROJO
            PdfPTable tablaRecuadro = new PdfPTable(1);
            PdfPCell celdaRecuadro = new PdfPCell();
            celdaRecuadro.setBorderColor(Color.RED);
            celdaRecuadro.setBorderWidth(2f);
            celdaRecuadro.setPadding(10f);
            
            Paragraph pRecuadro = new Paragraph("R.U.T.: 76.543.210-K\nFACTURA ELECTRÓNICA\nN° " + pago.getFolioSii(), fontRoja);
            pRecuadro.setAlignment(Element.ALIGN_CENTER);
            celdaRecuadro.addElement(pRecuadro);
            
            tablaRecuadro.addCell(celdaRecuadro);
            
            PdfPCell celdaContenedoraDer = new PdfPCell(tablaRecuadro);
            celdaContenedoraDer.setBorder(Rectangle.NO_BORDER);
            tablaPrincipal.addCell(celdaContenedoraDer);

            document.add(tablaPrincipal);
            document.add(new Paragraph("\n"));

            // Datos del Cliente (En la vida real estos vendrían del Usuario)
            document.add(new Paragraph("Cliente ID: " + pago.getUsuarioId(), fontNormal));
            document.add(new Paragraph("Orden de Compra: " + pago.getOrdenCompra(), fontNormal));
            document.add(new Paragraph("Fecha: " + pago.getFechaHora().toLocalDate().toString() + "\n\n", fontNormal));

            // Totales
            Paragraph totales = new Paragraph(
                    "Monto Neto: $" + pago.getMontoNeto() + "\n" +
                    "IVA (19%): $" + pago.getIva() + "\n" +
                    "TOTAL: $" + pago.getMontoTotal(), fontNormal);
            totales.setAlignment(Element.ALIGN_RIGHT);
            document.add(totales);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}

