package kahoot.clabs.kahoot_clabs.gameplay.application.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.AnswerOptionSnapshot;

/**
 * Anti-corruption port used by Gameplay to obtain an immutable view of a quiz
 * without coupling its application use cases to the Quiz domain model.
 */
public interface QuizSnapshotPort {

    Optional<PublishedQuizSnapshot> findPublishedById(UUID quizId);

    record PublishedQuizSnapshot(UUID organizationId, List<QuestionSnapshot> questions) {
    }

    record QuestionSnapshot(
            UUID id,
            String title,
            String description,
            String type,
            int points,
            int timeLimitSeconds,
            List<AnswerOptionSnapshot> options) {
    }
}
