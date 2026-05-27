# BFF Web (Backend For Frontend) - SmartLogix

# ¿Qué hace?
Este microservicio actúa como la única puerta de entrada (API Gateway) para las aplicaciones cliente (Frontend). Su objetivo es recibir las peticiones del usuario, orquestar llamadas a los distintos microservicios internos y devolver una respuesta consolidada y limpia al frontend (Patrón DTO).

# ¿Cómo funciona?
Está construido con Spring Boot y expone endpoints REST. Utiliza **OpenFeign** para realizar peticiones HTTP de forma declarativa hacia los demás microservicios. Implementa manejo de excepciones (try-catch) para gestionar errores comunes como el 404 (No encontrado) y evitar que la aplicación cliente se rompa si un servicio interno falla.

# Integración
* **Se comunica con:** `ms_catalogo` (vía Feign Client) para obtener el detalle de los productos.
* **Futuras integraciones:** Actuará como puente hacia `ms_pagos` para iniciar transacciones.

