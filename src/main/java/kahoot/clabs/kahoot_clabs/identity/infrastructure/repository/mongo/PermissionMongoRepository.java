package kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.PermissionDocument;

public interface PermissionMongoRepository extends MongoRepository<PermissionDocument, UUID> {

    Optional<PermissionDocument> findByNameIgnoreCaseAndModuleIgnoreCase(String name, String module);
}
