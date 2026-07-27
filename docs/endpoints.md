# Endpoints — kahoot-clabs

Base URL local: `http://localhost:8081`

Swagger UI: `http://localhost:8081/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8081/v3/api-docs`

> Nota: hoy todos los endpoints están públicos (`permitAll`). No hay JWT ni autorización efectiva.

---

## Identity — Auth

Base: `/api/v1/auth`

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Registrar usuario |
| `POST` | `/api/v1/auth/login` | Login |

---

## Identity — Users

Base: `/api/v1/users`

| Método | Ruta | Descripción | Content-Type |
| --- | --- | --- | --- |
| `GET` | `/api/v1/users/{id}` | Obtener perfil de usuario | — |
| `PUT` | `/api/v1/users/{id}/profile` | Actualizar perfil (opcional avatar) | `multipart/form-data` |
| `PUT` | `/api/v1/users/{id}/password` | Cambiar contraseña | `application/json` |
| `PUT` | `/api/v1/users/{id}/role` | Asignar rol | `application/json` |

---

## Organization

Base: `/api/v1/organizations`

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/organizations/signup` | Signup: crea usuario + organización |
| `POST` | `/api/v1/organizations` | Crear organización |
| `GET` | `/api/v1/organizations/{id}` | Obtener organización |
| `PUT` | `/api/v1/organizations/{id}` | Actualizar organización |
| `POST` | `/api/v1/organizations/{id}/members` | Agregar miembro |
| `POST` | `/api/v1/organizations/{id}/invitations` | Invitar miembro |
| `DELETE` | `/api/v1/organizations/{id}/members/{userId}` | Remover miembro |

---

## Quiz — Categories

Base: `/api/v1/categories`

| Método | Ruta | Descripción | Query |
| --- | --- | --- | --- |
| `POST` | `/api/v1/categories` | Crear categoría | — |
| `GET` | `/api/v1/categories/{id}` | Obtener categoría | — |
| `GET` | `/api/v1/categories` | Listar categorías por organización | `organizationId` (requerido) |
| `PUT` | `/api/v1/categories/{id}` | Actualizar categoría | — |
| `DELETE` | `/api/v1/categories/{id}` | Eliminar categoría | — |

---

## Quiz — Quizzes

Base: `/api/v1/organizations/{organizationId}/quizzes`

### CRUD y listado

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes` | Crear quiz |
| `GET` | `/api/v1/organizations/{organizationId}/quizzes` | Listar quizzes |
| `GET` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}` | Obtener quiz |
| `PUT` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}` | Actualizar quiz |

### Categorías del quiz

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/categories/{categoryId}` | Asignar categoría |
| `DELETE` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/categories/{categoryId}` | Quitar categoría |

### Preguntas

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions` | Agregar pregunta |
| `PUT` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}` | Actualizar pregunta |
| `PUT` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/order` | Reordenar preguntas |     --- esto revisar
| `DELETE` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}` | Eliminar pregunta |

### Opciones de respuesta

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/options` | Agregar opción |
| `PUT` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/options/{optionId}` | Actualizar opción |
| `PUT` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/options/order` | Reordenar opciones |
| `DELETE` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/options/{optionId}` | Eliminar opción |

### Assets / imágenes

| Método | Ruta | Descripción | Content-Type |
| --- | --- | --- | --- |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/assets` | Agregar asset (URL) | `application/json` |
| `PUT` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/assets/{assetId}` | Actualizar asset | `application/json` |
| `DELETE` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/assets/{assetId}` | Eliminar asset | — |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/questions/{questionId}/assets/images` | Subir imagen a S3 | `multipart/form-data` (`file`, opcional `altText`) |

### Ciclo de vida

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/publish` | Publicar quiz |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/archive` | Archivar quiz |
| `POST` | `/api/v1/organizations/{organizationId}/quizzes/{quizId}/duplicate` | Duplicar quiz |

---

## Gameplay — Game Sessions

## revisar como funciona luego
Base: `/api/v1/game-sessions`

| Método | Ruta | Descripción | Query / Body |
| --- | --- | --- | --- |
| `POST` | `/api/v1/game-sessions` | Crear sesión de juego | body: `CreateGameSessionCommand` |
| `GET` | `/api/v1/game-sessions` | Listar sesiones por quiz | query: `quizId` |
| `GET` | `/api/v1/game-sessions/{id}` | Obtener sesión | — |
| `POST` | `/api/v1/game-sessions/{id}/players` | Unirse a sesión por id | body: `JoinGameSessionCommand` |
| `POST` | `/api/v1/game-sessions/by-pin/{pin}/players` | Unirse a sesión por PIN | body: `JoinGameSessionCommand` |
| `POST` | `/api/v1/game-sessions/{id}/start` | Iniciar partida | — |
| `GET` | `/api/v1/game-sessions/{id}/current-question` | Pregunta actual | — |
| `POST` | `/api/v1/game-sessions/{id}/answers` | Enviar respuesta | body: `SubmitAnswerCommand` |
| `POST` | `/api/v1/game-sessions/{id}/current-question/close` | Cerrar pregunta actual | — |
| `POST` | `/api/v1/game-sessions/{id}/next-question` | Avanzar a siguiente pregunta | — |
| `GET` | `/api/v1/game-sessions/{id}/leaderboard` | Leaderboard | — |
| `POST` | `/api/v1/game-sessions/{id}/finish` | Finalizar partida | — |
| `POST` | `/api/v1/game-sessions/{id}/cancel` | Cancelar partida | — |
| `GET` | `/api/v1/game-sessions/{id}/results` | Resultados finales | — |

---

## Resumen por contexto

| Contexto | Controller | Endpoints |
| --- | --- | --- |
| Identity | `AuthController` | 2 |
| Identity | `UserController` | 4 |
| Organization | `OrganizationController` | 7 |
| Quiz | `CategoryController` | 5 |
| Quiz | `QuizController` | 22 |
| Gameplay | `GameSessionController` | 14 |
| **Total** | | **54** |
