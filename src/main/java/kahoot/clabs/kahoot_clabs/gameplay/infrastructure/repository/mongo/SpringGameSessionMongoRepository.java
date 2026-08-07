package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.GameSessionDocument;

public interface SpringGameSessionMongoRepository extends MongoRepository<GameSessionDocument, UUID> {

    List<GameSessionDocument> findByOrganizationId(UUID organizationId);

    List<GameSessionDocument> findByOrganizationIdAndStatus(UUID organizationId, String status);

    List<GameSessionDocument> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);

    List<GameSessionDocument> findByOrganizationIdAndQuizIdOrderByCreatedAtDesc(
            UUID organizationId, UUID quizId);

    List<GameSessionDocument> findByOrganizationIdAndStatusInOrderByCreatedAtDesc(
            UUID organizationId, Collection<String> statuses);

    List<GameSessionDocument> findByOrganizationIdAndQuizIdAndStatusInOrderByCreatedAtDesc(
            UUID organizationId, UUID quizId, Collection<String> statuses);

    List<GameSessionDocument> findByQuizId(UUID quizId);

    List<GameSessionDocument> findByHostUserId(UUID hostUserId);

    boolean existsByOrganizationIdAndId(UUID organizationId, UUID id);
}
