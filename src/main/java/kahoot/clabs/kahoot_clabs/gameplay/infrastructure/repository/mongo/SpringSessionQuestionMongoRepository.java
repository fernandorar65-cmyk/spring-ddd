package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.SessionQuestionDocument;

public interface SpringSessionQuestionMongoRepository extends MongoRepository<SessionQuestionDocument, UUID> {

    List<SessionQuestionDocument> findBySessionId(UUID sessionId);

    void deleteBySessionId(UUID sessionId);
}
