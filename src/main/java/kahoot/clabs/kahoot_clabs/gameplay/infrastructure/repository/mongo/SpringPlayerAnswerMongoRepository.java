package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.PlayerAnswerDocument;

public interface SpringPlayerAnswerMongoRepository extends MongoRepository<PlayerAnswerDocument, UUID> {

    List<PlayerAnswerDocument> findBySessionPlayerIdIn(Collection<UUID> sessionPlayerIds);

    List<PlayerAnswerDocument> findBySessionQuestionIdIn(Collection<UUID> sessionQuestionIds);

    void deleteBySessionPlayerIdIn(Collection<UUID> sessionPlayerIds);

    void deleteBySessionQuestionIdIn(Collection<UUID> sessionQuestionIds);
}
