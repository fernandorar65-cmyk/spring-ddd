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
    public ResponseEntity<ApiResponse<GameSessionResponse>> create(@Valid @RequestBody CreateGameSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED, "Game session created", createGameSessionUseCase.execute(command)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameSessionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session retrieved", getGameSessionUseCase.execute(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GameSessionResponse>>> listByQuiz(@RequestParam UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game sessions retrieved", listGameSessionsByQuizUseCase.execute(quizId)));
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<ApiResponse<GameSessionResponse>> join(
            @PathVariable UUID id,
            @Valid @RequestBody JoinGameSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED, "Player joined game session", joinGameSessionUseCase.execute(id, command)));
    }

    @PostMapping("/by-pin/{pin}/players")
    public ResponseEntity<ApiResponse<GameSessionResponse>> joinByPin(
            @PathVariable String pin,
            @Valid @RequestBody JoinGameSessionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        HttpStatus.CREATED,
                        "Player joined game session",
                        joinGameSessionByPinUseCase.execute(pin, command)));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<GameSessionResponse>> start(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session started", startGameSessionUseCase.execute(id)));
    }

    @GetMapping("/{id}/current-question")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> currentQuestion(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Current question retrieved", getCurrentQuestionUseCase.execute(id)));
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<ApiResponse<AnswerSubmissionResponse>> submitAnswer(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitAnswerCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED, "Answer submitted", submitAnswerUseCase.execute(id, command)));
    }

    @PostMapping("/{id}/current-question/close")
    public ResponseEntity<ApiResponse<QuestionResultResponse>> closeCurrentQuestion(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Current question closed", closeCurrentQuestionUseCase.execute(id)));
    }

    @PostMapping("/{id}/next-question")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> nextQuestion(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Advanced to next question", moveToNextQuestionUseCase.execute(id)));
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<ApiResponse<LeaderboardResponse>> leaderboard(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Leaderboard retrieved", getLeaderboardUseCase.execute(id)));
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<ApiResponse<GameSessionResponse>> finish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session finished", finishGameSessionUseCase.execute(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<GameSessionResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game session cancelled", cancelGameSessionUseCase.execute(id)));
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<ApiResponse<GameResultsResponse>> results(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Game results retrieved", getGameResultsUseCase.execute(id)));
    }
}
