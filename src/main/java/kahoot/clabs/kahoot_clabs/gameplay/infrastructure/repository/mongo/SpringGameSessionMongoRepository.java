package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.GameSessionReadDocument;

/**
 * Spring Data Mongo derived queries (Prisma/EF-style method names).
 * Inherited: save, findById, findAll, deleteById, existsById, count, …
 */
public interface SpringGameSessionMongoRepository extends MongoRepository<GameSessionReadDocument, UUID> {

    List<GameSessionReadDocument> findByOrganizationId(UUID organizationId);

    List<GameSessionReadDocument> findByOrganizationIdAndStatus(UUID organizationId, String status);

    List<GameSessionReadDocument> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);

    List<GameSessionReadDocument> findByOrganizationIdAndQuizIdOrderByCreatedAtDesc(
            UUID organizationId, UUID quizId);

    List<GameSessionReadDocument> findByOrganizationIdAndStatusInOrderByCreatedAtDesc(
            UUID organizationId, Collection<String> statuses);

    List<GameSessionReadDocument> findByOrganizationIdAndQuizIdAndStatusInOrderByCreatedAtDesc(
            UUID organizationId, UUID quizId, Collection<String> statuses);

    List<GameSessionReadDocument> findByQuizId(UUID quizId);

    List<GameSessionReadDocument> findByHostUserId(UUID hostUserId);

    boolean existsByOrganizationIdAndId(UUID organizationId, UUID id);
}
