# Bounded Contexts - Kahoot Clabs (MVP)

## Objetivo

El proyecto estará organizado por Bounded Contexts siguiendo Domain-Driven Design (DDD).

Cada contexto representa un subdominio con responsabilidades claramente definidas.

Los contextos deben ser altamente cohesivos y tener el menor acoplamiento posible.

---

# Estructura General

```
shared
identity
organization
quiz
gameplay
```

---

# 1. Shared

## Responsabilidad

Contiene todos los componentes reutilizables por el resto de bounded contexts.

No contiene lógica de negocio específica.

## Debe contener

### Domain

- AggregateRoot
- BaseEntity
- DomainEvent
- DomainException
- Identifier
- AuditableEntity

### Infrastructure

- JpaConfig
- OpenApiConfig
- GlobalExceptionHandler
- Common utilities

---

# 2. Identity

## Responsabilidad

Gestionar la identidad de los usuarios.

Este contexto solamente conoce:

- Usuarios
- Roles
- Permisos
- Autenticación

No conoce organizaciones.

No conoce quizzes.

No conoce gameplay.

---

## Aggregate Roots

- User
- Role

---

## Entities

- Permission
- RolePermission

---

## Value Objects

- Email
- Password
- FullName
- UserProfile
- UserStatus

---

## Repository Interfaces

- UserRepository
- RoleRepository
- PermissionRepository

---

## Casos de uso

- RegisterUser
- Login
- UpdateProfile
- ChangePassword
- AssignRole

---

## Persistencia

```
users

roles

permissions

role_permissions
```

---

# 3. Organization

## Responsabilidad

Administrar organizaciones.

Una organización representa un tenant.

Este contexto conoce:

- Organizaciones
- Miembros
- Roles dentro de la organización

No conoce autenticación.

No conoce quizzes.

No conoce gameplay.

---

## Aggregate Root

Organization

---

## Entities

OrganizationMember

---

## Value Objects

OrganizationName

OrganizationSlug

MemberStatus

---

## Repository Interfaces

OrganizationRepository

OrganizationMemberRepository

---

## Casos de uso

- CreateOrganization
- UpdateOrganization
- InviteMember
- RemoveMember
- AddMember

---

## Persistencia

```
organizations

organization_members
```

---

# 4. Quiz

## Responsabilidad

Administrar el contenido de los Kahoots.

Aquí únicamente vive el contenido.

Nunca la ejecución del juego.

---

## Aggregate Root

Quiz

---

## Entities

Question

AnswerOption

QuestionAsset

Category

---

## Value Objects

QuizTitle

EstimatedTime

Points

TimeLimit

MediaUrl

QuizVisibility

QuizStatus

QuestionType

---

## Repository Interfaces

QuizRepository

CategoryRepository

---

## Casos de uso

- CreateQuiz
- UpdateQuiz
- DeleteQuiz
- PublishQuiz
- ArchiveQuiz
- DuplicateQuiz
- AddQuestion
- RemoveQuestion

---

## Persistencia

```
categories

quizzes

questions

answer_options

question_assets
```

---

# 5. Gameplay

## Responsabilidad

Ejecutar una partida de Kahoot.

Este será el Core Domain del sistema.

No administra quizzes.

No administra usuarios.

Consume un Quiz ya publicado y ejecuta una sesión.

---

## Aggregate Root

GameSession

---

## Entities

SessionPlayer

SessionQuestion

PlayerAnswer

---

## Value Objects

GamePin

PlayerScore

ResponseTime

GameStatus

PlayerRank

---

## Repository Interfaces

GameSessionRepository

---

## Casos de uso

- CreateGameSession
- JoinGame
- StartGame
- NextQuestion
- SubmitAnswer
- CalculateScore
- FinishGame

---

## Persistencia

```
game_sessions

session_players

session_questions

player_answers
```

---

# Dependencias entre Contextos

```
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

## Reglas

Identity no depende de ningún contexto.

Organization depende únicamente de Identity para referenciar usuarios.

Quiz depende de Organization para conocer el propietario del Quiz.

Gameplay depende de:

- Identity
- Organization
- Quiz

pero únicamente mediante IDs o contratos, nunca accediendo directamente a sus agregados.

---

# Estado esperado del proyecto

```
src/main/java
└── kahoot/clabs/kahoot_clabs
    ├── shared
    ├── identity
    ├── organization
    ├── quiz
    └── gameplay
```

Cada contexto tendrá exactamente la misma estructura:

```
<context>
│
├── domain
│   ├── aggregate
│   ├── entity
│   ├── valueobject
│   ├── repository
│   ├── event
│   └── exception
│
├── application
│   ├── usecase
│   ├── command
│   ├── query
│   └── dto
│
└── infrastructure
    ├── controller
    ├── persistence
    ├── mapper
    ├── repository
    ├── adapter
    └── config
```