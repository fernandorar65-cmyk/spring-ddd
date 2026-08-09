package kahoot.clabs.kahoot_clabs.gameplay.application.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.QuestionType;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.QuizDifficulty;

public record QuestionCommand(
        @NotBlank @Size(max = 500) String title,
        String description,
        @NotNull QuestionType type,
        @NotNull QuizDifficulty difficulty,
        @Positive int points,
        @Positive @Max(300) int timeLimitSeconds) {
}
