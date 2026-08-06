package kahoot.clabs.kahoot_clabs.gameplay.application.readmodel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read model for game session queries (Mongo). Independent from the domain aggregate.
 */
public record GameSessionReadModel(
        UUID id,
        UUID organizationId,
        UUID quizId,
        UUID hostUserId,
        String status,
        int currentQuestionIndex,
        int playerCount,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
