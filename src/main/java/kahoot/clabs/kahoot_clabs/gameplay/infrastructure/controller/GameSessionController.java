package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import kahoot.clabs.kahoot_clabs.gameplay.application.command.HostActionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.JoinSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.LeaveSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.OpenQuestionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.SubmitAnswerCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.UpdateNicknameCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.LeaderboardEntryResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.PlayerAnswerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuestionResultResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.SessionPlayerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.SessionQuestionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.CreateGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetLeaderboardUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetMyAnswersUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetSessionQuestionsUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.ListGameSessionsUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.ManageSessionLifecycleUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.ManageSessionPlayersUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.ManageSessionQuestionsUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.SubmitAnswerUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/sessions")
@Tag(name = "Gameplay Sessions", description = "Sesiones de juego, lobby, preguntas, respuestas y ranking")
public class GameSessionController {

    private final CreateGameSessionUseCase createGameSessionUseCase;
    private final GetGameSessionUseCase getGameSessionUseCase;
    private final ListGameSessionsUseCase listGameSessionsUseCase;
    private final ManageSessionLifecycleUseCase manageSessionLifecycleUseCase;
    private final ManageSessionPlayersUseCase manageSessionPlayersUseCase;
    private final ManageSessionQuestionsUseCase manageSessionQuestionsUseCase;
    private final GetSessionQuestionsUseCase getSessionQuestionsUseCase;
    private final SubmitAnswerUseCase submitAnswerUseCase;
    private final GetMyAnswersUseCase getMyAnswersUseCase;
    private final GetLeaderboardUseCase getLeaderboardUseCase;

    public GameSessionController(
            CreateGameSessionUseCase createGameSessionUseCase,
            GetGameSessionUseCase getGameSessionUseCase,
            ListGameSessionsUseCase listGameSessionsUseCase,
            ManageSessionLifecycleUseCase manageSessionLifecycleUseCase,
            ManageSessionPlayersUseCase manageSessionPlayersUseCase,
            ManageSessionQuestionsUseCase manageSessionQuestionsUseCase,
            GetSessionQuestionsUseCase getSessionQuestionsUseCase,
            SubmitAnswerUseCase submitAnswerUseCase,
            GetMyAnswersUseCase getMyAnswersUseCase,
            GetLeaderboardUseCase getLeaderboardUseCase) {
        this.createGameSessionUseCase = createGameSessionUseCase;
        this.getGameSessionUseCase = getGameSessionUseCase;
        this.listGameSessionsUseCase = listGameSessionsUseCase;
        this.manageSessionLifecycleUseCase = manageSessionLifecycleUseCase;
        this.manageSessionPlayersUseCase = manageSessionPlayersUseCase;
        this.manageSessionQuestionsUseCase = manageSessionQuestionsUseCase;
        this.getSessionQuestionsUseCase = getSessionQuestionsUseCase;
        this.submitAnswerUseCase = submitAnswerUseCase;
        this.getMyAnswersUseCase = getMyAnswersUseCase;
        this.getLeaderboardUseCase = getLeaderboardUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear sesión", description = "Crea una sesión en LOBBY desde un quiz publicado y congela el snapshot.")
    public ResponseEntity<ApiResponse<GameSessionResponse>> create(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateGameSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Game session created",
                createGameSessionUseCase.execute(organizationId, command)));
    }

    @GetMapping
    @Operation(summary = "Listar sesiones", description = "Lista sesiones de la organización. Filtros: status (csv), quizId.")
    public ResponseEntity<ApiResponse<List<GameSessionResponse>>> list(
            @PathVariable UUID organizationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Game sessions listed",
                listGameSessionsUseCase.execute(organizationId, status, quizId)));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Obtener sesión")
    public ResponseEntity<ApiResponse<GameSessionResponse>> get(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Game session retrieved",
                getGameSessionUseCase.execute(organizationId, sessionId)));
    }

    @PostMapping("/{sessionId}/start")
    @Operation(summary = "Iniciar sesión", description = "Sale de LOBBY y abre la primera pregunta.")
    public ResponseEntity<ApiResponse<GameSessionResponse>> start(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody HostActionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Game session started",
                manageSessionLifecycleUseCase.start(organizationId, sessionId, command)));
    }

    @PostMapping("/{sessionId}/cancel")
    @Operation(summary = "Cancelar sesión")
    public ResponseEntity<ApiResponse<GameSessionResponse>> cancel(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody HostActionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Game session cancelled",
                manageSessionLifecycleUseCase.cancel(organizationId, sessionId, command)));
    }

    @PostMapping("/{sessionId}/finish")
    @Operation(summary = "Finalizar sesión")
    public ResponseEntity<ApiResponse<GameSessionResponse>> finish(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody HostActionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Game session finished",
                manageSessionLifecycleUseCase.finish(organizationId, sessionId, command)));
    }

    @PostMapping("/{sessionId}/join")
    @Operation(summary = "Unirse a la sesión")
    public ResponseEntity<ApiResponse<GameSessionResponse>> join(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody JoinSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Joined game session",
                manageSessionPlayersUseCase.join(organizationId, sessionId, command)));
    }

    @PostMapping("/{sessionId}/leave")
    @Operation(summary = "Salir de la sesión")
    public ResponseEntity<ApiResponse<GameSessionResponse>> leave(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody LeaveSessionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Left game session",
                manageSessionPlayersUseCase.leave(organizationId, sessionId, command)));
    }

    @GetMapping("/{sessionId}/players")
    @Operation(summary = "Listar jugadores")
    public ResponseEntity<ApiResponse<List<SessionPlayerResponse>>> players(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Session players listed",
                manageSessionPlayersUseCase.listPlayers(organizationId, sessionId)));
    }

    @PatchMapping("/{sessionId}/players/me")
    @Operation(summary = "Actualizar nickname propio")
    public ResponseEntity<ApiResponse<SessionPlayerResponse>> updateNickname(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody UpdateNicknameCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Nickname updated",
                manageSessionPlayersUseCase.updateNickname(organizationId, sessionId, command)));
    }

    @PostMapping("/{sessionId}/questions/open")
    @Operation(summary = "Abrir pregunta", description = "Abre la pregunta actual o el índice indicado.")
    public ResponseEntity<ApiResponse<GameSessionResponse>> openQuestion(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody OpenQuestionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Question opened",
                manageSessionQuestionsUseCase.open(organizationId, sessionId, command)));
    }

    @PostMapping("/{sessionId}/questions/close")
    @Operation(summary = "Cerrar pregunta actual")
    public ResponseEntity<ApiResponse<GameSessionResponse>> closeQuestion(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody HostActionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Question closed",
                manageSessionQuestionsUseCase.close(organizationId, sessionId, command)));
    }

    @PostMapping("/{sessionId}/questions/next")
    @Operation(summary = "Siguiente pregunta", description = "Avanza a la siguiente o finaliza si no quedan.")
    public ResponseEntity<ApiResponse<GameSessionResponse>> nextQuestion(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody HostActionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Advanced to next question",
                manageSessionQuestionsUseCase.next(organizationId, sessionId, command)));
    }

    @GetMapping("/{sessionId}/questions")
    @Operation(summary = "Listar preguntas del snapshot")
    public ResponseEntity<ApiResponse<List<SessionQuestionResponse>>> listQuestions(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Parameter(description = "Si true, puede revelar correctas en preguntas cerradas")
            @RequestParam(defaultValue = "false") boolean asHost) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Session questions listed",
                getSessionQuestionsUseCase.list(organizationId, sessionId, asHost)));
    }

    @GetMapping("/{sessionId}/questions/current")
    @Operation(summary = "Pregunta actual")
    public ResponseEntity<ApiResponse<SessionQuestionResponse>> currentQuestion(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Current question retrieved",
                getSessionQuestionsUseCase.current(organizationId, sessionId)));
    }

    @GetMapping("/{sessionId}/questions/{sessionQuestionId}/result")
    @Operation(summary = "Resultado de una pregunta")
    public ResponseEntity<ApiResponse<QuestionResultResponse>> questionResult(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @PathVariable UUID sessionQuestionId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Question result retrieved",
                getSessionQuestionsUseCase.result(organizationId, sessionId, sessionQuestionId)));
    }

    @PostMapping("/{sessionId}/answers")
    @Operation(summary = "Enviar respuesta")
    public ResponseEntity<ApiResponse<PlayerAnswerResponse>> submitAnswer(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody SubmitAnswerCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Answer submitted",
                submitAnswerUseCase.execute(organizationId, sessionId, command)));
    }

    @GetMapping("/{sessionId}/answers/me")
    @Operation(summary = "Mis respuestas")
    public ResponseEntity<ApiResponse<List<PlayerAnswerResponse>>> myAnswers(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Player answers retrieved",
                getMyAnswersUseCase.execute(organizationId, sessionId, userId)));
    }

    @GetMapping("/{sessionId}/leaderboard")
    @Operation(summary = "Ranking de la sesión")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> leaderboard(
            @PathVariable UUID organizationId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Leaderboard retrieved",
                getLeaderboardUseCase.execute(organizationId, sessionId)));
    }
}
