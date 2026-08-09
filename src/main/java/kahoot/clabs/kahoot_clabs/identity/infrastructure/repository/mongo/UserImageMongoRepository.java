package kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.UserImageDocument;

public interface UserImageMongoRepository extends MongoRepository<UserImageDocument, UUID> {

    List<UserImageDocument> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
