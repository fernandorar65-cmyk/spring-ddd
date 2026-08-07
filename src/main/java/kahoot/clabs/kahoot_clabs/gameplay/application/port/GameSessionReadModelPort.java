package kahoot.clabs.kahoot_clabs.gameplay.application.port;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;

/**
 * Query-side port for game session read models (Mongo).
 * Write path stays on Postgres/JPA adapters.
 */
public interface GameSessionReadModelPort {

    Optional<GameSessionReadModel> findById(UUID id);

    List<GameSessionReadModel> findByOrganizationId(UUID organizationId);

    List<GameSessionReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status);

    List<GameSessionReadModel> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);

    /**
     * Filtered listing: empty/null statuses = no status filter; null quizId = no quiz filter.
     */
    List<GameSessionReadModel> search(UUID organizationId, Collection<String> statuses, UUID quizId);

    List<GameSessionReadModel> findByQuizId(UUID quizId);

    boolean existsByOrganizationIdAndId(UUID organizationId, UUID id);

    void save(GameSessionReadModel readModel);

    void deleteById(UUID id);
}
