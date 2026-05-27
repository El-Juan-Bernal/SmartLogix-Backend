# Microservicio de Catálogo - SmartLogix

## ¿Qué hace?
Es el encargado de gestionar todo el inventario y la vitrina virtual de SmartLogix. Permite consultar productos disponibles, filtrar por categorías y realizar búsquedas por nombre, además de proveer operaciones administrativas (CRUD) para mantener el catálogo actualizado.

## ¿Cómo funciona?
Desarrollado en Spring Boot utilizando Spring Data JPA para la persistencia de datos. Se conecta a una base de datos **MySQL** (`smartlogix_catalogo`). Utiliza validaciones con `jakarta.validation` para asegurar la integridad de los datos al crear o editar productos. Cuenta con un sistema de borrado lógico (campo `activo`) para ocultar productos sin perder su historial.

## Integración
* **Recibe peticiones de:** `ms_bff` (Backend For Frontend) para mostrar el detalle de los productos a los clientes.
