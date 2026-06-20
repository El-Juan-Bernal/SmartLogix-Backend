# 🛡️ ms_idp - Identity Provider (Proveedor de Identidad)

## Descripción
`ms_idp` es el microservicio encargado de gestionar la bóveda de credenciales dentro de la arquitectura de SmartLogix. Actúa como la primera barrera de entrada, garantizando el registro seguro mediante encriptación, delegando la creación asíncrona del perfil y administrando el ciclo de vida de las contraseñas (recuperación y cambio).

## 🚀 Stack Tecnológico
* **Java 25**
* **Spring Boot**
* **Spring Security** (Motor de encriptación BCrypt)
* **RabbitMQ** (Emisión de eventos AMQP)
* **MySQL** (Base de datos aislada `smartlogix_idp`)

## ⚙️ Flujo de Operación
Este microservicio procesa peticiones HTTP para el manejo de credenciales y se comunica asíncronamente con el resto del sistema:

1. **Registro (`/api/auth/register`):** Valida disponibilidad del correo, encripta la contraseña usando `BCryptPasswordEncoder`, persiste la credencial y dispara el evento `UsuarioRegistradoEvent` hacia RabbitMQ.
2. **Cambio de Clave (`/api/auth/cambiar-password`):** Verifica que la contraseña actual coincida con el hash de la base de datos antes de permitir y encriptar la nueva.
3. **Recuperación (`/api/auth/recuperar-password`):** Genera una clave temporal aleatoria, la encripta, la guarda y la retorna para restablecer el acceso del usuario.

## 📋 Contratos de Datos (DTOs)

**Entrada HTTP (RegistroUsuarioDTO):**
```json
{
  "username": "jusuario123",
  "email": "jusuario@correo.com",
  "password": "MiPasswordSeguro"
}

**Entrada HTTP (CambiarPasswordDTO):**
{
  "email": "jusuario@correo.com",
  "passwordActual": "MiPasswordSeguro",
  "passwordNueva": "NuevaPassword2026"
}

**Salida RabbitMQ (UsuarioRegistradoEvent):**
{
  "idAuth": 1,
  "email": "jusuario@correo.com",
  "username": "jusuario123"
}

## Desarrollado bajo principios de alta cohesión, bajo acoplamiento y tipado estricto. >j<

