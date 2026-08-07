package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.SessionAnswerOptionDocument;

public interface SpringSessionAnswerOptionMongoRepository
        extends MongoRepository<SessionAnswerOptionDocument, UUID> {

    List<SessionAnswerOptionDocument> findBySessionQuestionIdIn(Collection<UUID> sessionQuestionIds);

    void deleteBySessionQuestionIdIn(Collection<UUID> sessionQuestionIds);
}
