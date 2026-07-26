# Fase 2 - Paso 3
# Implementación de Ports & Adapters

> Estado: ⏳ Pendiente
>
> Objetivo:
>
> Adaptar completamente el proyecto a una Arquitectura Hexagonal (Ports & Adapters), manteniendo la Clean Architecture y DDD.
>
> A partir de esta fase toda dependencia hacia tecnologías externas deberá realizarse mediante Ports y Adapters.
>
> El dominio seguirá siendo completamente independiente de Spring.

---

# Objetivo

Al finalizar esta fase deberá existir una separación clara entre:

- Domain Ports
- Application Ports
- Adapters
- Infrastructure

El dominio nunca dependerá de Spring ni de implementaciones concretas.

---

# Arquitectura objetivo

```text
src/main/java
└── kahoot
    └── clabs
        └── kahoot_clabs

            shared

            identity

            organization

            quiz

            gameplay
```

Todos los bounded contexts deberán seguir exactamente la misma estructura.

---

# Arquitectura interna

```text
quiz
├── domain
│
│   ├── aggregate
│   ├── entity
│   ├── valueobject
│   ├── repository
│   ├── event
│   └── exception
│
├── application
│
│   ├── command
│   ├── query
│   ├── usecase
│   ├── dto
│   ├── mapper
│   └── port
│
└── infrastructure
    ├── controller
    │
    ├── persistence
    │   ├── entity
    │   ├── mapper
    │   ├── repository
    │   └── adapter
    │
    ├── storage
    │   └── adapter
    │
    └── config
```

Gameplay deberá seguir exactamente la misma estructura.

---

# Domain Ports

Los puertos del dominio representan dependencias del dominio hacia la persistencia.

Son únicamente interfaces.

Nunca contienen implementación.

Ubicación:

```
domain/repository
```

Ejemplos:

```
QuizRepository

CategoryRepository

GameSessionRepository
```

Ejemplo

```java
public interface QuizRepository {

    Quiz save(Quiz quiz);

    Optional<Quiz> findById(UUID id);

    List<Quiz> findAll();

    boolean existsById(UUID id);

    void delete(UUID id);

}
```

Estos puertos pertenecen al dominio.

No conocen Spring.

No conocen JPA.

---

# Application Ports

Los puertos de aplicación representan dependencias hacia servicios externos.

Ubicación

```
application/port
```

Ejemplos:

```
FileStorage

EmailSender

ImageProcessor

Clock

UuidGenerator
```

No implementar todavía estos servicios.

Únicamente dejar preparada la arquitectura.

El único puerto existente actualmente es:

```
PasswordHasher
```

Debe mantenerse como ejemplo para los demás.

---

# Repository Adapters

Los adapters implementan los puertos del dominio.

Ubicación

```
infrastructure/persistence/adapter
```

Ejemplos

```
JpaQuizRepositoryAdapter

JpaCategoryRepositoryAdapter

JpaGameSessionRepositoryAdapter
```

Ejemplo

```java
public class JpaQuizRepositoryAdapter
        implements QuizRepository {

}
```

Los adapters:

- utilizan Spring Data
- utilizan los mappers
- nunca contienen lógica de negocio

---

# Spring Data

Ubicación

```
infrastructure/persistence/repository
```

Ejemplo

```
SpringDataQuizRepository

SpringDataCategoryRepository

SpringDataGameSessionRepository
```

Estas interfaces únicamente extienden JpaRepository.

No contienen lógica.

---

# Flujo de persistencia

Guardar

```
UseCase

↓

QuizRepository (Port)

↓

JpaQuizRepositoryAdapter

↓

SpringDataQuizRepository

↓

PostgreSQL
```

Leer

```
PostgreSQL

↓

SpringDataQuizRepository

↓

JpaQuizRepositoryAdapter

↓

QuizRepository

↓

UseCase
```

El dominio nunca conoce cómo se almacenan los datos.

---

# Flujo completo

Escritura

```
HTTP Request

↓

Controller

↓

Command

↓

UseCase

↓

Aggregate Root

↓

Domain Port

↓

Adapter

↓

Spring Data

↓

Database
```

Lectura

```
HTTP Request

↓

Controller

↓

Query

↓

UseCase

↓

Domain Port

↓

Adapter

↓

Spring Data

↓

Database

↓

Response DTO
```

---

# Responsabilidades

## Domain

Contiene únicamente:

- Aggregate Roots
- Entities
- Value Objects
- Domain Events
- Domain Exceptions
- Repository Ports

Nunca depende de:

- Spring
- JPA
- Hibernate
- Controllers
- DTOs

---

## Application

Contiene:

- Commands
- Queries
- UseCases
- DTOs
- Application Ports

Los UseCases únicamente coordinan.

No contienen reglas de negocio.

---

## Infrastructure

Contiene:

- Controllers
- Spring Data
- Repository Adapters
- Configuración
- Implementaciones concretas

Toda dependencia tecnológica vive aquí.

---

# Reglas

## Nunca

El dominio no puede importar:

```
org.springframework.*

jakarta.persistence.*

org.hibernate.*
```

---

## Nunca

Los Controllers no pueden acceder directamente a:

```
JpaRepository

EntityManager

Entity JPA
```

Siempre deben utilizar UseCases.

---

## Nunca

Los UseCases no pueden utilizar:

```
SpringDataRepository
```

Siempre deben utilizar:

```
Ports
```

---

## Nunca

Los Repository Adapters no contienen:

- reglas de negocio
- validaciones
- cálculos

Solo adaptan.

---

# Resultado esperado

La arquitectura quedará completamente desacoplada.

```text
Controller

↓

Command / Query

↓

UseCase

↓

Domain Port

↓

Repository Adapter

↓

Spring Data Repository

↓

PostgreSQL
```

y para servicios externos:

```text
UseCase

↓

Application Port

↓

Adapter

↓

Servicio Externo
```

Ejemplos futuros:

```
FileStorage

↓

S3StorageAdapter
```

```
EmailSender

↓

SendGridAdapter
```

```
Clock

↓

SystemClockAdapter
```

```
UuidGenerator

↓

JavaUuidGeneratorAdapter
```

```
ImageProcessor

↓

ThumbnailGeneratorAdapter
```

---

# Criterios de aceptación

Al finalizar esta fase:

- Todos los repositorios del dominio son interfaces (Ports).
- Todos los adapters implementan esos puertos.
- Todos los Spring Data Repositories permanecen ocultos dentro de infraestructura.
- Ningún caso de uso depende de Spring Data.
- Ningún Aggregate Root conoce JPA.
- La arquitectura queda preparada para reemplazar PostgreSQL por otra tecnología sin modificar el dominio ni la aplicación.

---

# Nota importante

A partir de este momento, **toda nueva integración externa** (almacenamiento de archivos, correo electrónico, cache, mensajería, reloj del sistema, generación de UUID, etc.) deberá seguir el mismo patrón:

**Port (Application o Domain) → Adapter → Implementación concreta.**

No se permitirá que un UseCase o un Aggregate Root dependa directamente de una librería o framework externo.