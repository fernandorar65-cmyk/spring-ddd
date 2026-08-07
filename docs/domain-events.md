# Domain Events — estado MVP (aparcados)

## ¿Qué es un Domain Event?

Un **Domain Event** representa un hecho de negocio que **ya ocurrió** en el dominio.

Ejemplos:

* `QuizPublishedEvent` — “este quiz quedó publicado”
* `UserCreatedEvent` — “este usuario fue creado”

No es un comando (“publica el quiz”). Es una **noticia** del pasado.

Flujo típico cuando están cableados:

```
Aggregate (registerEvent)
    ↓
Use case / adapter (pullDomainEvents)
    ↓
Publisher / listeners
    ↓
Efectos secundarios (proyección Mongo, emails, otros BC…)
```

La infraestructura base ya existe:

* `shared.domain.DomainEvent`
* `AggregateRoot.registerEvent` / `pullDomainEvents`
* Clases `QuizPublishedEvent`, `UserCreatedEvent`

---

## Decisión MVP: aparcar (no cablear)

**No** usamos Domain Events todavía para sincronizar read models ni para side-effects.

Motivos:

1. La sync JPA → Mongo de quizzes **ya funciona** de forma síncrona vía `QuizProjectionPort` (desde el write adapter).
2. Cablear eventos ahora duplicaría caminos (proyección por adapter **y** por listener) o forzaría outbox/bus sin necesidad.
3. Las reglas del proyecto piden no introducir Kafka/Outbox salvo que se pida explícitamente.

### Qué hicimos al aparcar

* Quitamos comentarios/`registerEvent` comentados en `Quiz.publish()` y `User.create()` (código muerto confuso).
* Dejamos las clases de evento marcadas como **parked**.
* Documentamos que **no** son el mecanismo de sync actual.

### Qué NO hacemos en MVP

* No llamar `registerEvent(...)` desde aggregates.
* No publicar/consumir estos eventos.
* No proyectar Mongo desde listeners de Domain Events.

---

## Cuándo cablearlos (futuro)

Cablear tiene sentido cuando necesitemos, por ejemplo:

* Desacoplar proyección de los write adapters
* Notificar a otro bounded context sin importar su aggregate
* Side-effects (email, auditoría, métricas) reaccionando a hechos de dominio
* Eventual consistency / outbox

Hasta entonces: proyección sync por ports; eventos aparcados.
