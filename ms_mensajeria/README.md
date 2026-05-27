# Microservicio de Mensajería - SmartLogix

## ¿Qué hace?
Es el sistema responsable de las comunicaciones de salida hacia los clientes. Su función principal actual es enviar correos electrónicos de confirmación de compra, adjuntando dinámicamente los documentos tributarios (PDFs) correspondientes y registrando evidencia del envío en la base de datos.

## ¿Cómo funciona?
Es un servicio "Listener" (Consumidor). Está constantemente escuchando una cola específica en RabbitMQ. Cuando recibe un evento de venta aprobada, utiliza el patrón **Strategy/Factory** para decidir si debe usar la plantilla HTML de Boleta o Factura (renderizadas con **Thymeleaf**). Utiliza el protocolo SMTP (Gmail) para el despacho final.

## Integración
* **Escucha a:** RabbitMQ (cola `smartlogix.mensajeria.queue`), recibiendo eventos emitidos por `ms_pagos`.

