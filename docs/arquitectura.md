# Arquitectura - Bounded Contexts

Documento de referencia del estado **real** del código. Si el código y este documento se contradicen, gana el código y este archivo debe corregirse.

Diseño objetivo: `docs/correciones.md`.

---

## Contextos

```text
src/main/java/kahoot/clabs/kahoot_clabs
├── shared
├── identity
├── organization
├── quiz
└── gameplay
```

| Contexto | Responsabilidad | Estado |
|----------|-----------------|--------|
| `shared` | Abstracciones reutilizables y configuración transversal | Completo |
| `identity` | Usuarios, roles, permisos y autenticación | Dominio + aplicación + infraestructura |
| `organization` | Organizaciones (tenants) y membresías | Dominio + aplicación + infraestructura |
| `quiz` | Contenido de los kahoots | Solo dominio |
| `gameplay` | Ejecución de la partida (core domain) | Solo dominio |

Cada contexto usa la misma estructura interna:

```text
<contexto>
├── domain
│   ├── aggregate
│   ├── entity
│   ├── valueobject
│   ├── repository
│   ├── event
│   └── exception
├── application
│   ├── usecase
│   ├── command
│   ├── query
│   └── dto
└── infrastructure
    ├── controller
    ├── persistence
    ├── mapper
    ├── repository
    ├── adapter
    └── config
```

Desviaciones conscientes:

- `identity/application/port` aloja `PasswordHasher`. Es un puerto hexagonal, no un DTO ni un command.
- Los contextos sin capa de aplicación (`quiz`, `gameplay`) solo tienen `domain`. No se crean carpetas vacías.

---

## Dependencias

```text
                 shared
                    ▲
                    │
     ┌──────────────┼──────────────┐
     │              │              │
 identity     organization       quiz
      │              │             │
      └──────────────┼─────────────┘
                     │
                 gameplay
```

- `identity` no depende de ningún otro contexto.
- `organization` depende de `identity` solo para referenciar usuarios y roles.
- `quiz` referencia organización y autor por id.
- `gameplay` referencia los demás contextos únicamente por id.
- `domain` nunca importa Spring, JPA ni Jakarta (excepto validación en `application`).

---

## shared

| Clase | Uso |
|-------|-----|
| `BaseEntity` | Identidad por `id`; `equals`/`hashCode` por tipo + id |
| `AuditableEntity` | `createdAt` / `updatedAt` y `touch()` |
| `AggregateRoot` | `AuditableEntity` + registro de eventos de dominio |
| `DomainEvent` | Base de eventos (`eventId`, `createdAt`) |
| `DomainException` | Base de errores de negocio; se traduce a HTTP 400 |
| `Identifier` | Base para ids tipados. **Creado, aún sin adoptar** |

Jerarquía: `BaseEntity` → `AuditableEntity` → `AggregateRoot`.

---

## identity

- **Aggregate roots:** `User`, `Role`
- **Entities:** `Permission`, `RolePermission`
- **Value objects:** `Email`, `Password`, `FullName`, `UserProfile`, `UserStatus`, `RoleType`
- **Repositorios:** `UserRepository`, `RoleRepository`, `PermissionRepository`
- **Casos de uso:** `RegisterUser`, `LoginUser`, `GetUserProfile`, `UpdateProfile`, `ChangePassword`, `AssignRole`
- **Tablas:** `users`, `roles`, `permissions`, `role_permissions`

`User` **no conoce la organización**: la membresía vive en `organization`.

### Endpoints

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/users/{id}
PUT  /api/v1/users/{id}/profile
PUT  /api/v1/users/{id}/password
PUT  /api/v1/users/{id}/role
```

---

## organization

- **Aggregate root:** `Organization`
- **Entity:** `OrganizationMember`
- **Value objects:** `OrganizationName`, `OrganizationSlug`, `MemberStatus`, `OrganizationStatus`
- **Repositorios:** `OrganizationRepository` (escritura del agregado), `OrganizationMemberRepository` (solo lectura)
- **Casos de uso:** `SignUp`, `CreateOrganization`, `UpdateOrganization`, `GetOrganization`, `AddMember`, `InviteMember`, `RemoveMember`
- **Tablas:** `organizations`, `organization_members`

Los miembros se crean, cambian y eliminan **solo** a través de `Organization`. `SignUpUseCase` orquesta el onboarding: crea el tenant, registra al usuario vía `identity` y lo añade como miembro `ADMIN`.

### Endpoints

```text
POST   /api/v1/organizations/signup
POST   /api/v1/organizations
GET    /api/v1/organizations/{id}
PUT    /api/v1/organizations/{id}
POST   /api/v1/organizations/{id}/members
POST   /api/v1/organizations/{id}/invitations
DELETE /api/v1/organizations/{id}/members/{userId}
```

---

## quiz

- **Aggregate root:** `Quiz`
- **Entities:** `Question`, `AnswerOption`, `QuestionAsset`, `Category`
- **Value objects:** `QuizTitle`, `EstimatedTime`, `Points`, `TimeLimit`, `MediaUrl`, `QuizSettings`, `QuizVisibility`, `QuizStatus`, `QuizDifficulty`, `QuestionType`, `MediaType`
- **Repositorios:** `QuizRepository`, `CategoryRepository`
- **Tablas:** pendientes (`categories`, `quizzes`, `questions`, `answer_options`, `question_assets`)

Sin capa de aplicación ni persistencia todavía: **hoy no se puede guardar un quiz**.

---

## gameplay

- **Aggregate root:** `GameSession`
- **Entities:** `SessionPlayer`, `SessionQuestion`, `PlayerAnswer`
- **Value objects:** `GamePin`, `PlayerScore`, `ResponseTime`, `GameStatus`, `PlayerRank`
- **Repositorio:** `GameSessionRepository`
- **Tablas:** pendientes (`game_sessions`, `session_players`, `session_questions`, `player_answers`)

`SessionQuestion` copia `points` y `timeLimitSeconds` del quiz para que editar un quiz no altere partidas ya jugadas. El puntaje actual otorga los puntos completos de la pregunta a la respuesta correcta; el bono por velocidad queda pendiente.

---

## Consecuencias del layout por carpetas

Separar `aggregate/` y `entity/` en paquetes distintos **rompe la protección `package-private`** que antes impedía modificar entidades hijas fuera del agregado. Métodos como `Question.assignQuizId` u `OrganizationMember.acceptInvitation` ahora son públicos. La regla "modificar hijos solo vía el aggregate root" pasa a ser convención documentada en lugar de una restricción del compilador; un test de ArchUnit sería la forma de recuperar la garantía.

---

## Pendiente

1. Slice vertical de `quiz`: migración, entities JPA, mappers, adapters, casos de uso y controllers.
2. Aplicación e infraestructura de `gameplay` (incluye tiempo real).
3. Publicación de eventos de dominio: `UserCreatedEvent`, `QuizPublishedEvent` y `GameStartedEvent` se registran pero **nadie los consume**.
4. Adopción de ids tipados sobre `Identifier`.
5. JWT y protección real de rutas: `SecurityConfig` sigue en `permitAll`.
6. Tests unitarios de dominio y ArchUnit para las reglas de dependencia.
