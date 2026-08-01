# Inventario de tablas del proyecto

Estado al cruzar:

- Migraciones Flyway: `V1__schema.sql`, `V2__organization_catalogs.sql`, `V3__drop_gameplay.sql`
- Entidades JPA (`infrastructure/persistence`)
- Conceptos de dominio (`domain`)

`ddl-auto: validate` → Hibernate **no** crea tablas; solo valida. El esquema lo crea Flyway.

---

## Leyenda

| Estado | Significado |
|--------|-------------|
| **En BD (Flyway)** | Existe `CREATE TABLE` en migraciones activas |
| **JPA Entity** | Hay clase `@Entity` |
| **JoinTable** | Tabla de unión vía `@JoinTable` / `@ManyToMany` (sin entidad propia) |
| **Solo dominio** | Existe en dominio, sin tabla / sin mapeo JPA propio |
| **Eliminada** | Se crea en V1 histórico y se elimina en V3 |

---

## 1. Identity

| Tabla | Descripción corta | Flyway | JPA | Dominio |
|-------|-------------------|--------|-----|---------|
| `permissions` | Permisos del sistema | V1 | `PermissionEntity` | `Permission` |
| `roles` | Roles | V1 | `RoleEntity` | `Role` (aggregate) |
| `role_permissions` | N:N rol ↔ permiso | V1 | `@JoinTable` en `RoleEntity` (**sin** `*Entity`) | `RolePermission` |
| `users` | Usuarios | V1 | `UserEntity` | `User` (aggregate) |
| `user_images` | Imágenes del usuario | V1 | `UserImagesEntity` | `UserImages` |

---

## 2. Organization

| Tabla | Descripción corta | Flyway | JPA | Dominio |
|-------|-------------------|--------|-----|---------|
| `organizations` | Organizaciones / tenants | V1 | `OrganizationEntity` | `aggregate.Organization` |
| `organization_members` | Miembros de una org | V1 | `OrganizationMemberEntity` | `OrganizationMember` |
| `organization_departments` | Catálogo de departamentos | V2 | `OrganizationDepartmentEntity` | `OrganizationDepartment` |
| `organization_jobs` | Catálogo de puestos | V2 | `OrganizationJobEntity` | `OrganizationJob` |
| `organization_statuses` | Catálogo estados de org | V2 | `OrganizationStatusCatalogEntity` | `OrganizationStatusCatalog` |
| `organization_member_statuses` | Catálogo estados de miembro | V2 | `OrganizationMemberStatusCatalogEntity` | `OrganizationMemberStatusCatalog` |

### Notas organization

- `organization.domain.entity.Organization` es un **duplicado** del aggregate; **no** tiene tabla aparte.
- Los statuses de org/miembro también viven como **enums** usados en columnas `VARCHAR`.

---

## 3. Quiz

| Tabla | Descripción corta | Flyway | JPA | Dominio |
|-------|-------------------|--------|-----|---------|
| `quizzes` | Quizzes | V1 | `QuizEntity` | `Quiz` (aggregate) |
| `categories` | Categorías por org | V1 | `CategoryEntity` | `Category` |
| `quiz_categories` | N:N quiz ↔ categoría | V1 | `QuizCategoryEntity` | (relación en aggregate/repos) |
| `questions` | Preguntas del quiz | V1 | `QuestionEntity` | `Question` |
| `answer_options` | Opciones de respuesta | V1 | `AnswerOptionEntity` | `AnswerOption` |
| `question_assets` | Media de una pregunta | V1 | `QuestionAssetEntity` | `QuestionAsset` |

---

## 4. Gameplay — retirado

El bounded context `gameplay` se eliminó del código (endpoints, dominio, JPA) hasta definir el flujo.

| Tabla | Estado |
|-------|--------|
| `game_sessions` | Eliminada en V3 |
| `session_players` | Eliminada en V3 |
| `session_questions` | Eliminada en V3 |
| `session_answer_options` | Eliminada en V3 |
| `player_answers` | Eliminada en V3 |
| `session_leaderboard` | Eliminada en V3 |

Nota: V1 aún contiene el `CREATE` histórico (no se edita una migración ya aplicada). V3 hace `DROP TABLE IF EXISTS`.

---

## 5. Pendientes de migración

**Ninguna** respecto al código activo (`identity`, `organization`, `quiz`).

---

## 6. Conceptos de dominio sin tabla propia

| Concepto | Por qué no tiene tabla |
|----------|------------------------|
| `RolePermission` | Se persiste vía join `role_permissions` |
| `UserProfile` (VO) | Campos embebidos en `users` |
| `QuizSettings` (VO) | Campos embebidos en `quizzes` |
| Enums de status | Valores en columnas `VARCHAR` |
| `entity.Organization` (duplicado) | No se usa como persistencia |

---

## 7. Resumen de conteos (esquema activo)

| Origen | Cantidad |
|--------|----------|
| Tablas activas tras V3 | **17** |
| `@Entity` JPA | **16** |
| Solo `@JoinTable` | **1** (`role_permissions`) |

| Contexto | Tablas |
|----------|--------|
| Identity | 5 |
| Organization | 6 |
| Quiz | 6 |
| Gameplay | 0 (retirado) |
| **Total activo** | **17** |
