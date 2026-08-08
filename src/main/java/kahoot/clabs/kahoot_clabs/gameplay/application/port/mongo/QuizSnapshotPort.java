package kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot;

/**
 * Anti-corruption port: other BCs obtain a published quiz snapshot without importing Quiz aggregates.
 */
public interface QuizSnapshotPort {

    /**
     * Returns a snapshot only when the quiz exists, belongs to the organization and is PUBLISHED.
     */
    Optional<PublishedQuizSnapshot> findPublishedByOrganizationAndId(UUID organizationId, UUID quizId);
}
