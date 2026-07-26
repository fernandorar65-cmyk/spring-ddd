package kahoot.clabs.kahoot_clabs.gameplay.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateGameSessionCommand(
        @NotNull UUID organizationId,
        @NotNull UUID quizId,
        @NotNull UUID hostUserId
) {
}
