# BFF Web (Backend For Frontend) - SmartLogix

# ¿Qué hace?
Este microservicio actúa como la única puerta de entrada (API Gateway) para las aplicaciones cliente (Frontend). Su objetivo es recibir las peticiones del usuario, orquestar llamadas a los distintos microservicios internos y devolver una respuesta consolidada y limpia al frontend (Patrón DTO).

# ¿Cómo funciona?
Está construido con Spring Boot y expone endpoints REST. Utiliza **OpenFeign** para realizar peticiones HTTP de forma declarativa hacia los demás microservicios. Implementa manejo de excepciones (try-catch) para gestionar errores comunes como el 404 (No encontrado) y evitar que la aplicación cliente se rompa si un servicio interno falla.

# 🌐 bff_web - Backend for Frontend

## Descripción
El `bff_web` actúa como el "Director de Orquesta" y el único punto de entrada público (API Gateway simplificado) para la plataforma SmartLogix. Su responsabilidad es recibir las peticiones de los clientes (Frontend, Aplicaciones Móviles o Postman), orquestar la comunicación con los microservicios internos en la red privada y devolver una respuesta consolidada y limpia.

## 🚀 Stack Tecnológico
* **Java 25**
* **Spring Boot**
* **Spring Cloud OpenFeign** (Cliente HTTP declarativo)
* **Lombok**

## ⚙️ Rutas y Enrutamiento (Puerto 8080)
El BFF aísla los puertos internos de la arquitectura y expone una API unificada bajo el prefijo `/api/v1/`:

* **Identidad (`/api/v1/auth` -> `ms_idp`):** * `[POST] /register`: Registro de nuevas credenciales.
  * `[PUT] /cambiar-password`: Actualización segura de contraseñas.
  * `[POST] /recuperar-password`: Generación de claves temporales.
* **Perfiles (`/api/v1/perfil` -> `ms_usuario`):** * `[PUT] /completar/{authId}`: Relleno de datos personales.
  * `[POST] /{usuarioId}/direcciones`: Gestión de libreta de direcciones.
* **Catálogo (`/api/v1/detalle-producto` -> `ms_catalogo`):** * `[GET] /{id}`: Obtención de bandeja consolidada de productos.
* **Pagos (`/api/v1/pagos` -> `ms_pagos`):** * `[POST] /procesar`: Gestión de transacciones.
* **Mensajería (`/api/v1/mensajeria` -> `ms_mensajeria`):** * `[POST] /enviar`: Disparo de notificaciones a clientes.

## 🛡️ Manejo de Excepciones y Resiliencia
Implementa interceptores a través de `try-catch` y `FeignException` para capturar los errores internos de los microservicios (como correos duplicados o productos no encontrados). Esto previene que una falla en un microservicio se convierta en un `Error 500` genérico, retornando en su lugar códigos HTTP (400, 404) legibles y seguros para el cliente.

---
*Desarrollado bajo principios de alta cohesión, bajo acoplamiento y tipado estricto.* **>j<**

