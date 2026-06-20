# 📦 ms_envios - Servicio de Logística y Despachos

## Descripción
`ms_envios` es el microservicio responsable de gestionar la logística de los pedidos dentro de la arquitectura de SmartLogix. Funciona de manera completamente desacoplada y asíncrona, reaccionando a los eventos de pago para coordinar la preparación, asignar rutas y priorizar las entregas.

## 🚀 Stack Tecnológico
* **Java 25**
* **Spring Boot**
* **RabbitMQ** (Arquitectura Orientada a Eventos AMQP)
* **Docker** (Contenerización)

## ⚙️ Flujo de Operación
Este microservicio no recibe tráfico HTTP directo del BFF. Su operación se basa en la escucha activa de colas:

1. **Suscripción:** Escucha la cola designada en el Exchange de RabbitMQ.
2. **Captura de Evento:** Intercepta los mensajes de `PagoConfirmadoDTO` emitidos por `ms_pagos`.
3. **Procesamiento de Reglas:** * Valida que el pago haya sido exitoso (`"exito": true`).
   * Evalúa la ruta hacia la dirección de destino.
   * Verifica el indicador `requiereExpress` para asignar prioridad inmediata al despacho logístico.

## 📋 Ejemplo de Payload Consumido
```json
{
  "pedidoId": 1050,
  "estado": "APROBADO",
  "direccionDestino": "Av. Los Carrera 123, Concepción",
  "requiereExpress": true,
  "mensaje": "Pago aprobado - VERSION NUEVA",
  "folioSii": "F-32754"
}

