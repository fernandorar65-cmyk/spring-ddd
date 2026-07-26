# Fase 2 - Paso 1
# Implementación del Modelo de Dominio y Mapeo JPA
## Quiz & Gameplay

> Estado: ⏳ Pendiente
>
> Objetivo:
>
> Implementar completamente el **modelo de dominio** y el **modelo de persistencia (JPA)** para los bounded contexts **Quiz** y **Gameplay**, dejando la base preparada para comenzar posteriormente con los Repository Adapters, Use Cases y Controllers.
>
> **Este documento NO contempla lógica de negocio, casos de uso ni endpoints.**
>
> Únicamente se implementará el modelo del dominio y su representación en la base de datos.

---

# Objetivo de esta fase

Al finalizar este paso deberá existir:

- ✅ Aggregate Roots
- ✅ Entidades del dominio
- ✅ Value Objects
- ✅ Entidades JPA
- ✅ Relaciones JPA
- ✅ Mappers Domain ↔ Entity

Todavía NO deberá existir:

- Controllers
- REST API
- Use Cases
- Repository Adapters
- Spring Data Repositories
- Servicios
- JWT
- Seguridad
- Validaciones de aplicación

---

# Arquitectura esperada

## Quiz

```text
quiz
├── domain
│
├── aggregate
│   └── Quiz.java
│
├── entity
│   ├── Category.java
│   ├── Question.java
│   ├── AnswerOption.java
│   └── QuestionAsset.java
│
├── valueobject
│   ├── EstimatedTime.java
│   ├── MediaType.java
│   ├── MediaUrl.java
│   ├── Points.java
│   ├── QuestionType.java
│   ├── QuizDifficulty.java
│   ├── QuizSettings.java
│   ├── QuizStatus.java
│   ├── QuizTitle.java
│   ├── QuizVisibility.java
│   └── TimeLimit.java
│
└── infrastructure
    └── persistence
        ├── entity
        │   ├── QuizEntity.java
        │   ├── CategoryEntity.java
        │   ├── QuizCategoryEntity.java
        │   ├── QuestionEntity.java
        │   ├── AnswerOptionEntity.java
        │   └── QuestionAssetEntity.java
        │
        └── mapper
            ├── QuizMapper.java
            ├── QuestionMapper.java
            └── CategoryMapper.java
```

---

## Gameplay

```text
gameplay
├── domain
│
├── aggregate
│   └── GameSession.java
│
├── entity
│   ├── SessionPlayer.java
│   ├── SessionQuestion.java
│   └── PlayerAnswer.java
│
├── valueobject
│   ├── GamePin.java
│   ├── GameStatus.java
│   ├── PlayerRank.java
│   ├── PlayerScore.java
│   └── ResponseTime.java
│
└── infrastructure
    └── persistence
        ├── entity
        │   ├── GameSessionEntity.java
        │   ├── SessionPlayerEntity.java
        │   ├── SessionQuestionEntity.java
        │   ├── PlayerAnswerEntity.java
        │   └── SessionLeaderboardEntity.java
        │
        └── mapper
            └── GameSessionMapper.java
```

---

# Reglas del Dominio

## Muy importante

Las clases dentro de

```text
domain/
```

deben ser completamente independientes de cualquier framework.

Está prohibido utilizar:

- Spring
- Hibernate
- JPA
- Lombok
- Jakarta Persistence

No utilizar anotaciones como:

```
@Entity
@Table
@Column
@Id
@GeneratedValue
@ManyToOne
@OneToMany
@JoinColumn
```

El dominio debe permanecer completamente puro.

---

# Aggregate Roots

Los únicos Aggregate Roots serán:

## Quiz

Responsable de controlar:

- Questions
- Categories
- Assets
- Publicación

Todas las modificaciones deberán realizarse a través de Quiz.

---

## GameSession

Responsable de controlar:

- SessionPlayers
- SessionQuestions
- PlayerAnswers

Toda modificación deberá realizarse desde GameSession.

---

# Entidades del Dominio

## Quiz

Debe contener únicamente información del dominio.

No debe conocer JPA.

Debe contener:

- id
- organizationId
- createdBy
- title
- description
- visibility
- difficulty
- status
- estimatedTime

Colecciones:

- categories
- questions

---

## Category

Representa una categoría de Quiz.

Campos:

- id
- organizationId
- name
- description
- color
- icon

---

## Question

Campos:

- id
- title
- description
- type
- order
- points
- timeLimit

Colecciones:

- answerOptions
- assets

---

## AnswerOption

Campos:

- id
- text
- correct
- order

---

## QuestionAsset

Campos:

- id
- mediaType
- mediaUrl
- thumbnailUrl
- durationSeconds

---

# Gameplay

## GameSession

Campos:

- id
- organizationId
- quizId
- hostUserId
- gamePin
- status
- startedAt
- endedAt

Colecciones:

- players
- questions

---

## SessionPlayer

Campos:

- id
- userId
- nickname
- score
- joinedAt
- leftAt

---

## SessionQuestion

Snapshot de la pregunta.

Campos:

- id
- questionId
- order
- points
- timeLimitSeconds
- startedAt
- endedAt

Colección:

- answers

---

## PlayerAnswer

Campos:

- id
- answerOptionId
- responseTime
- awardedPoints
- answeredAt

---

# Persistencia (JPA)

Crear únicamente las entidades JPA.

No implementar lógica.

No implementar repositorios.

---

# QuizEntity

Representa:

```
quizzes
```

Relaciones:

- Organization
- User
- QuestionEntity
- QuizCategoryEntity

---

# CategoryEntity

Representa:

```
categories
```

---

# QuizCategoryEntity

Representa:

```
quiz_categories
```

Tabla puente.

---

# QuestionEntity

Representa:

```
questions
```

Relaciones:

- QuizEntity
- AnswerOptionEntity
- QuestionAssetEntity

---

# AnswerOptionEntity

Representa:

```
answer_options
```

---

# QuestionAssetEntity

Representa:

```
question_assets
```

---

# GameSessionEntity

Representa:

```
game_sessions
```

Relaciones:

- SessionPlayers
- SessionQuestions

---

# SessionPlayerEntity

Representa:

```
session_players
```

---

# SessionQuestionEntity

Representa:

```
session_questions
```

---

# PlayerAnswerEntity

Representa:

```
player_answers
```

---

# SessionLeaderboardEntity

Representa:

```
session_leaderboard
```

---

# Relaciones JPA

Implementar las relaciones utilizando únicamente JPA.

Usar:

- @OneToMany
- @ManyToOne
- @OneToOne
- @JoinColumn
- @JoinTable

según corresponda.

Evitar relaciones bidireccionales cuando no sean necesarias.

Priorizar navegación desde el Aggregate Root.

---

# Mappers

Crear únicamente mappers.

No acceder a la base de datos.

No utilizar repositorios.

No contener lógica de negocio.

Solo convertir:

```
Domain
↓

Entity
↓

Domain
```

Crear:

## Quiz

- QuizMapper
- QuestionMapper
- CategoryMapper

---

## Gameplay

- GameSessionMapper

---

# Convenciones

## Dominio

- POJOs puros
- Sin dependencias externas
- Sin setters innecesarios
- Mantener invariantes del dominio

---

## Persistencia

Las entidades JPA pueden utilizar:

- Constructor vacío protegido
- Relaciones JPA
- UUID como PK
- FetchType.LAZY por defecto
- Cascade únicamente donde sea necesario

---

## UUID

Todas las entidades utilizarán UUID.

No utilizar IDs autoincrementales.

---

## Auditoría

Todas las entidades persistentes deberán contener:

- createdAt
- updatedAt

---

# Fuera del alcance

No implementar todavía:

- Repository Interfaces
- Repository Adapters
- Spring Data Repositories
- Use Cases
- Commands
- Queries
- DTOs
- Controllers
- REST
- JWT
- Security
- Eventos
- Validaciones de aplicación

---

# Criterios de aceptación

Se considerará completado este paso cuando existan:

## Dominio

```
Quiz
Category
Question
AnswerOption
QuestionAsset

GameSession
SessionPlayer
SessionQuestion
PlayerAnswer
```

Todos completamente independientes de Spring y JPA.

---

## Persistencia

```
QuizEntity
CategoryEntity
QuizCategoryEntity
QuestionEntity
AnswerOptionEntity
QuestionAssetEntity

GameSessionEntity
SessionPlayerEntity
SessionQuestionEntity
PlayerAnswerEntity
SessionLeaderboardEntity
```

Con todas sus relaciones JPA correctamente definidas.

---

## Mappers

```
QuizMapper
QuestionMapper
CategoryMapper
GameSessionMapper
```

Convirtiendo correctamente:

```
Domain ↔ Entity
```

---

# Resultado esperado

Al finalizar este paso el proyecto tendrá completamente definidos:

- Modelo del Dominio
- Modelo Relacional
- Modelo de Persistencia JPA
- Mapeo entre Dominio y Persistencia

Esto permitirá que el siguiente paso sea implementar únicamente:

- Spring Data Repositories
- Repository Adapters
- Persistencia completa

sin necesidad de modificar nuevamente las entidades.