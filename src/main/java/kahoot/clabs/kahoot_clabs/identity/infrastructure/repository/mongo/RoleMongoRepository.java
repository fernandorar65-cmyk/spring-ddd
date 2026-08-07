package kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.RoleDocument;

public interface RoleMongoRepository extends MongoRepository<RoleDocument, UUID> {

    Optional<RoleDocument> findByType(String type);
}
