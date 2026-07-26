# Estado, arquitectura y avance del proyecto

**Proyecto:** `kahoot-clabs`  
**Última actualización:** 25 de julio de 2026  
**Propósito:** referencia rápida del estado real de la arquitectura, los módulos, los controladores y el avance funcional.

## Resumen de avance

| Área | Estado | Avance | Comentario |
| --- | --- | --- | --- |
| Fundaciones compartidas | Implementado | Alto | Agregados, entidades base, eventos, excepciones y configuración transversal. |
| Identidad | Implementado | Alto | API, casos de uso, persistencia y dominio de usuarios, roles y permisos. |
| Organizaciones | Implementado | Alto | API, casos de uso, persistencia y dominio de tenants y membresías. |
| Quizzes | Parcial | Medio | Dominio, entidades JPA y migración creados; faltan aplicación, adapters y API. |
| Gameplay | Parcial | Medio | Dominio, entidades JPA y migración creados; faltan aplicación, adapters y API. |
| Seguridad | Pendiente | Bajo | BCrypt disponible; no existe autenticación ni autorización aplicada. |
| Calidad automatizada | Bloqueada | Bajo | Existen pruebas de integración, pero fallan al aplicar la migración V4 en H2. |
| CI/CD | Pendiente | Bajo | No hay pipeline de validación visible. |

## Arquitectura general

El proyecto es un monolito modular organizado por bounded contexts. Cada módulo se divide, cuando está implementado, en tres capas:

```text
infrastructure  →  application  →  domain
```

| Capa | Responsabilidad | Reglas |
| --- | --- | --- |
| `domain` | Agregados, entidades, value objects, reglas de negocio, puertos y eventos. | Java puro; no puede depender de Spring, JPA, HTTP ni DTOs. |
| `application` | Casos de uso, comandos, queries y DTOs. | Orquesta el dominio y transacciones; no contiene reglas de negocio ni SQL. |
| `infrastructure` | Controllers, JPA, repositories, adapters, mappers, seguridad y configuración. | Implementa puertos del dominio y adapta sistemas externos. |

### Mapa de bounded contexts

```text
kahoot.clabs.kahoot_clabs
├── shared
├── identity
├── organization
├── quiz
└── gameplay
```

| Contexto | Responsabilidad de negocio | Agregado raíz principal | Estado técnico |
| --- | --- | --- | --- |
| `shared` | Conceptos reutilizables entre contextos. | `AggregateRoot` y entidades base. | Implementado. |
| `identity` | Identidad, usuarios, credenciales, roles y permisos. | `User`, `Role`. | Vertical slice implementado. |
| `organization` | Organizaciones como tenants y membresías. | `Organization`. | Vertical slice implementado. |
| `quiz` | Creación y publicación de contenido de juego. | `Quiz`. | Dominio y JPA listos; sin casos de uso ni REST. |
| `gameplay` | Ejecución de partidas y puntuación. | `GameSession`. | Dominio y JPA listos; sin casos de uso ni REST. |

## Avance por contexto

### Shared

| Componente | Estado | Descripción |
| --- | --- | --- |
| `BaseEntity` | Implementado | Identidad y comparación por id/tipo. |
| `AuditableEntity` | Implementado | Fechas de creación y actualización. |
| `AggregateRoot` | Implementado | Registro y consulta de eventos de dominio. |
| `DomainEvent` | Implementado | Base para eventos ocurridos en el dominio. |
| `DomainException` | Implementado | Excepciones de reglas de negocio. |
| `Identifier` | Parcial | Existe como abstracción, pero aún no se usa como id tipado en los contextos. |
| Configuración JPA | Implementado | El escaneo de entidades y repositorios queda restringido a infraestructura. |
| Manejo global de errores | Implementado | Traduce excepciones a respuestas HTTP; se debe diferenciar mejor 400, 404 y 409. |

### Identity

| Elemento | Estado | Detalle |
| --- | --- | --- |
| Dominio | Implementado | `User`, `Role`, `Permission`, `RolePermission` y value objects como `Email`, `Password` y `FullName`. |
| Reglas principales | Implementado | Cambiar perfil, email, contraseña, rol, avatar y estado; registrar login. |
| Casos de uso | Implementado | Registro, login, consulta de perfil, actualización de perfil, cambio de contraseña y asignación de rol. |
| Persistencia | Implementado | Entidades JPA, Spring Data repositories, adapters y mappers. |
| API REST | Implementado | `AuthController` y `UserController`. |
| Eventos | Parcial | `UserCreatedEvent` se registra, pero aún no se publica. |

### Organization

| Elemento | Estado | Detalle |
| --- | --- | --- |
| Dominio | Implementado | `Organization` y `OrganizationMember` modelan el tenant y sus miembros. |
| Reglas principales | Implementado | Crear, actualizar, activar/suspender, añadir, invitar, cambiar rol y eliminar miembros. |
| Casos de uso | Implementado | Signup, creación, actualización, consulta, alta, invitación y eliminación de miembros. |
| Persistencia | Implementado | Entidades JPA, Spring Data repositories, adapters y mappers. |
| API REST | Implementado | `OrganizationController`. |
| Flujo pendiente | Pendiente | El agregado contiene `acceptInvitation`, pero no hay caso de uso ni endpoint para aceptarla. |

### Quiz

| Elemento | Estado | Detalle |
| --- | --- | --- |
| Dominio | Implementado | `Quiz`, preguntas, opciones, assets y categorías. |
| Reglas principales | Implementado | Agregar preguntas y opciones, validar preparación y publicar el quiz. |
| Value objects | Implementado | Título, puntos, tiempo límite, visibilidad, estado, dificultad y configuración. |
| Eventos | Parcial | `QuizPublishedEvent` se registra, pero no se publica. |
| Esquema de datos | Implementado | V4 crea `quizzes`, `categories`, `quiz_categories`, `questions`, `answer_options` y `question_assets`. |
| Entidades JPA | Implementado | Modelos JPA existentes y registrados en el escaneo de entidades. |
| Repositorios/adapters/mappers | Pendiente | No hay capa de persistencia conectada al dominio. |
| Casos de uso y API | Pendiente | No hay controller ni endpoints de quiz. |

### Gameplay

| Elemento | Estado | Detalle |
| --- | --- | --- |
| Dominio | Implementado | `GameSession`, jugadores, preguntas de sesión, respuestas y ranking. |
| Reglas principales | Implementado | Máquina de estados, inicio de sesión y cálculo de puntajes. |
| Snapshot de preguntas | Implementado en dominio | `SessionQuestion` conserva puntos y tiempo para proteger el histórico de la partida. |
| Eventos | Parcial | `GameStartedEvent` se registra, pero no se publica. |
| Esquema de datos | Implementado | V4 crea sesiones, jugadores, preguntas de sesión, respuestas y leaderboard. |
| Entidades JPA | Implementado | Modelos JPA existentes y registrados en el escaneo de entidades. |
| Repositorios/adapters/mappers | Pendiente | No hay persistencia conectada al dominio. |
| Casos de uso, API y tiempo real | Pendiente | No hay controller, WebSocket ni SSE. |

## Controladores y endpoints

### `AuthController`

**Ruta base:** `/api/v1/auth`  
**Contexto:** `identity`

| Método | Ruta | Caso de uso | Respuesta | Estado |
| --- | --- | --- | --- | --- |
| `POST` | `/register` | `RegisterUserUseCase` | `AuthUserResponse` | Implementado |
| `POST` | `/login` | `LoginUserUseCase` | `AuthUserResponse` | Implementado |

El login valida credenciales y devuelve información del usuario, pero no entrega JWT ni crea sesión autenticada.

### `UserController`

**Ruta base:** `/api/v1/users`  
**Contexto:** `identity`

| Método | Ruta | Caso de uso | Respuesta | Estado |
| --- | --- | --- | --- | --- |
| `GET` | `/{id}` | `GetUserProfileUseCase` | `UserProfileResponse` | Implementado |
| `PUT` | `/{id}/profile` | `UpdateProfileUseCase` | `UserProfileResponse` | Implementado |
| `PUT` | `/{id}/password` | `ChangePasswordUseCase` | `204 No Content` | Implementado |
| `PUT` | `/{id}/role` | `AssignRoleUseCase` | `UserProfileResponse` | Implementado |

> Seguridad pendiente: estas rutas están actualmente abiertas. La futura autorización debe garantizar que un usuario solo modifique sus propios datos y que la asignación de roles esté restringida a administradores autorizados.

### `OrganizationController`

**Ruta base:** `/api/v1/organizations`  
**Contexto:** `organization`

| Método | Ruta | Caso de uso | Respuesta | Estado |
| --- | --- | --- | --- | --- |
| `POST` | `/signup` | `SignUpUseCase` | `SignUpResponse` | Implementado |
| `POST` | `/` | `CreateOrganizationUseCase` | `OrganizationResponse` | Implementado |
| `GET` | `/{id}` | `GetOrganizationUseCase` | `OrganizationResponse` | Implementado |
| `PUT` | `/{id}` | `UpdateOrganizationUseCase` | `OrganizationResponse` | Implementado |
| `POST` | `/{id}/members` | `AddMemberUseCase` | `OrganizationResponse` | Implementado |
| `POST` | `/{id}/invitations` | `InviteMemberUseCase` | `OrganizationResponse` | Implementado |
| `DELETE` | `/{id}/members/{userId}` | `RemoveMemberUseCase` | `OrganizationResponse` | Implementado |

> Seguridad pendiente: cada operación debe validar la pertenencia del solicitante a la organización y el permiso requerido para administrar miembros.

### Controllers pendientes

| Contexto | Controller esperado | Avance |
| --- | --- | --- |
| `quiz` | `QuizController` | Pendiente |
| `gameplay` | `GameSessionController` y canal en tiempo real | Pendiente |

## Persistencia y tablas

| Migración | Estado | Tablas o cambios principales |
| --- | --- | --- |
| `V1__foundation_baseline.sql` | Aplicada | Baseline inicial. |
| `V2__users_schema.sql` | Aplicada | `organizations`, `users`, `roles`, `permissions`, `role_permissions`. |
| `V3__organization_context.sql` | Aplicada | `organization_members`; la relación usuario-organización pasa a las membresías. |
| `V4__quiz_and_gameplay_schema.sql` | Implementada, con incidencia en tests | Alineación de columnas y tablas de `quiz` y `gameplay`. |
| `V5__quiz_and_gameplay_relationship_indexes.sql` | Implementada | Índices de lectura para las relaciones de Quiz y Gameplay. |

### Tablas por contexto

| Contexto | Tablas |
| --- | --- |
| `identity` | `users`, `roles`, `permissions`, `role_permissions` |
| `organization` | `organizations`, `organization_members` |
| `quiz` | `quizzes`, `categories`, `quiz_categories`, `questions`, `answer_options`, `question_assets` |
| `gameplay` | `game_sessions`, `session_players`, `session_questions`, `player_answers`, `session_leaderboard` |

## Calidad, seguridad y riesgos actuales

| Área | Situación actual | Siguiente acción |
| --- | --- | --- |
| Pruebas | Las 5 pruebas de integración fallan al inicializar Flyway V4 sobre H2. | Migrar integración a PostgreSQL con Testcontainers o hacer V4 compatible con H2. |
| Seguridad | Todas las rutas usan `permitAll`; CSRF está deshabilitado. | Incorporar JWT o sesión y autorización por recurso/tenant. |
| Autorización | Los roles y permisos existen en BD, pero no se aplican a las rutas. | Implementar guards de aplicación o Spring Security por permisos. |
| Eventos | Los agregados registran eventos, pero no hay publicación ni consumidores. | Definir publicación post-transacción u outbox. |
| Arquitectura | No hay pruebas ArchUnit de las reglas de dependencia. | Añadir reglas que protejan dominio y límites entre contextos. |
| Observabilidad | No hay CI ni estrategia visible de métricas/trazas. | Añadir pipeline, health checks, logs estructurados y métricas. |
| Documentación | README y auditoría actualizados; el detalle de arquitectura debe mantenerse sincronizado con cada cambio. | Actualizar documentos dentro del mismo cambio funcional. |

## Próximos hitos

| Prioridad | Hito | Resultado esperado |
| --- | --- | --- |
| P0 | Recuperar pruebas verdes | Migraciones verificadas contra PostgreSQL y `mvn verify` confiable. |
| P0 | Proteger la API | Login con token/sesión y acceso validado por usuario y organización. |
| P1 | Completar Quiz | Crear, editar, consultar y publicar quizzes mediante API. |
| P1 | Publicar eventos | Eventos entregados de manera consistente después de persistir cambios. |
| P2 | Endurecer la arquitectura | Tests unitarios de dominio, ArchUnit y CI. |
| P2 | Completar Gameplay | Sesiones y respuestas persistidas con comunicación en tiempo real. |

## Referencias

- [README](../README.md): inicio rápido, stack y uso local.
- [Arquitectura](arquitectura.md): agregados, reglas y diseño de los contextos.
- [Diseño objetivo](correciones.md): evolución prevista del proyecto.
- [Auditoría de arquitectura](auditoria-arquitectura.md): riesgos, recomendaciones y criterios para producción.
