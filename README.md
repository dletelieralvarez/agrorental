# Seguridad y Calidad en el Desarrollo de Software

## 1. Requisitos del Frontend

El frontend de la aplicación debe cumplir con los siguientes aspectos:

- Desarrollado utilizando **Spring Framework**, específicamente:
  - **Spring Boot**
  - **Spring Security**
  - **Thymeleaf** para la generación dinámica de vistas.
- Implementación de **protección de URLs**:
  - Las páginas definidas como públicas deben ser accesibles sin autenticación.
  - Todas las demás páginas deben estar protegidas y requerir autenticación.
- Debe existir una **página de Login**.
  - El sistema contará con al menos **tres usuarios** registrados para autenticarse y acceder a las secciones privadas.
- Todas las páginas deberán contar con **estilos CSS**, permitiendo un diseño visual funcional y agradable.

## 2. Funcionalidades

La aplicación contará con funcionalidades **públicas** y **privadas**, descritas a continuación:

### 2.1 Funcionalidades Públicas

- **Página de Inicio:**
  - Debe mostrar información relacionada con la agricultura, incluyendo:
    - Eventos
    - Fechas importantes de cosecha
    - Información gubernamental
    - Organizaciones agrícolas
    - Publicidad de empresas
    - Avisos destacados de arriendo de maquinaria.

- **Registro de Usuarios:**
  - Los usuarios podrán registrarse proporcionando:
    - Nombre
    - Correo electrónico
    - Contraseña

- **Búsqueda de Maquinaria Disponible:**
  - Se debe permitir realizar búsquedas por:
    - Tipo de maquinaria
    - Ubicación
    - Disponibilidad por fecha
    - Precio

### 2.2 Funcionalidades Privadas (Requieren Autenticación)

- **Perfil de Usuario:**
  - Los usuarios registrados podrán completar y editar información como:
    - Dirección
    - Teléfono
    - Cultivos

- **Publicación de Avisos de Arriendo de Maquinaria:**
  - Los usuarios podrán agregar y gestionar publicaciones de maquinaria disponible.

- **Reserva de Maquinaria Disponible:**
  - Los usuarios podrán reservar maquinaria que se encuentre publicada.

- **Visualización de Detalle de Maquinaria:**
  - Acceso a información como:
    - Marca
    - Año de fabricación
    - Capacidad
    - Historial de mantenciones
    - Condiciones de arriendo
    - Métodos de pago disponibles.

---

Este documento establece la base de los requisitos de seguridad, estructura y funcionalidad del proyecto que se implementará.

Grupo 7
Danitza Romina Letelier Alvarez
Mauricio Tapia Ortega