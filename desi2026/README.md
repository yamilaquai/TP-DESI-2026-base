# TP Integrador DESI 2026 — Sistema de Gestión Inmobiliaria

## Estructura del proyecto

```
src/main/java/tuti/desi/
├── Desi2026Application.java          ← Clase principal Spring Boot
├── entidades/
│   ├── Ciudad.java
│   ├── Contrato.java
│   ├── Factura.java
│   ├── Incidente.java
│   ├── Persona.java
│   ├── Propiedad.java
│   ├── Provincia.java
│   ├── Publicacion.java              ← Epic 2
│   └── Visita.java
├── enums/
│   ├── CategoriaIncidente.java
│   ├── EstadoContrato.java
│   ├── EstadoDisponibilidad.java
│   ├── EstadoFactura.java
│   ├── EstadoIncidente.java
│   ├── EstadoPublicacion.java        ← Epic 2
│   ├── EstadoVisita.java
│   ├── MedioPago.java
│   ├── Prioridad.java
│   └── TipoPropiedad.java
├── historial/
│   ├── HistorialEstadoContrato.java
│   └── HistorialEstadoPublicacion.java ← Epic 2
├── persistencia/
│   ├── contratoPersistencia.java
│   ├── personaPersistencia.java
│   ├── propiedadPersistencia.java
│   └── publicacionPersistencia.java  ← Epic 2
├── presentacion/
│   ├── ContratoController.java       ← Epic 3
│   └── PublicacionController.java    ← Epic 2
└── servicios/
    ├── contratoServicios.java        ← Epic 3
    └── publicacionServicios.java     ← Epic 2

src/main/resources/
├── application.properties
├── static/css/estilos.css
└── templates/
    ├── index.html
    ├── contratos/
    │   ├── cargarContrato.html       ← Alta y Edición (HU 3.1, 3.3)
    │   └── listado.html              ← Listado (HU 3.4)
    └── publicaciones/
        ├── formulario.html           ← Alta y Edición (HU 2.1, 2.3)
        └── listado.html              ← Listado (HU 2.4)
```

## Pasos para correr el proyecto

### 1. Crear la base de datos MySQL
```sql
CREATE DATABASE desi2026 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar application.properties
Ajustar usuario y contraseña según el entorno local:
```properties
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Arrancar el proyecto en Eclipse STS
- Click derecho sobre el proyecto → Run As → Spring Boot App
- Hibernate creará las tablas automáticamente (ddl-auto=update)

### 4. Cargar datos de prueba
Ejecutar `init_db.sql` en MySQL Workbench o consola.

### 5. Acceder a la aplicación
- Menú principal: http://localhost:8080
- Publicaciones:  http://localhost:8080/publicaciones
- Contratos:      http://localhost:8080/contratos/listado

## Epics implementados

| Epic | Módulo        | HU implementadas |
|------|---------------|-----------------|
| 2    | Publicaciones | 2.1 Alta, 2.2 Eliminación, 2.3 Modificación, 2.4 Listado |
| 3    | Contratos     | 3.1 Alta, 3.2 Eliminación, 3.3 Modificación, 3.4 Listado |

## Correcciones aplicadas al código original

| Archivo             | Problema original               | Corrección                        |
|---------------------|---------------------------------|-----------------------------------|
| `pom.xml`           | Spring Boot 4.0.6 (inexistente) | Actualizado a 3.3.2               |
| `pom.xml`           | `spring-boot-starter-webmvc`    | Reemplazado por `spring-boot-starter-web` |
| `Ciudad.java`       | `serProvincia()` (typo)         | Renombrado a `setProvincia()`     |
| `Persona.java`      | `getApellid()` (typo)           | Renombrado a `getApellido()`      |
| `Persona.java`      | Sin campo `eliminado`           | Agregado para eliminación lógica  |
| `Propiedad.java`    | Sin campo `ciudad` ni `propietario` | Agregados (requeridos por HU 1.1 y listados) |
| `Publicacion.java`  | Sin relación a `Propiedad`      | Agregado `@ManyToOne propiedad`   |
| `HistorialEstadoPublicacion.java` | Sin FK a `Publicacion` | Agregado `@ManyToOne publicacion` |
| `contratoServicios.java` | Sin validación de transiciones de estado | Agregadas transiciones correctas |
| `personaPersistencia.java` | Solo `buscarTodasPersonas()` | Agregado `buscarNoEliminadas()`   |
| `propiedadPersistencia.java` | Solo `buscarTodasActivas()` | Agregado `buscarDisponibles()`    |
