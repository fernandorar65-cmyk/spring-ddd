package kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.UserDocument;

public interface UserMongoRepository extends MongoRepository<UserDocument, UUID> {

    Optional<UserDocument> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
