package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuestionAssetDocument;

public interface SpringQuestionAssetMongoRepository extends MongoRepository<QuestionAssetDocument, UUID> {

    List<QuestionAssetDocument> findByQuestionIdIn(Collection<UUID> questionIds);

    void deleteByQuestionIdIn(Collection<UUID> questionIds);
}
