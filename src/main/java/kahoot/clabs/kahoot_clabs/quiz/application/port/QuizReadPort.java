package kahoot.clabs.kahoot_clabs.quiz.application.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModel;

/**
 * Query-side port for reading quiz projections.
 */
public interface QuizReadPort {

    Optional<QuizReadModel> findById(UUID id);

    List<QuizReadModel> findByOrganizationId(UUID organizationId);

    List<QuizReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status);

    List<QuizReadModel> findByOrganizationIdOrderByUpdatedAtDesc(UUID organizationId);

    boolean existsByOrganizationIdAndId(UUID organizationId, UUID id);
}
