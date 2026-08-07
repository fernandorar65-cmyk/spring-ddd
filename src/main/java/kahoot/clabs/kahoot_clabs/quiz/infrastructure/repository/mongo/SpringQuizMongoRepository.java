package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuizReadDocument;

/**
 * Spring Data Mongo derived queries (Prisma/EF-style method names).
 * Inherited: save, findById, findAll, deleteById, existsById, count, …
 */
public interface SpringQuizMongoRepository extends MongoRepository<QuizReadDocument, UUID> {

    List<QuizReadDocument> findByOrganizationId(UUID organizationId);

    List<QuizReadDocument> findByOrganizationIdAndStatus(UUID organizationId, String status);

    List<QuizReadDocument> findByStatus(String status);

    List<QuizReadDocument> findByOrganizationIdOrderByUpdatedAtDesc(UUID organizationId);

    boolean existsByOrganizationIdAndId(UUID organizationId, UUID id);
}
