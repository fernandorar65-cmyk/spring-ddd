# CQRS — Excepciones MVP (lectura vía write repos)

## Objetivo

El target de arquitectura es:

* **Commands / Writes** → Aggregate → Write Repository → JPA → PostgreSQL
* **Queries / Reads** → Query → Read Port → Mongo adapter → MongoDB

Esta separación es **obligatoria** para listados y búsquedas de alto volumen (quizzes, sesiones).

---

## Excepciones MVP aceptadas (intencionales)

Hasta que exista read model Mongo + sync para estos bounded contexts / agregados, las siguientes **queries pueden leer por write repository ports (JPA)**:

| Bounded context | Queries afectadas | Write port usado | Motivo MVP |
|-----------------|-------------------|------------------|------------|
| **identity** | `GetUserProfileUseCase`, `GetUserRolesUseCase` | `UserRepository` (+ roles/permissions según caso) | Volumen bajo; sin proyección Mongo aún |
| **organization** | `GetOrganizationUseCase` | `OrganizationRepository` | Detalle puntual; sin proyección Mongo aún |
| **quiz (categories)** | `ListCategoriesUseCase`, `GetCategoryUseCase` | `CategoryRepository` | Catálogo pequeño; sin proyección Mongo aún |

### Reglas de la excepción

1. Es **deuda documentada**, no el patrón por defecto.
2. El use case sigue siendo de **solo lectura** (`@Transactional(readOnly = true)` cuando aplique): no mutar aggregates.
3. Controllers/use cases **no** deben filtrar documentos Mongo ni entidades JPA al API; siguen mapeando a DTOs de application.
4. **No** ampliar esta excepción a nuevos listados pesados (quizzes, sessions, leaderboards, search).
5. Cuando se añada Mongo para identity / organization / categories, migrar estas queries a un `*ReadPort` y retirarlas de esta lista.

---

## Ya alineado con CQRS (Mongo)

| Área | Query / listado | Read port |
|------|-----------------|-----------|
| Quiz listados | `ListQuizzesUseCase` | `QuizReadPort` |
| Gameplay list/get sesión | `ListGameSessionsUseCase`, `GetGameSessionUseCase` | `GameSessionReadModelPort` |
| Gameplay leaderboard / questions / answers | `GetLeaderboardUseCase`, `GetSessionQuestionsUseCase`, `GetMyAnswersUseCase` | `GameSessionReadModelPort` (documento enriquecido) |

Proyección write → read (quiz): tras `QuizRepository.save/delete`, `JpaQuizRepositoryAdapter` llama a `QuizProjectionPort` (Mongo en runtime; no-op en `@Profile("test")`).

Cross-context freeze de quiz: gameplay usa `QuizSnapshotPort` + `PublishedQuizSnapshot` (no importa aggregates de quiz).

Domain Events (`QuizPublishedEvent`, `UserCreatedEvent`) están **aparcados** en MVP: no impulsan la proyección. Ver `docs/domain-events.md`.

---

## Deuda de lectura restante

* `GetQuizUseCase` — detalle completo del quiz (preguntas/opciones) sigue en `QuizRepository` (JPA). Aceptable para edición del aggregate; listados ya van por Mongo. Migrar a un read model de detalle solo si el detalle de lectura no necesita el write model.

## Criterio para cerrar la excepción

Una query sale de esta lista cuando:

1. Existe documento/proyección Mongo (o read port) adecuado, y
2. El write adapter / proyección mantiene el read model sincronizado tras commands, y
3. El query use case deja de depender del write repository port.
