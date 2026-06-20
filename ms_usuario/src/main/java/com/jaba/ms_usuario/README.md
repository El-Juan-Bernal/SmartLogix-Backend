# 👥 ms_usuario - Servicio de Gestión de Perfiles y Direcciones

## Descripción
`ms_usuario` es el microservicio responsable de administrar los datos personales, perfiles e información de despacho (libreta de direcciones) de los clientes dentro de la plataforma de SmartLogix. Opera de manera reactiva ante las acciones de autenticación para asegurar la disponibilidad inmediata del espacio del usuario.

## 🚀 Stack Tecnológico
* **Java 25**
* **Spring Boot**
* **Spring Data JPA**
* **RabbitMQ** (Consumo de eventos AMQP)
* **MySQL** (Base de datos aislada `smartlogix_usuarios`)

## ⚙️ Flujo de Operación
El ciclo de vida del perfil de un usuario se divide en dos fases sincronizadas:

1. **Creación Asíncrona (Segundo Plano):** El servicio escucha activamente la cola `usuario.creado.queue`. Al capturar un evento `UsuarioRegistradoEvent` emitido por el IdP, el listener inicializa automáticamente un registro base ("cascarón vacío") en la base de datos vinculando el identificador único `authId`.
2. **Actualización Síncrona (HTTP):** Cuando el cliente inicia sesión por primera vez y completa su formulario de datos desde el frontend, el BFF envía una petición PUT hacia `/api/usuarios/completar/{authId}`, donde se validan y asocian las propiedades personales definitivas.

## 📋 Contratos de Datos

**Entrada RabbitMQ (UsuarioRegistradoEvent):**
```json
{
  "idAuth": 1,
  "email": "jusuario@correo.com",
  "username": "jusuario123"
}

