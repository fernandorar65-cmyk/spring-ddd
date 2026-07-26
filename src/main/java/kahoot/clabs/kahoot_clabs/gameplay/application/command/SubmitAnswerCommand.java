package kahoot.clabs.kahoot_clabs.gameplay.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record SubmitAnswerCommand(
        @NotNull UUID sessionPlayerId,
        @NotNull UUID sessionQuestionId,
        @NotNull UUID answerOptionId
) {
}
