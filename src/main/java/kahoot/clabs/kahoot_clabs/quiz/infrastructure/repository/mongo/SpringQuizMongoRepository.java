package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuizDocument;

public interface SpringQuizMongoRepository extends MongoRepository<QuizDocument, UUID> {

    List<QuizDocument> findByOrganizationId(UUID organizationId);

    List<QuizDocument> findByOrganizationIdAndStatus(UUID organizationId, String status);

    List<QuizDocument> findByStatus(String status);

    List<QuizDocument> findByOrganizationIdOrderByUpdatedAtDesc(UUID organizationId);

    boolean existsByOrganizationIdAndId(UUID organizationId, UUID id);
}
