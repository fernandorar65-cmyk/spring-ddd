# Auditoría de arquitectura y escalabilidad

**Proyecto:** `kahoot-clabs`  
**Fecha de auditoría:** 25 de julio de 2026  
**Alcance:** revisión estática del repositorio y validación de compilación/pruebas.  
**Estado:** auditoría de solo lectura; este documento no implica que los puntos hayan sido corregidos.

## Resumen ejecutivo

El proyecto dispone de una base sólida para un backend multi-tenant: está organizado por bounded contexts, conserva el dominio libre de Spring/JPA y separa las entidades de persistencia de los agregados de dominio. `identity` y `organization` son los contextos más maduros y cubren el flujo desde HTTP hasta persistencia.

No obstante, el sistema todavía no está listo para un despliegue productivo ni para evolucionar con confianza. Hay dos bloqueadores inmediatos:

1. La suite de pruebas no puede iniciar porque la migración Flyway V4 no es compatible con H2.
2. Todos los endpoints están públicos: no existe autenticación efectiva ni autorización por usuario, rol u organización.

Adicionalmente, los contextos `quiz` y `gameplay` tienen buen modelado de dominio y entidades JPA, pero carecen del resto del slice vertical: casos de uso, adaptadores de repositorio, mappers y API. La documentación también describe un estado anterior al código actual.

La prioridad recomendada es recuperar una base verificable y segura antes de ampliar funcionalidades.

## Contexto técnico observado

| Área | Estado |
| --- | --- |
| Lenguaje y framework | Java 21 y Spring Boot 3.5.16 |
| Persistencia | PostgreSQL, JPA/Hibernate y Flyway |
| Pruebas | Spring Boot Test, MockMvc y H2; Testcontainers declarado pero no utilizado |
| Documentación API | springdoc OpenAPI |
| Contextos | `identity`, `organization`, `quiz`, `gameplay` y `shared` |
| Compilación | Correcta con `mvn -DskipTests compile` |
| Pruebas | Fallan 5 de 5 al iniciar el contexto |
| CI/CD | No se encontró pipeline configurado |

## Evaluación de arquitectura

### Fortalezas

#### Organización por bounded contexts

La estructura principal está alineada con DDD y Clean Architecture:

```text
kahoot_clabs/
├── shared/
├── identity/
├── organization/
├── quiz/
└── gameplay/
```

Cada contexto maduro separa `domain`, `application` e `infrastructure`. Esta orientación reduce el acoplamiento accidental que suele aparecer en proyectos organizados exclusivamente por controladores, servicios y repositorios globales.

#### Dominio aislado de frameworks

Las clases bajo `*/domain/**` no importan Spring, JPA ni anotaciones de infraestructura. Esto respeta la regla de dependencias y permite probar reglas de negocio sin levantar el contenedor de Spring.

La separación entre agregados de dominio y modelos JPA también es correcta. Por ejemplo, `User` no está anotado como entidad JPA; la persistencia utiliza `UserEntity` y mappers en infraestructura.

#### Modelo de dominio con comportamiento

Los agregados expresan acciones de negocio en lugar de exponer setters genéricos:

- `User` cambia perfil, contraseña, rol y estado mediante métodos explícitos.
- `Organization` centraliza altas, invitaciones, cambios de rol y bajas de miembros.
- `Quiz` valida su publicación y la preparación de las preguntas.
- `GameSession` modela estados y reglas de inicio de una partida.

También hay un uso consistente de value objects para conceptos relevantes, como correo electrónico, contraseña, título de quiz, tiempo límite, puntos y PIN de juego.

#### Persistencia con controles correctos

La configuración de producción usa `ddl-auto: validate` y `open-in-view: false`. Son decisiones adecuadas: Flyway es la fuente de verdad del esquema y no se prolonga la sesión de persistencia hasta la capa web.

## Hallazgos prioritarios

### Crítico — La suite de pruebas no es ejecutable

La orden `sh mvnw test` falla antes de ejecutar los casos de prueba. Flyway no puede aplicar `V4__quiz_and_gameplay_schema.sql` en H2.

El problema concreto está en la sintaxis de alteración múltiple:

```sql
ALTER TABLE permissions
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
```

H2 rechaza esta variante, aunque PostgreSQL la admite. El perfil de prueba está configurado con H2 en `src/test/resources/application-test.yml`, por lo que fallan los cinco tests existentes al cargar `ApplicationContext`.

**Impacto**

- No hay señal de calidad automatizada sobre cambios nuevos.
- Las regresiones de migración y persistencia llegan tarde.
- Un pipeline de CI, si se añadiera sin corregir esto, fallaría de forma sistemática.

**Recomendación**

Se recomienda usar PostgreSQL con Testcontainers en las pruebas de integración. La dependencia ya está declarada en `pom.xml`, pero no está siendo utilizada. Esta opción valida el dialecto y el comportamiento real de producción.

Como medida temporal, separar los `ADD COLUMN` en sentencias independientes puede hacer V4 compatible con H2. No debe sustituir las pruebas contra PostgreSQL, porque pueden existir otras diferencias de dialecto.

### Crítico — La API no tiene control de acceso

`SecurityConfig` deshabilita CSRF y permite cualquier petición:

```java
.csrf(AbstractHttpConfigurer::disable)
.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
```

No se entrega un JWT, sesión ni otro artefacto de autenticación durante el login. En consecuencia, rutas sensibles, como cambio de contraseña y asignación de rol, son invocables sin identificar al solicitante.

**Impacto**

- Un consumidor puede modificar el rol o la contraseña de cualquier usuario si conoce su identificador.
- No existe aislamiento efectivo por tenant.
- Los permisos almacenados en la base de datos no se aplican.

**Recomendación**

Implementar autenticación basada en JWT o sesión antes de exponer el servicio:

1. Dejar públicas únicamente las rutas de registro, login y documentación que se decida publicar.
2. Asociar la identidad autenticada a cada petición.
3. Verificar ownership para recursos de usuario.
4. Verificar pertenencia y rol dentro de la organización para recursos multi-tenant.
5. Añadir pruebas negativas de autorización a cada endpoint sensible.

### Alto — `quiz` y `gameplay` son slices verticales incompletos

`quiz` y `gameplay` contienen dominio y entidades JPA, y sus tablas se crean en V4. Sin embargo, no tienen repositorios Spring Data, adapters de los puertos de dominio, mappers, casos de uso ni controladores.

**Impacto**

- El modelo se mantiene y valida al arrancar Hibernate, pero no aporta funcionalidad al producto.
- El coste de mantenimiento aumenta porque la infraestructura anticipa decisiones sin una API o flujo de aplicación validado.
- El diseño de los agregados no está protegido por pruebas de casos de uso reales.

**Recomendación**

Completar primero `quiz` como slice vertical:

1. Repositorio de dominio y adapter de persistencia.
2. Mapper entre `Quiz` y sus entidades de persistencia.
3. Casos de uso `CreateQuiz`, `AddQuestion`, `PublishQuiz` y `GetQuiz`.
4. Controller REST con DTOs de entrada y salida.
5. Pruebas de dominio e integración PostgreSQL.

`gameplay` debería seguir después. Al introducir tiempo real, definir desde el inicio un canal explícito —WebSocket o SSE— y evitar que la lógica del juego dependa de controladores web.

### Alto — Acoplamiento entre contextos en la aplicación

`organization` invoca directamente casos de uso y repositorios de `identity` en flujos como el registro de organización. Aunque la dependencia no está en el dominio, crea conocimiento directo de las APIs internas del otro contexto.

**Impacto**

- Cambios en `identity` pueden forzar cambios en `organization`.
- Es más difícil extraer o escalar contextos de forma independiente.
- Se mezclan responsabilidades de orquestación entre bounded contexts.

**Recomendación**

Introducir un puerto local en `organization`, por ejemplo `UserProvisioningPort` o `IdentityAccessPort`. Su implementación puede delegar inicialmente a `identity` dentro del mismo monolito. Esto crea una Anti-Corruption Layer y conserva un contrato estable para la aplicación de `organization`.

### Alto — Eventos de dominio sin publicación

`UserCreatedEvent`, `QuizPublishedEvent` y `GameStartedEvent` se registran en los agregados. Sin embargo, `pullDomainEvents()` no se invoca fuera de `AggregateRoot`, por lo que ningún evento llega a un consumidor.

**Impacto**

- Se mantiene complejidad conceptual sin efecto funcional.
- Futuros procesos asíncronos podrían duplicar acciones si se implementan sin estrategia transaccional.

**Recomendación**

Definir una única estrategia antes de añadir consumidores:

- Para efectos internos simples: publicar eventos tras una transacción exitosa.
- Para integración fiable o futura distribución: implementar patrón outbox.

Si no se usará en el corto plazo, documentar el mecanismo como reservado o retirarlo temporalmente.

### Medio — Cobertura de pruebas insuficiente

Hay tres clases de prueba, todas basadas en Spring Boot e integración HTTP. No existen pruebas unitarias para el dominio ni pruebas arquitectónicas.

Las reglas más valiosas todavía no tienen cobertura automatizada:

- `Quiz.publish()` y las condiciones de publicación.
- Máquina de estados de `GameSession`.
- Gestión de miembros de `Organization`.
- Reglas de contraseñas, estados y roles de `User`.
- Restricciones de dependencia entre capas y contextos.

**Recomendación**

Adoptar una pirámide de pruebas:

1. Pruebas unitarias de dominio como capa principal.
2. Pruebas de aplicación con repositorios falsos o adaptadores controlados.
3. Pruebas de integración con PostgreSQL Testcontainers para persistencia, migraciones y HTTP.
4. ArchUnit para garantizar que `domain` no depende de infraestructura y que no se introducen dependencias indebidas entre contextos.

Incorporar JaCoCo como señal complementaria; la cobertura no sustituye pruebas de reglas de negocio.

### Medio — Documentación desactualizada

El README y `docs/arquitectura.md` indican que las tablas de `quiz` y `gameplay` están pendientes o que esos contextos solo contienen dominio. La migración V4 y las entidades JPA demuestran que el estado cambió.

**Impacto**

- Una persona nueva en el equipo tomará decisiones basadas en información incorrecta.
- Se dificulta priorizar el trabajo pendiente.

**Recomendación**

Actualizar la documentación en el mismo pull request que altera la arquitectura o el esquema. Mantener un apartado de estado por contexto:

| Contexto | Dominio | Aplicación | Persistencia | API |
| --- | --- | --- | --- | --- |
| identity | Sí | Sí | Sí | Sí |
| organization | Sí | Sí | Sí | Sí |
| quiz | Sí | No | Parcial | No |
| gameplay | Sí | No | Parcial | No |

### Medio — Límites de agregado no protegidos por el compilador

Las entidades hijas se encuentran en paquetes distintos de sus agregados. Por ello, operaciones como `Question.assignQuizId()` y métodos de `OrganizationMember` son públicas. La regla de modificar hijos solo a través de su aggregate root queda como convención.

**Recomendación**

- Mantener las mutaciones de entidades hijas fuera de DTOs, controllers y repositorios.
- Añadir reglas ArchUnit que impidan invocaciones desde capas no autorizadas cuando sea viable.
- Considerar co-localizar agregados y entidades hijas si se necesita visibilidad de paquete.

### Medio — Mapeo de errores HTTP mejorable

El manejador global trata `DomainException` como `400 Bad Request`. Excepciones de recurso inexistente, como las de usuario u organización no encontrados, terminan representándose también como 400.

**Recomendación**

Estandarizar el contrato de errores:

| Caso | Código recomendado |
| --- | --- |
| Recurso inexistente | `404 Not Found` |
| Email o slug duplicado | `409 Conflict` |
| Regla de negocio inválida | `422 Unprocessable Entity` o `400 Bad Request` |
| Error de validación de request | `400 Bad Request` |
| Sin autenticación | `401 Unauthorized` |
| Sin permisos | `403 Forbidden` |

## Riesgos de escalabilidad

### Aislamiento multi-tenant

La plataforma representa organizaciones, pero la seguridad actual no obliga a comprobar el tenant al acceder a recursos. Antes de escalar horizontalmente o exponer APIs externas, cada query y comando debe derivar la organización desde el usuario autenticado o validar explícitamente su pertenencia.

No se debe aceptar un `organizationId` como única fuente de autorización desde el cliente.

### Consistencia entre contextos

Los contextos comparten el mismo proceso y base de datos, lo cual es adecuado para la etapa actual. Sin embargo, el acceso directo de aplicación a aplicación debe evolucionar hacia puertos explícitos y eventos publicados de forma fiable.

No se recomienda extraer microservicios todavía. Primero se debe consolidar el monolito modular: contratos internos, pruebas de arquitectura, seguridad y observabilidad.

### Modelo temporal y auditoría

El proyecto usa `LocalDateTime` en entidades y eventos. Para un sistema distribuido, con usuarios en distintas zonas horarias y ejecución en múltiples nodos, se recomienda adoptar `Instant` u `OffsetDateTime` para timestamps técnicos y convertir a zona local únicamente en los bordes de entrada/salida.

### Observabilidad y operaciones

No se encontró configuración de CI ni una estrategia visible de métricas, trazas, health checks o alertas.

Para una evolución sostenible:

1. Crear CI que ejecute compilación, pruebas y análisis arquitectónico en cada pull request.
2. Exponer health checks y métricas protegidas.
3. Añadir logging estructurado con identificador de correlación.
4. Registrar `organizationId`, `userId` y operación en logs de auditoría, evitando datos sensibles.

## Hoja de ruta recomendada

### Fase 0 — Recuperar confianza en la entrega

- [ ] Migrar pruebas de integración a PostgreSQL Testcontainers o hacer las migraciones compatibles con H2.
- [ ] Asegurar que `mvn verify` sea verde.
- [ ] Añadir GitHub Actions que ejecute `mvn verify` en push y pull request.
- [ ] Actualizar README y documentación arquitectónica según el estado de V4.

### Fase 1 — Seguridad y aislamiento por organización

- [ ] Implementar autenticación JWT o basada en sesión.
- [ ] Proteger rutas privadas.
- [ ] Autorizar cambios de perfil, contraseña y rol.
- [ ] Autorizar operaciones de organización mediante membresía y rol.
- [ ] Añadir pruebas de `401`, `403` y acceso entre tenants.

### Fase 2 — Completar `quiz`

- [ ] Crear adapters, repositorios y mappers.
- [ ] Implementar casos de uso y API.
- [ ] Probar publicación y persistencia de un quiz.
- [ ] Decidir y ejecutar la estrategia de publicación de `QuizPublishedEvent`.

### Fase 3 — Endurecer la arquitectura

- [ ] Crear Anti-Corruption Layer entre `organization` e `identity`.
- [ ] Añadir ArchUnit y pruebas unitarias de agregados.
- [ ] Adoptar IDs tipados basados en `Identifier`, o eliminar la abstracción si no se usará.
- [ ] Corregir rehidratación de campos de auditoría en mappers.
- [ ] Estandarizar errores HTTP.

### Fase 4 — Completar `gameplay`

- [ ] Implementar repositorio, aplicación y API de sesiones.
- [ ] Diseñar comunicación en tiempo real con WebSocket o SSE.
- [ ] Mantener snapshots de preguntas y opciones para proteger partidas históricas.
- [ ] Definir consistencia, idempotencia y orden de eventos para respuestas concurrentes.

## Criterios de salida para producción inicial

Antes de considerar el sistema listo para usuarios externos, se deben cumplir como mínimo los siguientes criterios:

- Build y migraciones verdes contra PostgreSQL en CI.
- Autenticación y autorización aplicadas a todos los endpoints privados.
- Aislamiento de datos validado con pruebas entre dos organizaciones.
- Pruebas unitarias para reglas centrales de cada agregado activo.
- Pruebas de integración para registro, login, organización y creación/publicación de quiz.
- Contrato de errores estable y documentado.
- Documentación de arquitectura alineada al código.
- Health checks, logs estructurados y un proceso de despliegue reproducible.

## Conclusión

La arquitectura actual es una buena base de monolito modular orientado a DDD. La inversión realizada en separar dominio, aplicación e infraestructura es correcta y debe preservarse.

El foco inmediato debe ser la operabilidad: pruebas reproducibles, seguridad y automatización de entrega. Después, completar `quiz` de punta a punta permitirá validar el patrón arquitectónico con un segundo bounded context antes de afrontar la complejidad de tiempo real de `gameplay`.

Corregir estos puntos en el orden propuesto reducirá riesgo, facilitará la colaboración del equipo y permitirá escalar el producto sin sacrificar los límites de dominio que ya están bien encaminados.
