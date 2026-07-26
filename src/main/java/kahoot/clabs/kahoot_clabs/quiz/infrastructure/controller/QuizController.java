package kahoot.clabs.kahoot_clabs.quiz.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.quiz.application.command.CreateQuizCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.CreateQuizUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.GetQuizUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.ListQuizzesUseCase;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/quizzes")
public class QuizController {

    private final CreateQuizUseCase createQuizUseCase;
    private final GetQuizUseCase getQuizUseCase;
    private final ListQuizzesUseCase listQuizzesUseCase;

    public QuizController(
            CreateQuizUseCase createQuizUseCase,
            GetQuizUseCase getQuizUseCase,
            ListQuizzesUseCase listQuizzesUseCase) {
        this.createQuizUseCase = createQuizUseCase;
        this.getQuizUseCase = getQuizUseCase;
        this.listQuizzesUseCase = listQuizzesUseCase;
    }

    @PostMapping
    public ResponseEntity<QuizResponse> create(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateQuizCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createQuizUseCase.execute(organizationId, command));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getById(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId) {
        return ResponseEntity.ok(getQuizUseCase.execute(organizationId, quizId));
    }

    @GetMapping
    public ResponseEntity<List<QuizResponse>> list(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(listQuizzesUseCase.execute(organizationId));
    }
}
