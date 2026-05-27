# Microservicio de Pagos - SmartLogix

## ¿Qué hace?
Centraliza y procesa todas las transacciones financieras del eCommerce. Se encarga de interactuar con pasarelas de pago externas (Webpay, MercadoPago, Khipu), gestionar la emisión de documentos tributarios (Boletas/Facturas) ante el SII y notificar al sistema cuando una venta ha sido concretada exitosamente.

## ¿Cómo funciona?
Utiliza Spring Boot y MySQL. Implementa el patrón **Circuit Breaker** (con Resilience4j) para proteger el sistema si una pasarela externa se cae, asegurando alta disponibilidad. Usa el patrón **Factory** para generar dinámicamente PDFs de boletas o facturas según corresponda. Al confirmar un pago, publica un evento estructurado hacia un servidor de mensajería asíncrona.

## Integración
* **Envía mensajes a:** RabbitMQ (Exchanges y Colas) para notificar a `ms_mensajeria` y `ms_bodega` sobre ventas aprobadas.
* **Servicios Externos:** Transbank (Webpay), MercadoPago, Khipu, SII y Slack (vía Webhook para reporte de fallos).

