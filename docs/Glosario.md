# Glosario del Dominio
## Sistema de Triage y Gestión de Solicitudes Académicas

> Este glosario define el **lenguaje ubicuo** del dominio — los términos que
> usan por igual el código, la documentación y el equipo de desarrollo.
> Toda clase, método y variable del proyecto debe usar exactamente estos nombres.

---

## Entidades

| Término | Clase Java | Paquete | Descripción |
|---------|-----------|---------|-------------|
| **Solicitud** | `Solicitud` | `domain.entity` | Agregado raíz del sistema. Representa un trámite académico iniciado por un estudiante o administrativo. Tiene un ciclo de vida definido y un historial auditable de todas las acciones realizadas sobre ella. |
| **Usuario** | `Usuario` | `domain.entity` | Actor del sistema que puede registrar solicitudes, clasificarlas, atenderlas o cerrarlas según su rol. Puede estar activo o inactivo. |

---

## Value Objects

| Término | Clase Java | Paquete | Descripción |
|---------|-----------|---------|-------------|
| **DescripcionSolicitud** | `DescripcionSolicitud` | `domain.valueobject.solicitud` | Texto libre que describe el motivo de una solicitud. Debe tener entre 10 y 1000 caracteres (RN-06). Es inmutable. |
| **Prioridad** | `Prioridad` | `domain.valueobject.solicitud` | Encapsula el nivel de urgencia y su justificación obligatoria. La justificación debe tener mínimo 5 caracteres (RN-07). Es inmutable. |
| **Responsable** | `Responsable` | `domain.valueobject.solicitud` | Referencia al usuario asignado para atender una solicitud. Guarda el ID y nombre del usuario en el momento de la asignación para evitar acoplamiento entre agregados. |
| **ObservacionCierre** | `ObservacionCierre` | `domain.valueobject.solicitud` | Texto obligatorio que se registra al cerrar una solicitud. Debe tener mínimo 20 caracteres (RN-08). Es inmutable. |
| **EntradaHistorial** | `EntradaHistorial` | `domain.valueobject.solicitud` | Registro inmutable de una acción realizada sobre una solicitud. Incluye fecha, descripción de la acción, usuario responsable y observaciones. Nunca se construye manualmente desde fuera del agregado. |
| **Email** | `Email` | `domain.valueobject.usuario` | Correo electrónico institucional de un usuario. Valida el formato en el constructor (RN-09). Es inmutable. |

---

## Enumeraciones

| Término | Clase Java | Paquete | Valores | Descripción |
|---------|-----------|---------|---------|-------------|
| **EstadoSolicitud** | `EstadoSolicitud` | `domain.valueobject.solicitud` | `REGISTRADA`, `CLASIFICADA`, `EN_ATENCION`, `ATENDIDA`, `CERRADA` | Representa el ciclo de vida completo de una solicitud. Solo son válidas las transiciones definidas en `esTransicionValida()` (RN-02). |
| **TipoSolicitud** | `TipoSolicitud` | `domain.valueobject.solicitud` | `HOMOLOGACION`, `REGISTRO_ASIGNATURA`, `CANCELACION_ASIGNATURA`, `SOLICITUD_CUPO`, `CONSULTA_ACADEMICA`, `OTRO` | Categoría de la solicitud académica. Se asigna durante la clasificación (RF-02). |
| **NivelPrioridad** | `NivelPrioridad` | `domain.valueobject.solicitud` | `CRITICA`, `ALTA`, `MEDIA`, `BAJA` | Nivel de urgencia de una solicitud. Forma parte del Value Object `Prioridad` (RN-07). |
| **CanalOrigen** | `CanalOrigen` | `domain.valueobject.solicitud` | `PRESENCIAL`, `CORREO_ELECTRONICO`, `SAC`, `TELEFONICO`, `CSU` | Canal por el cual fue recibida la solicitud. Es obligatorio al registrar (RF-01). |
| **TipoUsuario** | `TipoUsuario` | `domain.valueobject.usuario` | `ESTUDIANTE`, `DOCENTE`, `COORDINADOR`, `ADMINISTRATIVO` | Rol del usuario en el sistema. Determina qué operaciones puede realizar (RN-13). |
| **EstadoUsuario** | `EstadoUsuario` | `domain.valueobject.usuario` | `ACTIVO`, `INACTIVO` | Indica si un usuario está habilitado para operar en el sistema (RN-04, RN-10). |

---

## Excepciones del Dominio

| Término | Clase Java | Código HTTP | Descripción |
|---------|-----------|------------|-------------|
| **TransicionInvalidaException** | `TransicionInvalidaException` | 400 | Se lanza cuando se intenta una transición de estado no permitida por el ciclo de vida (RN-02). |
| **SolicitudCerradaException** | `SolicitudCerradaException` | 400 | Se lanza cuando se intenta modificar una solicitud en estado `CERRADA`. Una solicitud cerrada es inmutable (RN-01). |
| **PermisoInsuficienteException** | `PermisoInsuficienteException` | 403 | Se lanza cuando un usuario intenta realizar una operación para la cual no tiene el rol requerido (RN-13). |
| **UsuarioInactivoException** | `UsuarioInactivoException` | 400 | Se lanza cuando se intenta registrar una solicitud con un solicitante inactivo, o asignar como responsable a un usuario inactivo (RN-04, RN-10). |
| **SinResponsableException** | `SinResponsableException` | 400 | Se lanza cuando se intenta iniciar la atención de una solicitud sin tener un responsable asignado (RN-05). |
| **DescripcionInvalidaException** | `DescripcionInvalidaException` | 400 | Se lanza cuando la descripción de una solicitud no cumple el rango de 10 a 1000 caracteres (RN-06). |
| **ObservacionInvalidaException** | `ObservacionInvalidaException` | 400 | Se lanza cuando la observación de cierre tiene menos de 20 caracteres (RN-08). |
| **PrioridadSinJustificacionException** | `PrioridadSinJustificacionException` | 400 | Se lanza cuando se crea una prioridad sin justificación o con menos de 5 caracteres (RN-07). |
| **EmailInvalidoException** | `EmailInvalidoException` | 400 | Se lanza cuando el correo electrónico no tiene un formato válido (RN-09). |
| **UsuarioNoEncontradoException** | `UsuarioNoEncontradoException` | 404 | Se lanza cuando se busca un usuario por ID y no existe en el sistema. |

---

## Servicios de Dominio

| Término | Clase Java | Paquete | Descripción |
|---------|-----------|---------|-------------|
| **RegistrarSolicitudService** | `RegistrarSolicitudService` | `domain.service` | Valida que el solicitante esté activo antes de crear la solicitud. Coordina lo que `Solicitud` no puede hacer sola porque necesita verificar la existencia del usuario. |
| **ClasificarSolicitudService** | `ClasificarSolicitudService` | `domain.service` | Orquesta la clasificación completa: llama a `Solicitud.clasificar()` y `Solicitud.asignarPrioridad()` en una sola operación. |
| **AsignarResponsableService** | `AsignarResponsableService` | `domain.service` | Verifica que el responsable esté activo antes de construir el Value Object `Responsable` y llamar a `Solicitud.asignarResponsable()`. |

---

## Ciclo de Vida de la Solicitud

```
REGISTRADA ──→ CLASIFICADA ──→ EN_ATENCION ──→ ATENDIDA ──→ CERRADA
    │                                                            │
    └── Estado inicial al crear                                  └── Estado final, inmutable
```

| Transición | Método del dominio | Rol requerido |
|------------|--------------------|---------------|
| `REGISTRADA → CLASIFICADA` | `Solicitud.clasificar()` + `Solicitud.asignarPrioridad()` | COORDINADOR |
| `CLASIFICADA → EN_ATENCION` | `Solicitud.iniciarAtencion()` | COORDINADOR o ADMINISTRATIVO |
| `EN_ATENCION → ATENDIDA` | `Solicitud.atender()` | El responsable asignado |
| `ATENDIDA → CERRADA` | `Solicitud.cerrar()` | COORDINADOR |

---

## Reglas de Negocio Referenciadas

| Código | Descripción resumida |
|--------|----------------------|
| **RN-01** | Una solicitud `CERRADA` es inmutable — ninguna operación puede modificarla. |
| **RN-02** | Solo son válidas las transiciones de estado definidas en `EstadoSolicitud.esTransicionValida()`. |
| **RN-04** | Solo se puede asignar como responsable a un usuario con estado `ACTIVO`. |
| **RN-05** | No se puede iniciar atención sin un responsable asignado previamente. |
| **RN-06** | La descripción debe tener entre 10 y 1000 caracteres. |
| **RN-07** | La prioridad requiere un nivel y una justificación de mínimo 5 caracteres. |
| **RN-08** | La observación de cierre debe tener mínimo 20 caracteres. |
| **RN-09** | El email debe tener formato válido (`usuario@dominio.extension`). |
| **RN-10** | Un usuario inactivo no puede recibir nuevas asignaciones. |
| **RN-13** | Cada operación del dominio verifica que el usuario tenga el `TipoUsuario` requerido. |