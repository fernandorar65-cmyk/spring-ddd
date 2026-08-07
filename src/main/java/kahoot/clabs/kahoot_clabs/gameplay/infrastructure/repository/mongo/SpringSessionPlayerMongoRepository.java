package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.SessionPlayerDocument;

public interface SpringSessionPlayerMongoRepository extends MongoRepository<SessionPlayerDocument, UUID> {

    List<SessionPlayerDocument> findBySessionId(UUID sessionId);

    void deleteBySessionId(UUID sessionId);
}
