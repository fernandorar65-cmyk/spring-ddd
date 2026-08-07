package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.CategoryDocument;

public interface SpringCategoryMongoRepository extends MongoRepository<CategoryDocument, UUID> {

    List<CategoryDocument> findByOrganizationId(UUID organizationId);
}
