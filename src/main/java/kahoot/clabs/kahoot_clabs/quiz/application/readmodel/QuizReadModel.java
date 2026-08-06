package kahoot.clabs.kahoot_clabs.quiz.application.readmodel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Read model for quiz queries (Mongo). Independent from the domain aggregate.
 */
public record QuizReadModel(
        UUID id,
        UUID organizationId,
        UUID createdById,
        String title,
        String description,
        String thumbnail,
        String status,
        String difficulty,
        Long estimatedTimeMinutes,
        int playCount,
        double averageRating,
        boolean template,
        List<UUID> categoryIds,
        int questionCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
