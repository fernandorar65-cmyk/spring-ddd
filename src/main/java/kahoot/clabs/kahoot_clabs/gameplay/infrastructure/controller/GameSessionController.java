package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.CreateGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.JoinGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.SubmitAnswerCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.AnswerSubmissionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.CurrentQuestionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameResultsResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.LeaderboardResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuestionResultResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.CancelGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.CloseCurrentQuestionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.CreateGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.FinishGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetCurrentQuestionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetGameResultsUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetLeaderboardUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.JoinGameSessionByPinUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.JoinGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.ListGameSessionsByQuizUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.MoveToNextQuestionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.StartGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.SubmitAnswerUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/game-sessions")
@Tag(name = "Game Sessions", description = "Partidas en vivo: lobby, preguntas, respuestas, leaderboard y resultados")
public class GameSessionController {

    private final CreateGameSessionUseCase createGameSessionUseCase;
    private final GetGameSessionUseCase getGameSessionUseCase;
    private final ListGameSessionsByQuizUseCase listGameSessionsByQuizUseCase;
    private final JoinGameSessionUseCase joinGameSessionUseCase;
    private final JoinGameSessionByPinUseCase joinGameSessionByPinUseCase;
    private final StartGameSessionUseCase startGameSessionUseCase;
    private final GetCurrentQuestionUseCase getCurrentQuestionUseCase;
    private final SubmitAnswerUseCase submitAnswerUseCase;
    private final CloseCurrentQuestionUseCase closeCurrentQuestionUseCase;
    private final MoveToNextQuestionUseCase moveToNextQuestionUseCase;
    private final GetLeaderboardUseCase getLeaderboardUseCase;
    private final FinishGameSessionUseCase finishGameSessionUseCase;
    private final CancelGameSessionUseCase cancelGameSessionUseCase;
    private final GetGameResultsUseCase getGameResultsUseCase;

    public GameSessionController(
            CreateGameSessionUseCase createGameSessionUseCase,
            GetGameSessionUseCase getGameSessionUseCase,
            ListGameSessionsByQuizUseCase listGameSessionsByQuizUseCase,
            JoinGameSessionUseCase joinGameSessionUseCase,
            JoinGameSessionByPinUseCase joinGameSessionByPinUseCase,
            StartGameSessionUseCase startGameSessionUseCase,
            GetCurrentQuestionUseCase getCurrentQuestionUseCase,
            SubmitAnswerUseCase submitAnswerUseCase,
            CloseCurrentQuestionUseCase closeCurrentQuestionUseCase,
            MoveToNextQuestionUseCase moveToNextQuestionUseCase,
            GetLeaderboardUseCase getLeaderboardUseCase,
            FinishGameSessionUseCase finishGameSessionUseCase,
            CancelGameSessionUseCase cancelGameSessionUseCase,
            GetGameResultsUseCase getGameResultsUseCase) {
        this.createGameSessionUseCase = createGameSessionUseCase;
        this.getGameSessionUseCase = getGameSessionUseCase;
        this.listGameSessionsByQuizUseCase = listGameSessionsByQuizUseCase;
        this.joinGameSessionUseCase = joinGameSessionUseCase;
        this.joinGameSessionByPinUseCase = joinGameSessionByPinUseCase;
        this.startGameSessionUseCase = startGameSessionUseCase;
        this.getCurrentQuestionUseCase = getCurrentQuestionUseCase;
        this.submitAnswerUseCase = submitAnswerUseCase;
        this.closeCurrentQuestionUseCase = closeCurrentQuestionUseCase;
        this.moveToNextQuestionUseCase = moveToNextQuestionUseCase;
        this.getLeaderboardUseCase = getLeaderboardUseCase;
        this.finishGameSessionUseCase = finishGameSessionUseCase;
        this.cancelGameSessionUseCase = cancelGameSessionUseCase;
        this.getGameResultsUseCase = getGameResultsUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Crear sesión de juego",
            description = "Crea una partida en lobby a partir de un quiz publicado. Copia un snapshot de preguntas/opciones y genera un PIN.")
    public ResponseEntity<ApiResponse<GameSessionResponse>> create(@Valid @RequestBody CreateGameSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED, "Game session created", createGameSessionUseCase.execute(command)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sesión", description = "Devuelve el estado actual de la sesión de juego (jugadores, status, pregunta actual).")
    public ResponseEntity<ApiResponse<GameSessionResponse>> getById(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session retrieved", getGameSessionUseCase.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Listar sesiones por quiz", description = "Lista las sesiones de juego asociadas a un quiz.")
    public ResponseEntity<ApiResponse<List<GameSessionResponse>>> listByQuiz(
            @Parameter(description = "Identificador del quiz") @RequestParam UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game sessions retrieved", listGameSessionsByQuizUseCase.execute(quizId)));
    }

    @PostMapping("/{id}/players")
    @Operation(summary = "Unirse por id de sesión", description = "Agrega un jugador a la sesión usando el UUID de la partida (estado lobby).")
    public ResponseEntity<ApiResponse<GameSessionResponse>> join(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id,
            @Valid @RequestBody JoinGameSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED, "Player joined game session", joinGameSessionUseCase.execute(id, command)));
    }

    @PostMapping("/by-pin/{pin}/players")
    @Operation(summary = "Unirse por PIN", description = "Agrega un jugador a la sesión usando el PIN de lobby (flujo típico del jugador).")
    public ResponseEntity<ApiResponse<GameSessionResponse>> joinByPin(
            @Parameter(description = "PIN de la partida") @PathVariable String pin,
            @Valid @RequestBody JoinGameSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        HttpStatus.CREATED,
                        "Player joined game session",
                        joinGameSessionByPinUseCase.execute(pin, command)));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Iniciar partida", description = "Pasa la sesión de lobby a running, abre la primera pregunta. Requiere jugadores y preguntas.")
    public ResponseEntity<ApiResponse<GameSessionResponse>> start(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session started", startGameSessionUseCase.execute(id)));
    }

    @GetMapping("/{id}/current-question")
    @Operation(summary = "Pregunta actual", description = "Devuelve la pregunta abierta actualmente para que host/jugadores la muestren.")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> currentQuestion(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Current question retrieved", getCurrentQuestionUseCase.execute(id)));
    }

    @PostMapping("/{id}/answers")
    @Operation(summary = "Enviar respuesta", description = "Registra la respuesta de un jugador a la pregunta actual y calcula puntos según reglas de dominio.")
    public ResponseEntity<ApiResponse<AnswerSubmissionResponse>> submitAnswer(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id,
            @Valid @RequestBody SubmitAnswerCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED, "Answer submitted", submitAnswerUseCase.execute(id, command)));
    }

    @PostMapping("/{id}/current-question/close")
    @Operation(summary = "Cerrar pregunta actual", description = "Cierra la pregunta en curso y devuelve el resultado parcial (quién acertó, etc.).")
    public ResponseEntity<ApiResponse<QuestionResultResponse>> closeCurrentQuestion(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Current question closed", closeCurrentQuestionUseCase.execute(id)));
    }

    @PostMapping("/{id}/next-question")
    @Operation(summary = "Siguiente pregunta", description = "Avanza a la siguiente pregunta del snapshot y la abre.")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> nextQuestion(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Advanced to next question", moveToNextQuestionUseCase.execute(id)));
    }

    @GetMapping("/{id}/leaderboard")
    @Operation(summary = "Leaderboard", description = "Devuelve el ranking actual de jugadores por puntaje.")
    public ResponseEntity<ApiResponse<LeaderboardResponse>> leaderboard(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Leaderboard retrieved", getLeaderboardUseCase.execute(id)));
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "Finalizar partida", description = "Marca la sesión como finalizada cuando el host cierra el juego.")
    public ResponseEntity<ApiResponse<GameSessionResponse>> finish(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session finished", finishGameSessionUseCase.execute(id)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar partida", description = "Cancela la sesión (por ejemplo desde lobby o si el host aborta).")
    public ResponseEntity<ApiResponse<GameSessionResponse>> cancel(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session cancelled", cancelGameSessionUseCase.execute(id)));
    }

    @GetMapping("/{id}/results")
    @Operation(summary = "Resultados finales", description = "Devuelve el resumen final de la partida (ranking y métricas).")
    public ResponseEntity<ApiResponse<GameResultsResponse>> results(
            @Parameter(description = "Identificador de la sesión") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game results retrieved", getGameResultsUseCase.execute(id)));
    }
}
