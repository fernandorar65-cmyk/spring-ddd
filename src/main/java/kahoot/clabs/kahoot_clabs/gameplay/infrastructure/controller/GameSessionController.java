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
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.CreateGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.GetGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.JoinGameSessionByPinUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.JoinGameSessionUseCase;
import kahoot.clabs.kahoot_clabs.gameplay.application.usecase.ListGameSessionsByQuizUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/game-sessions")
public class GameSessionController {

    private final CreateGameSessionUseCase createGameSessionUseCase;
    private final GetGameSessionUseCase getGameSessionUseCase;
    private final ListGameSessionsByQuizUseCase listGameSessionsByQuizUseCase;
    private final JoinGameSessionUseCase joinGameSessionUseCase;
    private final JoinGameSessionByPinUseCase joinGameSessionByPinUseCase;

    public GameSessionController(
            CreateGameSessionUseCase createGameSessionUseCase,
            GetGameSessionUseCase getGameSessionUseCase,
            ListGameSessionsByQuizUseCase listGameSessionsByQuizUseCase,
            JoinGameSessionUseCase joinGameSessionUseCase,
            JoinGameSessionByPinUseCase joinGameSessionByPinUseCase) {
        this.createGameSessionUseCase = createGameSessionUseCase;
        this.getGameSessionUseCase = getGameSessionUseCase;
        this.listGameSessionsByQuizUseCase = listGameSessionsByQuizUseCase;
        this.joinGameSessionUseCase = joinGameSessionUseCase;
        this.joinGameSessionByPinUseCase = joinGameSessionByPinUseCase;
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
}
