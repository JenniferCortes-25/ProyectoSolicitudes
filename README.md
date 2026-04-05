# Sistema de Triage y Gestión de Solicitudes Académicas

> Programación Avanzada — Universidad del Quindío

---

## Integrantes

- Sara Valentina Acosta
- Jennifer Andréa Cortés
- Sara María Echeverri

---

## Descripción

El Programa de Ingeniería de Sistemas y Computación cuenta con más de 1.400 estudiantes, docentes y administrativos que realizan solicitudes académicas a través de múltiples canales como atención presencial, correo electrónico y sistemas académicos.

El sistema centraliza y gestiona estas solicitudes de forma estructurada, permitiendo clasificarlas, priorizarlas, asignar responsables y mantener un historial auditable de cada trámite, garantizando trazabilidad y atención oportuna.

---

## Compilar y ejecutar las pruebas

### Clonar el repositorio

```bash
git clone https://github.com/JenniferCortes-25/ProyectoSolicitudes.git
cd ProyectoSolicitudes
```

### Ejecutar las pruebas

En IntelliJ haz clic derecho sobre la clase `SolicitudTest` y selecciona **Run 'SolicitudTest'**.

### Ejecutar la aplicación

Abre `ProyectoSolicitudesApplication.java` y haz clic en el botón ▶ junto al método `main`.

---

## API REST

Con la aplicación corriendo, acceder a la documentación interactiva en:

```
http://localhost:8080/swagger-ui.html
```

La especificación OpenAPI en formato JSON está disponible en:

```
http://localhost:8080/api-docs
```

### Endpoints principales

| Método | Endpoint | Descripción | Rol requerido |
|--------|----------|-------------|---------------|
| `POST` | `/api/solicitudes` | Registrar una nueva solicitud | Cualquier usuario activo |
| `GET` | `/api/solicitudes` | Listar solicitudes (filtro por estado opcional) | — |
| `GET` | `/api/solicitudes/{id}` | Ver detalle de una solicitud | — |
| `PUT` | `/api/solicitudes/{id}/clasificar` | Clasificar: asignar tipo y prioridad | COORDINADOR |
| `PUT` | `/api/solicitudes/{id}/asignar` | Asignar responsable | COORDINADOR |
| `PUT` | `/api/solicitudes/{id}/cerrar` | Cerrar una solicitud | COORDINADOR |
| `GET` | `/api/solicitudes/{id}/historial` | Ver historial de acciones | — |

### Ciclo de vida de una solicitud

```
REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
```

### Ejemplo de uso — Registrar una solicitud

**Request:**
```http
POST /api/solicitudes
Content-Type: application/json

{
  "descripcion": "Solicito homologación de la asignatura Programación I cursada en otra universidad",
  "canalOrigen": "CORREO_ELECTRONICO",
  "solicitanteId": "E-001"
}
```

**Response `201 Created`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "descripcion": "Solicito homologación de la asignatura Programación I cursada en otra universidad",
  "canalOrigen": "CORREO_ELECTRONICO",
  "estado": "REGISTRADA",
  "tipo": null,
  "nivelPrioridad": null,
  "justificacionPrioridad": null,
  "solicitanteId": "E-001",
  "solicitanteNombre": null,
  "responsableId": null,
  "responsableNombre": null,
  "fechaRegistro": "2025-04-05T10:30:00",
  "totalEntradaHistorial": 1
}
```

### Códigos de respuesta

| Código | Significado | Cuándo ocurre |
|--------|-------------|---------------|
| `201` | Creado | Solicitud registrada exitosamente |
| `200` | OK | Consulta o operación exitosa |
| `400` | Bad Request | Datos inválidos o transición de estado incorrecta |
| `403` | Forbidden | El usuario no tiene el rol requerido |
| `404` | Not Found | Solicitud o usuario no encontrado |
| `500` | Internal Server Error | Error inesperado del servidor |
