# 🛡️ ms_idp - Identity Provider (Proveedor de Identidad)

## Descripción
`ms_idp` es el microservicio encargado de gestionar la bóveda de credenciales dentro de la arquitectura de SmartLogix. Actúa como la primera barrera de entrada para nuevos usuarios, garantizando el registro seguro mediante encriptación y delegando la creación del perfil de manera asíncrona.

## 🚀 Stack Tecnológico
* **Java 25**
* **Spring Boot**
* **Spring Security** (Motor de encriptación BCrypt)
* **RabbitMQ** (Emisión de eventos AMQP)
* **MySQL** (Base de datos aislada `smartlogix_idp`)

## ⚙️ Flujo de Operación
Este microservicio recibe peticiones HTTP para registrar cuentas nuevas y se comunica asíncronamente con el resto del sistema:

1. **Recepción:** Acepta peticiones POST en `/api/auth/register` protegidas por un DTO.
2. **Validación:** Verifica que el correo electrónico no exista previamente en la base de datos.
3. **Seguridad:** Encripta la contraseña en texto plano utilizando `BCryptPasswordEncoder` antes de la persistencia.
4. **Persistencia:** Guarda las credenciales básicas en la tabla `usuarios_auth`.
5. **Emisión de Evento:** Transforma los datos del usuario en un JSON limpio utilizando `Jackson2JsonMessageConverter` y dispara el evento `UsuarioRegistradoEvent` hacia el exchange `usuarios.exchange` en RabbitMQ.

## 📋 Contratos de Datos

**Entrada HTTP (RegistroUsuarioDTO):**
```json
{
  "username": "jusuario123",
  "email": "jusuario@correo.com",
  "password": "MiPasswordSeguro"
}

