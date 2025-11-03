# Seguridad y Calidad en el Desarrollo de Software

# 🧩 Configuración de Wallet y Conexión Oracle Cloud

## 0. Configuración de Wallet

Cada desarrollador debe crear un archivo `.env` en la raíz del proyecto (no versionado en Git) y definir en él la variable `DB_URL` con la ruta local a su carpeta **Wallet_VPI3OXVG2QH7QK56**.  
Esta ruta puede variar según el sistema operativo o el entorno local de desarrollo.

### Ejemplo

**macOS / Linux**
```bash
DB_URL=jdbc:oracle:thin:@mydb_high?TNS_ADMIN=/Users/<usuario>/Wallet_VPI3OXVG2QH7QK56
DB_USER=<tu_usuario>
DB_PASSWORD=<tu_password>
```

**Windows**
```bash
DB_URL=jdbc:oracle:thin:@mydb_high?TNS_ADMIN=C:\Users\<usuario>\Wallet_VPI3OXVG2QH7QK56
DB_USER=<tu_usuario>
DB_PASSWORD=<tu_password>
```

> ⚠️ **Nota:** El archivo `.env` no debe ser subido al repositorio.  
> Asegúrate de que `.env` esté incluido en `.gitignore`.

---

## ⚙️ 1. Configuración de conexión en Spring Boot

El archivo `application.properties` debe incluir la siguiente línea para importar el archivo `.env` local:

```properties
spring.config.import=optional:file:.env[.properties]
```

Esto permite que Spring Boot cargue automáticamente las variables definidas en el archivo `.env`
(como `DB_URL`, `DB_USER`, `DB_PASSWORD`, etc.) **sin requerir dependencias adicionales**.

### Ejemplo completo:

```properties
spring.config.import=optional:file:.env[.properties]

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

---

## 🐳 2. Uso en Docker

En el entorno Docker, la **wallet se incluye directamente en la imagen**, por lo que la configuración
de `TNS_ADMIN` no requiere modificación.  
El archivo `.env` local se utiliza solo para entornos de desarrollo.

---

## ✅ 3. Buenas prácticas

- No subir la wallet ni credenciales al repositorio.
- Mantener `.env` fuera de control de versiones.
- Verificar que la ruta de la wallet sea válida antes de ejecutar `./mvnw spring-boot:run`.



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