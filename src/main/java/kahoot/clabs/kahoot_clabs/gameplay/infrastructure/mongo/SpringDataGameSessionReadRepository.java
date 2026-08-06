package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data Mongo derived queries (Prisma/EF-style method names).
 * Inherited: save, findById, findAll, deleteById, existsById, count, …
 */
public interface SpringDataGameSessionReadRepository extends MongoRepository<GameSessionReadDocument, UUID> {

    List<GameSessionReadDocument> findByOrganizationId(UUID organizationId);

    List<GameSessionReadDocument> findByOrganizationIdAndStatus(UUID organizationId, String status);

    List<GameSessionReadDocument> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);

    List<GameSessionReadDocument> findByQuizId(UUID quizId);

    List<GameSessionReadDocument> findByHostUserId(UUID hostUserId);

    boolean existsByOrganizationIdAndId(UUID organizationId, UUID id);
}
