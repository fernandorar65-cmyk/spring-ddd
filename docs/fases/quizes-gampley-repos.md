# Fase 2 - Paso 2
# Implementación de la Persistencia (Repository Adapters)

> Estado: ⏳ Pendiente
>
> Objetivo:
>
> Implementar la primera capa de infraestructura para los bounded contexts **Quiz** y **Gameplay**.
>
> En esta fase el dominio ya existe y también las entidades JPA. Ahora únicamente se debe conectar ambos modelos mediante Repository Adapters.
>
> **No se implementarán casos de uso, controllers ni lógica de negocio.**

---

# Objetivo

Al finalizar este paso deberá ser posible:

```java
quizRepository.save(quiz);

quizRepository.findById(id);

quizRepository.findAll();

categoryRepository.save(category);

gameSessionRepository.save(gameSession);
```

Nada más.

Todavía no existirán:

- REST API
- Controllers
- DTOs
- Commands
- Queries
- UseCases
- JWT
- Security

---

# Arquitectura esperada

## Quiz

```text
quiz
├── domain
│
│── aggregate
│── entity
│── valueobject
│── repository
│   ├── QuizRepository.java
│   └── CategoryRepository.java
│
└── infrastructure
    └── persistence
        ├── entity
        │
        ├── mapper
        │
        ├── repository
        │   ├── SpringDataQuizRepository.java
        │   ├── SpringDataCategoryRepository.java
        │   └── SpringDataQuestionRepository.java
        │
        └── adapter
            ├── JpaQuizRepositoryAdapter.java
            └── JpaCategoryRepositoryAdapter.java
```

---

## Gameplay

```text
gameplay
├── domain
│
│── aggregate
│── entity
│── valueobject
│── repository
│   └── GameSessionRepository.java
│
└── infrastructure
    └── persistence
        ├── entity
        │
        ├── mapper
        │
        ├── repository
        │   └── SpringDataGameSessionRepository.java
        │
        └── adapter
            └── JpaGameSessionRepositoryAdapter.java
```

---

# Objetivos

Implementar únicamente:

- Spring Data Repositories
- Repository Adapters

No implementar lógica de negocio.

---

# Spring Data Repositories

Crear interfaces que extiendan JpaRepository.

Estas interfaces son exclusivamente infraestructura.

Nunca deben ser utilizadas desde Application o Domain.

---

## Quiz

Crear:

```
SpringDataQuizRepository

SpringDataCategoryRepository

SpringDataQuestionRepository
```

---

## Gameplay

Crear:

```
SpringDataGameSessionRepository
```

---

# Repository Adapters

Implementar los puertos definidos en Domain.

Nunca exponer entidades JPA al dominio.

Siempre convertir utilizando los mappers.

---

## Quiz

Implementar:

```
JpaQuizRepositoryAdapter
```

Debe implementar:

```
QuizRepository
```

Operaciones mínimas:

```
save()

findById()

findAll()

existsById()

delete()
```

---

## Category

Implementar:

```
JpaCategoryRepositoryAdapter
```

Debe implementar:

```
CategoryRepository
```

Operaciones:

```
save()

findById()

findAll()

delete()
```

---

## Gameplay

Implementar:

```
JpaGameSessionRepositoryAdapter
```

Debe implementar:

```
GameSessionRepository
```

Operaciones:

```
save()

findById()

findAll()
```

---

# Mappers

Los Repository Adapters nunca deben manipular directamente las entidades del dominio.

Siempre utilizar:

```
Domain

↓

Mapper

↓

Entity

↓

Spring Data

↓

Entity

↓

Mapper

↓

Domain
```

---

# Flujo esperado

Guardar un Quiz

```
Quiz

↓

QuizMapper

↓

QuizEntity

↓

SpringDataQuizRepository

↓

PostgreSQL
```

Leer un Quiz

```
PostgreSQL

↓

SpringDataQuizRepository

↓

QuizEntity

↓

QuizMapper

↓

Quiz
```

---

# Reglas

## Dominio

El dominio continúa sin depender de:

- Spring
- Hibernate
- JPA

---

## Infrastructure

Toda la persistencia vive exclusivamente en:

```
infrastructure.persistence
```

---

## Repository Adapter

Debe contener únicamente:

- Conversión Domain ↔ Entity
- Delegación hacia Spring Data

No implementar:

- Validaciones
- Reglas de negocio
- Casos de uso
- Eventos

---

## Spring Data Repository

Debe contener únicamente acceso a datos.

No agregar lógica.

---

# Qué NO implementar

Todavía NO crear:

```
application

controller

dto

command

query

service
```

Tampoco:

- JWT
- Security
- Eventos
- WebSockets

---

# Criterios de aceptación

Al finalizar este paso debe funcionar correctamente:

## Quiz

```
quizRepository.save()

quizRepository.findById()

quizRepository.findAll()

quizRepository.delete()
```

---

## Category

```
categoryRepository.save()

categoryRepository.findById()

categoryRepository.findAll()
```

---

## Gameplay

```
gameSessionRepository.save()

gameSessionRepository.findById()

gameSessionRepository.findAll()
```

---

# Restricciones

- No modificar el dominio.
- No modificar las entidades JPA ya creadas, salvo correcciones menores.
- No duplicar lógica entre los adapters y los mappers.
- Todo acceso a la base de datos debe pasar por Spring Data.
- Todo acceso desde el dominio debe pasar por los puertos (`QuizRepository`, `CategoryRepository`, `GameSessionRepository`).
- Los Repository Adapters son la única implementación concreta de los puertos del dominio.

---

# Resultado esperado

Al finalizar esta fase la arquitectura quedará así:

```
Domain

↓

Repository Interface (Port)

↓

JpaRepositoryAdapter

↓

SpringDataRepository

↓

PostgreSQL
```

El dominio podrá persistirse completamente sin conocer ningún detalle de Spring o JPA.

Este será el último paso de infraestructura antes de comenzar con la capa de aplicación.

---

# Próxima fase

Una vez finalizada esta tarea, el siguiente paso será implementar la **Application Layer** del contexto **Quiz**, comenzando por el CRUD de Categorías mediante Vertical Slices:

- CreateCategory
- UpdateCategory
- DeleteCategory
- GetCategory
- ListCategories

Solo después se continuará con el CRUD de Quiz y el resto de funcionalidades.